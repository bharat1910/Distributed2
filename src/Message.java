
public class Message implements Comparable<Message> {
	int priority;
	String s;

	@Override
	public int compareTo(Message o) {
		return this.priority - o.priority;
	}
}