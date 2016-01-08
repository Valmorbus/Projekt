package projectv2;

import javafx.scene.image.ImageView;

public abstract class GameObject extends ImageView{
	private double posX, posY;
	private int networkId;
	private double rotate;
	private double speed;
	private short owningPlayer;
	
	public GameObject(){
		
	}

	public GameObject(double posX, double posY, double rotate, double speed) {
		super();
		this.posX = posX;
		this.posY = posY;
		//this.rotate = rotate;
		this.speed = speed;
		this.setRotate(rotate);
		this.setTranslateX(posX);
		this.setTranslateY(posY);
		
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posyY) {
		this.posY = posyY;
	}

	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}
/*
	public double getRotate() {
		return rotate;
	}

	public void setRotate(double rotate) {
		this.rotate = rotate;
	}*/

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public short getOwningPlayer() {
		return owningPlayer;
	}

	public void setOwningPlayer(short owningPlayer) {
		this.owningPlayer = owningPlayer;
	}

	protected void update() {
		// räkna position
		// updatera när något särskilt händer
	}

}
