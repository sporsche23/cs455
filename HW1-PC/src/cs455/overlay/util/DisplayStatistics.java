package cs455.overlay.util;

public class DisplayStatistics {
	
	private int totalPacketsSent = 0;
	private int totalPacketsReceived = 0;
	private int totalPacketsRelayed = 0;
	private int totalValuesSent = 0;
	private int totalValuesReceived = 0;
	private int count = 0;
	
	//this is used to display the sent/received and relayed statistics
	public synchronized void display(int size, int node, int packSent, int packRec, int packRel, long sumSent, long sumRec) {
		totalPacketsSent += packSent;
		totalPacketsReceived += packRec;
		totalPacketsRelayed += packRel;
		totalValuesSent += sumSent;
		totalValuesReceived += sumRec;
		count++;
		System.out.println("NodeID | Packets Sent | Packets Received | Packets Relayed | Sum Values Sent | Sum Values Received");
		System.out.println(node + " | " + packSent + " | " + packRec + " | " + packRel
				+ " | " + sumSent + " | " + sumRec);
		System.out.println("\n");
		
		if (count == size) {
			//call method to print out the total sums
			displaySums();
		}
	}
	
	public void resetCount() {
		count = 0;
	}
	
	private void displaySums() {
		System.out.println("Sum" + " | " + totalPacketsSent + " | " + totalPacketsReceived + " | " + totalPacketsRelayed
				+ " | " + totalValuesSent + " | " + totalValuesReceived);
	}

}
