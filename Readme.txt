Group Members -
Amit Jaspal (jaspal2)
Bharat Thatavarti (thatava2)

1) Description of Algorithms (further explained in the Report.txt file)-
Total Ordering - we have used a sequencer to generated multicast messages that are totally ordered. Before any process can send a message, it gets the sequence number for the message from the sequencer. At each process, the listener maintains a queue of messages received at any point of time. Once the appropriate message, one with sequencer number = number seen so far at the process + 1, shows up, it is processed. Post this, the queue is checked if the next set of messages can be delivered as well.

Causal Ordering - This is achieved by the use of vector timestamps. At the listener, for each incoming message, a check is performed to see if this is the next message from the process and that it does not violate any causal constraints. This is the code snippet highlighting the algorithm used :
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
Basically, for every process that is not the one which sends the message, the corresponding value in the vector timestamp at the receiving process must be greater than the one in the timestamp shown by the message. For the value corresponding to the receiving process the value in the message must be one greater than the value seen at the process.

2) Directions to compile the source code -
All the files inside the src directory should be compiled - javac *.java

3) Command line input format -
Each process must be started by running Chat.java : java Chat configFile 100 0.4 1
The first input parameter is the config file
The second represents the mean delay to be introduced before a message can be sent [the delay is a random number between 0 and 2 * mean delay in ms]
The third parameter represents the probability with which the messages can be dropped
The fourth represents the process number

For total ordering a seqencer must be started as well : java Sequencer configFile

4) Config file format :
2
1 2000
2 2001
3 2002
4 2003

The first line determines whether total or causal ordering needs to be run - 1 for causal and any other number for total. 
The second line to the end of the file indicates the number of processes and the port on which each of them must be started (locally).
