package projectv2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import packets.Packet;
import packets.Packet.PacketTypes;
import packets.Packet00Login;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameServer extends Thread {
	// private InetAddress ipAdress;
	private DatagramSocket socket;
	private ArrayList<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();
	Game game = new Game();
	PlayerMP player;
	//Player player;

	public static void main(String[] args) {

		GameServer gs = new GameServer();
		gs.start();
	}

	public GameServer() {
		try {
			this.socket = new DatagramSocket(5005);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("serverStart");
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
				/*
				 * String message = new String(packet.getData());
				 * System.out.println("Client " +
				 * packet.getAddress().getHostAddress() +" " + packet.getPort()
				 * + " " + message); if
				 * (message.trim().equalsIgnoreCase("ping")){
				 * System.out.println("Client " +message);
				 * sendData("pong".getBytes(), packet.getAddress(),
				 * packet.getPort()); }
				 */

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			// finally{
			// socket.close();
			// }

		}

	}

	public void sendData(byte[] data, InetAddress ipAdress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAdress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendDataToAllClients(byte[] data) {
		for (PlayerMP p : connectedPlayers) {
			sendData(data, p.ipAdress, p.port);

		}
	}

	private void parsePacket(byte[] data, InetAddress adress, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		switch (type) {
		case INVALID:
			break;
		case LOGIN:
			Packet00Login packet = new Packet00Login(data);
			System.out.println("connected " + adress.getHostAddress().toString() + " Has connected");
			player = new PlayerMP(adress, port);
			if (player != null) {
				this.connectedPlayers.add(player);
				game.addPlayer(player);
				System.out.println("liftof");
				//game.player = player;
			}
			System.out.println("liftof");
			break;
		case DISCONNECT:
			break;
		default:
			break;
		}
	}

}
