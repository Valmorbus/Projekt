package projectv2;



import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Bullet {
	double directionX; 
	double directionY;
	Circle c = new Circle(15, 15, 15);
	
	Bullet(){
		c.setFill(Color.RED);
		
	}

	public Bullet(double directionX, double directionY) {
		super();
		this.directionX = directionX;
		this.directionY = directionY;
		
		
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

	public Circle getR() {
		return c;
	}

	public void setR(Circle c) {
		this.c = c;
	}
	
	
	

}
