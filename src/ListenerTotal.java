import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;


/*
 * Listner thread for collecting messages
 * sent by other processes. This thread is used while 
 * implementing total ordering of messages. 
 */
public class ListenerTotal extends Thread
{
	int processId;
	Map<Integer, Integer> processToPort;
	Map<Integer, Set<Integer>> messagesFromProcesses;
	Lock lock;
	Queue<Message> messageQueue;
	Lock messageQueueLock;
	int priority;
	
	public ListenerTotal(int processId, Map<Integer, Integer> processToPort, Lock lock, Queue<Message> messageQueue, Lock messageQueueLock)
	{
		this.processId = processId;
		this.processToPort = processToPort;
		this.lock = lock;
		messagesFromProcesses  = new HashMap<Integer, Set<Integer>>();
		this.messageQueue = messageQueue;
		this.messageQueueLock = messageQueueLock;
		
		for (int i : processToPort.keySet()) {
			messagesFromProcesses.put(i, new HashSet<Integer>());
		}
		// 7: processId of the sequencer.
		messagesFromProcesses.put(7, new HashSet<Integer>());
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
						if (strList.length == 4) {
							this.priority = Integer.parseInt(strList[3].trim());
						}
						continue;
					}
					
					Message m = new Message();
					m.s = new String(buffer);
					m.priority = Integer.parseInt(new String(buffer).split(":")[3].trim());
					
					while(!messageQueueLock.tryLock());
					messageQueue.add(m);
					messageQueueLock.unlock();
					
					sendAcknowledgement(Integer.parseInt(strList[1].trim()), Integer.parseInt(strList[2].trim()));
				}
			} catch (Exception e) {
				System.out.println("Error in lsitener :");
				e.printStackTrace();
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
