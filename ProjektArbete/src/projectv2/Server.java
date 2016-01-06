package projectv2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Platform;

public class Server extends Thread {
	private DataOutputStream out;
	private DataInputStream in;
	int i = 150;
	int j = 1;
	Random random = new Random();
	private ArrayList<Player> users = new ArrayList<Player>();
	
	DatagramSocket ds;
	

	public static void main(String[] args) {
		Thread thread = new Thread(() -> {
			new Server().acceptClient();
		});
		thread.start();
	}

	private void acceptClient() {
		try {
			//ServerSocket server = new ServerSocket(8001);
			ds = new DatagramSocket(0);
			System.out.println("Server start");
			

			//while (true) {

				Socket playerSocket;
				try {
					//playerSocket = server.accept();
					
					byte[] bArray = new byte[1024];
					DatagramPacket packet = new DatagramPacket(bArray, bArray.length,InetAddress.getByName("localhost"), 8000);
					bArray[0] = (byte) 1337;
					
					ds.send(packet);
					
					System.out.println("connected");
					byte playerId = 124;
					
					
					
					/*
					in = new DataInputStream(playerSocket.getInputStream());
					System.out.println(in.readDouble());
					System.out.println(in.readDouble());
					out = new DataOutputStream(playerSocket.getOutputStream());
					System.out.println("här");
					users.add(new Player());*/
					
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				//}

				//run();
				// Scanner sc = new Scanner(System.in);
				// out.writeDouble(sc.nextDouble());
				// out.flush();

				/*
				 * for (int i = 0; i < user.length; i++) { System.out.println(
				 * "Connection from " +playerSocket.getInetAddress()); out = new
				 * DataOutputStream(playerSocket.getOutputStream()); in = new
				 * DataInputStream(playerSocket.getInputStream()); if (user[i]
				 * == null){ user[i] = new Users(out, in, user); Thread thread =
				 * new Thread(); thread.start(); break; }
				 * 
				 * }
				 */

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	@Override
	public void run() {
		double d = 150;
		double c = 150;
		
		while (true) {

			try {
				System.out.println(in.readDouble());
				// System.out.println(d);
				for (int i = 0; i < users.size(); i++) {
					out.writeDouble(d);
					out.flush();
					out.writeDouble(c);
					out.flush();
					d++;
					c++;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
