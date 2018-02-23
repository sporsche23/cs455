package cs455.overlay.routing;

public class Route {
	
	byte[] ip;
	int port;
	int id;
	
	public Route(int id, byte[] ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getID() {
		return id;
	}
	
	public byte[] getIP() {
		return ip;
	}
}
