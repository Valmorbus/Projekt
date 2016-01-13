package projectv2;

import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.GameClient;
import net.GameServer;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;
import packets.Packet04Hit;

public class Game {

	private Timeline playerLoop;
	private Bullet bullet;
	private ArrayList<Bullet> bulletArray = new ArrayList<Bullet>();
	private ArrayList<PlayerMP> gameObjects = new ArrayList<PlayerMP>();
	private ArrayList<Ellipse> explosions = new ArrayList<Ellipse>();
	private Scene scene;
	private PlayerMP player;
	private Pane root;
	private GameClient gc;
	private GameServer gs;

	private Media music = new Media(getClass().getResource("/Music.mp3").toString());
	private Media[] soundEffects = { new Media(getClass().getResource("/Explosion.mp3").toString()),
			new Media(getClass().getResource("/Laser.mp3").toString()),
			new Media(getClass().getResource("/Rocket.mp3").toString()) };
	private MediaPlayer musicPlayer = new MediaPlayer(music);
	private Stage primaryStage;
	private String ipAdress = "localhost";

	private ArrayList<Text> playerNames = new ArrayList<Text>();

	/**
	 * Sätter spelplanen till maxyta för skärmen. Skulle det vara så att
	 * spelarna har olika upplösning kommer det att bli fel för den med sämst
	 * upplösning. Detta går att åtgärda med att ändra width till 1920 och
	 * height till 1080, men skulle leda till att spelare med sämre upplösning
	 * kan åka så långt att de inte ser sin spelare. går även att ändra till
	 * mindre för att anpassa, men detta skulle belöna de som köpt en sämre
	 * dator vilket jag vägrar.
	 */
	private final Rectangle2D GAME_MAP = Screen.getPrimary().getBounds();
	private final double SCREEN_WIDTH = GAME_MAP.getWidth();
	private final double SCREEN_HEIGHT = GAME_MAP.getHeight();

	// Att göra
	// hit Packet + death
	// player collition
	// se till att allt stängs ner korrekt

	/**
	 * Runs a dedicated server
	 * 
	 * @param runServer
	 * 
	 */
	public Game(boolean runServer) {
		startServer(runServer);
	}

	/**
	 * Runs the game and gameclient
	 * 
	 * @param ip
	 *            - The ipadress on witch the client will connect. Default
	 *            "localhost"
	 */
	public Game(String ip) {
		this.ipAdress = ip;
	}

	// kör server från main genom game konstruktor
	private synchronized void startServer(boolean server) {
		if (server) {
			gs = new GameServer(this);
			gs.start();
		}
	}

	public synchronized void runGame(Stage primary, String login, double posx, double posy) {

		primaryStage = primary;
		String userName = login;
		root = new Pane();

		double r = setStartRotate(posx, posy);
		player = new PlayerMP(userName, posx, posy, r, 0, null, 0);

		root.setStyle("-fx-background-color: black;");
		scene = new Scene(root);
		primaryStage.setTitle(userName);
		musicPlayer.play();
		musicPlayer.setVolume(0.5);

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
			// ska skicka ett disconnectpavket när man stänger ner fönstret
			Packet01Disconnect packet = new Packet01Disconnect(player.getName());
			packet.writeData(gc);
			musicPlayer.stop();
			playerLoop.stop();
			// gc.setRunning(false);
			try {
				gc.shotDownClient();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			/*
			 * if (gc.isAlive()) try { gc.join(); } catch (Exception e1) { //
			 * TODO Auto-generated catch block e1.printStackTrace(); }
			 */
			System.out.println(gc.isAlive());
			if (gs != null) {
				try {
					gs.shotDownServer();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// gs.setRunning(false);

			}
			// kolla om det går att stänga ner main på något ev sätt
		});
		Packet00Login loginPacket = new Packet00Login(player.getName(), player.getTranslateX(), player.getTranslateY(),
				player.getRotate());
		if (gs != null) {
			gs.addConnection((PlayerMP) player, loginPacket);
		}
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("ESCAPE"));

