package detector;

import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import org.jnetpcap.*;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**************************************************************
* This is a Singleton.
* Communicates with Analyzer singleton.
* Processes packets received from native PCAP library.
* ************************************************************/
public class NetInterceptor implements PcapPacketHandler {


    private static final NetInterceptor instance = new NetInterceptor();
    private ArrayList<Pcap> channels = null;
    private BlockingQueue<PcapPacket> packetQueue = new LinkedBlockingQueue<PcapPacket>();
    //volatile int a=0, b=0;


    private NetInterceptor()
    {
        Init();
    }


    public static NetInterceptor getInstance()
    {
        return instance;
    }


    public void startInterceptLoop()
    {
        startPacketReceivers();
        startPacketQueueHandler();
        LogHandler.Log("Loops started at "+ new Date(System.currentTimeMillis()).toString()+"...");
    }


    /*
    * Method accepts the next packet from LibPcap and puts it in @packetQueue.
    * Queue is processed by separate thread.
    * */
    @Override
    public void nextPacket(PcapPacket packet, Object userData)
    {
        try {
            packetQueue.put(packet);
            //System.out.println(a+" --- "+b);
        } catch (InterruptedException e) {

        }
        //a++;
    }


    private void Init()
    {
        // 1) Load native library
        String jreArch = System.getProperty("os.arch");
        boolean is64bitJRE = jreArch.indexOf("64") != -1;
        try
        {
            System.loadLibrary(is64bitJRE ? "jnetpcap.x64" : "jnetpcap.x86");
        }
        catch (UnsatisfiedLinkError e)
        {
            LogHandler.Err(e);
            return;
        }

        // 2) Network interface discovering : IP4 ONLY
        List<PcapIf> interfaces = getIPv4Interfaces();
        if(interfaces == null || interfaces.size()==0) {
            LogHandler.Err(new Exception("Network interfaces not found!"));
            return;
        }

        // 3) Open channels for each interface
        channels = new ArrayList<Pcap>(getChannels(interfaces));
        if(channels == null || channels.size()==0) {
            LogHandler.Err(new Exception("Cant open any interface. Do I have rights?"));
            return;
        }
    }


    /*
    * Creates a receiver loop in new thread for each network interface.
    * Loop`s callback saves received packets to @packetQueue
    * */
    private void startPacketReceivers()
    {
        for(final Pcap channel : channels)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("__Reciever");
                    channel.loop(Pcap.LOOP_INFINITE, NetInterceptor.this, null);
                }
            }).start();
        }
    }


    /*
    * Creates a separate thread for a packetQueue processor
    * */
    private void startPacketQueueHandler()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("__PacketQueue");
                while(true)
                {
                    // Each iteration looks for new packets and if found,
                    // offers it to the Analyzer`s singleton instance
                    try {
                        PcapPacket rawPacket = packetQueue.take();
                        if(rawPacket != null) {
                            Analyzer.getInstance().register(new Packet(rawPacket));
                            //b++;
                            //System.out.println(a+" --- "+b);
                        }

                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }


    /*
    * Method looks up for all interfaces which has at least one IPv4 address
    * */
    private List<PcapIf> getIPv4Interfaces()
    {
        List<PcapIf> interfaces = new ArrayList<PcapIf>();
        StringBuilder errorBuffer = new StringBuilder();

        int ifaceDiscover = Pcap.findAllDevs(interfaces, errorBuffer);
        if ( ifaceDiscover != Pcap.OK || interfaces.isEmpty() ) {
            String errorMsg = String.format("Error during interfaces discover:\n%s", errorBuffer);
            LogHandler.Err(new Exception(errorMsg));
            return null;
        }

        List<PcapIf> IPv4Interfaces = new ArrayList<PcapIf>();

        for(PcapIf iface : interfaces)
        {
            for (PcapAddr addr : iface.getAddresses())
                if(addr.getAddr().getFamily() == PcapSockAddr.AF_INET)
                {
                    IPv4Interfaces.add(iface);
                    break;
                }
        }

        return IPv4Interfaces;
    }


    /*
    * Method open the handles of selected interfaces
    * */
    private List<Pcap> getChannels(List<PcapIf> interfaces)
    {
        ArrayList<Pcap> channelList = new ArrayList<Pcap>();

        int packetSize = 64 * 1024;             // 65kB
        int flags = Pcap.MODE_NON_PROMISCUOUS;
        int timeout = 1000;                     // 1sec

        for(PcapIf iface : interfaces)
        {
            StringBuilder errorBuffer = new StringBuilder();
            Pcap pcap = Pcap.openLive( iface.getName(), packetSize, flags, timeout, errorBuffer );

            if(pcap == null){
                String errorMsg = String.format("Error during openLive call: %s", errorBuffer);
                LogHandler.Err(new Exception(errorMsg));
            }
            else {

                // Get the filter for channel
                String filterExpr = createFilterForInterface(iface);
                boolean filterSet = setInterfaceFilter(pcap, filterExpr);

                // Add interface to list only if filter got set
                if(filterSet) {
                    channelList.add(pcap);
                    LogHandler.Log(String.format("Filter '%s' set for channel '%s'", filterExpr, iface.getName()));
                }
            }
        }

        return channelList;
    }


    /*
    * Creates a filter for specific interface:
    *   1) TCP, UDP only
    *   2) Outgoing traffic only
    *   3) TrafficFlow which goes out of local network
    * */
    private String createFilterForInterface(PcapIf iface)
    {
        // Get the MAC of this channel
        IPv4Address ipAddress = getInterfaceIPv4Address(iface);
        if(ipAddress == null)
        {
            LogHandler.Err(new Exception("Cant get mac address of interface!"));
            return null;
        }

        String filter = String.format(
                "(tcp or udp) and (src host %s) and (not dst net %s)",
                ipAddress.toString(),
                ipAddress.getNetwork()
        );

        return filter;
    }


    /*
    * Sets up a filter to specific channel
    * */
    private boolean setInterfaceFilter(Pcap channel, String filterExpr)
    {
        if(filterExpr == null)
        {
            LogHandler.Err(new Exception("Null filter expression for: "+channel.toString()));
            return false;
        }

        PcapBpfProgram filter = new PcapBpfProgram();
        int optimize = 0;
        int network = 0xFFFFFF00;

        if (channel.compile(filter, filterExpr, optimize, network) != Pcap.OK) {
            LogHandler.Err(new Exception("Error setting filter(1): "+channel.getErr()));
            return false;
        }

        if (channel.setFilter(filter) != Pcap.OK){
            LogHandler.Err(new Exception("Error setting filter(2): "+channel.getErr()));
            return false;
        }

        return true;
    }


    /*
    * Extract the IPv4 address from concrete network interface if it has one
    * */
    private IPv4Address getInterfaceIPv4Address(PcapIf iface)
    {
        for(PcapAddr addr : iface.getAddresses())
        {
            if(addr.getAddr().getFamily() == PcapSockAddr.AF_INET)
            {
                byte[] address = addr.getAddr().getData();
                return new IPv4Address(address);
            }
        }

        return null;
    }

}
