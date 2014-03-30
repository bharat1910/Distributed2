import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UDPTotal
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
		Lock lock = new ReentrantLock();
		Lock messageQueueLock = new ReentrantLock();
		
		while ((str = brFile.readLine()) != null) {
			processToPort.put(Integer.parseInt(str.split(" ")[0]), Integer.parseInt(str.split(" ")[1]));
		}
		
		brFile.close();
		
		Queue<Message> messageQueue = new PriorityQueue<Message>();
		
		ListenerTotal l = new ListenerTotal(Integer.parseInt(args[0]), processToPort, lock, messageQueue, messageQueueLock);
		l.start();
		
		SenderTotal s = new SenderTotal(Integer.parseInt(args[0]), processToPort, l, Integer.parseInt(args[1]), Double.parseDouble(args[2]), lock);
		s.start();
		
		PublisherTotal p = new PublisherTotal(l, messageQueue, messageQueueLock);
		p.start();
	}
	
	public static String getTimeStamp(int[] v)
	{
		String s = "";
		for (int i : v) {
			s += i + " ";
		}
		return s.trim();
	}
}
