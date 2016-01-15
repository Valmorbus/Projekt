package projectv2;

import javafx.scene.image.ImageView;

/**
 * An abstract game Object to be extended to the games objects which are to be
 * placed in the game. Extends ImageView so that the object can be directly
 * added to a Pane.
 * All parts of the GameObject class are not yet implemented in the game
 * 
 * @author Simon Borgström
 *
 */
public abstract class GameObject extends ImageView {
	private double posX, posY;
	private int networkId;
	private double speed;
	private short owningPlayer;

	/**
	 * Default constructor of a game object to be inherited
	 */
	public GameObject() {

	}

	/**
	 * Constructor of game object to be instantiated with set X,Y, Rotate and
	 * speed parameters
	 * 
	 * @param posX
	 *            - The TranslateX of the Game Object
	 * @param posY
	 *            - The TranslateY of the Game Object
	 * @param rotate
	 *            - The Rotate of the Game Object
	 * @param speed
	 *            - - The speed of the Game Object
	 */
	public GameObject(double posX, double posY, double rotate, double speed) {
		super();
		this.speed = speed;
		this.posX = posX;
		this.posY = posY;
		this.setRotate(rotate);
		this.setTranslateX(posX);
		this.setTranslateY(posY);

	}

/**
 * Sets the position on X coordinate 
 * @param posX - as a double
 */
	public void setPosX(double posX) {
		this.posX = posX;
	}
	/**
	 * Sets the position on Y coordinate 
	 * @param posY - as a double
	 */
	public void setPosY(double posyY) {
		this.posY = posyY;
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

}
