package projectv2;

import java.net.InetAddress;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class PlayerMP extends Player {

	public InetAddress ipAdress;
	public int port;
	private ImageView graphics = new ImageView();
	private String name;
	//private Image image = new Image("projectv2/untitled.png");

	public PlayerMP() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	public PlayerMP(String name, double posX, double posY, double rotate, double speed, InetAddress ipAdress, int port) {
		super(posX, posY, rotate, speed); //, ipAdress, port);
		this.name = name;
		this.image = new Image("/Untitled.png");
		this.graphics.setImage(image);
		//this.getGraphics().setTranslateX(posX);
		//this.getGraphics().setTranslateY(posY);
		//this.getGraphics().setRotate(rotate);
		this.ipAdress = ipAdress;
		this.port = port;
	}


	public PlayerMP(String name, InetAddress ipAdress, int port) {
		super();
		this.ipAdress = ipAdress;
		this.port = port;
		this.image = new Image("/Untitled.png");
		this.graphics.setImage(image);
		this.name = name;
		// TODO Auto-generated constructor stub
	}
	
	public PlayerMP(int lives, String name) {
		super(lives, name);
		// TODO Auto-generated constructor stub
	}

	public void tick(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	

}
