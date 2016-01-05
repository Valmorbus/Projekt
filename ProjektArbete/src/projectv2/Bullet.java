package projectv2;



import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class Bullet {
	double directionX; 
	double directionY;
	Circle c = new Circle(15, 15, 50);
	Ellipse e = new Ellipse(15, 2.5);
	
	Bullet(){
		e.setFill(Color.RED);
		
		
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

	public Ellipse getR() {
		return e;
	}

	public void setR(Ellipse e) {
		this.e = e;
	}
	
	
	

}
