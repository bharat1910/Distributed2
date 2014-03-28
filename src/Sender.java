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
	Listener listener;
	int sentCount;
	int delay;
	double probability;
	
	public Sender(int processId, Map<Integer, Integer> processToPort, Listener listener, int delay, double probability)
	{
		this.processId = processId;
		this.processToPort = processToPort;
		this.listener = listener;
		this.delay = delay;
		this.probability = probability;
		sentCount = 0;
	}
	
	@Override
	public void run()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str;
		DatagramSocket datagramSocket = null;
		
		try {
			datagramSocket = new DatagramSocket();
			
			while ((str = br.readLine()) != null) {
				
				sentCount++;
				
				// The first part of the list is the message, to which we append the processId to identify the
				// sender at the receiver
				byte[] buffer = (str + ":" + processId + ":" + sentCount).getBytes();
				
				// Localhost : all processes on the same machine
				InetAddress address = InetAddress.getByName("localhost");
				
				boolean allReceived = false;
				while (!allReceived) {
					for (int i : processToPort.keySet()) {
						if (!listener.acknowledgementReceived(i, sentCount)) {
							DatagramPacket packet = new DatagramPacket(buffer, 
									   buffer.length,
									   address,
									   processToPort.get(i));
							
							//Drop messages with the probability specified in the input
							double toSend = Math.random();
							if (toSend > probability) {
								
								//Introducing a random delay in the range 0, 2 * delay
								Thread.sleep((long) Math.ceil((Math.random() * delay * 2)));
								
								datagramSocket.send(packet);
							}
						}
					}
					
					Thread.sleep(1000);
					
					allReceived = true;
					for (int i : processToPort.keySet()) {
						if (!listener.acknowledgementReceived(i, sentCount)) {
							allReceived = false;
						}
					}
				}
			}	
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			datagramSocket.close();
		}
	}
}
