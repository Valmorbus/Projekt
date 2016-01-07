package packets;

import net.GameClient;
import net.GameServer;

public class Packet01Disconnect extends Packet{
	
	private String username;

	public Packet01Disconnect(byte[] data) {
		super(01);
		this.username = readData(data);
		// TODO Auto-generated constructor stub
	}
	public Packet01Disconnect(String username) {
		super(01);
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
		return ("01" + this.username).getBytes();
	
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	

}
