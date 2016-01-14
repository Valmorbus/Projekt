package projectv2;

import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Bullet extends GameObject {
	double directionX;
	double directionY;
	private Ellipse ellipse;
	private int damage;

	Bullet() {
		this.ellipse = new Ellipse(15, 2.5);
		ellipse.setFill(Color.RED);
		ellipse.setEffect(new Glow(0));
		ellipse.setEffect(new Bloom(0));
		this.damage = 5;
	}

	public Bullet(double directionX, double directionY, double rotate) {
		super();
		this.ellipse = new Ellipse(15, 2.5);
		this.ellipse.setTranslateX(directionX);
		this.ellipse.setTranslateY(directionY);
		this.ellipse.setRotate(rotate);
		ellipse.setFill(Color.RED);
		ellipse.setEffect(new Glow(0));
		ellipse.setEffect(new Bloom(0));
		this.damage = 5;

	}

	public double getDirectionX() {
		return directionX;
	}

	public void setDirectionX(double directionX) {
		this.directionX = directionX;
	}

	public double getDirectionY() {
		return directionY;
	}

	public void setDirectionY(double directionY) {
		this.directionY = directionY;
	}

	public Ellipse getEllipse() {
		return ellipse;
	}

	public void setEllipse(Ellipse e) {
		this.ellipse = e;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	

}
