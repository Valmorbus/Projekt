package projectv2;

import java.net.InetAddress;
import javafx.scene.image.Image;

/**
 * A player glass for the game. This class extends GameObject and as such it also extends ImageView.
 * Thus the player object can be placed directly on a pane. 
 * @author Simons
 *
 */
public class Player extends GameObject {
	private int lives;
	protected String name;
	protected Image image;
	public InetAddress ipAdress;
	public int port;
	
	/**
	 * A player for a future single player version of the game.
	 * @param lives - The number of lives the player starts with
	 * @param name - the 
	 */
	public Player(int lives, String name) {
		this.image = new Image("/Untitled.png");
		this.setImage(image);
		this.lives = lives;
		this.name = name;
	}
/**
 * A player for a future single player version of the game.
 * @param posX - The TranslateX position of the player 
 * @param posY - The TranslateY position of the player 
 * @param rotate - The Rotate of the player 
 * @param speed - The speed which the player travels
 */
	public Player(double posX, double posY, double rotate, double speed) {
		super(posX, posY, rotate, speed);
		this.image = new Image("/Untitled.png"); // "resource/Untitled.png";
		this.setImage(image);
		this.lives = 100;
		this.setImage(this.image);
		
	}
/**
 * Returns the players set amount of hit points
 * @return - the hit points of the player
 */
	public int getLives() {
		return lives;
	}
/**
 * Sets the amount of hitpoints for the player
 * @param lives - The amount of hitpoints the player should have
 */
	public void setLives(int lives) {
		this.lives = lives;
	}
/**
 * The players username
 * @return - The players username as a String
 */
	public String getName() {
		return name;
	}
/**
 * Sets the name of the player
 * @param name - The name of the player as a String
 */
	public void setName(String name) {
		this.name = name;
	}

}
