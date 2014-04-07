import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 * Chat is a entry point to the application which initiates objects 
 * for causal and total ordering implementations based on the argument
 * passed. 
 */

public class Chat
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader brFile = new BufferedReader(new FileReader(args[0]));
		String s = brFile.readLine();
		brFile.close();
		
		if (s.equalsIgnoreCase("1")) {
			UDP udp = new UDP();
			udp.start(args);
		} else {
			UDPTotal udpTotal = new UDPTotal();
			udpTotal.start(args);
		}
	}
}
