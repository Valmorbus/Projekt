package projectv2;

import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class Bullet extends GameObject {
	double directionX;
	double directionY;
	Circle c = new Circle(15, 15, 50);
	private Ellipse e = new Ellipse(15, 2.5);

	Bullet() {
		e.setFill(Color.RED);
		e.setEffect(new Glow(0));
		e.setEffect(new Bloom(0));

	}

	public Bullet(double directionX, double directionY) {
		super();
		this.directionX = directionX;
		this.directionY = directionY;

	}
	public Bullet(double directionX, double directionY, double rotate) {
		super();
		this.e.setTranslateX(directionX);
		this.e.setTranslateY(directionY);
		this.e.setRotate(rotate);
		e.setFill(Color.RED);
		e.setEffect(new Glow(0));
		e.setEffect(new Bloom(0));

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

	public Ellipse getR() {
		return e;
	}

	public void setR(Ellipse e) {
		this.e = e;
	}

}
