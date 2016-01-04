package projectv2;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

public class Player {
	private int lives; 
	private String Name;
	private ImageView graphics = new ImageView();
	private Image image;
	private Polygon triangle; 
	
	
	public Player(int lives, String name) {
		super();
		this.image = new Image("C:/Users/borgs_000/workspace/ProjektArbete/src/projectv2/images.jpg");
		this.graphics.setImage(image);
		this.triangle = new Polygon(15, 30, 3);
		this.lives = lives;
		Name = name;
	}
	public Player (Image image){
		this.image = image;
		this.graphics.setImage(image);
		this.lives = 3;
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
	
	public Polygon getTriangle(){
		return this.triangle;
	}
	
	
	
	
	

}
