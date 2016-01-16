package packets;

import net.GameClient;
import net.GameServer;
/**
 * A move Packet to enable players to update the position of their player to the server which
 * forwards it to the connected clients.
 * 
 * @author Simon Borgström
 * @version 1.0
 */
public class Packet02Move extends Packet{

	private String username;
	private double x, y, rotate;
	/**
	 * Constructor for Packet02Move. Creates a move packet with the username of the player to be moved, its
	 * x coordinate, its y coordinate and its rotation as a byte array for the player that logs in.
	 * 
	 * @param data
	 *            - byte array of data
	 */
	public Packet02Move(byte[] data) {
		super(02);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.x =Double.parseDouble(dataArray[1]);
		this.y =Double.parseDouble(dataArray[2]);;
		this.rotate = Double.parseDouble(dataArray[3]);;
		// TODO Auto-generated constructor stub
	}
	/**
	 * Constructor for Packet02Move. Creates a move packet with the username of the player to be moved, its
	 * x coordinate, its y coordinate and its rotation for the player that logs in.
	 * 
	 * @param username - String 
	 * @param x - x coordinate
	 * @param y - y coordinate 
	 * @param rotate - rotation
	 */
	public Packet02Move(String username, double x, double y, double rotate) {
		super(02);
		this.username = username;
		this.x =x;
		this.y =y;
		this.rotate = rotate;
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
		
	}
	/**
	 * {@inheritDoc}
	 */
		
	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
		
	}
	/**
	 * {@inheritDoc}
	 */
		
	@Override
	public byte[] getData() {
		return ("02" + this.username+","+this.x+","+this.y+","+this.rotate).getBytes();
	
	}
	/**
	 * Returns the username of the player
	 * @return - Strng username
	 */
		public String getUsername() {
			return username;
		}
	/**
	 * Sets the username of the player
	 * @param username - String
	 */
		public void setUsername(String username) {
			this.username = username;
		}
	/**
	 * Returns the x coordinate of the player login in
	 * @return x double
	 */
		public double getX() {
			return x;
		}
	/**
	 * Sets the x coordinate of the player login in
	 * @param x - double
	 */
		public void setX(double x) {
			this.x = x;
		}
		/**
		 * Returns the y coordinate of the player login in
		 * @return y double
		 */
		public double getY() {
			return y;
		}
		/**
		 * Sets the y coordinate of the player login in
		 * @param y - double
		 */
		public void setY(double y) {
			this.y = y;
		}
		/**
		 * Returns the rotation of the player login in
		 * @return rotation double
		 */
		public double getRotate() {
			return rotate;
		}
		/**
		 * Sets the rotation of the player login in
		 * @param rotate - double
		 */
		public void setRotate(double rotate) {
			this.rotate = rotate;
		}


}

