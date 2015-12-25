package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {
	private int lives; 
	private String Name;
	private ImageView graphics = new ImageView();
	private Image image;
	private double posX;
	private double posY; 
	
	
	public Player(int lives, String name) {
		super();
		this.image = new Image("C:/Users/borgs_000/Desktop/iges.jpg");
		this.graphics.setImage(image);
		
		this.lives = lives;
		Name = name;
	}


	public int getLives() {
		return lives;
	}


	public void setLives(int lives) {
		this.lives = lives;
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	} 
	
	
	public ImageView getGraphics() {
		return graphics;
	}

	public void setGraphics(ImageView graphics) {
		this.graphics = graphics;
	}
	
	
	
	
	

}
