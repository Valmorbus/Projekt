package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import packets.Packet;
import packets.Packet.PacketTypes;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;
import packets.Packet04Hit;
import projectv2.Bullet;
import projectv2.PlayerMP;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameServer extends Thread {

	private DatagramSocket socket;
	private ArrayList<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();
	private ArrayList<Bullet> connectedBullets = new ArrayList<Bullet>();
	private boolean running = true;
	private Stage stage;
	private TextArea output;

	public GameServer(Stage stage) {
		try {
			this.socket = new DatagramSocket(5005);
			this.stage = stage;
			setServerStage();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			output.setText("Server starts " + InetAddress.getLocalHost().toString() + "\n");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
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
			sendData(data, p.getIpAdress(), p.getPort());
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
			PlayerMP player = new PlayerMP(((Packet00Login) packet).getUsername(), ((Packet00Login) packet).getX(),
					((Packet00Login) packet).getY(), ((Packet00Login) packet).getRotate(), 0, adress, port);
			connectedPlayers.add(player);
			output.setText(output.getText() + "Port: " + player.getPort() + " Playername: " + player.getName() + " IP "
					+ player.getIpAdress() + "\n");
			addConnection(player, (Packet00Login) packet);
		}
			break;
		case DISCONNECT: {
			packet = new Packet01Disconnect(data);
			output.setText(output.getText() + "User " + ((Packet01Disconnect) packet).getUsername() + " "
					+ adress.getHostAddress().toString() + " port " + port + " Has left " + "\n");

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

		if (getPlayerMP(packet.getUsername()) != null) {
			int index = getPlayerMPIndex(packet.getUsername());
			connectedPlayers.get(index).setLives(connectedPlayers.get(index).getLives() - packet.getDamage());
			packet.writeData(this);
		}
	}

	private void handleShoot(Packet03Shoot packet) {
		connectedBullets.add(new Bullet(packet.getX(), packet.getY(), packet.getRotate()));
		packet.writeData(this);
		connectedBullets.remove(0);
	}

	private void handleMove(Packet02Move packet) {
		Platform.runLater(() -> {
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
		try {
			output.setText(output.getText() + " " + "client is reachable" + player2.getIpAdress() + " reached: "
					+ player2.getIpAdress().isReachable(5000) + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (PlayerMP p : connectedPlayers) {
			if (player2.getName().equalsIgnoreCase(p.getName())) {
				if (p.getIpAdress() == null) {
					p.setIpAdress(player2.getIpAdress());
					System.out.println(" i if " + p.getIpAdress());
				}
				if (p.getPort() == 0) {
					p.setPort(player2.getPort());
					System.out.println(" i if " + p.getPort());
				}

				alreadyConnected = true;
			} else {
				try {
					// uppdaterar nya spelaren om gamla spelares positioner
					packet = new Packet00Login(player2.getName(), player2.getTranslateX(), player2.getTranslateY(),
							player2.getRotate());
					sendData(packet.getData(), p.getIpAdress(), p.getPort());
					// skickar att tidigare spelare är connected
					packet = new Packet00Login(p.getName(), p.getTranslateX(), p.getTranslateY(), p.getRotate());
					sendData(packet.getData(), player2.getIpAdress(), player2.getPort());

				} catch (Exception e) {
					output.setText(output.getText() + " " + "Server cant send packet " + e + "\n");
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

	public void shutDownServer() throws SocketException {
		this.socket.close();
	}

	private void setServerStage() {
		Pane pane = new Pane();
		this.output = new TextArea();
		Scene serverScene = new Scene(pane);
		serverScene.getStylesheets().add("/net/Server.css");
		pane.setStyle("-fx-background-color: black;");
		output.setId("text-area");
		output.setWrapText(true);
		pane.getChildren().add(output);
		
		this.stage.setScene(serverScene);
	}

}
