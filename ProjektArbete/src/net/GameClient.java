package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import packets.Packet;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;
import packets.Packet04Hit;
import packets.Packet.PacketTypes;
import projectv2.Game;
import projectv2.PlayerMP;

/**
 * A client with which the {@link Game} connects to the {@link GameServer}. This
 * class uses User Datagram Protocol to send and receive {@link Packet}s to and
 * from the {@link GameServer}. The GameClient extends {@link Thread}.
 * 
 * @author Simon Borgström
 * @version 1.0
 * @see Thread
 *
 */

public class GameClient extends Thread {

	private InetAddress ipAdress;
	private DatagramSocket socket;
	private Game game;
	private boolean running = true;

	/**
	 * Instantiates the GameClient. The client takes a {@link Game} as parameter
	 * as well as a {@link String} which is converted to an {@link InetAddress}.
	 * If the {@link InetAddress} can't convert the {@link String} it will
	 * default to "localhost".
	 * 
	 * @param game
	 *            - The game on which this GameClient is run.
	 * @param ipAdress
	 *            - The String which will be converted to an {@link InetAddress}
	 *            .
	 */
	public GameClient(Game game, String ipAdress) {
		try {
			this.socket = new DatagramSocket();
			this.ipAdress = InetAddress.getByName(ipAdress);
			this.game = game;
			System.out.println(ipAdress.toString());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			try {
				this.ipAdress = InetAddress.getByName("localhost");
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			System.out.println("Not a valid IP");
		}

	}

	/**
	 * Runs the {@link GameClient}. Tries to send {@link Packet}s to the
	 * {@link GameServer} as well as receive them.
	 */
	@Override
	public void run() {
		System.out.println("Client start");
		while (running) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());

		}
		this.socket.close();
	}

	private void parsePacket(byte[] data, InetAddress adress, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		case INVALID:
			break;
		case LOGIN: {
			packet = new Packet00Login(data);
			System.out
					.println("User " + ((Packet00Login) packet).getUsername() + " " + adress.getHostAddress().toString()
							+ " port " + port + " Has connected " + ((Packet00Login) packet).getUsername());
			handleLogin((Packet00Login) packet, adress, port);
		}
			break;
		case DISCONNECT: {
			packet = new Packet01Disconnect(data);
			System.out.println(
					"User " + ((Packet01Disconnect) packet).getUsername() + " " + adress.getHostAddress().toString()
							+ " port " + port + " Has left " + ((Packet01Disconnect) packet).getUsername());
			this.game.removePlayerMP(((Packet01Disconnect) packet).getUsername());
		}
			break;
		case MOVE: {
			packet = new Packet02Move(data);
			handleMove((Packet02Move) packet);
		}
			break;
		case SHOOT: {
			packet = new Packet03Shoot(data);
			handleShoot((Packet03Shoot) packet);
		}
			break;
		case HIT: {
			packet = new Packet04Hit(data);
			handleHit((Packet04Hit) packet);
		}
			break;

		default:
			break;
		}
	}

	private void handleHit(Packet04Hit packet) {
		this.game.damagePlayer(packet.getUsername(), packet.getDamage());

	}

	private void handleShoot(Packet03Shoot packet) {
		this.game.updateShoots(packet.getX(), packet.getY(), packet.getRotate());

	}

	private void handleMove(Packet02Move packet) {
		this.game.updatePlayers(packet.getUsername(), packet.getX(), packet.getY(), packet.getRotate());

	}

	private void handleLogin(Packet00Login packet, InetAddress adress, int port) {

		PlayerMP player = new PlayerMP(((Packet00Login) packet).getUsername(), packet.getX(), packet.getY(),
				packet.getRotate(), 0, adress, port);
		this.game.addPlayer(player);

	}

	/**
	 * Sends {@link Packet} to {@link GameServer}.
	 * 
	 * @param data
	 *            - The array of bytes to send.
	 */

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAdress, 5005);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Returns {@link GameClient}s {@link DatagramSocket}.
	 * 
	 * @return {@link DatagramSocket} socket
	 */

	public DatagramSocket getSocket() {
		return socket;
	}

	/**
	 * Sets the {@link DatagramSocket} of the {@link GameClient}.
	 * 
	 * @param {@link
	 * 			DatagramSocket} socket
	 */
	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	/**
	 * Used to check if the loop in run() method in {@link GameClient} is still
	 * running.
	 * 
	 * @return true if running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Used to turn on or of the loop in {@link GameClient}s method run().
	 * 
	 * @param running
	 *            - set false to shut down the loop
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Used to shut down the {@link DatagramSocket} of {@link GameClient}.
	 * 
	 * @throws SocketException
	 *             error accessing the Socket
	 */
	public void shutDownClient() throws SocketException {
		this.socket.close();

	}

}
