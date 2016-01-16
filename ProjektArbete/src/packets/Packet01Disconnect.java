package packets;

import net.GameClient;
import net.GameServer;

/**
 * A disconnect Packet to enable players to disconnect their player from the
 * server which forwards it to the connected clients.
 * 
 * @author Simon Borgström
 * @version 1.0
 */
public class Packet01Disconnect extends Packet {

	private String username;

	/**
	 * Constructor for Packet01Disconnect. Creates a disconnect packet with the
	 * username, as a byte array for the player that disconnects.
	 * 
	 * @param data
	 *            - byte array of data
	 */
	public Packet01Disconnect(byte[] data) {
		super(01);
		this.username = readData(data);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor for Packet01Disconnect. Creates a disconnect packet with the
	 * username, for the player that disconnects.
	 * 
	 * @param data
	 *            - byte array of data
	 */
	public Packet01Disconnect(String username) {
		super(01);
		this.username = username;
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
		/**
		 * {@inheritDoc}
		 */
	}

	@Override
	public byte[] getData() {
		return ("01" + this.username).getBytes();

	}

	/**
	 * Returns the username of the player
	 * 
	 * @return - Strng username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username of the player
	 * 
	 * @param username
	 *            - String
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}
