package projectv2;

import java.net.InetAddress;

import javafx.scene.image.Image;

public class PlayerMP extends Player {

	public InetAddress ipAdress;
	public int port;
	//private Image image = new Image("projectv2/untitled.png");

	public PlayerMP() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PlayerMP(InetAddress ipAdress, int port) {
		super();
		this.ipAdress = ipAdress;
		this.port = port;
		this.graphics = super.graphics;
		this.image = null;
		// TODO Auto-generated constructor stub
	}
	
	public PlayerMP(int lives, String name) {
		super(lives, name);
		// TODO Auto-generated constructor stub
	}

	public void tick(){
		
	}
	

}
