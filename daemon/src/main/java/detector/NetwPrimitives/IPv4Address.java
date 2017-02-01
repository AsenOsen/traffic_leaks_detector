package detector.NetwPrimitives;


import detector.Data.IpInfoDB;

public class IPv4Address {


    private byte[] address;
    private String addressStr = null;


    /*
    * ------------------------------------ Constructors
    * */
    public IPv4Address(byte[] ipv4)
    {
        address = ipv4;
        addressStr = this.toString();
    }
    public IPv4Address(IPv4Address ip)
    {
        this(ip==null ? new byte[0] : ip.address);
    }
    /*
    * ------------------------------------
    * */


    public String getNetwork()
    {
        String ipAsString = this.toString();

        int lastByteIdx = ipAsString.lastIndexOf('.');
        if(lastByteIdx == -1)
            return null;

        return ipAsString.substring(0, lastByteIdx);
    }


    public boolean isValid()
    {
        return address!=null && address.length==4;
    }


    /*@WebMethod
    public String resolveHost()
    {
        try {
            InetAddress host = InetAddress.getByAddress(address);
            return host.getHostName();
        } catch (UnknownHostException e) {
            return this.toString();
        }
    }*/


    public IpInfo getIpInfo()
    {
        return IpInfoDB.getInstance().getIpInfo(this);
    }


    @Override
    public String toString() {

        if(address.length != 4)
            return null;

        return  (address[0] & 0xFF)+"."+
                (address[1] & 0xFF)+"."+
                (address[2] & 0xFF)+"."+
                (address[3] & 0xFF);

    }


    @Override
    public int hashCode()
    {
        return addressStr == null ? 0 : addressStr.hashCode();
    }


    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof IPv4Address))
            return false;

        return obj.hashCode() == this.hashCode();
    }
}
