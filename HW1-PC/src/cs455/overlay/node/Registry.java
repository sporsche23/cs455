package cs455.overlay.node;
import cs455.overlay.routing.Route;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPCache;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.CommandParser;
import cs455.overlay.util.DisplayStatistics;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;
import cs455.overlay.wireformats.SendDeregistration;
import cs455.overlay.wireformats.SendRegistration;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class Registry extends Node {
	
	
	private EventFactory eventFactory;
	private static byte[] originalAddress = null;
	private static int[] nodeIds = new int[128];
	private static Map<Integer, Tuple<byte[], Integer>> nodesRegistered = new HashMap<Integer, Tuple<byte[], Integer>>();
	private static TCPServerThread server = null;
	private static byte[] regInfoString = null;
	ArrayList<RoutingTable> routeTables = null;
	int[] allMessNodes = null;
	Integer completionCounter = 0;
	TCPCache cache = null;
	DisplayStatistics displayStats = new DisplayStatistics();
	
	private void Initialize(int port) throws IOException
	{	
		
		new Thread (() -> new CommandParser().registryCommands(this)).start();
//		new Thread (() -> new CommandParser().registryCommands(this)).start();
		eventFactory = new EventFactory(this);
		server = new TCPServerThread(port);
		this.routeTables = new ArrayList<RoutingTable>();
		this.cache = new TCPCache();
		Thread t = new Thread(server);
		t.start();

	}

	public static void main(String[] args) {
		
		int port = Integer.parseInt(args[0]);
		
		Registry registryServer = new Registry();
		try {
			registryServer.Initialize(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnEvent(Event e, TCPConnection connect) throws IOException {
//		System.out.println("In OnEvent for RegistryServer");
		
		byte type = e.getType();
		
		switch (type) {
		case Protocol.SendRegistration:
			SendRegistration sr = (SendRegistration) e;
			int iD = registerNode(sr.getType(), sr.getIpAddr(), sr.getPortNumber());
//			System.out.println("adding id and connection to cache" + connect);
			cache.addMap(iD, connect);
			RegistryReportsRegistrationStatus regStatus = new RegistryReportsRegistrationStatus();
			String message = "Registration request successful. The number of messaging nodes currently registered"
					+ "is (" + nodesRegistered.size() + ")";
			regStatus.setStatus(iD, (byte) message.getBytes().length, message.getBytes());
			connect.sendMessage(regStatus.getBytes());
			break;
		case Protocol.NodeReportsOverlaySetupStatus:
			NodeReportsOverlaySetupStatus nR = (NodeReportsOverlaySetupStatus) e;
			int stat = nR.getStatus();
			byte[] info = nR.getInfoString();
			String s = new String(info);
//			System.out.println(s + stat);
			break;
		case Protocol.OverlayNodeReportsTaskFinished:
			OverlayNodeReportsTaskFinished taskFinished = (OverlayNodeReportsTaskFinished) e;
			checkCompletion(taskFinished.getID());
			break;
		case Protocol.OverlayNodeReportsTrafficSummary:
			OverlayNodeReportsTrafficSummary trafficSummary = (OverlayNodeReportsTrafficSummary) e;
//			System.out.println("Node ID: " + trafficSummary.getNodeID() + " packetsSent: " + trafficSummary.getNumPacketsSent() +
//					" PacketsReceived: " + trafficSummary.getNumPacketsRec() + " PacketsRelayed: " + trafficSummary.getNumPacketsRel() +
//					" sumSent: " + trafficSummary.getSumSent() + "sumReceived: " + trafficSummary.getSumDataRec());
			synchronized(displayStats) {
//				System.out.println(trafficSummary.getSumSent() + " | " + trafficSummary.getSumDataRec());
				displayStats.display(allMessNodes.length, trafficSummary.getNodeID(), trafficSummary.getNumPacketsSent(),
						trafficSummary.getNumPacketsRec(), trafficSummary.getNumPacketsRel(),
						trafficSummary.getSumSent(), trafficSummary.getSumDataRec());
			}
			break;
		case Protocol.SendDeregistration:
			SendDeregistration dereg = (SendDeregistration) e;
			RegistryReportsDeregistrationStatus rrds = new RegistryReportsDeregistrationStatus();
//			System.out.println("saldjflskdf" + this.cache.getById(dereg.getNodeId()));
			this.cache.getById(dereg.getNodeId()).sendMessage(rrds.getBytes());
			this.cache.remove(dereg.getNodeId());
			this.nodesRegistered.remove(dereg.getNodeId());
			break;
		}

	}
	
	public static int registerNode(byte type, byte[] address, int port) {
		byte sentType = type;
		byte[] sentAddress = address;
		int sentPort = port;
		boolean unique = false;
		int rand = 0;
		
//		System.out.println("sent address is: " + sentAddress);
//		System.out.println("original ip Address is: " + originalAddress);
//		System.out.println("port is: " + sentPort);
		
		//new node is trying to register, check the IPAddress given vs the IP address from the socket
		//if match, generate random number between 0 and 127, and store the info in (hashmap?)
//		System.out.println("they are the same");

		Random randomGenerator = new Random();
		while (!unique) {
			rand = randomGenerator.nextInt(128);
//			System.out.println("Random number generated: " + rand);
			if (nodeIds[rand] != rand) {
				// this number hasn't been generated yet, add number to array
				nodeIds[rand] = rand;
				unique = true;
			} else {
//				System.out.println("random number has already been generated, need a new number");
			}
		}
		// generate an entry in hashmap?
		nodesRegistered.put(rand, new Tuple<byte[], Integer>(sentAddress, port));
//		System.out.println(nodesRegistered.size());
		for (Entry<Integer, Tuple<byte[], Integer>> key : nodesRegistered.entrySet()) {
			Integer k = key.getKey();
			Tuple<byte[], Integer> value = key.getValue();
			byte[] ip = value.s1;
			int p = value.s2;
//			System.out.println(k + " " + ip + " " + p);
		}
		
		return rand;

	}

	public static void deregisterNode(byte typeRec, byte[] ipAddrRec, int portNumRec, int nodeIDRec) {
		//remove the entry in the hashmap that corresponds to nodeIDRec
	}
	
	//set up the overlay and send each node its routing table
	public void setupOverlay(byte size) throws IOException {
		
		//check that there are 10 more nodes in the hashmap
		if(nodesRegistered.size() > 128) {
			System.out.println("The overlay can only have at most 128 nodes, please remove some");
		}
//		else if (nodesRegistered.size() >= 10) {
			//put all nodeID's into an integer array
			allMessNodes = new int[nodesRegistered.size()];
			int count = 0;
			Tuple<byte[], Integer> tup1;
			Tuple<byte[], Integer> tup2;
			Tuple<byte[], Integer> tup3;
			Tuple<byte[], Integer> tup4;
			
			for (Entry<Integer, Tuple<byte[], Integer>> key : nodesRegistered.entrySet()) {
				Integer k = key.getKey();
				allMessNodes[count] = k;
				count++;
//				System.out.println(Arrays.toString(allMessNodes));
			}
			
			for (Integer i = 0; i < allMessNodes.length; i++) {
//				System.out.println(allMessNodes.length);
				int hop1 = (i + 1) % allMessNodes.length;
				int hop2 = (i + 2) % allMessNodes.length;
				int hop4 = (i + 4) % allMessNodes.length;
				int hop8 = (i + 8) % allMessNodes.length;
				
//				Tuple<byte[], Integer> value = allMessNodes[i+1].getValue();
				tup1 = nodesRegistered.get(allMessNodes[hop1]);
				tup2 = nodesRegistered.get(allMessNodes[hop2]);
				tup3 = nodesRegistered.get(allMessNodes[hop4]);
				tup4 = nodesRegistered.get(allMessNodes[hop8]);
				
//				System.out.println(tup1.s1 + " " + tup1.s2);
//				System.out.println(tup1 + ", " + tup2 + ", " + tup3 + ", " + tup4);
				
//				System.out.println(cache.getById(allMessNodes[i]));
				TCPConnection connect = cache.getById(allMessNodes[i]);
//				for (int j = 0; j < allMessNodes.length; j++) {
//					TCPConnection c = cache.getById(allMessNodes[j]);
////					System.out.println(c);
//				}
				RegistrySendsNodeManifest nodeMan = new RegistrySendsNodeManifest();
				nodeMan.setSizeList(size, (byte)allMessNodes.length, allMessNodes);
				RoutingTable temp = new RoutingTable();
				
				if (size == 1) {
					temp.addRoute(new Route(allMessNodes[hop1], tup1.s1, tup1.s2));
//					System.out.println(allMessNodes[hop1] + " " + tup1.s1 + " " + tup1.s2);
					nodeMan.setNode1(allMessNodes[hop1], tup1.s1, tup1.s2);
//					System.out.println("sending overlay message");
					connect.sendMessage(nodeMan.getBytes1());
				}
				else if (size == 2) {
					temp.addRoute(new Route(allMessNodes[hop1], tup1.s1, tup1.s2));
					temp.addRoute(new Route(allMessNodes[hop2], tup2.s1, tup2.s2));
//					System.out.println(allMessNodes[hop1] + " " + tup1.s1 + " " + tup1.s2);
					nodeMan.setNode2(allMessNodes[hop1], tup1.s1, tup1.s2,
							allMessNodes[hop2], tup2.s1, tup2.s2);
//					System.out.println("sending overlay message");
					connect.sendMessage(nodeMan.getBytes2());
				}
				else if(size == 4) {
//					System.out.println(Arrays.toString(allMessNodes));
					temp.addRoute(new Route(allMessNodes[hop1], tup1.s1, tup1.s2));
					temp.addRoute(new Route(allMessNodes[hop2], tup2.s1, tup2.s2));
					temp.addRoute(new Route(allMessNodes[hop4], tup3.s1, tup3.s2));
					temp.addRoute(new Route(allMessNodes[hop8], tup4.s1, tup4.s2));
					nodeMan.setNode3(allMessNodes[hop1], tup1.s1, tup1.s2,
							allMessNodes[hop2], tup2.s1, tup2.s2, 
							allMessNodes[hop4], tup3.s1, tup3.s2);
					nodeMan.setNode4(allMessNodes[hop8], tup4.s1, tup4.s2);
//					System.out.println("sending overlay message");
					connect.sendMessage(nodeMan.get4Bytes());
				}
				else {
					temp.addRoute(new Route(allMessNodes[hop1], tup1.s1, tup1.s2));
					temp.addRoute(new Route(allMessNodes[hop2], tup2.s1, tup2.s2));
					temp.addRoute(new Route(allMessNodes[hop4], tup3.s1, tup3.s2));
//					System.out.println(allMessNodes[hop1] + " " + tup1.s1 + " " + tup1.s2);
					nodeMan.setNode3(allMessNodes[hop1], tup1.s1, tup1.s2,
							allMessNodes[hop2], tup2.s1, tup2.s2, 
							allMessNodes[hop4], tup3.s1, tup3.s2);
//					System.out.println("sending overlay message");
					connect.sendMessage(nodeMan.getBytes());
				}
				this.routeTables.add(temp);

			}
//		}
//		else {
//			System.out.println("There must be at least 10 nodes to setup the overlay");
//		}
	}
	
	public void sendPackets(int numPackets) throws IOException {
		
		synchronized(completionCounter) {
			completionCounter = 0;
			synchronized(this.displayStats) {
				this.displayStats.resetCount();
			}
		}
		
		for (Integer i = 0; i < allMessNodes.length; i++) {
			TCPConnection connect = cache.getById(allMessNodes[i]);
			RegistryRequestsTaskInitiate initiateTask = new RegistryRequestsTaskInitiate();
			initiateTask.setInfo(numPackets);
			connect.sendMessage(initiateTask.getBytes());
		}
	}
	
	private void checkCompletion(int nodeID) throws IOException {
		//check that the nodeID equals an ID in the int array, if so, increment the counter
//		System.out.println(nodeID + " has send all of its messages");
		synchronized(completionCounter) {
			completionCounter++;
			
			//check to see if all nodes have completed
			if (completionCounter == allMessNodes.length) {
				//send the traffic summary request to all registered nodes
				RegistryRequestsTrafficSummary rrts = new RegistryRequestsTrafficSummary();
				for (int i = 0; i < allMessNodes.length; i++) {
					TCPConnection connect = cache.getById(allMessNodes[i]);
					connect.sendMessage(rrts.getBytes());
				}
			}
			else {
//				System.out.println("Still waiting on nodes to finish sending their packets");
			}
		}

//		System.out.println(completionCounter);
		
	}
	
	public void printRoutingTables() {
		for (RoutingTable r: routeTables) {
			System.out.println(r);
		}
		
	}
	
	public void printListOfNodes() {
		for (Entry<Integer, Tuple<byte[], Integer>> key : nodesRegistered.entrySet()) {
			Integer k = key.getKey();
			Tuple<byte[], Integer> value = key.getValue();
			byte[] ip = value.s1;
			int p = value.s2;
			System.out.println(k + " " + ip + " " + p);
		}
	}
}
