package packets;

import net.GameClient;
import net.GameServer;

public class Packet00Login extends Packet{
	
	private String username;

	public Packet00Login(byte[] data) {
		super(00);
		this.username = readData(data);
		// TODO Auto-generated constructor stub
	}
	public Packet00Login(String username) {
		super(00);
		this.username = username;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
		
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
		
	}
	@Override
	public byte[] getData() {
		return ("00" + this.username).getBytes();
	
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	

}
