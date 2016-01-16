package spacegame;

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
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;
import packets.Packet04Hit;

/**
 * The Game class sets the game background and creates a local player on that background.
 * It contains all the elements for running a network client for this specific multiplayer game. 
 * The game class is mainly used to run the graphic contents of this game, as well as input from local player,
 * which is then forwarded to the client.  
 * At the moment it only works when connected to a server, since, for example bullets are spawned by the server. 
 * 
 * @author Simon Borgström
 * @version 1.0
 * @since 2015-01-15
 *
 */

public class Game {

	private Timeline playerLoop;
	private Timeline timeline;
	private Bullet bullet;
	private ArrayList<Bullet> bulletArray = new ArrayList<Bullet>();
	private ArrayList<PlayerMP> playerArray = new ArrayList<PlayerMP>();
	private ArrayList<Ellipse> explosions = new ArrayList<Ellipse>();
	private ArrayList<Text> playerNames = new ArrayList<Text>();
	private Scene scene;
	private PlayerMP player;
	private Pane root;
	private GameClient gc;
	private Media music;
	private Media[] soundEffects = { new Media(getClass().getResource("/Explosion.mp3").toString()),
			new Media(getClass().getResource("/Laser.mp3").toString()),
			new Media(getClass().getResource("/Rocket.mp3").toString()) };
	private MediaPlayer musicPlayer;
	private String ipAdress;
	
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
	
/**
 * Constructs a Game instance. This is the only way to start the game. 
 * Requires an ip adress to with the client will be connected, an username for the player
 * and a starting position of said player represented by the posx and posy values. 
 * 
 * @param ip - The IP-address to which the client are to connect
 * @param userName - The username of the player
 * @param posx - the X coordinate of the players start position
 * @param posy - the Y coordinate of the players start position
 */
	public Game(String ip, String userName, double posx, double posy) {
		this.ipAdress = ip;
		double r = setStartRotate(posx, posy);
		this.player = new PlayerMP(userName, posx, posy, r, 0, null, 0);   
		this.music = new Media(getClass().getResource("/Music.mp3").toString());
		musicPlayer = new MediaPlayer(music);
		//jag antar att detta bör klassas som komposition, då spelet slutar försvinner spelaren. 
		//dock lever ju spelaren kvar i de andra spelarnas spel (förvisso under egna kopior av objektet. 
		// men utan ett spel, ingen spelare. 
	}
	/**
	 * Instantiates the game. Takes a stage on which to setup the game.
	 * @param primary - the Stage on which to setup the game
	 */

