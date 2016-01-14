package projectv2;

import java.net.InetAddress;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

public class PlayerMP extends Player {

	private InetAddress ipAdress;
	private int port;
//	private ImageView graphics = new ImageView();
	private String name;
	private int ammo;
	private Ellipse hitbox; 
	private boolean isAlive;
	private Image[] ship = {new Image((getClass().getResource("/damaged.png").toString())),
			new Image((getClass().getResource("/verydamaged.png").toString())),
			new Image((getClass().getResource("/extremedamage.png").toString())),
			new Image((getClass().getResource("/destroyed.png").toString()))};

	public PlayerMP() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	public PlayerMP(String name, double posX, double posY, double rotate, double speed, InetAddress ipAdress, int port) {
		super(posX, posY, rotate, speed); //, ipAdress, port);
		this.name = name;
		this.image = new Image("/Untitled.png");
		this.graphics.setImage(image);
		this.ipAdress = ipAdress;
		this.port = port;
		this.ammo = 8;
		this.isAlive = true;
		
		hitbox = new Ellipse(image.getWidth()/2.5, image.getHeight()/2.5);
		//hitbox.setFill(Color.TRANSPARENT);
		hitbox.centerXProperty().bind(this.translateXProperty().add(image.getWidth()/2));
		hitbox.centerYProperty().bind(this.translateYProperty().add(image.getHeight()/2));
		hitbox.rotateProperty().bind(this.rotateProperty());
		
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


	public int getAmmo() {
		return ammo;
	}


	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}


	public InetAddress getIpAdress() {
		return ipAdress;
	}


	public void setIpAdress(InetAddress ipAdress) {
		this.ipAdress = ipAdress;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public Ellipse getHitbox() {
		return hitbox;
	}


	public void setHitbox(Ellipse hitbox) {
		this.hitbox = hitbox;
	}


	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	public void showDamage() {
		if (this.getLives() > 75)
			this.setImage(image);
		if (this.getLives() < 75)
			this.setImage(ship[0]);
		if (this.getLives()<50)
			this.setImage(ship[2]);
		if (this.getLives() <25)
			this.setImage(ship[1]);
		if (this.isAlive == false)
			this.setImage(ship[3]);
	}
	

}
