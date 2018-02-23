package cs455.overlay.node;

import java.io.IOException;

import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.Event;

public abstract class Node {
	
	public abstract void OnEvent(Event e, TCPConnection connect) throws IOException;

}
