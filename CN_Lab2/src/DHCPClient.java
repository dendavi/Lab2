import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Arrays;

public class DHCPClient {
	private static final int MAX_BUFFER_SIZE = 1024; // 1024 bytesa
	private static int listenPort =  1234;
	private static String serverIP = "10.33.14.246";
	private static int serverPort =  1234;
	
	private static DatagramSocket socket = null;
	private static boolean assignedIP;
	private static byte[] clientIP;
	
	private static long startTime;
	private static String logFileName = "ClientLog.txt";
	private static String NL;

	/*
	 * public DHCPClient(int servePort) { listenPort = servePort; new
	 * DHCPServer(); }
	 */

	public DHCPClient() {
		//setting dhcp_client startingtime
		startTime = System.currentTimeMillis();
		
		System.out.println("Connecting to DHCPServer at " + serverIP + " on port " + serverPort + "...");
		try {
			socket = new DatagramSocket(listenPort);  
			assignedIP = false;   //ip not assigned when client starts
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printToConsole("DHCPClient: init complete server started" + "\n");
	}

	public DHCPClient(int parseInt) {
		listenPort = parseInt;
	}

	private void sendTestPacket() {
		//byte[] payload = new byte[MAX_BUFFER_SIZE];
		int length = 6;
		byte[] payload = new byte[length];
		payload[0] = 'h';
		payload[1] = '3';
		payload[2] = 'l';
		payload[3] = 'l';
		payload[4] = 'o';
		payload[5] = '!';
		DatagramPacket p;
		try {
			p = new DatagramPacket(payload, length, InetAddress.getByName(serverIP), serverPort);
			//System.out.println("Connection Established Successfully!");
			System.out.println("Sending data: " + Arrays.toString(p.getData()));
			socket.send(p); //throws i/o exception
			socket.send(p);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public static  void sendPacket(byte[] payload, String serverIP) {
		assert(payload.length <= MAX_BUFFER_SIZE);
		
			try {
				DatagramPacket p = new DatagramPacket(payload, payload.length, InetAddress.getByName(serverIP), serverPort);
				System.out.println("Sending data: " + 
						//Arrays.toString(p.getData()) + 
						"to " + p.getAddress().toString());
				socket.send(p); //throws i/o exception
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	public static  void broadcastPacket(byte[] payload) {
		assert(payload.length <= MAX_BUFFER_SIZE);
		String broadcastIP = "255.255.255.255";
		sendPacket(payload,broadcastIP);
	}
	public static  void sendPacketToLab(byte[] payload) {
		assert(payload.length <= MAX_BUFFER_SIZE);
		String labIP = "10.33.14.246";
		sendPacket(payload,labIP);
	}
	public static byte[] receivePacket() {
		System.out.println("Listening on port " + listenPort + "...");
		byte[] payload = new byte[MAX_BUFFER_SIZE];
		int length = MAX_BUFFER_SIZE;
		DatagramPacket p = new DatagramPacket(payload, length);
		
		try {
			socket.receive(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // throws i/o exception

		System.out.println("Connection established from " + p.getPort()+ p.getAddress());
		System.out.println("Data Received: " + Arrays.toString(p.getData()));
		//log("log.txt", "DHCPServer: packet received");
		
		return p.getData();

	}
	private static void printToConsole(String message) {
		Timestamp logTime = new Timestamp(System.currentTimeMillis());
		String data = new String(logTime.toString() + " - " + printUpTime() + "\n" + message + "\n");
		System.out.println(data);
	}
	
	public static long upTime() {
		return System.currentTimeMillis()-startTime;
	}
	
	public static String printUpTime(){
		return new String("Server Uptime: " + upTime()+"ms");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DHCPClient client;
		
		if (args.length >= 1) { 
			client = new DHCPClient(Integer.parseInt(args[0])); 
		} else {
			client = new DHCPClient();

		}
		 
	    DHCPMessage msgTest = new DHCPMessage();
		msgTest.discoverMsg(DHCPUtility.getMacAddress());
		DHCPUtility.printMacAddress();
		sendPacketToLab(msgTest.externalize());
		printToConsole("DHCPClient: Broadcasting Discover Message");
		DHCPMessage msg = new DHCPMessage(receivePacket());
		msg.printMessage();
		printToConsole("DHCPClient: DHCP Offer Message Received"+ "\n" + msg.toString());
		msgTest.requestMsg(DHCPUtility.getMacAddress(), new byte[]{(byte)192,(byte)168,1,9});
		broadcastPacket(msgTest.externalize());
		printToConsole("DHCPClient: Broadcasting Request Message");
		DHCPMessage msg1 = new DHCPMessage(receivePacket());
		msg1.printMessage();
		printToConsole("DHCPClient: DHCP ACK Message Received"+ "\n" + msg1.toString());
		// }

	}
	
	
}
