package spacegame;

import java.net.InetAddress;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Ellipse;

/**
 * A multiplayer instance of the {@link Player} class which it extends. The
 * purpose is mainly to add InetAddress and port so it can be connected to a
 * server via client.
 * 
 * @author Simon Borgström
 * @version 1.0
 * @see ImageView
 * @see Player
 */
public class PlayerMP extends Player {

	private InetAddress ipAdress;
	private int port;
	private int ammo;
	private Ellipse hitbox;

	/**
	 * Constructs an PlayerMP with a specified name, TranslateX, TranslateY,
	 * Rotate, double Speed, InetAddress and a port. The PlayerMP has an
	 * automatically assigned hitbox which covers the image.
	 * 
	 * @param name
	 *            - The name of the PlayerMP
	 * @param posX
	 *            - The TranslateX of the PlayerMP
	 * @param posY
	 *            - The TranslateY of the PlayerMP
	 * @param rotate
	 *            - The Rotate of the PlayerMP
	 * @param speed
	 *            - The speed of the PlayerMP
	 * @param ipAdress
	 *            - The InetAddress of the PlayerMP
	 * @param port
	 *            - The port of the PlayerMP
	 */
	public PlayerMP(String name, double posX, double posY, double rotate, double speed, InetAddress ipAdress,
			int port) {
		super(name, posX, posY, rotate, speed); // , ipAdress, port);
		this.image = new Image("/Untitled.png");
		this.setImage(image);
		this.ipAdress = ipAdress;
		this.port = port;
		this.ammo = 8;
		this.isAlive = true;

		hitbox = new Ellipse(image.getWidth() / 2.5, image.getHeight() / 2.5);
		hitbox.centerXProperty().bind(this.translateXProperty().add(image.getWidth() / 2));
		hitbox.centerYProperty().bind(this.translateYProperty().add(image.getHeight() / 2));
		hitbox.rotateProperty().bind(this.rotateProperty());

	}

	/**
	 * Returns the amount of ammo the PlayerMP has
	 * 
	 * @return - integer ammo
	 */
	public int getAmmo() {
		return ammo;
	}

	/**
	 * Sets the amount of ammo the PlayerMP has
	 * 
	 * @param ammo
	 *            - integer
	 */
	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

	/**
	 * Sets the InetAddress of the PlayerMP
	 * 
	 * @return - The InetAddress of the PlayerMP
	 */
	public InetAddress getIpAdress() {
		return ipAdress;
	}

	/**
	 * Sets the InetAddress of the PlayerMP
	 * 
	 * @param ipAdress
	 *            - InetAddress
	 */
	public void setIpAdress(InetAddress ipAdress) {
		this.ipAdress = ipAdress;
	}

	/**
	 * Returns the port of the PlayerMP
	 * 
	 * @return - port as an integer
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port of the playerMP
	 * 
	 * @param port
	 *            - integer
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * returns the hitbox for playerMP as default the hitbox is binded to the
	 * PlayerMPs Image
	 * 
	 * @return - Ellipse hitbox
	 */
	public Ellipse getHitbox() {
		return hitbox;
	}

	/**
	 * Sets a new hitbox for the PlayerMP Use with caution since the default
	 * hitbox is binded with the PlayerMPs Image.
	 * 
	 * @param hitbox
	 *            - Ellipse to make the new hitbox.
	 */
	public void setHitbox(Ellipse hitbox) {
		this.hitbox = hitbox;
	}

}
