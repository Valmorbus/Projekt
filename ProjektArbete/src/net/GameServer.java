package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import packets.Packet;
import packets.Packet.PacketTypes;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;
import projectv2.Bullet;
import projectv2.Game;
import projectv2.PlayerMP;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameServer extends Thread {
	
	private DatagramSocket socket;
	private ArrayList<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();
	private ArrayList<Bullet> connectedBullets = new ArrayList<Bullet>();
	private Game game;
	

	public GameServer(Game game) {
		this.game = game;
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

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
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
		Packet packet = null;
		switch (type) {
		case INVALID:
			break;
		case LOGIN: {
			packet = new Packet00Login(data);
			PlayerMP player = new PlayerMP(((Packet00Login) packet).getUsername(), ((Packet00Login)packet).getX(), ((Packet00Login)packet).getY(), ((Packet00Login)packet).getRotate(),
					0, adress, port);
			connectedPlayers.add(player);

			System.out.println(player.port + " " + player.getName() + " " + player.ipAdress);
			addConnection(player, (Packet00Login) packet);
		}
			break;
		case DISCONNECT: {
			packet = new Packet01Disconnect(data);
			System.out.println(
					"User " + ((Packet01Disconnect) packet).getUsername() + " " + adress.getHostAddress().toString()
							+ " port " + port + " Has left " + ((Packet01Disconnect) packet).getUsername());
			removeConnection((Packet01Disconnect) packet);
			
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
		}
	}

	private void handleShoot(Packet03Shoot packet) {
		connectedBullets.add(new Bullet(packet.getX(), packet.getY(), packet.getRotate()));
		packet.writeData(this);
		connectedBullets.remove(0);
	}

	private void handleMove(Packet02Move packet) {
		Platform.runLater(()->{
				if (getPlayerMP(packet.getUsername()) != null) {
					int index = getPlayerMPIndex(packet.getUsername());
					connectedPlayers.get(index).setPosX(packet.getX());
					connectedPlayers.get(index).setPosY(packet.getY());
					connectedPlayers.get(index).setRotate(packet.getRotate());
					packet.writeData(this);
				}
		});
		
		
	}

	public void addConnection(PlayerMP player2, Packet00Login packet) {
		boolean alreadyConnected = false;
	
		for (PlayerMP p : connectedPlayers)
		{
			if (player2.getName().equals(p.getName())) {
				if (p.ipAdress == null) {
					p.ipAdress = player2.ipAdress;
				}
				if (p.port == 0) { 
					p.port = player2.port;
				}
				alreadyConnected = true;
			}
			else {
				try {
					
					//uppdaterar nya spelaren om gamla spelares positioner
					/*
					packet = new Packet00Login(p.getName(), p.getTranslateX(), p.getTranslateY(), p.getRotate());
					sendData(packet.getData(), p.ipAdress, p.port);
					
					
					// skickar att tidigare spelare är connected
				
					sendData(packet.getData(), player2.ipAdress, player2.port);
				*/
					//detta ska vara korrekt sätt att skriva på, problemet är att spelare tilldelas förra connected player och inte nuvarande
					 packet = new Packet00Login(player2.getName(), player2.getTranslateX(),player2.getTranslateY(),player2.getRotate());
					 sendData(packet.getData(), p.ipAdress, p.port);
					 packet = new Packet00Login(p.getName(), p.getTranslateX(), p.getTranslateY(), p.getRotate());				
					sendData(packet.getData(), player2.ipAdress, player2.port);
					 
					
				} catch (Exception e) {
					System.out.println("Server cant send packet " + e);
				}
			}
		}
		if (!alreadyConnected) {
			this.connectedPlayers.add(player2);	
		}
	}

	private PlayerMP getPlayerMP(String username) {
		for (PlayerMP playerMP : connectedPlayers) {
			if (playerMP.getName().equals(username))
				return playerMP;
		}
		return null;
	}

	private int getPlayerMPIndex(String username) {
		int index = 0;
		for (PlayerMP playerMP : connectedPlayers) {
			if (playerMP.getName().equals(username))
				break;
			index++;
		}
		return index;
	}

	public void removeConnection(Packet01Disconnect packet) {
		connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
		packet.writeData(this);
	}

}
