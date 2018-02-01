package cs455.overlay.node;
import java.net.*;

import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.*;

public class RegistryServer extends Thread implements Node, Runnable {
	
	private ServerSocket serverSocket = null;
	private EventFactory eventFactory = null;
//	private int successStatus = 0;
	
	public RegistryServer(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
		eventFactory = new EventFactory().makeEventFactory(this);
	}
	
	public void run() {
		while(true) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				System.out.println("Connected to port: " + server.getPort());
				
				//all below will be removed for the actual messages
				DataInputStream in = new DataInputStream(server.getInputStream());
//				DataInputStream in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
				System.out.println(in.readUTF());
				
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
//				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
				out.writeUTF("Thank you for connecting to " + server.getLocalSocketAddress());
				

			}
			catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		
		int port = Integer.parseInt(args[0]);
		try {
			Thread t = new RegistryServer(port);
			t.start();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	//to be implemented
	@Override
	public void OnEvent(Event e) {
		
	}

}
