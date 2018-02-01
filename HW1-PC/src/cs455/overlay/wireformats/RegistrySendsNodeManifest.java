package cs455.overlay.wireformats;

public class RegistrySendsNodeManifest implements Event{

	@Override
	public byte getType() {

		return 6;
	}

	@Override
	public byte[] getBytes() {

		return null;
	}

}
