package cs455.overlay.node;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cs455.overlay.transport.TCPCache;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.CommandParser;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.OverlayNodeSendsData;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;
import cs455.overlay.wireformats.SendDeregistration;
import cs455.overlay.wireformats.SendRegistration;

import java.io.*;

public class MessagingNode extends Node{

	private static TCPServerThread clientSock = null;
	private static EventFactory eventFactory;
	private static int port = -1;
	private static int nodeID = -1;
	private static Socket client = null;
	private static Map<Integer, Tuple<byte[], Integer>> relayTable = new HashMap<Integer, Tuple<byte[], Integer>>();
	private int[] allNodes = null;
	private int[] routingTable = null;
	Thread t = null;
	TCPCache cache = null;
	private volatile int sendTracker = 0;
	private volatile int receiveTracker = 0;
	private volatile int packetsRelayed = 0;
	private volatile long sendSummation = 0;
	private volatile long receiveSummation = 0;
	
	private int prevSendTracker = 0;
	private int prevReceiveTracker = 0;
	private int prevPacketsRelayed = 0;
	private long prevSendSummation= 0;
	private long prevReceiveSummation= 0;
	TCPConnection connection = null;
	
	private void Initialize(String serverName, int port) throws IOException
	{
		this.eventFactory = new EventFactory(this);
		clientSock = new TCPServerThread(0);
		Thread t  = new Thread(clientSock);
		t.start();
		
		new Thread (() -> new CommandParser().messagingCommands(this)).start();

		sendRegMessage(serverName, port);
	}
	
