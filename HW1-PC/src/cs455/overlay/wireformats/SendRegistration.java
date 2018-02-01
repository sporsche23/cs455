package cs455.overlay.wireformats;

import java.io.*;
import java.util.Arrays;

import cs455.overlay.wireformats.Event;

public class SendRegistration implements Event {
	
//	private byte length;
	private byte[] ipAddr;
	private int portNum;
	private byte typeRec;
	private byte[] ipAddrRec;
	private int portNumRec;
	
	@Override
	public byte getType() {
		return 2;
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		System.out.println(Arrays.toString(ipAddr));
		System.out.println("portNum = " + portNum);
		
		dout.writeByte(getType());
		dout.writeByte(ipAddr.length);
		dout.write(ipAddr);
		dout.writeInt(portNum);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	
	
	//place for the MessagingNode to send the port number and INetAddress
	public void setPortInetAddress(int port, byte[] iNet) {
		portNum = port;
		ipAddr = iNet;
	}
	
	//unpack the marshalled bytes
	public void unPackBytes(byte[] marshalledBytes) throws IOException {
		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		typeRec = din.readByte();
		byte lengthRec = din.readByte();
		byte[] identifyerBytes = new byte[lengthRec];
		din.readFully(identifyerBytes);
		ipAddrRec = identifyerBytes;
		portNumRec = din.readInt();
		
		baInputStream.close();
		din.close();
	}

}