	public void runGame(Stage primary) {

		Stage primaryStage = primary;
		root = new Pane();
		root.setStyle("-fx-background-color: black;");
		scene = new Scene(root);
		primaryStage.setTitle("Space game");
		musicPlayer.play();
		musicPlayer.setVolume(0.5);

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
		setOnClose();
		});
		Packet00Login loginPacket = new Packet00Login(player.getName(), player.getTranslateX(), player.getTranslateY(),
				player.getRotate());
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

	
	private synchronized void gameLoop() {
		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 60), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!getPlayerArray().isEmpty()) {
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
		timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 2), ev -> {
			if (!explosions.isEmpty())
				removeExplosions();
			if (player.getAmmo() < 8)
				player.setAmmo(player.getAmmo() + 1);
			for (PlayerMP p : playerArray) {
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
			for (PlayerMP player : playerArray) {
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
					movePlayer(5, 15);
				break;
			}
			case LEFT: {
				if (player.isAlive())
					movePlayer(-5, 15);
				break;
			}
			case UP: {
				if (player.isAlive())
					movePlayer(25);
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
// overloaded to only take one parameter since the movement is only to propel forward to turn parameter is necessary
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
				root.getChildren().add(bullet);
				bullet.setTranslateX(player.getTranslateX() + (player.getImage().getWidth()) / 2);
				bullet.setTranslateY(player.getTranslateY() + (player.getImage().getHeight() / 2));
				bullet.setRotate(player.getRotate());
				bulletArray.add(bullet);

				moveBulletFirst(bullet);
				updateShootsToServer(bullet);
				removeBullet(bullet);
			}
		});
	}
	
	private void moveBullet(Bullet bullet) {
	
		double bulletX = bullet.getTranslateX();
		double bulletY = bullet.getTranslateY();
		bullet.setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getRotate())) * 25);
		bullet.setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getRotate())) * 25);
	
	}

	private void moveBulletFirst(Bullet bullet) {
		double bulletX = bullet.getTranslateX();
		double bulletY = bullet.getTranslateY();
		bullet.setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getRotate())) * 30);
		bullet.setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getRotate())) * 30);
	}
	
	private void checkHit() {
		if (!bulletArray.isEmpty()) {
			for (int i = 0; i < bulletArray.size(); i++) {
				if (bulletArray.get(i).getTranslateX() <= -SCREEN_WIDTH - 100
						|| bulletArray.get(i).getTranslateX() >= SCREEN_WIDTH + 100
						|| bulletArray.get(i).getTranslateY() <= -SCREEN_HEIGHT - 100
						|| bulletArray.get(i).getTranslateY() >= SCREEN_HEIGHT + 100) {
					removeBullet(bulletArray.get(i));
				}
				for (int j = 0; j < getPlayerArray().size(); j++) {
					if (bulletArray.get(i).getBoundsInParent()
							.intersects(getPlayerArray().get(j).getHitbox().getBoundsInParent())) {
						damage(playerArray.get(j), bulletArray.get(i).getDamage());
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
			root.getChildren().remove(bullet);
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
			ellipse.setTranslateX(bullet.getTranslateX());
			ellipse.setTranslateY(bullet.getTranslateY());
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
/**
 * Adds a player to the game. This could be used by the client to add a player connected to the server
 * or used by the local player to get added in the game. 
 * @param player2 - the PlayerMP to be connected
 */
	public synchronized void addPlayer(PlayerMP player2) {
		getPlayerArray().add(player2);
		addPlayerLocally(player2);
	}

	private void addPlayerLocally(PlayerMP player) {
		Platform.runLater(() -> {
			root.getChildren().add(player);
			Text playerLabel = new Text(player.getName());
			playerLabel.setFill(Color.RED);
			root.getChildren().add(playerLabel);
			playerNames.add(playerLabel);
			player.setLives(100);

		});
	}

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
		Packet03Shoot packet = new Packet03Shoot(bullet.getTranslateX(),
				bullet.getTranslateY(), bullet.getRotate());
		packet.writeData(gc);
		removeExplosions();
	}
	
	private int getPlayerMPIndex(String username) {
		int index = 0;// -1
		for (PlayerMP p : getPlayerArray()) {
			if (p instanceof PlayerMP && p.getName().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}
/**
 * Update the connected players position as mandated by the game server
 * @param userName - The connected PlayerMP to be moved
 * @param x - Sets the players TranslateX 
 * @param y - Sets the players TranslateY
 * @param rotate - Sets the players Rotate
 */
	public synchronized void updatePlayers(String userName, double x, double y, double rotate) {
		int index = getPlayerMPIndex(userName);
		Platform.runLater(() -> {
			this.getPlayerArray().get(index).setTranslateX(x);
			this.getPlayerArray().get(index).setTranslateY(y);
			this.getPlayerArray().get(index).setRotate(rotate);
		});
	}
/**
 * Finds the graphics of a PlayerMP added to the games Pane 
 * @param username - the PlayerMP to be found
 * @return - The Panes added children index of the wanted player
 */
	private int getRootPlayer(String username) {
		int PlayerIndex = getPlayerMPIndex(username);
		int index = 0;
		for (Node p : root.getChildren()) {
			if (p.equals(getPlayerArray().get(PlayerIndex))) {
				break;
			}
			index++;
		}
		return index;
	}

	/**
	 * Removes a player from the game
	 * @param username - The username of the player who are to be removed
	 */
	public synchronized void removePlayerMP(String username) {
		Platform.runLater(() -> {
			int index = 0;
			for (PlayerMP p : getPlayerArray()) {
				if (p instanceof PlayerMP && p.getName().equals(username)) {
					break;
				}
				index++;
			}
			int rootIndex = getRootPlayer(username);
			root.getChildren().remove(rootIndex);
			root.getChildren().remove(rootIndex); // tar bort playername text
			playerArray.get(index).setHitbox(null);
			getPlayerArray().remove(index);
		});
	}
/**
 * Creates a bullet instance to be added to the game. 
 * The bullet spawns when the server communicates to the client that a connected
 * player has fired a shot.  
 * @param x - The bullets TranslateX position
 * @param y - The bullets TranslateY position
 * @param rotate - The bullets rotation
 */
	public synchronized void updateShoots(double x, double y, double rotate) {
		Bullet bullet = new Bullet(x, y, rotate);
		moveBulletFirst(bullet);
		bulletArray.add(bullet);
		Platform.runLater(() -> {
			root.getChildren().add(bullet);
			bullet.setTranslateX(x);
			bullet.setTranslateY(y);
			bullet.setRotate(rotate);
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
		if (getPlayerArray().size() >= 2) {
			for (PlayerMP p : playerArray) {
				if (!p.isAlive())
					playersDestroyed++;
			}
			if (player.isAlive() && playersDestroyed + 1 == getPlayerArray().size()) {
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
		for (PlayerMP player2 : playerArray) {
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
/**
 * Notifies the server that this player has hit another connected player
 * @param player - The player who has been damaged
 * @param damage - The amount of damage the player takes
 */
	public void damage(PlayerMP player, int damage) {
		Packet04Hit packet = new Packet04Hit(player.getName(), damage);
		packet.writeData(gc);
	}
/**
 * Notifies the local player if another player has been hit and adjusts that 
 * players hit points and graphics accordingly.  
 * @param username - The connected player that has been hit
 * @param damage - The amount of damage the player takes 
 */
	public void damagePlayer(String username, int damage) {
		int index = getPlayerMPIndex(username);
		getPlayerArray().get(index).setLives(getPlayerArray().get(index).getLives() - damage);
		if (getPlayerArray().get(index).isAlive())
			getPlayerArray().get(index).setEffect(new Glow(1));
		getPlayerArray().get(index).showDamage();

		if (getPlayerArray().get(index).getLives() <= 0) {
			getPlayerArray().get(index).setAlive(false);
			getPlayerArray().get(index).showDamage();
		}
	}
	private void setOnClose() {
		// ska skicka ett disconnectpavket när man stänger ner fönstret
		Packet01Disconnect packet = new Packet01Disconnect(player.getName());
		packet.writeData(gc);
		musicPlayer.stop();	
		try {
			playerLoop.stop();  //avslutar alla loopar, kastar exceptions
			timeline.stop();
			gc.setRunning(false);
			gc.shutDownClient();
			gc.join();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	/**
	 * A synchronized instance of the ArrayList of connected players
	 * @return -The ArrayList of connected PlayerMP
	 */
	private synchronized ArrayList<PlayerMP> getPlayerArray() {
		return playerArray;
	}
	

}
