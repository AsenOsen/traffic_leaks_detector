package detector;

import detector.Analyzer.Analyzer;
import detector.NetwPrimitives.IPv4Address;
import detector.NetwPrimitives.Packet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jnetpcap.*;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**************************************************************
* This is a Singleton.
* Communicates with Analyzer singleton.
* Processes packets received from native PCAP library.
* ************************************************************/
public class NetInterceptor implements PcapPacketHandler
{

    private static final NetInterceptor instance = new NetInterceptor();

    private List<IPv4Address> localIps = new ArrayList<IPv4Address>();
    private List<Pcap> channels = null;
    private BlockingQueue<PcapPacket> packetQueue = new LinkedBlockingQueue<PcapPacket>();

    private ExecutorService packetReceiverService = Executors.newCachedThreadPool();;
    private Thread queueThread = null;

    private volatile boolean isInterceptorActive = false; // interceptor`s integrity state
    private int connFailedAttempts = 0;                   // amount of failed interceptor runs
    private int reconnectsCount = 0;                      // amount of total interceptor`s reconnects
    //volatile int a=0, b=0;


    private NetInterceptor()
    {
        loadNativeLib();
    }


    public static NetInterceptor getInstance()
    {
        return instance;
    }


    /*
    * Checks the state of interceptor,
    * and if down or obsolete, then tries restart.
    * */
    public void makeActive()
    {
        if(needRestart())
        {
            LogModule.Log("Interceptor needs reconnection. Trying to reconfigure...");
            if (initChannels()) {
                LogModule.Log("Interceptor configuring: receivers and queue handler...");
                startPacketReceivers();
                startPacketQueueHandler();
                LogModule.Log("Interceptor configuration complete.");
            }
            else{
                LogModule.Warn("Configuration try failed!");
            }
        }
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
        }
        catch (InterruptedException e) {

        }
        //a++;
    }


    /*
    * Loads native library
    * */
    private void loadNativeLib()
    {
        String JREArch = System.getProperty("os.arch", null);
        if(JREArch == null)
        {
            LogModule.Err(new Exception("System property 'os.arch' must be set!"));
            return;
        }

        boolean is64bitJRE = JREArch.indexOf("64") != -1;
        try
        {
            System.loadLibrary(is64bitJRE ? "jnetpcap.x64" : "jnetpcap.x86");
        }
        catch (UnsatisfiedLinkError e) {
            LogModule.Err(e);
        }
    }


    /*
    * Looking for interfaces and open sniffing channel for each of them
    * */
    private boolean initChannels()
    {
        // if too much fails
        if(connFailedAttempts > 5)
        {
            LogModule.Err(new Exception("Network interfaces not found on this computer. Or could not access them."));
            return false;
        }

        // 1) network interfaces discovering : IP4 ONLY
        LogModule.Log("Looking for network interfaces...");
        List<PcapIf> interfaces = getInterfaces();
        if(interfaces == null || interfaces.size()==0) {
            LogModule.Warn("Could not find any interface! Try number: "+ connFailedAttempts +"...");
            connFailedAttempts++;
            return false;
        }
        // 1.2) add each interface`s IP to consistency buffer
        localIps.clear();
        for(PcapIf iface : interfaces)
            localIps.addAll(getInterfaceIPv4Address(iface));

        // 2) close each loop sniffing channel (in case of restarting)
        if(channels!=null) {
            for (Pcap channel : channels) {
                channel.breakloop();
                channel.close();
            }
            channels.clear();
        }

        // 3) open channel for each discovered interface
        channels = new ArrayList<Pcap>(getChannels(interfaces));
        if(channels.size()==0)
        {
            LogModule.Warn("Cant open any interface. Do app have root rights?");
            connFailedAttempts++;
            return false;
        }
        else
        {
            String msg = "%d loops started at %s. ReconnectNo: "+(++reconnectsCount);
            LogModule.Log(String.format(msg, channels.size(), new Date(System.currentTimeMillis()).toString()));
            connFailedAttempts = 0;
            return true;
        }
    }


    /*
    * Determines whether interceptor has
    * an obsolete state and need to be restarted
    * */
    private boolean needRestart()
    {
        return !isInterceptorActive || interfacesChanged();
    }


    /*
    * Checks if network devices changed since last interceptor settling
    * */
    private boolean interfacesChanged()
    {
        // get current devices` IPs
        List<IPv4Address> discoveredIps = new ArrayList<IPv4Address>();
        for(PcapIf iface : getInterfaces())
            discoveredIps.addAll(getInterfaceIPv4Address(iface));

        // devices` config has changed
        if(localIps.size() != discoveredIps.size())
            return true;

        boolean isIntegrityOk = localIps.containsAll(discoveredIps);
        return !isIntegrityOk;
    }


    /*
    * Creates a receiver loop in new thread for each network interface(channel).
    * Loop`s callback saves received packets to @packetQueue
    * */
    private void startPacketReceivers()
    {
        for(final Pcap channel : channels)
        {
            packetReceiverService.submit(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("__Receiver-"+System.currentTimeMillis());
                    channel.loop(Pcap.LOOP_INFINITE, NetInterceptor.this, null);
                    isInterceptorActive = false;
                }
            });
        }

        isInterceptorActive = true;
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
                    try
                    {
                       rawPacket = packetQueue.take();
                    }
                    catch (InterruptedException e) { continue; }

                    if(rawPacket != null) {
                        Analyzer.getInstance().registerPacket(new Packet(rawPacket));
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
    @NotNull
    private List<PcapIf> getInterfaces()
    {
        List<PcapIf> interfaces = new ArrayList<PcapIf>();
        StringBuilder errorBuffer = new StringBuilder();

        int ifaceDiscover = Pcap.findAllDevs(interfaces, errorBuffer);
        if ( ifaceDiscover != Pcap.OK ) {
            String errorMsg = String.format("Error during interfaces discover! Error: %s", errorBuffer);
            LogModule.Warn(errorMsg);
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
                LogModule.Warn(errorMsg);
            }
            else {

                // Get the filter for channel
                String filterExpr = createFilterForInterface(iface);
                boolean filterSet = setInterfaceFilter(pcap, filterExpr);

                // Add interface to list only if filter got set
                if(filterSet) {
                    channelList.add(pcap);
                    LogModule.Log(String.format("Filter '%s' set for channel '%s'", filterExpr, iface.getName()));
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

        // Get the IPs of this channel
        List<IPv4Address> ipAddresses = getInterfaceIPv4Address(iface);
        if(ipAddresses.size() == 0)
        {
            LogModule.Err(new Exception("Cant get IPv4 address of interface!"));
            return null;
        }

        StringBuilder filter = new StringBuilder();

        for(IPv4Address address : ipAddresses)
        {
            String l_filter = String.format(
                    "((tcp or udp) and (src host %s) and (not dst net %s))",
                    address.toString(),
                    address.getNetwork()
            );
            filter.append(l_filter).append(" or ");
        }

        return filter.append("1=2").toString();
    }


    /*
    * Sets up a filter to specific channel
    * */
    private boolean setInterfaceFilter(Pcap channel, String filterExpr)
    {
        assert filterExpr != null;

        if(filterExpr == null)
        {
            LogModule.Err(new Exception("Null filter expression for: "+channel.toString()));
            return false;
        }

        PcapBpfProgram filter = new PcapBpfProgram();
        int optimize = 0;
        int network = 0xFFFFFF00;

        if (channel.compile(filter, filterExpr, optimize, network) != Pcap.OK) {
            LogModule.Err(new Exception("Error setting filter(1): "+channel.getErr()));
            return false;
        }

        if (channel.setFilter(filter) != Pcap.OK){
            LogModule.Err(new Exception("Error setting filter(2): "+channel.getErr()));
            return false;
        }

        return true;
    }


    /*
    * Extract the IPv4 address from concrete network interface if it has one
    * */
    private List<IPv4Address> getInterfaceIPv4Address(PcapIf iface)
    {
        assert iface != null;

        List<IPv4Address> addresses = new ArrayList<IPv4Address>();

        for(PcapAddr addr : iface.getAddresses())
        {
            if(addr.getAddr().getFamily() == PcapSockAddr.AF_INET)
            {
                byte[] address = addr.getAddr().getData();
                addresses.add(new IPv4Address(address));
                //return new IPv4Address(address);
            }
        }

        return addresses;
    }

}
