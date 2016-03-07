import java.net.Inet4Address;
import java.net.InetAddress;
 
/**
 * This class represents a DHCP application level message packet 
 */
 
/**
 * @author Laivz
 *
 */
public class DHCPMessage {
	private static final int BOOTREQUEST = 1;
	private static final int BOOTREPLY = 2;
	private static final int DHCPREQUEST = 1;
	private static final int DHCPREPLY = 2;
	private static final int ETHERNET10MB = 1;
	
	//Operation Code:
	//Specifies the general type of message
	private byte op; 
	
	//Hardware Type:
	//Specifies the type of hardware used for the local network
	private byte hType; 
	
	//Hardware Address Length: 
	//Specifies how long hardware addresses are in this message. 
	private byte hLen;
	
	//Hops: 
	private byte hops;
	
	//Transaction Identifier: (32-bit)
	//Identification field generated by client
	//private byte[] xid = new byte[3];
	private int xid;
	
	//Seconds: (16-bit)
	//Number of seconds elapsed since a client began an attempt to acquire or renew a lease. 
	//private byte[] secs = new byte[1];
	private short secs;
	
	//Flags: (16-bit)
	//1bit broadcast flag (0-1)
	//15 bit reserverd
	//private byte[] flags = new byte[1];
	private short flags;
	
	//Client IP Address: (32-bit)
	private byte[] cIAddr;
	//private InetAddress cIAddr = new Inet4Address();

	//"Your" IP Address: (32-bit)
	private byte[] yIAddr;
	//Server IP Address: (32-bit)
	private byte[] sIAddr;
	//Gateway IP Address: (32-bit)
	private byte[] gIAddr;
	
	//Client Hardware Address: (128-bit : 16 bytes)
	private byte[] cHAddr;
	
	//Server Name: (512-bit : 64 bytes)
	private byte[] sName;
	
	//Boot Filename: (1024-bit : 128 bytes)
	private byte[] file;
	
	//Options: (variable)
	private DHCPOptions options;
	
	
 
	public DHCPMessage() {
		cIAddr = new byte[4];
		yIAddr = new byte[4];
		sIAddr = new byte[4];
		gIAddr = new byte[4];
		cHAddr = new byte[16];
		sName = new byte[64];
		file = new byte[128];
		options = new DHCPOptions();
 
		this.printMessage();
	}
	
	public byte[] discoverMsg(byte[] cMacAddress) {
		op = DHCPREQUEST;
		hType = ETHERNET10MB; // (0x1) 10Mb Ethernet
		hLen = 6; // (0x6)
		hops = 0; // (0x0)
		xid = 556223005; // (0x21274A1D)
		secs = 0;  // (0x0)
		flags = 0; // (0x0)
		// DHCP: 0............... = No Broadcast

		cIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		yIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		sIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		gIAddr = new byte[] { 0, 0, 0, 0 }; // 0.0.0.0
		cHAddr = cMacAddress; // 08002B2ED85E
		sName = new byte[sName.length]; // <Blank>
		file = new byte[file.length]; // <Blank>
		// DHCP: Magic Cookie = [OK]
		// DHCP: Option Field (options)
		// DHCP: DHCP Message Type = DHCP Discover
		// DHCP: Client-identifier = (Type: 1) 08 00 2b 2e d8 5e
		// DHCP: Host Name = JUMBO-WS
		// DHCP: Parameter Request List = (Length: 7) 01 0f 03 2c 2e 2f 06
		// DHCP: End of this option field

		return this.externalize();
	}
	
	/**
	 * Converts a DHCPMessage object to a byte array.
	 * @return  a byte array with   information from DHCPMessage object.
	 */
	public byte[] externalize() {
		int staticSize = 236;
		byte[] options = this.options.externalize();
		int size = staticSize + options.length;
		byte[] msg = new byte[size];
		
		//add each field to the msg array
		//single bytes
		msg[0] = this.op;
		msg[1] = this.hType;
		msg[2] = this.hLen;
		msg[3] = this.hops;
		
		//add multibytes
		for (int i=0; i < 4; i++) msg[4+i] = inttobytes(xid)[i];
		for (int i=0; i < 2; i++) msg[8+i] = shorttobytes(secs)[i];
		for (int i=0; i < 2; i++) msg[10+i] = shorttobytes(flags)[i];
		for (int i=0; i < 4; i++) msg[12+i] = cIAddr[i];
		for (int i=0; i < 4; i++) msg[16+i] = yIAddr[i];
		for (int i=0; i < 4; i++) msg[20+i] = sIAddr[i];
		for (int i=0; i < 4; i++) msg[24+i] = gIAddr[i];
		for (int i=0; i < 16; i++) msg[28+i] = cHAddr[i];
		for (int i=0; i < 64; i++) msg[44+i] = sName[i];
		for (int i=0; i < 128; i++) msg[108+i] = file[i];
		
		//add options
		for (int i=0; i < options.length; i++) msg[staticSize+i] = options[i];
      
		return msg;
	}
 
