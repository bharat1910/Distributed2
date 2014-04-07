import java.util.Queue;
import java.util.concurrent.locks.Lock;

/*
 * PublisherTotal Thread is responsible for pruning messages 
 * received by the Listner; if the sequence number of 
 * the message is not matched with the expected value
 * then the PublisherTotal keeps the message in the buffers and 
 * publishes it only when the matching happens.   
 */

public class PublisherTotal extends Thread
{
	int published = 0;
	ListenerTotal listener;
	Queue<Message> messageQueue;
	Lock messageQueueLock;
	int seen;
	
	public PublisherTotal(ListenerTotal listener, Queue<Message> messageQueue, Lock messageQueueLock)
	{
		this.published = 0;
		this.listener = listener;
		this.messageQueue = messageQueue;
		this.messageQueueLock = messageQueueLock;
		seen = 0;
	}
	
	@Override
	public void run()
	{
		while(true) {
			while(!messageQueueLock.tryLock());
				if (!messageQueue.isEmpty()) {
					if (messageQueue.peek().priority == seen + 1) {
						System.out.println(messageQueue.remove().s);
						seen++;
					}
				}
			messageQueueLock.unlock();
		}
	}
}
