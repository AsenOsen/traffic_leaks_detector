package detector.NetwPrimitives;


import detector.DB_IpInfo;

public class IPv4Address {


    private byte[] address;


    /*
    * ------------------------------------ Constructors
    * */
    public IPv4Address(byte[] ipv4)
    {
        address = ipv4;
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
        return DB_IpInfo.getInstance().getIpInfo(this);
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
    public int hashCode() {
        return this.toString() == null ?
                0 : this.toString().trim().hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IPv4Address))
            return false;

        return obj.hashCode() == this.hashCode();
    }
}
