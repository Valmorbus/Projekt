package packets;

import net.GameClient;
import net.GameServer;

/**
 * An abstract Packet class to be extended into data-packets to be interpreted
 * by client and server.
 * 
 * @author Simon Borgström
 * @version 1.0
 */

public abstract class Packet {
	/**
	 * Types of Packet available are (-1) for INVALID, (00) for LOGIN, (01) for
	 * DISCONNECT, (02) for MOVE, (03)for SHOOT and (04) for HIT.(
	 * 
	 * @author Simons
	 *
	 */
	public static enum PacketTypes {
		INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02), SHOOT(03), HIT(04);

		private int packetId;

		/**
		 * PacketTypes is used to look up what type of packet is send to
		 * server/client Used in a switch statement to send the corresponding
		 * packetId to the correct methods.
		 * 
		 * @param packetId
		 */
		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}

		/**
		 * Returns the packets ID
		 * 
		 * @return packetID
		 */
		public int getId() {
			return packetId;
		}

	}

	public byte packetId;

	/**
	 * Constructor for Packet, the packetId is interpreted to decide which type
	 * of PacketType is being created.
	 * 
	 * @param packetId
	 *            - the type of Packet to be crated.
	 * @see PacketTypes
	 */
	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}

	/**
	 * Writes the data from a client to the server.
	 * 
	 * @param client
	 *            - the client to be used to send the data.
	 */
	public abstract void writeData(GameClient client);

	/**
	 * Writes the data from the server to all connected clients.
	 * 
	 * @param server
	 *            - The server to send the data.
	 */
	public abstract void writeData(GameServer server);

	/**
	 * Reads the byte array of data in a Packet. readData removes the Packets
	 * packetId and focuses only on the actual data being received.
	 * 
	 * @param data
	 *            - the byte array to be read
	 * @return - The data to be interpreted.
	 */
	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2);
	}
/**
 * Looks up the Packet type to make sure its packetId transports it to the right position in the server/client receiving it.  
 * @param packetId - The packetId to be interpreted.
 * @return enum the corresponing {@link PacketTypes}.
 */
	public static PacketTypes lookupPacket(String packetId) {
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch (NumberFormatException e) {
			return PacketTypes.INVALID;
		}

	}
	private static PacketTypes lookupPacket(int id) {
		for (PacketTypes p : PacketTypes.values()) {
			if (p.getId() == id) {
				return p;
			}

		}
		return PacketTypes.INVALID;
	}
/**
 * Returns the data in the form of a byte array. 
 * @return byte array data. 
 */
	public abstract byte[] getData();

}
