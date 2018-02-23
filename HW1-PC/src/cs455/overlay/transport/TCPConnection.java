package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.OverlayNodeSendsData;

public class TCPConnection {
	
	private Socket socket;
	private DataOutputStream dout;
	private DataInputStream din;
//	private LinkedBlockingQueue<OverlayNodeSendsData> sendQueue = new LinkedBlockingQueue<OverlayNodeSendsData>();
	
	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		dout = new DataOutputStream(socket.getOutputStream());
		din = new DataInputStream(socket.getInputStream());
	}
	
	public void receiveMessage() {
//		System.out.println("In receiveMessage");
		int dataLength;
		while (socket != null) {
			try {
				dataLength = din.readInt();
				
				byte[] data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
//				System.out.println("Data in receiveMessage" + Arrays.toString(data));
				EventFactory ef = EventFactory.getInstance();
				ef.processMessage(data, this);
//				System.out.println("finished processing");
//				System.out.println(Arrays.toString(data));
//				RegistryServer.sendMessage();
			}
			catch (SocketException se) {
				System.out.println(se.getMessage());
				break;
			}
			catch (IOException e) {
				System.out.println(e.getMessage());
				break;
			}
		}
		System.out.println("socket is closed");
	}
	
	public synchronized void sendMessage(byte[] dataToSend) throws IOException {
		int dataLength = dataToSend.length;
		dout.writeInt(dataLength);
		dout.write(dataToSend, 0, dataLength);
		dout.flush();
	}
//	
//	public int getPort() {
//		return this.socket.getPort();
//	}
	
//	public void addPacket(OverlayNodeSendsData packet) {
//		sendQueue.add(packet);
//	}
//	
//	public void sendPacket() {
//		//check if queue is null
//		if (sendQueue.poll() == null) {
//			System.out.println("queue is empty");
//		}
//		else {
//			//grab the first item in the queue and send it
//			sendQueue.poll();
//		}
//	}
	
	public synchronized void initialize() {
		Thread receiver = new Thread(new Runnable() {
			@Override
			public void run() {
				receiveMessage();
			}
		});
		receiver.start();
	}

}
