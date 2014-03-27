import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;


public class Sender extends Thread
{
	int processId;
	Map<Integer, Integer> processToPort;
	
	public Sender(int processId, Map<Integer, Integer> processToPort)
	{
		this.processId = processId;
		this.processToPort = processToPort;
	}
	
	@Override
	public void run()
	{
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str;
		
		try {
			while ((str = br.readLine()) != null) {
				
				// The message is ':' separated. The format is 'message : destination processId'
				String[] strList = str.split(":");
				
				// The first part of the list is the message, to which we append the processId to identify the
				// sender at the receiver
				byte[] buffer = (strList[0] + ": Sent By - " + processId).getBytes();
				
				// Localhost : all processes on the same machine
				InetAddress address = InetAddress.getLocalHost();
				
				// The second part of the input is the destination
				DatagramPacket packet = new DatagramPacket(buffer, 
														   buffer.length,
														   address,
														   processToPort.get(Integer.parseInt(strList[1])));
				DatagramSocket datagramSocket = new DatagramSocket();
				datagramSocket.send(packet);
			}	
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
