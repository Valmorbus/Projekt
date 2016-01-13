package projectv2;

import java.net.InetAddress;
import java.net.URL;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

public class Player extends GameObject {
	private int lives;
	protected String name;
	protected ImageView graphics = new ImageView();
	protected Image image;
	public InetAddress ipAdress;
	public int port;

	public Player(int lives, String name) {
		this.image = new Image("/Untitled.png");
		this.graphics.setImage(image);
		this.lives = lives;
		this.name = name;
	}

	public Player(Image image) {
		this.image = image;
		this.graphics.setImage(image);
		this.lives = 100;
	}

	public Player() {
		this.image = new Image("/Untitled.png"); // "resource/Untitled.png";
		this.graphics.setImage(image);
		this.lives = 100;

	}

	public Player(InetAddress ipAdress, int port) {
		this.image = new Image("/Untitled.png"); // "resource/Untitled.png";
		this.graphics.setImage(image);
		this.lives = 100;
		this.ipAdress = ipAdress;
		this.port = port;
	}

	public Player(double posX, double posY, double rotate, double speed) { // ,
																			// InetAddress
																			// ipAdress,
																			// int
																			// port)
																			// {
		super(posX, posY, rotate, speed);
		this.image = new Image("/Untitled.png"); // "resource/Untitled.png";
		this.graphics.setImage(image);
		this.lives = 100;
		this.setImage(this.image);
		// this.ipAdress = ipAdress;
		// this.port = port;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ImageView getGraphics() {
		return graphics;
	}

	public void setGraphics(Image graphics) {
		this.graphics.setImage(graphics);
	}

	

}
