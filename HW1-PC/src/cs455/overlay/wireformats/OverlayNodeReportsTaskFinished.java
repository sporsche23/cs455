package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class OverlayNodeReportsTaskFinished implements Event {

	private byte len;
	private byte[] ipAddress;
	private int portNum;
	private int nodeID;
	
	@Override
	public byte getType() {
		return 10;
	}
	
	public int getID() {
		return nodeID;
	}
	
	public void setInfo(byte[] ipAddr, int port, int id) {
		len = (byte) ipAddr.length;
		ipAddress = ipAddr;
		portNum = port;
		nodeID = id;
	}

	@Override
	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(ipAddress.length);
		dout.write(ipAddress);
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
		
		byte type = din.readByte();
		len = din.readByte();
		byte[] identifyerBytes = new byte[len];
		din.readFully(identifyerBytes);
		ipAddress = identifyerBytes;
		portNum = din.readInt();
		nodeID = din.readInt();
		
		baInputStream.close();
		din.close();
	}

}
