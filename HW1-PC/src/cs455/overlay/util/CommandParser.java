package cs455.overlay.util;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;

public class CommandParser {
	
	Scanner inputScanner = new Scanner(System.in);

	public void registryCommands(Registry registry) {
		while(true) {
			
			System.out.print("Enter a command: ");
			String input = inputScanner.nextLine();
			String[] args = input.split(" ");
			
			if(args[0].equals("list-messaging-nodes")) { 
				System.out.println("Printing all messaging nodes");
				
				//print the list of Hashmaps (hostname, port-number, node ID)
				registry.printListOfNodes();
			}
			else if(args[0].equals("setup-overlay")) {
				int sizeRout = Integer.parseInt(args[1]);
				System.out.println("Setting up the overlay " + sizeRout);
				//reigstry should set up the overlay (Registry-Sends-Node _manifest)
				try {
					registry.setupOverlay((byte)sizeRout);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(args[0].equals("list-routing-tables")) {
				System.out.println("Printing info about each routing table");
				//list information about routing table for each node
				registry.printRoutingTables();
			}
			else if(args[0].equals("start")) {
				int numMessages = Integer.parseInt(args[1]);
				System.out.println("start the message passing " + numMessages);
				//command each messaging node to send the specified # of messages
				try {
					registry.sendPackets(numMessages);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				continue;
			}
		}
		
	}
	
	public void messagingCommands(MessagingNode message) {
		while (true) {
			 System.out.print("Enter a command: ");
			 String input = inputScanner.nextLine();
			 String[] args = input.split(" ");

			 if(args[0].equals("print-counters-and-diagnostics")) {
			 System.out.println("printing counters and diagnostics");
			 //print the information about the number of messages send/received/relayed
			 message.printCounters();
			 }
			 else if(args[0].equals("exit-overlay")) {
//			 System.out.println("Exiting the overlay");
			 //allows node to exit the overlay

				try {
					message.deregister();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			 }
			 else {
			 continue;
			 }
		}

	}
}
