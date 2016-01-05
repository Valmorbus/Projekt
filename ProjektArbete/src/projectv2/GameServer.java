package projectv2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameServer extends Thread{
	//private InetAddress ipAdress;
	private DatagramSocket socket;
	
	
	public static void main(String[] args) {
		
			GameServer gs = new GameServer();
			gs.start();
	}

	public GameServer() {
		try {
			this.socket = new DatagramSocket(5001);
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
			String message = new String(packet.getData());
			System.out.println("Client " + packet.getAddress().getHostAddress() +" " + packet.getPort() + " " + message);
			if (message.trim().equalsIgnoreCase("ping")){
				System.out.println("Client " +message);
				sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
			}
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

}
