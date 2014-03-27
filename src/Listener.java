import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class Listener extends Thread
{
	int processId;
	Map<Integer, Integer> processToPort;
	
	public Listener(int processId, Map<Integer, Integer> processToPort)
	{
		this.processId = processId;
		this.processToPort = processToPort;
	}
	
	@Override
	public void run()
	{
		while (true) {
			try {
				DatagramSocket datagramSocket = new DatagramSocket(processToPort.get(processId));

				byte[] buffer = new byte[65504];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

				datagramSocket.receive(packet);
				buffer = packet.getData();
				
				System.out.println(new String(buffer));
				
				datagramSocket.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
}
