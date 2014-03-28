import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;


public class Publisher extends Thread
{
	int[] vectorTime;
	List<String> messageQueue;
	int processId;
	Lock lock;
	Lock messageQueueLock;
	
	public Publisher(int[] vectorTime, List<String> messageQueue, int processId, Lock lock, Lock messageQueueLock)
	{
		this.vectorTime = vectorTime;
		this.messageQueue = messageQueue;
		this.lock = lock;
		this.messageQueueLock = messageQueueLock;
	}
	
	@Override
	public void run()
	{
		while(true) {
			while(!messageQueueLock.tryLock());
			Iterator<String> i = messageQueue.iterator();
			while(i.hasNext()) {
				String s = i.next();
				if (canBePublished(s)) {
					System.out.println(s);
					i.remove();
				}
			}
			messageQueueLock.unlock();
		}
	}
	
	public boolean canBePublished(String s)
	{
		int fromProcess = Integer.parseInt(s.split(":")[1]);
		int[] messageTimestamp = getVectorListFromString(s.split(":")[3]);
		
		for (int i=0; i<vectorTime.length; i++) {
			if (i != (fromProcess - 1)) {
				if(messageTimestamp[i] > vectorTime[i]) {
					return false;
				}
			}
		}
		
		if (messageTimestamp[fromProcess - 1] == vectorTime[fromProcess - 1] + 1) {
			while(!lock.tryLock());
			vectorTime[fromProcess - 1] = vectorTime[fromProcess - 1] + 1;
			lock.unlock();
			return true;
		} else {
			return false;
		}
	}
	
	public int[] getVectorListFromString(String s)
	{
		String[] list = s.trim().split(" ");
		int[] result = new int[vectorTime.length];
		
		for (int i=0; i<list.length; i++) {
			result[i] = Integer.parseInt(list[i]);
		}
		
		return result;
	}
}
