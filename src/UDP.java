import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class UDP extends Thread
{
	int port;
	
	public UDP(int port)
	{
		this.port = port;
	}
	
	public void run()
	{
		int i = 0;
		while (i++ < 10) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(i);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		// Receives the arguments
		// 0 : processId
		
		// The input file that contains the list of processId and its corresponding port
		BufferedReader brFile = new BufferedReader(new FileReader("portIds.txt"));
		String str;
		Map<Integer, Integer> processToPort = new HashMap<Integer, Integer>();
		
		while ((str = brFile.readLine()) != null) {
			processToPort.put(Integer.parseInt(str.split(" ")[0]), Integer.parseInt(str.split(" ")[1]));
		}
		
		brFile.close();
		
		Sender s = new Sender(Integer.parseInt(args[0]), processToPort);
		s.start();
		
		Listener l = new Listener(Integer.parseInt(args[0]), processToPort);
		l.start();
	}
}
