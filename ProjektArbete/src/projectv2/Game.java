package projectv2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.GameClient;
import net.GameServer;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;

public class Game extends Thread {

	private Timeline playerLoop;
	private Bullet bullet;
	private List<Bullet> bulletArray = new LinkedList<Bullet>();
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
	private MediaPlayer effectPlayer; // = new MediaPlayer(null);
	private Stage primaryStage;
	private String ipAdress = "localhost";
	
	private final Rectangle2D GAME_MAP = Screen.getPrimary().getBounds();
	private final double SCREEN_WIDTH = GAME_MAP.getWidth();
	private final double SCREEN_HEIGHT = GAME_MAP.getHeight();

	public Game(boolean runServer, String iplogin) {
		startServerClient(runServer, iplogin);
	}

	public Game(String ip) {
		this.ipAdress = ip;
	}

	// k�r server fr�n main genom game konstruktor?
	private synchronized void startServerClient(boolean server, String iplogin) {
		new Thread(this).start();
		this.ipAdress = iplogin;
		if (server) {

			gs = new GameServer(this);
			gs.start();
		}
	}

	public synchronized void runGame(Stage primary, String login, double posx, double posy) {

		primaryStage = primary;
		System.out.println("k�r h�r");
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
			if (gs != null) {
				Packet01Disconnect packet = new Packet01Disconnect(player.getName());
				packet.writeData(gc);
			}
		});
		Packet00Login loginPacket = new Packet00Login(player.getName(), player.getTranslateX(), player.getTranslateY(),
				player.getRotate());
		if (gs != null) {
			gs.addConnection((PlayerMP) player, loginPacket);
		}
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("ESCAPE"));

		if (login.equals(null) || login.equals(""))
			ipAdress = "localhost";

		gc = new GameClient(this, ipAdress);
		gc.start();

		addPlayer(player);
		loginPacket.writeData(gc);
		updateLocalGraphics();
		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 60), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!getGameObjects().isEmpty()) {
					playerMovements();

					for (int i = 0; i < getGameObjects().size(); i++) {
						lost(getGameObjects().get(i));
					}
				}
				if (bullet != null) {
					checkHit();
				}

			}
		}));
		playerLoop.setCycleCount(-1);
		playerLoop.play();
	}

	private void checkHit() {
		if (!bulletArray.isEmpty()) {
			for (int i = 0; i < bulletArray.size(); i++) {
				if (bulletArray.get(i).getEllipse().getTranslateX() <= -100
						|| bulletArray.get(i).getEllipse().getTranslateX() >= scene.getWidth() + 100
						|| bulletArray.get(i).getEllipse().getTranslateY() <= -100
						|| bulletArray.get(i).getEllipse().getTranslateY() >= scene.getHeight() + 100) {
					removeBullet(bulletArray.get(i));
				}
				for (int j = 0; j < getGameObjects().size(); j++) {
					if (bulletArray.get(i).getEllipse().getBoundsInParent()
							.intersects(getGameObjects().get(j).getBoundsInParent())) {
						System.out.println("hit " + getGameObjects().get(j).getName());
						gameObjects.get(j).setLives(getGameObjects().get(j).getLives() - 15);
						System.out.println(getGameObjects().get(j).getLives());
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

	private void moveBullet(Bullet bullet) {
		Platform.runLater(() -> {
			double bulletX = bullet.getEllipse().getTranslateX();
			double bulletY = bullet.getEllipse().getTranslateY();
			bullet.getEllipse().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getEllipse().getRotate())) * 25);
			bullet.getEllipse().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getEllipse().getRotate())) * 25);
		});

	}

	private void moveBulletFirst(Bullet bullet) {
		double bulletX = bullet.getEllipse().getTranslateX();
		double bulletY = bullet.getEllipse().getTranslateY();
		bullet.getEllipse().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getEllipse().getRotate())) * 100);
		bullet.getEllipse().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getEllipse().getRotate())) * 100);
	}

	private void playerMovements() {
		scene.setOnKeyPressed(e -> {

			switch (e.getCode()) {
			case RIGHT: {
				movePlayer(5, 10);
				break;
			}
			case LEFT: {
				movePlayer(-5, 10);
				break;
			}
			case UP: {
				movePlayer(20);
				break;
			}
			case SPACE: {
				shoot();
				break;
			}
			}
		});
	}

	private void movePlayer(int turn, double speed) {

		Platform.runLater(() -> {
			playEffect(soundEffects[2]);
			double x = player.getTranslateX();
			double y = player.getTranslateY();
			player.setRotate(player.getRotate() + turn);
			player.setTranslateX(x + Math.cos(Math.toRadians(player.getRotate())) * speed);
			player.setTranslateY(y + Math.sin(Math.toRadians(player.getRotate())) * speed);
		});
		playerOutOfBounds();
		update(speed);
	}

	private void movePlayer(double speed) {

		Platform.runLater(() -> {
			playEffect(soundEffects[2]);
			double x = player.getTranslateX();
			double y = player.getTranslateY();
			player.setTranslateX(x + Math.cos(Math.toRadians(player.getRotate())) * speed);
			player.setTranslateY(y + Math.sin(Math.toRadians(player.getRotate())) * speed);
		});
		playerOutOfBounds();
		update(speed);
	}

	private void shoot() {
		Platform.runLater(() -> {
			if (bulletArray.size() <= 10) {
				playEffect(soundEffects[1]);
				bullet = new Bullet();
				// root.getChildren().add(bullet.getEllipse());
				bullet.getEllipse().setTranslateX(player.getTranslateX() + (player.getImage().getWidth()) / 2);
				bullet.getEllipse().setTranslateY(player.getTranslateY() + (player.getImage().getHeight() / 2));
				bullet.getEllipse().setRotate(player.getRotate());
				bulletArray.add(bullet);

				moveBulletFirst(bullet);
				updateShoots(bullet);
			}
		});

	}

	public void addPlayer(PlayerMP player2) {

		getGameObjects().add(player2);

		System.out.println("gameobjects " + getGameObjects().size() + " player id " + player2.getName());
		System.out.println(player2.ipAdress + " " + player2.port);
		System.out.println("name: " + player2.getName());
		//if (!this.player.getName().equalsIgnoreCase(player2.getName()));
		//player2.setImage(new Image("/secondship.png"));

		addLocalPlayer(player2);
	}

	public void addLocalPlayer(PlayerMP player) {
		Platform.runLater(() -> {
			System.out.println("addlocalplayer");
			root.getChildren().add(player);
			Label playerLabel = new Label(player.getName());
			playerLabel.setTextFill(Color.RED);
			root.getChildren().add(playerLabel);
			
			playerLabel.setTranslateX(player.getTranslateX());
			playerLabel.setTranslateY(player.getTranslateY());

		});
	}

	// synchronize?
	public synchronized void update(double speed) {
		Platform.runLater(() -> {
			player.setRotate(player.getRotate());
			player.setTranslateX(player.getTranslateX());
			player.setTranslateY(player.getTranslateY());
			player.setSpeed(speed);

			Packet02Move packet = new Packet02Move(player.getName(), player.getTranslateX(), player.getTranslateY(),
					player.getRotate());
			packet.writeData(gc);

			// temporary position
			// removeExplosions();
		});
	}

	private void updateShoots(Bullet bullet) {
		Packet03Shoot packet = new Packet03Shoot(null, bullet.getEllipse().getTranslateX(),
				bullet.getEllipse().getTranslateY(), bullet.getEllipse().getRotate());
		packet.writeData(gc);
		removeExplosions();
	}

	// synchronize?
	private void updateLocalGraphics() {
		// Platform.runLater(()->{
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 1), ev -> {

			if (!explosions.isEmpty())
				removeExplosions();

		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		// });

	}

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

	public void updatePlayers(String userName, double x, double y, double rotate) {
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
				System.out.println(p.toString() + " " + gameObjects.get(PlayerIndex));
				break;
			}
			index++;
		}
		return index;
	}

	public void removePlayerMP(String username) {
		int index = 0;
		for (PlayerMP p : getGameObjects()) {
			if (p instanceof PlayerMP && p.getName().equals(username)) {
				break;
			}
			index++;
		}
		Platform.runLater(() -> {
			root.getChildren().remove(getRootPlayer(username));

		});
		getGameObjects().remove(index);

	}

	public void updateShoots(double x, double y, double rotate) {
		Bullet bullet = new Bullet(x, y, rotate);

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
		effectPlayer = new MediaPlayer(media);
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

	private void lost(Player player) {
		if (player.getLives() <= 0) {
			System.out.println(player.getName());
			removePlayerMP(player.getName());

			// Packet01Disconnect packet = new
			// Packet01Disconnect(player.getName());
			// packet.writeData(gc);
			// System.exit(0);
			// Platform.exit();

		}
	}

	private void Lost() {
		primaryStage.close();
	}
	
	private double setStartRotate(double x, double y){
		double rotate = 0;
		if (x > 500)
			rotate = -90;
		if (x <500)
			rotate =+90;
		if (y > 500)
			rotate = -90;
		if (y <500)
			rotate =+90;
		return rotate;
	}

	public synchronized ArrayList<PlayerMP> getGameObjects() {
		return gameObjects;
	}

	public synchronized void setGameObjects(ArrayList<PlayerMP> gameObjects) {
		this.gameObjects = gameObjects;
	}

}
