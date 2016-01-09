package packets;

import net.GameClient;
import net.GameServer;

public class Packet03Shoot extends Packet{

	private String username;
	private double x, y, rotate;

	public Packet03Shoot(byte[] data) {
		super(03);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x =Double.parseDouble(dataArray[1]);
		this.y =Double.parseDouble(dataArray[2]);;
		this.rotate = Double.parseDouble(dataArray[3]);;
		// TODO Auto-generated constructor stub
	}
	public Packet03Shoot(String username, double x, double y, double rotate) {
		super(03);
		this.username = username;
		this.x =x;
		this.y =y;
		this.rotate = rotate;
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
	//skickar argument. Lägg till i konstruktor för andra/fler argument
	@Override
	public byte[] getData() {
		return ("03" + this.username+","+this.x+","+this.y+","+this.rotate).getBytes();
	
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getRotate() {
		return rotate;
	}
	public void setRotate(double rotate) {
		this.rotate = rotate;
	}
	
	
	
	
	
	

}

