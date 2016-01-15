package projectv2;

import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * A class which extends the {@link Ellipse} class. This class acts as projectile in
 * the game.
 * 
 * @author Simon Borgström
 * @version 1.0
 *
 */
public class Bullet extends Ellipse implements GameObject {
	private int damage;

	/**
	 * Default constructor of bullet. It already has predetermined RadiusX,
	 * RadiusY, Fill and effects. It also has a default damage value for the
	 * game.
	 */
	Bullet() {
		this.setRadiusX(15);
		this.setRadiusY(2.5);
		this.setFill(Color.RED);
		this.setEffect(new Glow(0));
		this.setEffect(new Bloom(0));
		this.damage = 5;
	}

	/**
	 * Constructs a Bullet object for the game with parameters to set its
	 * TranslateX, TranslateY and Rotate. It already has predetermined RadiusX,
	 * RadiusY, Fill and effects. It also has a default damage value for the
	 * game.
	 * 
	 * @param directionX
	 *            - Sets the TranslateX of the Bullet
	 * @param directionY
	 *            - Sets the TranslateY of the Bullet
	 * @param rotate
	 *            - Sets the Rotate of the bullet
	 */
	public Bullet(double directionX, double directionY, double rotate) {
		super();
		this.setRadiusX(15);
		this.setRadiusY(2.5);
		this.setTranslateX(directionX);
		this.setTranslateY(directionY);
		this.setRotate(rotate);

		this.setFill(Color.RED);
		this.setEffect(new Glow(0));
		this.setEffect(new Bloom(0));
		this.damage = 5;

	}

	/**
	 * Returns the damage value of the bullet. Default value of 5.
	 * 
	 * @return - integer damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * Sets the damage of the Bullet
	 * 
	 * @param damage
	 *            - integer damage
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

}
