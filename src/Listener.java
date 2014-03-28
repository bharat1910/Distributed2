import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

public class Listener extends Thread
{
	int processId;
	Map<Integer, Integer> processToPort;
	Map<Integer, Set<Integer>> messagesFromProcesses;
	Lock lock;
	int[] vectorTime;
	
	public Listener(int processId, Map<Integer, Integer> processToPort, Lock lock, int[] vectorTime)
	{
		this.processId = processId;
		this.processToPort = processToPort;
		this.lock = lock;
		this.vectorTime = vectorTime;
		messagesFromProcesses  = new HashMap<Integer, Set<Integer>>();
		
		for (int i : processToPort.keySet()) {
			messagesFromProcesses.put(i, new HashSet<Integer>());
		}
	}
	
	@Override
	public void run()
	{
		DatagramSocket datagramSocket = null;

			try {
				datagramSocket = new DatagramSocket(processToPort.get(processId));
				
				while (true) {
					byte[] buffer = new byte[65504];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	
					datagramSocket.receive(packet);
					buffer = packet.getData();
					
					String[] strList = new String(buffer).split(":");
					if (strList[0].equals("ack")) {
						messagesFromProcesses.get(Integer.parseInt(strList[1].trim())).add(Integer.parseInt(strList[2].trim()));
						continue;
					}
					
					System.out.println(new String(buffer));
					
					sendAcknowledgement(Integer.parseInt(strList[1].trim()), Integer.parseInt(strList[2].trim()));
				}
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				datagramSocket.close();
			}
	}
	
	public void sendAcknowledgement(int pId, int count) throws IOException
	{
		byte[] buffer = ("ack:" + processId + ":" + count).getBytes();
		
		// Localhost : all processes on the same machine
		InetAddress address = InetAddress.getByName("localhost");
		
		// The second part of the input is the destination
		DatagramPacket packet = new DatagramPacket(buffer, 
												   buffer.length,
												   address,
												   processToPort.get(pId));
		DatagramSocket datagramSocket = new DatagramSocket();
		
		datagramSocket.send(packet);
		
		datagramSocket.close();
	}
	
	public boolean acknowledgementReceived(int pId, int count)
	{
		if (messagesFromProcesses.get(pId).contains(count)) {
			return true;
		} else {
			return false;
		}
	}
}
