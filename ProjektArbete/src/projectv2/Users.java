package projectv2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Users implements Runnable{
	
	private DataOutputStream out;
	private DataInputStream in;
	private Users[] user = new Users[4];
	
	


	public Users(DataOutputStream out, DataInputStream in, Users[] user) {
		super();
		this.out = out;
		this.in = in;
		this.user = user;
	}

	@Override
	public void run() {
		while (true){
			try {
				String message = in.readUTF();
				for (int i = 0; i < user.length; i++) {
					if (user[i] != null){
						
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		
	}

}
