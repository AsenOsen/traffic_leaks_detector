package detector.NetwPrimitives;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/******************************************************************************
 *   This class is a wrapper for @PcapPacket instance.
 *   It parses the PcapPacket and stores its data in fields.
* ****************************************************************************/
public class Packet {


    // Static low-level packet header representations
    // using for parse PcapPacket in these instances
    private static final Tcp tcpHeader = new Tcp();
    private static final Udp udpHeader = new Udp();
    private static final Ip4 ipHeader  = new Ip4();


    private IPv4Address destAddress;
    private Port srcPort;
    private int payloadLen;


    public Packet(PcapPacket packet)
    {
        parsePacketData(packet);
    }


    public int getPayloadSize()
    {
        return payloadLen;
    }


    public IPv4Address getDestinationAddress()
    {
        return new IPv4Address(destAddress);
    }


    public Port getSourcePort()
    {
        return new Port(srcPort);
    }


    private void parsePacketData(PcapPacket packet)
    {
        // Synchronize shared static fields
        synchronized (Packet.class)
        {
            if (packet.hasHeader(tcpHeader))
            {
                payloadLen = tcpHeader.getPayload().length;
                srcPort = new Port(tcpHeader.source());
            } else if (packet.hasHeader(udpHeader))
            {
                payloadLen = udpHeader.getPayload().length;
                srcPort = new Port(udpHeader.source());
            }
        }
        synchronized (ipHeader)
        {
            if (packet.hasHeader(ipHeader))
                destAddress = new IPv4Address(ipHeader.destination());
        }
    }

}
