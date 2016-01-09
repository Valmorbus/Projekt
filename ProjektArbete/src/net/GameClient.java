package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.scene.image.Image;
import packets.Packet;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet.PacketTypes;
import projectv2.Game;
import projectv2.Main;
import projectv2.PlayerMP;

public class GameClient extends Thread {

	private InetAddress ipAdress;
	private DatagramSocket socket;
	private Game game;

	public GameClient(Game game, String ipAdress) {
		try {
			this.socket = new DatagramSocket();
			this.ipAdress = InetAddress.getByName(ipAdress);
			this.game = game;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		System.out.println("Client start");
		while (true) {
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

	}

	private void parsePacket(byte[] data, InetAddress adress, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		case INVALID:
			break;
		case LOGIN: {
			System.out.println("logon");
			packet = new Packet00Login(data);
			PlayerMP player = //new PlayerMP(((Packet00Login) packet).getUsername(), adress, port);
			new PlayerMP(((Packet00Login) packet).getUsername(), ((Packet00Login) packet).getX(), ((Packet00Login) packet).getY(), ((Packet00Login) packet).getRotate(), 0, adress, port);
			this.game.addPlayer(player);
		}
			break;
		case DISCONNECT: {
			packet = new Packet01Disconnect(data);
			System.out.println(
					"User " + ((Packet01Disconnect) packet).getUsername() + " " + adress.getHostAddress().toString()
							+ " port " + port + " Has left " + ((Packet01Disconnect) packet).getUsername());
			this.game.removePlayerMP(((Packet01Disconnect)packet).getUsername());
		}
			break;
		case MOVE : {
			packet = new Packet02Move(data);
			handleMove((Packet02Move) packet);
		}
		default:
			break;
		}
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

}