	public byte getOp() {
		return op;
	}
 
	public void setOp(byte op) {
		this.op = op;
	}
 
	public byte getHType() {
		return hType;
	}
 
	public void setHType(byte type) {
		hType = type;
	}
 
	public byte getHLen() {
		return hLen;
	}
 
	public void setHLen(byte len) {
		hLen = len;
	}
 
	public byte getHops() {
		return hops;
	}
 
	public void setHops(byte hops) {
		this.hops = hops;
	}
 
	public int getXid() {
		return xid;
	}
 
	public void setXid(int xid) {
		this.xid = xid;
	}
 
	public short getSecs() {
		return secs;
	}
 
	public void setSecs(short secs) {
		this.secs = secs;
	}
 
	public short getFlags() {
		return flags;
	}
 
	public void setFlags(short flags) {
		this.flags = flags;
	}
 
	public byte[] getCIAddr() {
		return cIAddr;
	}
 
	public void setCIAddr(byte[] addr) {
		cIAddr = addr;
	}
 
	public byte[] getYIAddr() {
		return yIAddr;
	}
 
	public void setYIAddr(byte[] addr) {
		yIAddr = addr;
	}
 
	public byte[] getSIAddr() {
		return sIAddr;
	}
 
	public void setSIAddr(byte[] addr) {
		sIAddr = addr;
	}
 
	public byte[] getGIAddr() {
		return gIAddr;
	}
 
	public void setGIAddr(byte[] addr) {
		gIAddr = addr;
	}
 
	public byte[] getCHAddr() {
		return cHAddr;
	}
 
	public void setCHAddr(byte[] addr) {
		cHAddr = addr;
	}
 
	public byte[] getSName() {
		return sName;
	}
 
	public void setSName(byte[] name) {
		sName = name;
	}
 
	public byte[] getFile() {
		return file;
	}
 
	public void setFile(byte[] file) {
		this.file = file;
	}
 
	public byte[] getOptions() {
		return options.externalize();
	}
 
	//no set options yet...
	/*public void setOptions(byte[] options) {
		this.options = options;
	}*/
	
	public void printMessage() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString() {
		String msg = new String();
		
		msg += "Operation Code: " + this.op + "\n";
		msg += "Hardware Type: " + this.hType  + "\n";
		msg += "Hardware Length: " + this.hLen  + "\n";
		msg += "Hops: " + this.hops + "\n";
		
		msg += Integer.toString(xid) + "\n";
		msg += Short.toString(secs) + "\n";
		msg += Short.toString(flags) + "\n";
		msg += cIAddr.toString() + "\n";
		msg += yIAddr.toString() + "\n";
	    msg += sIAddr.toString() + "\n";
		 msg += gIAddr.toString() + "\n";
		msg += cHAddr.toString() + "\n";
		msg += sName.toString() + "\n";
		 msg += file.toString() + "\n";
		 
		 msg += options.toString() + "\n";
		
		//add options
		 assert(file != null);
		 assert (options != null);
		//msg += options.toString();
		
		//return super.toString();
		return msg;
	}
	
	private byte[] inttobytes(int i){
		byte[] dword = new byte[4];
		dword[0] = (byte) ((i >> 24) & 0x000000FF);
		dword[1] = (byte) ((i >> 16) & 0x000000FF);
		dword[2] = (byte) ((i >> 8) & 0x000000FF);
		dword[3] = (byte) (i & 0x00FF);
		return dword;
	}
	
	private byte[] shorttobytes(short i){
		byte[] b = new byte[2];
		b[0] = (byte) ((i >> 8) & 0x000000FF);
		b[1] = (byte) (i & 0x00FF);
		return b;
	}
	
}