	public static void main(String[] args) {
		String serverName = args[0];
		port = Integer.parseInt(args[1]);
		
		MessagingNode messageNode = new MessagingNode();
		try {
			messageNode.Initialize(serverName, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		clientSock.setServerInfo(serverName);
	}
	
	private void sendRegMessage(String serverName, int port) throws IOException {
		
		connection = new TCPConnection(new Socket(serverName, port));
		connection.initialize();

		//send the information to the SendRegistration class
		SendRegistration sendReg = new SendRegistration();
		sendReg.setPortInetAddress(clientSock.getPort(), InetAddress.getLocalHost().getAddress());
		
		//get the marshalled bytes from SendRegistration
		byte[] marshalled = sendReg.getBytes();
		connection.sendMessage(marshalled);	
	
	}

	public void OnEvent(Event e, TCPConnection connect) throws IOException {
//		System.out.println("In OnEvent Messaging");
		byte type = e.getType();
		
		switch(type) {
		case Protocol.RegistryReportsRegistrationStatus:
			RegistryReportsRegistrationStatus regStatus = (RegistryReportsRegistrationStatus) e;
			nodeID = regStatus.getStatus();
//			System.out.println("Node ID = " + nodeID);
//			System.out.println(Arrays.toString(regStatus.getInfoString()));
			break;
		case Protocol.RegistrySendsNodeManifest:
			RegistrySendsNodeManifest nodeManifest = (RegistrySendsNodeManifest) e;
			cache = new TCPCache();
			byte size = nodeManifest.getSize();
			connectRoutingTable(size, nodeManifest, connect);
//			System.out.println("routing table size is: " + size);
			break;
		case Protocol.RegistryRequestsTaskInitiate:
			RegistryRequestsTaskInitiate taskInit = (RegistryRequestsTaskInitiate) e;
			int packToSend = taskInit.getNumPacketsToSend();
			sendPackets(packToSend, connect);
			break;
		case Protocol.OverlayNodeSendsData:
			OverlayNodeSendsData sendsData = (OverlayNodeSendsData) e;
//			System.out.println("sink from unpack: " + sendsData.getDestination());
			relayMessage(sendsData.getDestination(), sendsData.getSource(), sendsData.getPayload(), sendsData.getNodesTraversed());
			break;
		case Protocol.RegistryRequestsTrafficSummary:
			//send the sent/receive information
			sendTrafficSummary(connect);
			break;
		case Protocol.RegistryReportsDeregistrationStatus:
//			RegistryReportsDeregistrationStatus rrd = (RegistryReportsDeregistrationStatus) e;
			System.out.println("Exiting Overlay");
			System.exit(0);
		}		
	}
	
	private void connectRoutingTable(byte size, RegistrySendsNodeManifest nodeManifest, TCPConnection connect) throws UnknownHostException, IOException {
		allNodes = nodeManifest.getListNodes();
		NodeReportsOverlaySetupStatus nodeOver = new NodeReportsOverlaySetupStatus();
		TCPConnection connTable;
		routingTable = new int[size];
//		System.out.println(routingTable.length);
		if (size == 4) {
			int node1 = nodeManifest.getNode1ID();
			byte[] node1Ip = nodeManifest.getIP1();
			String ipAd = convertIP(node1Ip);
			int port1 = nodeManifest.getP1();
			int node2 = nodeManifest.getNode2ID();
			byte[] node2Ip = nodeManifest.getIP2();
			int port2 = nodeManifest.getP2();
			int node3 = nodeManifest.getNode3ID();
			byte[] node3Ip = nodeManifest.getIP3();
			int port3 = nodeManifest.getP3();
			int node4 = nodeManifest.getNode4ID();
			byte[] node4Ip = nodeManifest.getIP4();
			int port4 = nodeManifest.getP4();

			
			routingTable[0] = node1;
			routingTable[1] = node2;
			routingTable[2] = node3;
			routingTable[3] = node4;
				
			relayTable.put(node1, new Tuple<byte[], Integer>(node1Ip, port1));
			relayTable.put(node2, new Tuple<byte[], Integer>(node2Ip, port2));
			relayTable.put(node3, new Tuple<byte[], Integer>(node3Ip, port3));
			relayTable.put(node4, new Tuple<byte[], Integer>(node4Ip, port4));
			
//			System.out.println(Arrays.toString(routingTable));
			
			//now connect to the nodes whose information was just received.
			for (int i = 0; i < routingTable.length; i++) {
//				System.out.println(relayTable.get(routingTable[i]).s1);
//				System.out.println(relayTable.get(routingTable[i]).s2);
				connTable = new TCPConnection(new Socket(convertIP(relayTable.get(routingTable[i]).s1), relayTable.get(routingTable[i]).s2));
				connTable.initialize();
				
				int success = routingTable[i];
				String info = "Node " + nodeID + " Connected successfully to " + success;
//				System.out.println(info);
				cache.addMap(routingTable[i], connTable);

				
				nodeOver.setInfo(success, (byte)info.length(), info.getBytes());
				connect.sendMessage(nodeOver.getBytes());
				
			}	
			
		}
		else if (size == 1) {
			int node1 = nodeManifest.getNode1ID();
			byte[] node1Ip = nodeManifest.getIP1();
			int port1 = nodeManifest.getP1();
		
			routingTable[0] = node1;
			
			relayTable.put(node1, new Tuple<byte[], Integer>(node1Ip, port1));
			
			for (int i = 0; i < routingTable.length; i++) {
				connTable = new TCPConnection(new Socket(convertIP(relayTable.get(routingTable[i]).s1), relayTable.get(routingTable[i]).s2));
				connTable.initialize();
				
				int success = routingTable[i];
				String info = "Node " + nodeID + " Connected successfully to " + success;
//				System.out.println(info);
				cache.addMap(routingTable[i], connTable);
				
				nodeOver.setInfo(success, (byte)info.length(), info.getBytes());
				connect.sendMessage(nodeOver.getBytes());
				
			}

		}
		else if (size == 2) {
			int node1 = nodeManifest.getNode1ID();
			byte[] node1Ip = nodeManifest.getIP1();
			int port1 = nodeManifest.getP1();

			int node2 = nodeManifest.getNode2ID();
			byte[] node2Ip = nodeManifest.getIP2();
			int port2 = nodeManifest.getP2();
			
			routingTable[0] = node1;
			routingTable[1] = node2;
			
			relayTable.put(node1, new Tuple<byte[], Integer>(node1Ip, port1));
			relayTable.put(node2, new Tuple<byte[], Integer>(node2Ip, port2));
			
			for (int i = 0; i < routingTable.length; i++) {
				connTable = new TCPConnection(new Socket(convertIP(relayTable.get(routingTable[i]).s1), relayTable.get(routingTable[i]).s2));
				connTable.initialize();
				
				int success = routingTable[i];
				String info = "Node " + nodeID + " Connected successfully to " + success;
//				System.out.println(info);
				cache.addMap(routingTable[i], connTable);
				
				nodeOver.setInfo(success, (byte)info.length(), info.getBytes());
				connect.sendMessage(nodeOver.getBytes());
				
			}
		}
		else {
			int node1 = nodeManifest.getNode1ID();
			byte[] node1Ip = nodeManifest.getIP1();
			int port1 = nodeManifest.getP1();
			
			int node2 = nodeManifest.getNode2ID();
			byte[] node2Ip = nodeManifest.getIP2();
			int port2 = nodeManifest.getP2();
			int node3 = nodeManifest.getNode3ID();
			byte[] node3Ip = nodeManifest.getIP3();
			int port3 = nodeManifest.getP3();
			
			routingTable[0] = node1;
			routingTable[1] = node2;
			routingTable[2] = node3;
			
			relayTable.put(node1, new Tuple<byte[], Integer>(node1Ip, port1));
			relayTable.put(node2, new Tuple<byte[], Integer>(node2Ip, port2));
			relayTable.put(node3, new Tuple<byte[], Integer>(node3Ip, port3));
			

			
			//connect to the other nodes
			for (int i = 0; i < routingTable.length; i++) {
//				System.out.println(relayTable.get(routingTable[i]).s1 + " " + relayTable.get(routingTable[i]).s2);
				connTable = new TCPConnection(new Socket(convertIP(relayTable.get(routingTable[i]).s1), relayTable.get(routingTable[i]).s2));
				connTable.initialize();
				
				int success = routingTable[i];
				String info = "Node " + nodeID + " Connected successfully to " + success;
//				System.out.println(info);
				cache.addMap(routingTable[i], connTable);
				
				nodeOver.setInfo(success, (byte)info.length(), info.getBytes());
				connect.sendMessage(nodeOver.getBytes());
				
			}				
		}
//		System.out.println("Routing table: " + Arrays.toString(routingTable));
	}
	
	private void sendPackets(int num, TCPConnection registryConnection) throws IOException {
//		System.out.println(Arrays.toString(allNodes));
//		System.out.println(Arrays.toString(routingTable));
//		System.out.println(nodeID);
		Random randomGenerator = new Random();
		Random packetGenerator = new Random();
		int rand = 0;
		int packet = 0;
		int sinkID = 0;
		int sendToID = 0;
		int sourceID = nodeID;
		boolean unique = false;
		OverlayNodeSendsData sendDataNode = new OverlayNodeSendsData();
		TCPConnection connect;
		for (int i = 0; i < num; i++) {
			while ((sinkID = allNodes[randomGenerator.nextInt(allNodes.length)]) == nodeID); 
//				rand = randomGenerator.nextInt(allNodes.length);
//				System.out.println("Random number generated: " + rand);
					packet = packetGenerator.nextInt();
//					System.out.println("source and destination id's" + sourceID + " "
//					+ sinkID );
			
			int sinkIndex = getIndex(sinkID);
			int sourceIndex = getIndex(sourceID);
			
//			System.out.println("SourceIndex: " + sourceIndex + " " + "SinkIndex: " + sinkIndex);
			
			sendToID = findDestination(sourceIndex, sinkIndex);
			
			int[] dissTrace = new int[1];
			dissTrace[0] = nodeID;
//			System.out.println("sending message: " + packet+ " sink is: " + sinkID);
			connect = cache.getById(sendToID);

			sendDataNode.setInfo(sinkID, sourceID, packet, dissTrace.length, dissTrace);
			connect.sendMessage(sendDataNode.getBytes());
			
			//synchronize the counters
			synchronized(this) {
				sendTracker++;
				sendSummation += packet;
			}
		}
//		System.out.println("SendTracker :" + sendTracker);
//		System.out.println(sendSummation);
//		
		//have thread sleep to allow rest of packets to come through pipeline
		try {
			t.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//send the message that node has completed sending its messages

		OverlayNodeReportsTaskFinished onrtf = new OverlayNodeReportsTaskFinished();
		onrtf.setInfo(InetAddress.getLocalHost().getAddress(), port, nodeID);
		registryConnection.sendMessage(onrtf.getBytes());
	}
	
	private int findDestination(int sourceIndex, int sinkIndex) {

		int sendToID = 0;
		int diff = sinkIndex - sourceIndex;
//		System.out.println("difference is: " + diff);
//		System.out.println("length of all Nodes array negated" + (-allNodes.length + 1));
		if (diff > 0) {
			if (diff == 1) {
//				System.out.println("sink is 1 hop away");
				sendToID = routingTable[0];
			}
			else if (diff == 2 || diff == 3) {
//				System.out.println("sink is 2 or 3 hops away");
				sendToID = routingTable[1];
			}
			else if (diff == 4 || (diff > 4 && diff < 8)) {
//				System.out.println("sink is 4 or more hops away");
				sendToID = routingTable[2];
			}
			else {
				sendToID = routingTable[3];
			}
		}
		else {
			//diff is negative
			if (diff == (-allNodes.length + 1)) {
//				System.out.println("need to wrap, sink is 1 hop away");
				sendToID = routingTable[0];
			}
			else if (diff == (-allNodes.length + 2) || diff == (-allNodes.length + 3)) {
//				System.out.println("need to wrap, sink is 2 or 3 hops away");
				sendToID = routingTable[1];
			}
			else if (diff == (-allNodes.length + 4) || (diff > (-allNodes.length + 4) && diff < (-allNodes.length + 8) )) {
//				System.out.println("need to wrap, sink is 4 or more hops away");
				sendToID = routingTable[2];
			}
			else {
				sendToID = routingTable[3];
			}
		}
		return sendToID;
	}
	
	//check to see if the message has reached its destination, relay message if not
	private void relayMessage(int sink, int source, int payload, int[] nodesTraversed) throws IOException {
//		System.out.println("Receiving packet from: " + source);
//		System.out.println(Arrays.toString(nodesTraversed));
//		System.out.println("My NodeID is: " + nodeID);
//		System.out.println(source);
//		System.out.println("payload: " + payload);
		if (nodeID == sink) {
//			System.out.println("you have arrived at your destination");
			
			//synchronize the counters
			synchronized(this) {
				receiveTracker++;
				receiveSummation += payload;
//				System.out.println("receiveSummation to date: " + receiveSummation);
			}

//			System.out.println("ReceiverTracker: " + receiveTracker);
//			System.out.println(receiveSummation);
		}

		else {
//			System.out.println("There is still work to be done, pass on the message");
			int length = nodesTraversed.length + 1;
			int[] addTraverse = new int[length];
			for (int i = 0; i < nodesTraversed.length; i++) {
				addTraverse[i] = nodesTraversed[i];
			}
			addTraverse[addTraverse.length - 1] = nodeID;
			//see where the message now needs to go
			int sendToID = findDestination(getIndex(nodeID), getIndex(sink));
//			System.out.println("sending message to: " + sendToID + " sink is: " + sink);
			OverlayNodeSendsData sendDataNode = new OverlayNodeSendsData();
			TCPConnection connect;
			
			sendDataNode.setInfo(sink, nodeID, payload, addTraverse.length, addTraverse);
			connect = cache.getById(sendToID);
			connect.sendMessage(sendDataNode.getBytes());
			synchronized(this) {
				packetsRelayed++;
			}
	
		}
	}
	
	//get the index of node, used for relaying packets
	private int getIndex(int nodeID) {
		int index = 0;
		for (int i = 0; i < allNodes.length; i++) {
			if (nodeID == allNodes[i]) {
				index = i;
			}
		}	
		return index;
	}
	
	//send the Traffic Summary back to Registry
	private void sendTrafficSummary(TCPConnection connect) throws IOException {
//		System.out.println("receiveSummation: " + this.receiveSummation);
		OverlayNodeReportsTrafficSummary onrts = new OverlayNodeReportsTrafficSummary();
		onrts.setInfo(nodeID, sendTracker, packetsRelayed, sendSummation, receiveTracker, receiveSummation);
		connect.sendMessage(onrts.getBytes());
		
		//reset all of the traffic counters
		synchronized(this) {
			prevSendTracker = sendTracker;
			prevPacketsRelayed = packetsRelayed;
			prevReceiveTracker = receiveTracker;
			prevSendSummation = sendSummation;
			prevReceiveSummation = receiveSummation;
			
			sendTracker = 0;
			packetsRelayed = 0;
			sendSummation = 0;
			receiveTracker = 0;
			receiveSummation = 0;
		}
	}
	
	public void printCounters() {
		System.out.println("Diagnostics for node: " + nodeID);
		System.out.println("Packets Sent | Packets Received | Packets Relayed | Sum Values Sent | Sum Values Received");
		System.out.println(prevSendTracker + " | " + prevReceiveTracker + " | " + prevPacketsRelayed
				+ " | " + prevSendSummation + " | " + prevReceiveSummation);
	}
	
	//deregister from the overlay
	public void deregister() throws IOException {
		SendDeregistration dereg = new SendDeregistration();
		dereg.setInfo(InetAddress.getLocalHost().getAddress(), port, nodeID);
		connection.sendMessage(dereg.getBytes());
	}
	
	//convert IP to bytes
	private String convertIP(byte[] ip) {
		String s = "";
		for(int i = 0; i < ip.length; i++) {
			s += ip[i] & 0xff;
			if(i != ip.length - 1) {
				s += ".";
			}
		}
//		System.out.println(s);
		return s;
	}

}