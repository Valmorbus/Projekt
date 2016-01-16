package packets;

import net.GameClient;
import net.GameServer;

/**
 * A Hit Packet to enable players to update the server that it has hit another
 * player and the amount of damage said player will take, which will then be
 * created by the server to all connected clients.
 * 
 * @author Simon Borgström
 * @version 1.0
 */
public class Packet04Hit extends Packet {
	private String username;
	private int damage;

	/**
	 * Constructor for Packet04Hit. Creates a Hit packet that updates the server
	 * on the amount of damage a player takes, forwarding it to connected
	 * clients.
	 * 
	 * @param data
	 *            - byte array of data.
	 */
	public Packet04Hit(byte[] data) {
		super(04);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		this.damage = Integer.parseInt(dataArray[1]);
	}

	/**
	 * Constructor for Packet04Hit. Creates a Hit packet that updates the server
	 * on the amount of damage a player takes, forwarding it to connected
	 * clients.
	 * 
	 * @param username
	 *            - The player that has been hit.
	 * @param damage
	 *            - The amount of damage the player has taken.
	 */
	public Packet04Hit(String username, int damage) {
		super(04);
		this.username = username;
		this.damage = damage;
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
		return ("04" + this.username + "," + this.damage).getBytes();

	}

	/**
	 * Returns the username of the player that has been damaged
	 * 
	 * @return String username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the player that has been damaged
	 * 
	 * @param username
	 *            - String
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the amount of damage the player that has been hit takes
	 * 
	 * @return int damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * Sets the amount of damage the player that has been hit takes
	 * 
	 * @param damage
	 *            - integer
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

}