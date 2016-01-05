package projectv2;

public abstract class GameObject {
	private double posX, posyY;
	private int networkId;
	private double rotate;
	private double speed;
	private short owningPlayer;

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosyY() {
		return posyY;
	}

	public void setPosyY(double posyY) {
		this.posyY = posyY;
	}

	public int getNetworkId() {
		return networkId;
	}

	public void setNetworkId(int networkId) {
		this.networkId = networkId;
	}

	public double getRotate() {
		return rotate;
	}

	public void setRotate(double rotate) {
		this.rotate = rotate;
	}

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
