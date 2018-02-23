package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NodeReportsOverlaySetupStatus implements Event {
	
	private int successStatus;
	private byte[] infoString;

	@Override
	public byte getType() {
		return 7;
	}
	
	public int getStatus() {
		return successStatus;
	}
	
	public byte[] getInfoString() {
		return infoString;
	}
	
	public void setInfo(int successStat, byte length, byte[] infoStr) {
		successStatus = successStat;
		infoString = infoStr;
	}

	@Override
	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeByte(getType());
		dout.writeInt(successStatus);
		dout.writeByte(infoString.length);
		dout.write(infoString);
		
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
		successStatus = din.readInt();
		byte lengthRec = din.readByte();
		byte[] identifyerBytes = new byte[lengthRec];
		din.readFully(identifyerBytes);
		infoString = identifyerBytes;

		
		baInputStream.close();
		din.close();
	}

}
