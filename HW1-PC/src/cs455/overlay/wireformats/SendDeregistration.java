package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendDeregistration implements Event {
	
	private byte[] ipAddr;
	private int portNum;
	private int nodeID;
	private byte typeRec;

	@Override
	public byte getType() {
		return 4;
	}
	
	public byte[] getIPAddress() {
		return ipAddr;
	}
	
	public int getPort() {
		return portNum;
	}
	
	public int getNodeId() {
		return nodeID;
	}
	
	public void setInfo(byte[] ip, int port, int id) {
		ipAddr = ip;
		portNum = port;
		nodeID = id;
		
//		System.out.println(nodeID);
	}

	@Override
	public byte[] getBytes() throws IOException{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(ipAddr.length);
		dout.write(ipAddr);
		dout.writeInt(portNum);
		dout.writeInt(nodeID);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void unPackBytes(byte[] marshalledBytes) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		typeRec = din.readByte();
		byte lengthRec = din.readByte();
		byte[] identifyerBytes = new byte[lengthRec];
		din.readFully(identifyerBytes);
		ipAddr = identifyerBytes;
		portNum = din.readInt();
		nodeID = din.readInt();
		
//		System.out.println(nodeID);
		
		baInputStream.close();
		din.close();
	}

}
