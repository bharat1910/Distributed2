import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UDP
{
	public static void main(String[] args) throws IOException
	{
		// Receives the arguments
		// 0 : processId
		// 1 : mean delay (the delay will be in the range 0 - 2 * delay) in milliseconds
		// 2 : probability with which the messages are dropped
		
		// The input file that contains the list of processId and its corresponding port
		BufferedReader brFile = new BufferedReader(new FileReader("portIds.txt"));
		String str;
		Map<Integer, Integer> processToPort = new HashMap<Integer, Integer>();
		int[] vectorTime = new int[processToPort.size()];
		Lock lock = new ReentrantLock();
		
		while ((str = brFile.readLine()) != null) {
			processToPort.put(Integer.parseInt(str.split(" ")[0]), Integer.parseInt(str.split(" ")[1]));
		}
		
		brFile.close();
		
		Listener l = new Listener(Integer.parseInt(args[0]), processToPort, lock, vectorTime);
		l.start();
		
		Sender s = new Sender(Integer.parseInt(args[0]), processToPort, l, Integer.parseInt(args[1]), Double.parseDouble(args[2]), lock, vectorTime);
		s.start();
	}
}
