package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryReportsRegistrationStatus implements Event {

	private int successStatus;
	private byte[] informationString;
	
	@Override
	public byte getType() {
		return 3;
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
	
	public void setStatus(int succStatus, byte[] infoString) {
		successStatus = succStatus;
		informationString = infoString;
	}

}
