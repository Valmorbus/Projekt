package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

import packets.Packet;
import packets.Packet.PacketTypes;
import spacegame.Bullet;
import spacegame.PlayerMP;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;
import packets.Packet04Hit;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A Server with which the {@link Game} connects to with the {@link GameClient}.
 * This class uses User Datagram Protocol to send and receive {@link Packet}s to
 * and from the {@link GameClient}. The GameClient extends {@link Thread}. In
 * order to use the GameServer over anything else than a local network make sure
 * that the firewall isn't blocking the server and that the router port-forwards
 * to the local IP of the computer on which the server is run. The GameServer
 * extends {@link Thread}.
 * 
 * @author Simon Borgström
 * @version 1.0
 * @see Thread
 */

public class GameServer extends Thread {

	private DatagramSocket socket;
	private ArrayList<PlayerMP> connectedPlayers = new ArrayList<PlayerMP>();
	private ArrayList<Bullet> connectedBullets = new ArrayList<Bullet>();
	private boolean running = true;
	private Stage stage;
	private TextArea output;

	/**
	 * Constructor for the GameServer. It initialises a {@link DatagramSocket}
	 * set to listen on port 5005. It takes a {@link Stage} as a parameter on
	 * which it will set up a {@link TextArea} for data output.
	 * 
	 * @param stage
	 *            - The Stage which will be transformed for the GameServer.
	 */
	public GameServer(Stage stage) {
		try {
			this.socket = new DatagramSocket(5005);
			this.stage = stage;
			setServerStage();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs the {@link GameServer}. Tries to send {@link Packet}s to connected
	 * {@link GameClients} as well as receive them.
	 */
	@Override
	public void run() {
		try {
			output.setText("Server starts " + InetAddress.getLocalHost().toString() + "\n");
			output.setText(output.getText() + "Public IP: " + getPublicIP() + "\n");
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

	/**
	 * Used to send data to a specific {@link GameClient}. Used when the data is
	 * not intended for all connected {@link GameClient}s.
	 * 
	 * @param data
	 *            - the byte array of data to be sent.
	 * @param ipAdress
	 *            - The {@link InetAddress} of the client.
	 * @param port
	 *            - the port which the client listens to.
	 */
	public void sendData(byte[] data, InetAddress ipAdress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAdress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Used by {@link Packet}s to send data to all connected {@link GameClient}
	 * s. Or you could say used to send the {@link Packet}.
	 * 
	 * @param data
	 *            - The byte array of data to be sent.
	 */
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
			packet = new Packet00Login(data); // exempel på polymorphism.
												// packet00Login är ett packet
												// då den ärver packet.
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
				connectedPlayers.get(index).setTranslateX(packet.getX());
				connectedPlayers.get(index).setTranslateY(packet.getY());
				connectedPlayers.get(index).setRotate(packet.getRotate());
				packet.writeData(this);
			}
		});

	}

	private void addConnection(PlayerMP player2, Packet00Login packet) {
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

	private void removeConnection(Packet01Disconnect packet) {
		connectedPlayers.remove(getPlayerMPIndex(packet.getUsername()));
		packet.writeData(this);
	}

	/**
	 * Returns {@link GameServer}s {@link DatagramSocket}.
	 * 
	 * @return {@link DatagramSocket} socket
	 */
	public DatagramSocket getSocket() {
		return socket;
	}

	/**
	 * Sets the {@link DatagramSocket} of the {@link GameServer}.
	 * 
	 * @param {@link
	 * 			DatagramSocket} socket
	 */
	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	/**
	 * Used to check if the loop in run() method in {@link GameServer} is still
	 * running.
	 * 
	 * @return true if running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Used to turn on or of the loop in {@link GameServer}s method run().
	 * 
	 * @param running
	 *            - set false to shut down the loop
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * Used to shut down the {@link DatagramSocket} of {@link GameServer}.
	 * 
	 * @throws SocketException
	 *             error accessing the Socket
	 */
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

	private String getPublicIP() {

		URL connection;
		String str = null;
		try {
			connection = new URL("http://checkip.amazonaws.com/");
			URLConnection con = connection.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			str = reader.readLine();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;

	}

}
