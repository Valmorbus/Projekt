package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import projectv2.Main;

public class GameClient extends Thread {

	private InetAddress ipAdress;
	private DatagramSocket socket;
	private Main main;
	private int port = 3340;

	public static void main(String[] args) {

		GameClient gc = new GameClient("localhost");

		gc.start();

		gc.sendData("ping".getBytes());
	}

	public GameClient(String ipAdress) {
		try {
			this.socket = new DatagramSocket();
			this.ipAdress = InetAddress.getByName(ipAdress);
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
			String message = new String(packet.getData());
			// System.out.println("Client" + message);
			System.out.println("Server " + message);
			if (message.trim().equalsIgnoreCase("pong")) {
				System.out.println("Server " + message);
			}
		}

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
