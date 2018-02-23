package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryReportsDeregistrationStatus implements Event{
	

	private int successStatus = 1;
	byte[] informationString = "Deregistration successful".getBytes();
	


	@Override
	public byte getType() {
		return 5;
	}
	
	public int getStatus() {
		return successStatus;
	}
	
	public byte[] getInfoString() {
		return informationString;
	}

	@Override
	public byte[] getBytes() throws IOException{
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeInt(successStatus);
		dout.writeByte(informationString.length);
		dout.write(informationString);
		
		
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
		successStatus = din.readInt();
		byte lengthRec = din.readByte();
		byte[] identifyerBytes = new byte[lengthRec];
		din.readFully(identifyerBytes);
		informationString = identifyerBytes;
		
		baInputStream.close();
		din.close();
	}

}
