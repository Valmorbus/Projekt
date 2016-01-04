package projectv2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;

public class Server {
	private DataOutputStream out;
	private DataInputStream in;
	private Users[] user = new Users[4];
	
	
	
	public static void main(String[] args) {
		Thread thread = new Thread(()->{
			new Server().connectToClient();
		});
		thread.start();
	}
	private void connectToClient(){
		try {
			ServerSocket server = new ServerSocket(8000);
			Platform.runLater(()->{
				System.out.println("connection Established");
			});
			while (true){
				Socket playerSocket = server.accept();
				for (int i = 0; i < user.length; i++) {
					System.out.println("Connection from " +playerSocket.getInetAddress());
					out = new DataOutputStream(playerSocket.getOutputStream());
					in = new DataInputStream(playerSocket.getInputStream());
					if (user[i] == null){
						user[i] = new Users(out, in, user);
						Thread thread = new Thread();
						thread.start();
						break;
					}
					
				}
				
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
