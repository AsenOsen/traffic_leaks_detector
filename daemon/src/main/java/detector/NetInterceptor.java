package detector;

import com.sun.istack.internal.Nullable;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import org.jetbrains.annotations.NotNull;
import org.jnetpcap.*;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**************************************************************
* This is a Singleton.
* Communicates with Analyzer singleton.
* Processes packets received from native PCAP library.
* ************************************************************/
public class NetInterceptor implements PcapPacketHandler {


    private static final NetInterceptor instance = new NetInterceptor();
    private List<Pcap> channels = null;
    private BlockingQueue<PcapPacket> packetQueue = new LinkedBlockingQueue<PcapPacket>();

    private List<Thread> receivers = new ArrayList<Thread>();
    private Thread queueThread = null;

    private long lastProcessedPacketTime = 0;
    private int reloadAttempts = 0;
    //volatile int a=0, b=0;


    static {
        // Load native library
        String jreArch = System.getProperty("os.arch");
        boolean is64bitJRE = jreArch.indexOf("64") != -1;
        try
        {
            System.loadLibrary(is64bitJRE ? "jnetpcap.x64" : "jnetpcap.x86");
        }
        catch (UnsatisfiedLinkError e)
        {
            LogHandler.Err(e);
        }
    }


    private NetInterceptor()
    {

    }


    public static NetInterceptor getInstance()
    {
        return instance;
    }


    public void startInterceptLoop()
    {
        Init();
        startPacketReceivers();
        startPacketQueueHandler();
    }


    /*
    * Determines whether interceptor loops are down or not.
    * This can happen after PC`s hibernation mode.
    * */
    public boolean isDown()
    {
        // if no packets were processed in last 10 seconds, then everything is down
        return (System.currentTimeMillis() - lastProcessedPacketTime) > 10000;
    }


    /*
    * Method accepts the next packet from LibPCAP and puts it in @packetQueue.
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
        // if too much connection reloadAttempts
        if(reloadAttempts > 5)
        {
            LogHandler.Err(new Exception("Network interfaces not found on this computer. Or could not access them."));
            return;
        }

        // 1) network interfaces discovering : IP4 ONLY
        LogHandler.Log("Looking for network interfaces...");
        List<PcapIf> interfaces = getIPv4Interfaces();
        if(interfaces == null || interfaces.size()==0) {
            reloadAttempts++;
            LogHandler.Warn("Could not find any interface! Trying again..."+ reloadAttempts +"...");
            return;
        }

        // 2) kill each channel in case of restarting
        if(channels!=null) {
            for (Pcap channel : channels)
                channel.breakloop();
        }

        // 3) open channel for each discovered interface
        channels = new ArrayList<Pcap>(getChannels(interfaces));
        if(channels.size()==0)
        {
            LogHandler.Err(new Exception("Cant open any interface. Do app have root rights?"));
            return;
        } else {
            interceptorSucceeded();
        }
    }


    /*
    * Runs when interceptor loops were successfully started
    * */
    private void interceptorSucceeded()
    {
        String msg = "%d loops started at %s...";
        LogHandler.Log(String.format(msg, channels.size(), new Date(System.currentTimeMillis()).toString()));
        reloadAttempts = 0;
    }


    /*
    * Creates a receiver loop in new thread for each network interface.
    * Loop`s callback saves received packets to @packetQueue
    * */
    private void startPacketReceivers()
    {
        for(Thread receiver : receivers)
            receiver.interrupt();
        receivers.clear();

        for(final Pcap channel : channels)
        {
            Thread newReceiver = new Thread(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("__Reciever");
                    channel.loop(Pcap.LOOP_INFINITE, NetInterceptor.this, null);
                }
            });
            receivers.add(newReceiver);
            newReceiver.start();
        }
    }


    /*
    * Creates a separate thread for a packetQueue processor
    * */
    private void startPacketQueueHandler()
    {
        if(queueThread!=null && queueThread.isAlive() && !queueThread.isInterrupted())
            return;

        queueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("__PacketQueue");
                // Each iteration looks for new packets and if found,
                // offers it to the Analyzer`s singleton instance
                while(true)
                {
                    PcapPacket rawPacket;
                    try {
                       rawPacket = packetQueue.take();
                    }
                    catch (InterruptedException e) { continue; }

                    if(rawPacket != null) {
                        Analyzer.getInstance().register(new Packet(rawPacket));
                        lastProcessedPacketTime = System.currentTimeMillis();
                        //b++;
                        //System.out.println(a+" --- "+b);
                    }
                }
            }
        });

        queueThread.start();
    }


    /*
    * Method looks up for all interfaces which has at least one IPv4 address
    * */
    @Nullable
    private List<PcapIf> getIPv4Interfaces()
    {
        List<PcapIf> interfaces = new ArrayList<PcapIf>();
        StringBuilder errorBuffer = new StringBuilder();

        int ifaceDiscover = Pcap.findAllDevs(interfaces, errorBuffer);
        if ( ifaceDiscover != Pcap.OK ) {
            String errorMsg = String.format("Error during interfaces discover! Error: %s", errorBuffer);
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
    @NotNull
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
    @Nullable
    private String createFilterForInterface(PcapIf iface)
    {
        assert iface != null;

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
    @Nullable
    private IPv4Address getInterfaceIPv4Address(PcapIf iface)
    {
        assert iface != null;

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
