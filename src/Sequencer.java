import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sequencer
{
	public static void main(String[] args) throws NumberFormatException, IOException
	{
		// The input file that contains the list of processId and its corresponding port
		BufferedReader brFile = new BufferedReader(new FileReader(args[0]));
		String str;
		Map<Integer, Integer> processToPort = new HashMap<Integer, Integer>();
		int priority = 0;
		Map<String, Integer> messages = new HashMap<String, Integer>();
		
		brFile.readLine();
		
		while ((str = brFile.readLine()) != null) {
			processToPort.put(Integer.parseInt(str.split(" ")[0]), Integer.parseInt(str.split(" ")[1]));
		}
		
		brFile.close();
		
		DatagramSocket datagramSocketReceive = null;
		DatagramSocket datagramSocketSend = null;

		try {
			datagramSocketReceive = new DatagramSocket(9007);
			datagramSocketSend = new DatagramSocket();
			
			while (true) {
				byte[] buffer = new byte[65504];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				datagramSocketReceive.receive(packet);
				buffer = packet.getData();
				int fromProcess = Integer.parseInt(new String(buffer).split(":")[0].trim());
				
				if (messages.containsKey(new String(buffer))) {
					priority = messages.get(new String(buffer));
				} else {
					priority = priority + 1;
					messages.put(new String(buffer), priority);
				}
				
				System.out.println("Assigned priority : " + priority + " to a message from " + fromProcess);
				buffer = ("ack:" + 7 + ":" + new String(buffer).split(":")[1].trim() + ":" + priority).getBytes();
				
				// Localhost : all processes on the same machine
				InetAddress address = InetAddress.getByName("localhost");
				
				// The second part of the input is the destination
				packet = new DatagramPacket(buffer, 
											buffer.length,
											address,
											processToPort.get(fromProcess));
				
				datagramSocketSend.send(packet);
			}
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			datagramSocketReceive.close();
			datagramSocketSend.close();
		}
	}
}
