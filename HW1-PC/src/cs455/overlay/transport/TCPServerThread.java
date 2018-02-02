package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.wireformats.EventFactory;

public class TCPServerThread implements Runnable{
	int listeningPort = -1; 
	EventFactory m_eventFactory = null;
	private ServerSocket serverSocket = null;
	
	
	
	public TCPServerThread(int port, EventFactory eventFactory) {
		listeningPort = port;
		m_eventFactory = eventFactory;
	}

	@Override
	public void run() {
		
		while(true) {
			try {
				serverSocket = new ServerSocket(listeningPort);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
			Socket sock = null;
			try {
				sock = serverSocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Just connected to " + sock.getRemoteSocketAddress());
			System.out.println("Connected to port: " + sock.getPort());
			
			TCPSender send = null;
			try {
				send = new TCPSender(sock);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Thread s = new Thread(send);
			s.start();
			
			TCPReceiverThread tr = null;
			try {
				tr = new TCPReceiverThread(sock, m_eventFactory);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread t = new Thread(tr);
			t.start();
		}
		
		
	}

}
