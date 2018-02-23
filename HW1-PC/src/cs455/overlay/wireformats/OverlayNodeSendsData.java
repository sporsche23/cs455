package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsData implements Event {

	private int destination;
	private int source;
	private int payload;
	private int numHops;
	private int[] nodeIDsTraversed;
	
	@Override
	public byte getType() {
		return 9;
	}
	
	public int getDestination() {
		return destination;
	}
	
	public int getSource() {
		return source;
	}
	
	public int getPayload() {
		return payload;
	}
	
	public int getNumHops() {
		return numHops;
	}
	
	public int[] getNodesTraversed() {
		return nodeIDsTraversed;
	}
	
	public void setInfo(int dest, int sc, int num, int hops, int[] nodeIDs) {
		destination = dest;
		source = sc;
		payload = num;
		numHops = hops;
		nodeIDsTraversed = nodeIDs;
	}

	@Override
	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeInt(destination);
		dout.writeInt(source);
		dout.writeInt(payload);
		dout.writeInt(numHops);
		for (int i = 0; i < numHops; i++) {
			dout.writeInt(nodeIDsTraversed[i]);
		}
		
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
		
		int type = din.readByte();
		destination = din.readInt();
		source = din.readInt();
		payload = din.readInt();
		numHops = din.readInt();
		int[] hops = new int[numHops];
		for (int i = 0; i < numHops; i++) {
			hops[i] = din.readInt();
		}
		nodeIDsTraversed = hops;

		baInputStream.close();
		din.close();
	}

}
