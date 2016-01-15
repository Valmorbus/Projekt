package projectv2;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A player class for the game. This class extends {@link ImageView}. Thus the
 * player object can be placed directly on a pane. This class also implements
 * the {@link GameObject} (currently unused).
 * 
 * @author Simon Borgström
 * @version 1.0
 * @see ImageView
 *
 */
public class Player extends ImageView implements GameObject {
	private int lives;
	protected boolean isAlive;
	protected String name;
	private double speed;
	protected Image image;
	private Image[] ship = { new Image((getClass().getResource("/damaged.png").toString())),
			new Image((getClass().getResource("/verydamaged.png").toString())),
			new Image((getClass().getResource("/extremedamage.png").toString())),
			new Image((getClass().getResource("/destroyed.png").toString())) };

	/**
	 * A player for a future single player version of the game.
	 * 
	 * @param lives
	 *            - The number of lives the player starts with
	 * @param name
	 *            - the name of the player.
	 * 
	 */
	public Player(int lives, String name) {
		this.image = new Image("/Untitled.png");
		this.setImage(image);
		this.lives = lives;
		this.name = name;
	}

	/**
	 * A player for a future single player version of the game.
	 * 
	 * @param name
	 *            - The name of the player as a String
	 * @param posX
	 *            - The TranslateX position of the player
	 * @param posY
	 *            - The TranslateY position of the player
	 * @param rotate
	 *            - The Rotate of the player
	 * @param speed
	 *            - The speed which the player travels
	 * 
	 */
	public Player(String name, double posX, double posY, double rotate, double speed) {
		this.image = new Image("/Untitled.png"); // "resource/Untitled.png";
		this.setImage(image);
		this.lives = 100;
		this.setImage(this.image);
		this.name = name;
		this.speed = speed;
		this.setRotate(rotate);
		this.setTranslateX(posX);
		this.setTranslateY(posY);

	}

	/**
	 * Returns the players set amount of hit points
	 * 
	 * @return - the hit points of the player
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * Sets the amount of hitpoints for the player
	 * 
	 * @param lives
	 *            - The amount of hitpoints the player should have
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}

	/**
	 * The players username
	 * 
	 * @return - The players username as a String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the player
	 * 
	 * @param name
	 *            - The name of the player as a String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Checks the status of the player
	 * 
	 * @return - true if player is still alive in the game, otherwise false
	 */
	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * Sets if the player is still alive in the game
	 * 
	 * @param isAlive
	 *            - true if the player is still alive otherwise false
	 */
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	/**
	 * Returns the speed of the gameObject
	 * 
	 * @return - The speed as a double
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Sets this gameObjects speed
	 * 
	 * @param speed
	 *            - sets this gamObjects speed as
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Sets the players image depending on the amount of damage the player has
	 * taken This is the only indicator to how damaged the player is
	 */
	public void showDamage() {
		if (this.getLives() > 75)
			this.setImage(image);
		if (this.getLives() < 75)
			this.setImage(ship[0]);
		if (this.getLives() < 50)
			this.setImage(ship[2]);
		if (this.getLives() < 25)
			this.setImage(ship[1]);
		if (this.isAlive == false)
			this.setImage(ship[3]);
	}

}