		if (ipAdress.equals(null) || ipAdress.equals(""))
			ipAdress = "localhost";
		gc = new GameClient(this, ipAdress);
		gc.start();
		addPlayer(player);
		loginPacket.writeData(gc);
		updateLocalGraphics();
		gameLoop();

	}

	private void gameLoop() {
		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 60), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!getGameObjects().isEmpty()) {
					playerMovements();
					updateLabels();
					if (player.isAlive())
						movePlayer(0.3);
					playerDied();
				}
				if (!bulletArray.isEmpty()) {
					checkHit();
				}
			}
		}));
		playerLoop.setCycleCount(-1);
		playerLoop.play();
	}

	private synchronized void updateLocalGraphics() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 2), ev -> {
			if (!explosions.isEmpty())
				removeExplosions();
			if (player.getAmmo() < 8)
				player.setAmmo(player.getAmmo() + 1);
			for (PlayerMP p : gameObjects) {
				p.setEffect(new Glow(0.3));
			}
			playerCollition();
			playerWon();
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	private synchronized void updateLabels() {
		for (Text name : playerNames) {
			for (PlayerMP player : gameObjects) {
				if (name.getText().equals(player.getName())) {
					name.setTranslateX(player.getTranslateX() + 25);
					name.setLayoutY(player.getTranslateY() + 100);
					name.setRotate(player.getRotate());
				}
			}
		}
	}

	private void playerMovements() {

		scene.setOnKeyPressed(e -> {

			switch (e.getCode()) {
			case RIGHT: {
				if (player.isAlive())
					movePlayer(5, 10);
				break;
			}
			case LEFT: {
				if (player.isAlive())
					movePlayer(-5, 10);
				break;
			}
			case UP: {
				if (player.isAlive())
					movePlayer(20);
				break;
			}
			case SPACE: {
				if (player.isAlive())
					shoot();
				break;
			}
			default:
				break;
			}
		});
	}

	private void movePlayer(int turn, double speed) {

		Platform.runLater(() -> {
			// playEffect(soundEffects[2]);
			double x = player.getTranslateX();
			double y = player.getTranslateY();
			player.setRotate(player.getRotate() + turn);
			player.setTranslateX(x + Math.cos(Math.toRadians(player.getRotate())) * speed);
			player.setTranslateY(y + Math.sin(Math.toRadians(player.getRotate())) * speed);

		});
		playerOutOfBounds();
		updateMovementsToServer(speed);
	}

	private void movePlayer(double speed) {

		Platform.runLater(() -> {
			// playEffect(soundEffects[2]);
			double x = player.getTranslateX();
			double y = player.getTranslateY();
			player.setTranslateX(x + Math.cos(Math.toRadians(player.getRotate())) * speed);
			player.setTranslateY(y + Math.sin(Math.toRadians(player.getRotate())) * speed);
			// ev parametter av speed för damage
		});
		playerOutOfBounds();
		updateMovementsToServer(speed);
	}

	private synchronized void shoot() {
		if (player.getAmmo() >= 1)
			player.setAmmo(player.getAmmo() - 1);
		Platform.runLater(() -> {
			if (!(player.getAmmo() <= 0)) {
				playEffect(soundEffects[1]);
				bullet = new Bullet();
				root.getChildren().add(bullet.getEllipse());
				bullet.getEllipse().setTranslateX(player.getTranslateX() + (player.getImage().getWidth()) / 2);
				bullet.getEllipse().setTranslateY(player.getTranslateY() + (player.getImage().getHeight() / 2));
				bullet.getEllipse().setRotate(player.getRotate());
				bulletArray.add(bullet);

				moveBulletFirst(bullet);
				updateShootsToServer(bullet);
				removeBullet(bullet);
			}
		});
	}
	
	private void moveBullet(Bullet bullet) {
	
		double bulletX = bullet.getEllipse().getTranslateX();
		double bulletY = bullet.getEllipse().getTranslateY();
		bullet.getEllipse().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getEllipse().getRotate())) * 25);
		bullet.getEllipse().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getEllipse().getRotate())) * 25);
	
	}

	private void moveBulletFirst(Bullet bullet) {
		double bulletX = bullet.getEllipse().getTranslateX();
		double bulletY = bullet.getEllipse().getTranslateY();
		bullet.getEllipse().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getEllipse().getRotate())) * 30);
		bullet.getEllipse().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getEllipse().getRotate())) * 30);
	}
	
	private void checkHit() {
		if (!bulletArray.isEmpty()) {
			for (int i = 0; i < bulletArray.size(); i++) {
				if (bulletArray.get(i).getEllipse().getTranslateX() <= -SCREEN_WIDTH - 100
						|| bulletArray.get(i).getEllipse().getTranslateX() >= SCREEN_WIDTH + 100
						|| bulletArray.get(i).getEllipse().getTranslateY() <= -SCREEN_HEIGHT - 100
						|| bulletArray.get(i).getEllipse().getTranslateY() >= SCREEN_HEIGHT + 100) {
					removeBullet(bulletArray.get(i));
				}
				for (int j = 0; j < getGameObjects().size(); j++) {
					if (bulletArray.get(i).getEllipse().getBoundsInParent()
							.intersects(getGameObjects().get(j).getHitbox().getBoundsInParent())) {
						System.out.println("hit " + getGameObjects().get(j).getName());
						damage(gameObjects.get(j), bulletArray.get(i).getDamage());
						removeBullet(bulletArray.get(i));
						playEffect(soundEffects[0]);
					}
				}
				moveBullet(bulletArray.get(i));
			}
		}
	}

	
	private void removeBullet(Bullet bullet) {
		createExplosion(bullet);
		Platform.runLater(() -> {
			root.getChildren().remove(bullet.getEllipse());
			bulletArray.remove(bullet);
		});
	}

	private void createExplosion(Bullet bullet) {
		Ellipse ellipse = new Ellipse(25, 25);
		ellipse.setFill(Color.YELLOW);
		ellipse.setStroke(Color.WHITE);
		ellipse.setEffect(new Glow(0));
		ellipse.setEffect(new Bloom(0));
		Platform.runLater(() -> {
			root.getChildren().add(ellipse);
			ellipse.setTranslateX(bullet.getEllipse().getTranslateX());
			ellipse.setTranslateY(bullet.getEllipse().getTranslateY());
			explosions.add(ellipse);
		});
	}

	private void removeExplosions() {
		if (!explosions.isEmpty()) {
			for (Ellipse e : explosions) {
				root.getChildren().remove(e);
			}
			explosions.removeAll(explosions);
		}
	}

	public void addPlayer(PlayerMP player2) {
		getGameObjects().add(player2);
		addLocalPlayer(player2);
	}

	private void addLocalPlayer(PlayerMP player) {
		Platform.runLater(() -> {
			root.getChildren().add(player);
			Text playerLabel = new Text(player.getName());
			playerLabel.setFill(Color.RED);
			root.getChildren().add(playerLabel);
			playerNames.add(playerLabel);
			player.setLives(100);

		});
	}

	// synchronize?
	private synchronized void updateMovementsToServer(double speed) {
		Platform.runLater(() -> {
			player.setRotate(player.getRotate());
			player.setTranslateX(player.getTranslateX());
			player.setTranslateY(player.getTranslateY());
			player.setSpeed(speed);

			Packet02Move packet = new Packet02Move(player.getName(), player.getTranslateX(), player.getTranslateY(),
					player.getRotate());
			packet.writeData(gc);
		});
	}

	private synchronized void updateShootsToServer(Bullet bullet) {
		moveBulletFirst(bullet);
		Packet03Shoot packet = new Packet03Shoot(null, bullet.getEllipse().getTranslateX(),
				bullet.getEllipse().getTranslateY(), bullet.getEllipse().getRotate());
		packet.writeData(gc);
		removeExplosions();
	}
	// synchronize?
	private int getPlayerMPIndex(String username) {
		int index = 0;// -1
		for (PlayerMP p : getGameObjects()) {
			if (p instanceof PlayerMP && p.getName().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	public synchronized void updatePlayers(String userName, double x, double y, double rotate) {
		int index = getPlayerMPIndex(userName);
		Platform.runLater(() -> {
			this.getGameObjects().get(index).setTranslateX(x);
			this.getGameObjects().get(index).setTranslateY(y);
			this.getGameObjects().get(index).setRotate(rotate);
		});
	}

	public int getRootPlayer(String username) {
		int PlayerIndex = getPlayerMPIndex(username);
		int index = 0;
		for (Node p : root.getChildren()) {
			if (p.equals(getGameObjects().get(PlayerIndex))) {
				break;
			}
			index++;
		}
		return index;
	}

	public synchronized void removePlayerMP(String username) {
		Platform.runLater(() -> {
			int index = 0;
			for (PlayerMP p : getGameObjects()) {
				if (p instanceof PlayerMP && p.getName().equals(username)) {
					break;
				}
				index++;
			}
			int rootIndex = getRootPlayer(username);
			root.getChildren().remove(rootIndex);
			root.getChildren().remove(rootIndex); // tar bort playername text
			gameObjects.get(index).setHitbox(null);
			getGameObjects().remove(index);
		});
	}

	public synchronized void updateShoots(double x, double y, double rotate) {
		Bullet bullet = new Bullet(x, y, rotate);
		moveBulletFirst(bullet);
		bulletArray.add(bullet);
		Platform.runLater(() -> {
			root.getChildren().add(bullet.getEllipse());
			bullet.getEllipse().setTranslateX(x);
			bullet.getEllipse().setTranslateY(y);
			bullet.getEllipse().setRotate(rotate);
			playEffect(soundEffects[1]);

			removeExplosions();
		});
	}

	private void playEffect(Media media) {
		MediaPlayer effectPlayer = new MediaPlayer(media);
		effectPlayer.setVolume(0.3);
		effectPlayer.play();
	}

	private void playerOutOfBounds() {
		if (player.getTranslateX() > SCREEN_WIDTH + 25) {
			player.setTranslateX(-25);
		} else if (player.getTranslateX() < -25) {
			player.setTranslateX(SCREEN_WIDTH + 25);
		} else if (player.getTranslateY() > SCREEN_HEIGHT + 25) {
			player.setTranslateY(-25);
		} else if (player.getTranslateY() < -25) {
			player.setTranslateY(SCREEN_HEIGHT + 25);
		}
	}

	private void playerDied() {
		if (!player.isAlive()) {
			Label gameOver = new Label("You lost");
			gameOver.setTextFill(Color.RED);
			gameOver.setFont(new Font(50));
			gameOver.setTranslateX(SCREEN_WIDTH / 2);
			gameOver.setTranslateY(SCREEN_HEIGHT / 2);
			root.getChildren().add(gameOver);
			playerLoop.stop();
		}
	}

	private void playerWon() {
		int playersDestroyed = 0;
		if (getGameObjects().size() >= 2) {
			for (PlayerMP p : gameObjects) {
				if (!p.isAlive())
					playersDestroyed++;
			}
			if (player.isAlive() && playersDestroyed + 1 == getGameObjects().size()) {
				Label won = new Label("You Won");
				won.setTextFill(Color.RED);
				won.setFont(new Font(50));
				won.setTranslateX(SCREEN_WIDTH / 2);
				won.setTranslateY(SCREEN_HEIGHT / 2);
				root.getChildren().add(won);
			}
		}
	}

	private double setStartRotate(double x, double y) {
		double rotate = 0;
		if (x > 500)
			rotate = -90;
		if (x < 500)
			rotate = +90;
		if (y > 500)
			rotate = -90;
		if (y < 500)
			rotate = +90;
		return rotate;
	}

	private void playerCollition() {
		for (PlayerMP player2 : gameObjects) {
			if (!player.getName().equals(player2.getName()))
				if (player.getBoundsInParent().intersects(player2.getBoundsInParent())) {
					if (player2.getTranslateX() > player.getTranslateX())
						player.setTranslateX(player.getTranslateX() - 15);
					else
						player.setTranslateX(player.getTranslateX() + 15);
					if (player2.getTranslateY() > player.getTranslateY())
						player.setTranslateY(player.getTranslateY() - 15);
					else
						player.setTranslateY(player.getTranslateY() + 15);
					damage(player2, 10);
				}
		}
	}

	public void damage(PlayerMP player, int damage) {
		Packet04Hit packet = new Packet04Hit(player.getName(), damage);
		packet.writeData(gc);
	}

	public void damagePlayer(String username, int damage) {
		System.out.println(username + " takes " + damage + "damage");
		int index = getPlayerMPIndex(username);
		getGameObjects().get(index).setLives(getGameObjects().get(index).getLives() - damage);
		if (getGameObjects().get(index).isAlive())
			getGameObjects().get(index).setEffect(new Glow(1));
		getGameObjects().get(index).showDamage();

		if (getGameObjects().get(index).getLives() <= 0) {
			getGameObjects().get(index).setAlive(false);
			getGameObjects().get(index).showDamage();
		}
	}

	public synchronized ArrayList<PlayerMP> getGameObjects() {
		return gameObjects;
	}

	public synchronized void setGameObjects(ArrayList<PlayerMP> gameObjects) {
		this.gameObjects = gameObjects;
	}

}
