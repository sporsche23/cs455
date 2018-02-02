package cs455.overlay.node;
import java.net.*;

import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.SendRegistration;

import java.io.*;

public class MessagingNode implements Node{

	private EventFactory eventFactory;
	
	private void Initialize(int port) throws IOException
	{
		
		eventFactory = EventFactory.getInstance();
		eventFactory.receiveNode(this);
		
		TCPServerThread messaging = new TCPServerThread(port, eventFactory);
		Thread t = new Thread(messaging);
		t.start();
	}
	
//	public MessagingNode() {
//		eventFactory = new EventFactory.getInstance();
//	}
	
	//insert either methods  here with the port number and iNetAddress
	//so that the SendRegister class can access the values and retrieve the information
	
	public static void main(String[] args) {
		String serverName = args[0];
		
		int port = Integer.parseInt(args[1]);
		
		MessagingNode messageNode = new MessagingNode();
		try {
			messageNode.Initialize(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Connecting to " + serverName + " on port " + port);
			Socket client = new Socket(serverName, port);
			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			System.out.println("Connected to Port: " + client.getPort());
			System.out.println("INetAddress is: " + client.getInetAddress());
			
			//generate INetAddress as a byte[]
			InetAddress iNetAddr = client.getInetAddress();
			byte[] iNetAddress = iNetAddr.getAddress();
			System.out.println("INetAddress is: " + iNetAddress);
			
			//get the portNumber
			int port2 = client.getPort();
			System.out.println("Port Number is: " + port2);
			
			//send the information to the SendRegistration class
			SendRegistration sendReg = new SendRegistration();
			sendReg.setPortInetAddress(port2, iNetAddress);
			
			//get the marshalled bytes from SendRegistration
			byte[] marshalled = sendReg.getBytes();
			
			//send the SendRegister Message to TCPSender, which sends the message to the Registry?
			TCPSender tcpSend = new TCPSender(client);
			tcpSend.sendData(marshalled);
	
			
			//close client for now, however this will change in the future, based on exit message
//			client.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnEvent(Event e) {
		
		
	}

}
