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
	int i = 150;
	int j = 1;
	
	
	
	public static void main(String[] args) {
		Thread thread = new Thread(()->{
			new Server().connectToClient();
		});
		thread.start();
	}
	private void connectToClient(){
		try {
			ServerSocket server = new ServerSocket(8016);
			
			while (true){
				Socket playerSocket = server.accept();
				System.out.println("connected");
				in = new DataInputStream(playerSocket.getInputStream());
				System.out.println(in.readDouble());
				System.out.println(in.readDouble());
				out = new DataOutputStream(playerSocket.getOutputStream());
				System.out.println("out " +out);
				System.out.println("här");
				out.writeDouble(i+j);
				out.flush();
				out.writeDouble(i+j);
				out.flush();
				
				/*for (int i = 0; i < user.length; i++) {
					System.out.println("Connection from " +playerSocket.getInetAddress());
					out = new DataOutputStream(playerSocket.getOutputStream());
					in = new DataInputStream(playerSocket.getInputStream());
					if (user[i] == null){
						user[i] = new Users(out, in, user);
						Thread thread = new Thread();
						thread.start();
						break;
					}
					
				}*/
				
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
