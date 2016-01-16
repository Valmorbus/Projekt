package packets;

import net.GameClient;
import net.GameServer;

/**
 * A Shoot Packet to enable players to update the server that it has fired a
 * shot, which will then be created by the server to all connected clients.
 * 
 * @author Simon Borgström
 * @version 1.0
 */
public class Packet03Shoot extends Packet {

	private double x, y, rotate;

	/**
	 * Constructor for Packet03Shoot. Creates a Shoot packet which creates a
	 * bullet forwarded by the server to connected clients with the x
	 * coordinate, the y coordinate and the rotation as a byte array for the
	 * bullet being created.
	 * 
	 * @param data
	 *            - byte array of data
	 */
	public Packet03Shoot(byte[] data) {
		super(03);
		String[] dataArray = readData(data).split(",");
		this.x = Double.parseDouble(dataArray[0]);
		this.y = Double.parseDouble(dataArray[1]);
		this.rotate = Double.parseDouble(dataArray[2]);

	}

	/**
	 * Constructor for Packet03Shoot. Creates a Shoot packet which creates a
	 * bullet forwarded by the server to connected clients with the x
	 * coordinate, the y coordinate and the rotation for the bullet being
	 * created.
	 * 
	 * @param x
	 *            - x coordinate
	 * @param y
	 *            - y coordinate
	 * @param rotate
	 *            - rotation
	 */
	public Packet03Shoot(double x, double y, double rotate) {
		super(03);
		this.x = x;
		this.y = y;
		this.rotate = rotate;
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
		return ("03" + this.x + "," + this.y + "," + this.rotate).getBytes();

	}

	/**
	 * Returns the x coordinate of the player login in
	 * 
	 * @return x double
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x coordinate of the player login in
	 * 
	 * @param x
	 *            - double
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Returns the y coordinate of the player login in
	 * 
	 * @return y double
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y coordinate of the player login in
	 * 
	 * @param y
	 *            - double
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Returns the rotation of the player login in
	 * 
	 * @return rotation double
	 */
	public double getRotate() {
		return rotate;
	}

	/**
	 * Sets the rotation of the player login in
	 * 
	 * @param rotate
	 *            - double
	 */
	public void setRotate(double rotate) {
		this.rotate = rotate;
	}

}
