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

public class GameClient extends Thread {

	private InetAddress ipAdress;
	private DatagramSocket socket;
	private Game game;
	private boolean running = true;

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

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAdress, 5005);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleLogin(Packet00Login packet, InetAddress adress, int port) {

		PlayerMP player = new PlayerMP(((Packet00Login) packet).getUsername(), packet.getX(), packet.getY(),
				packet.getRotate(), 0, adress, port);
		this.game.addPlayer(player);

	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void shutDownClient() throws SocketException {
		this.socket.close();

	}

}
