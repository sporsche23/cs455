package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendDeregistration implements Event {
	
	private byte[] ipAddr;
	private int portNum;
	private int nodeID;

	@Override
	public byte getType() {
		return 4;
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

}
