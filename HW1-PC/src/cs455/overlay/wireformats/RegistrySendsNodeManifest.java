package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RegistrySendsNodeManifest implements Event{
	
	private int type;
	private int[] listOfNodes;
	private byte numMessNodes;
	private byte routTableSize;
	private int node1;
	private int node2;
	private int node3;
	private int node4;
	private byte[] node1Addr;
	private byte[] node2Addr;
	private byte[] node3Addr;
	private byte[] node4Addr;
	private int port1;
	private int port2;
	private int port3;
	private int port4;

	@Override
	public byte getType() {
		return 6;
	}
	
	public byte getSize() {
		return routTableSize;
	}
	
	public int getNode1ID() {
		return node1;
	}
	
	public int getNode2ID() {
		return node2;
	}
	
	public int getNode3ID() {
		return node3;
	}
	
	public int getNode4ID() {
		return node4;
	}
	
	public int[] getListNodes() {
		return listOfNodes;
	}
	
	public byte[] getIP1() {
		return node1Addr;
	}
	
	public byte[] getIP2() {
		return node2Addr;
	}
	
	public byte[] getIP3() {
		return node3Addr;
	}
	
	public byte[] getIP4() {
		return node4Addr;
	}
	
	public int getP1() {
		return port1;
	}
	
	public int getP2() {
		return port2;
	}
	
	public int getP3() {
		return port3;
	}
	
	public int getP4() {
		return port4;
	}
	
	public void setNode1(int n1, byte[] n1a, int p1) {
		
		node1 = n1;
		node1Addr = n1a;
		port1 = p1;
	}
	
	public void setNode2(int n1, byte[] n1a, int p1, int n2, byte[] n2a, int p2) {
		
		node1 = n1;
		node1Addr = n1a;
		port1 = p1;
		node2 = n2;
		node2Addr = n2a;
		port2 = p2;
	}
	
	public void setNode3(int n1, byte[] n1a, int p1, int n2, byte[] n2a, int p2, 
			int n3, byte[]n3a, int p3) {
		
		node1 = n1;
		node1Addr = n1a;
		port1 = p1;
		node2 = n2;
		node2Addr = n2a;
		port2 = p2;
		node3 = n3;
		node3Addr = n3a;
		port3 = p3;

	}
	
	public void setNode4(int n4, byte[] n4a, int p4) {
		
		node4 = n4;
		node4Addr = n4a;
		port4 = p4;
	}
	
	public void setSizeList(byte tableSize, byte numNodes, int[] listNodes) {
		routTableSize = tableSize;
		numMessNodes = numNodes;
		listOfNodes = listNodes;
		
	}
	
	public byte[] getBytes1() throws IOException {
		
//		System.out.println("packing bytes " + Arrays.toString(listOfNodes));
//		System.out.println("NumMessNodes " + numMessNodes);
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(routTableSize);
		dout.writeInt(node1);
		dout.writeByte(node1Addr.length);
		dout.write(node1Addr);
		dout.writeInt(port1);

		dout.writeByte(numMessNodes);
		for (int i = 0; i < numMessNodes; i++) {
			dout.writeInt(listOfNodes[i]);
		}
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	
	public byte[] getBytes2() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(routTableSize);
		dout.writeInt(node1);
		dout.writeByte(node1Addr.length);
		dout.write(node1Addr);
		dout.writeInt(port1);
		dout.writeInt(node2);
		dout.writeByte(node2Addr.length);
		dout.write(node2Addr);
		dout.writeInt(port2);

		dout.writeByte(numMessNodes);
		for (int i = 0; i < numMessNodes; i++) {
			dout.writeInt(listOfNodes[i]);
		}
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(routTableSize);
		dout.writeInt(node1);
		dout.writeByte(node1Addr.length);
		dout.write(node1Addr);
		dout.writeInt(port1);
		dout.writeInt(node2);
		dout.writeByte(node2Addr.length);
		dout.write(node2Addr);
		dout.writeInt(port2);
		dout.writeInt(node3);
		dout.writeByte(node3Addr.length);
		dout.write(node3Addr);
		dout.writeInt(port3);
		dout.writeByte(numMessNodes);
		for (int i = 0; i < numMessNodes; i++) {
			dout.writeInt(listOfNodes[i]);
		}
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	
	public byte[] get4Bytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeByte(routTableSize);
		dout.writeInt(node1);
		dout.writeByte(node1Addr.length);
		dout.write(node1Addr);
		dout.writeInt(port1);
		dout.writeInt(node2);
		dout.writeByte(node2Addr.length);
		dout.write(node2Addr);
		dout.writeInt(port2);
		dout.writeInt(node3);
		dout.writeByte(node3Addr.length);
		dout.write(node3Addr);
		dout.writeInt(port3);
		dout.writeInt(node4);
		dout.writeByte(node4Addr.length);
		dout.write(node4Addr);
		dout.writeInt(port4);
		dout.writeByte(numMessNodes);
		for (int i = 0; i < numMessNodes; i++) {
			dout.writeInt(listOfNodes[i]);
		}
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public void unPackBytes(byte[] marshalledBytes) throws IOException {
		
//		System.out.println("unpacking bytes in RegistrySendsNodeManifest");

		
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readByte();
		routTableSize = din.readByte();
//		System.out.println(routTableSize);
		node1 = din.readInt();
		byte addr1 = din.readByte();
		byte[] nodeAddr1 = new byte[addr1];
		din.readFully(nodeAddr1);
		node1Addr = nodeAddr1;
		port1 = din.readInt();
		
		if (routTableSize >= 2) {
			node2 = din.readInt();
			byte addr2 = din.readByte();
			byte[] nodeAddr2 = new byte[addr2];
			din.readFully(nodeAddr2);
			node2Addr = nodeAddr2;
			port2 = din.readInt();
		}
		if (routTableSize >= 3) {
			
			node3 = din.readInt();
			byte addr3 = din.readByte();
			byte[] nodeAddr3 = new byte[addr3];
			din.readFully(nodeAddr3);
			node3Addr = nodeAddr3;
			port3 = din.readInt();
		}
		if (routTableSize == 4) {
			node4 = din.readInt();
//			System.out.println(node4);
			byte addr4 = din.readByte();
			byte[] nodeAddr4 = new byte[addr4];
			din.readFully(nodeAddr4);
			node4Addr = nodeAddr4;
			port4 = din.readInt();
		}


		numMessNodes = din.readByte();
		
//		System.out.println("NumMessNodes " + numMessNodes);
		
		int[] list = new int[numMessNodes];
		for (int i = 0; i < numMessNodes; i++) {
			list[i] = din.readInt();
		}
		listOfNodes = list;
//		System.out.println("list of nodes " + Arrays.toString(listOfNodes));
		
		baInputStream.close();
		din.close();
	}

}
