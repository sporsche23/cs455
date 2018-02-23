package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummary  implements Event {

	private int nodeID;
	private int numPacketsSent;
	private int numPacketsRelayed;
	private long sumDataSent;
	private int numPacketsReceived;
	private long sumDataReceived;
	
	@Override
	public byte getType() {
		return 12;
	}
	
	public int getNodeID() {
		return nodeID;
	}
	
	public int getNumPacketsSent() {
		return numPacketsSent;
	}
	
	public int getNumPacketsRel() {
		return numPacketsRelayed;
	}
	
	public long getSumSent() {
		return sumDataSent;
	}
	
	public int getNumPacketsRec() {
		return numPacketsReceived;
	}
	
	public long getSumDataRec() {
		return sumDataReceived;
	}
	
	public void setInfo(int id, int packSent, int packRel, long sumSent, int packRec, long sumRec) {
		nodeID = id;
		numPacketsSent = packSent;
		numPacketsRelayed = packRel;
		sumDataSent = sumSent;
		numPacketsReceived = packRec;
		sumDataReceived = sumRec;
//		System.out.println("IN SEND SUMMARY, sumReceived: " + sumDataReceived);
	}

	@Override
	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeInt(nodeID);
		dout.writeInt(numPacketsSent);
		dout.writeInt(numPacketsRelayed);
		dout.writeLong(sumDataSent);
		dout.writeInt(numPacketsReceived);
		dout.writeLong(sumDataReceived);
		
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
		nodeID = din.readInt();
		numPacketsSent = din.readInt();
		numPacketsRelayed = din.readInt();
		sumDataSent = din.readLong();
		numPacketsReceived = din.readInt();
		sumDataReceived = din.readLong();
		
//		System.out.println("IN SEND SUMMARY, and unpacked, sumReceived: " + sumDataReceived);
		
		baInputStream.close();
		din.close();
	}

}
