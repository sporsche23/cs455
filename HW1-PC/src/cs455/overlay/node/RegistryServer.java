package cs455.overlay.node;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.*;

public class RegistryServer implements Node {
	
	
	private EventFactory eventFactory;
//	private int successStatus = 0;
	
//	public RegistryServer(int port) throws IOException
//	{
//		serverSocket = new ServerSocket(port);
//		eventFactory = EventFactory.getInstance();
//	}
	
	private void Initialize(int port) throws IOException
	{
		
		eventFactory = EventFactory.getInstance();
		eventFactory.receiveNode(this);
		
		TCPServerThread server = new TCPServerThread(port, eventFactory);
		Thread t = new Thread(server);
		t.start();
	}

	public static void main(String[] args) {
		
		int port = Integer.parseInt(args[0]);
		
		RegistryServer registryServer = new RegistryServer();
		try {
			registryServer.Initialize(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	//to be implemented
	@Override
	public void OnEvent(Event e) {
		
	}

}
