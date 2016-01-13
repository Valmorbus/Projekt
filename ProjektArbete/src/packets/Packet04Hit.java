package packets;

import net.GameClient;
import net.GameServer;

public class Packet04Hit extends Packet{
	private String username;
	private int damage;

	public Packet04Hit(byte[] data) {
		super(04);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.damage =Integer.parseInt(dataArray[1]);	
	}
	
	public Packet04Hit(String username, int damage) {
		super(04);
		this.username = username;
		this.damage =damage;
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
		return ("04" + this.username+","+this.damage).getBytes();
	
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getDamage() {
		return damage;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}

	
	
	
	
	
	

}