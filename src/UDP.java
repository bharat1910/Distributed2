import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UDP
{
	public void start(String[] args) throws IOException
	{
		// Receives the arguments 
		// 1 : Config File
		// 2 : mean delay (the delay will be in the range 0 - 2 * delay) in milliseconds
		// 3 : probability with which the messages are dropped
		// 4 : Process Id
		
		// The input file that contains the list of processId and its corresponding port
		BufferedReader brFile = new BufferedReader(new FileReader(args[0]));
		String str;
		Map<Integer, Integer> processToPort = new HashMap<Integer, Integer>();
		Lock lock = new ReentrantLock();
		Lock messageQueueLock = new ReentrantLock();
		
		str = brFile.readLine();
		
		while ((str = brFile.readLine()) != null) {
			processToPort.put(Integer.parseInt(str.split(" ")[0]), Integer.parseInt(str.split(" ")[1]));
		}
		
		brFile.close();
		
		int[] vectorTime = new int[processToPort.size()];
		List<String> messageQueue = new ArrayList<String>();
		
		Listener l = new Listener(Integer.parseInt(args[3]), processToPort, lock, vectorTime, messageQueue, messageQueueLock);
		l.start();
		
		Sender s = new Sender(Integer.parseInt(args[3]), processToPort, l, Integer.parseInt(args[1]), Double.parseDouble(args[2]), lock, vectorTime);
		s.start();
		
		Publisher p = new Publisher(vectorTime, messageQueue, Integer.parseInt(args[3]), lock, messageQueueLock);
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
