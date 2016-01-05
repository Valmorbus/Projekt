package projectv2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javafx.application.Platform;

public class Server extends Thread {
	private DataOutputStream out;
	private DataInputStream in;
	private Users[] user = new Users[4];
	int i = 150;
	int j = 1;
	Random random = new Random();
	private ArrayList<Player> users = new ArrayList<Player>();

	public static void main(String[] args) {
		Thread thread = new Thread(() -> {
			new Server().connectToClient();
		});
		thread.start();
	}

	private void connectToClient() {
		try {
			ServerSocket server = new ServerSocket(8035);
			System.out.println("Server start");

			while (true) {

				Socket playerSocket;
				try {
					playerSocket = server.accept();
					System.out.println("connected");
					in = new DataInputStream(playerSocket.getInputStream());
					System.out.println(in.readDouble());
					System.out.println(in.readDouble());
					out = new DataOutputStream(playerSocket.getOutputStream());
					System.out.println("out " + out);
					System.out.println("här");
					users.add(new Player());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				run();
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
				d += 0.05;
				c += 0.005;
				out.writeDouble(d);
				out.flush();
				out.writeDouble(c);
				out.flush();
				// System.out.println(d);
				for (int i = 0; i < users.size(); i++) {
					out.writeDouble(users.get(i).getGraphics().getTranslateX());
					out.flush();
					out.writeDouble(users.get(i).getGraphics().getTranslateY());
					out.flush();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
