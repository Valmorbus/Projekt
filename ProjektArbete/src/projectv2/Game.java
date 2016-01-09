package projectv2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;

import javax.management.timer.Timer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.GameClient;
import net.GameServer;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet02Move;
import packets.Packet03Shoot;

public class Game{ // extends Thread {

	private Timeline playerLoop;
	private Bullet bullet;
	private List<Bullet> bulletArray = new LinkedList<Bullet>();
	private ArrayList<PlayerMP> gameObjects = new ArrayList<PlayerMP>();
	private ArrayList<Ellipse> explosions = new ArrayList<Ellipse>();
	private Scene scene;
	public PlayerMP player;
	private Pane root;
	private GameClient gc;
	private GameServer gs;
	// Label label;

	/**
	 * att göra. Kolla varför servern inte får sin klient att fungera lägga till
	 * skott över nätverk.
	 */

	public Game(String runServer) {
		if (runServer.equalsIgnoreCase("y")) {
			gs = new GameServer(this);
			gs.start();
		}
	}
	// kör server från main genom game konstruktor?

	public synchronized void runGame(Stage primaryStage) {
		System.out.println("name: ");
		Scanner sc = new Scanner(System.in);
		String userName = sc.nextLine();
		sc.close();
		root = new Pane();
		player = new PlayerMP(userName, 550, 550, -250, 0, null, 0);
		root.setStyle("-fx-background-color: black;");
		scene = new Scene(root);

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
			if (gs != null) {
				Packet01Disconnect packet = new Packet01Disconnect(player.getName());
				packet.writeData(gc);
			}
		});
		Packet00Login loginPacket = new Packet00Login(player.getName(), player.getTranslateX(), player.getTranslateY(),  //player.getX(), player.getY(),
				player.getRotate());

		gc = new GameClient(this, "localhost");
		gc.start();

		if (gs != null) {
			gs.addConnection((PlayerMP) player, loginPacket);
		}
		loginPacket.writeData(gc);
		
		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 60), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!gameObjects.isEmpty()) {
					playerMovements();
				}
				if (bullet != null) {
					checkHit();
				}
				
				//if (!gameObjects.isEmpty() && !root.getChildren().isEmpty())
				//movePlayer(0.2);

			}
		}));
		playerLoop.setCycleCount(-1);
		playerLoop.play();
	}


	private void checkHit() {
		if (!bulletArray.isEmpty()) {
			for (int i = 0; i < bulletArray.size(); i++) {
				if (bulletArray.get(i).getEllipse().getTranslateX() <= -scene.getWidth()
						|| bulletArray.get(i).getEllipse().getTranslateX() >= scene.getWidth()
						|| bulletArray.get(i).getEllipse().getTranslateY() <= -scene.getHeight()
						|| bulletArray.get(i).getEllipse().getTranslateY() >= scene.getHeight()) {
					removeBullet(bulletArray.get(i));
				}
				for (int j = 0; j < gameObjects.size(); j++) {
					if (bulletArray.get(i).getEllipse().getBoundsInParent()
							.intersects(gameObjects.get(j).getBoundsInParent())) {
						System.out.println("hit " + gameObjects.get(j).getName());
						gameObjects.get(j).setLives(gameObjects.get(j).getLives() - 1);
						System.out.println(gameObjects.get(j).getLives());
						removeBullet(bulletArray.get(i));
					}
				}
				moveBullet(bulletArray.get(i));
			}
		}
		if (player.getLives()<=0){
			System.exit(0);
		}
	}

	private void removeBullet(Bullet bullet) {
		createExplosion(bullet);
		Platform.runLater(() -> {
			root.getChildren().remove(bullet.getEllipse());
			bulletArray.remove(bullet);
		});
	}
	private void createExplosion(Bullet bullet){
		Ellipse ellipse = new Ellipse(25, 25);
		ellipse.setFill(Color.YELLOW);
		ellipse.setStroke(Color.WHITE);
		ellipse.setEffect(new Glow(0));
		ellipse.setEffect(new Bloom(0));
		Platform.runLater(()->{
			root.getChildren().add(ellipse);
			ellipse.setTranslateX(bullet.getEllipse().getTranslateX());
			ellipse.setTranslateY(bullet.getEllipse().getTranslateY());	
			explosions.add(ellipse);
		});
		
	}
	private void removeExplosions(){
		for(Ellipse e : explosions){
			root.getChildren().remove(e);
		}
		explosions.removeAll(explosions);
			
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
	//synchronize?
	private  void movePlayer(int turn, double speed) {
		Platform.runLater(() -> {
			double x = player.getTranslateX();
			double y = player.getTranslateY();
			player.setRotate(player.getRotate() + turn);
			player.setTranslateX(x + Math.cos(Math.toRadians(player.getRotate())) * speed);
			player.setTranslateY(y + Math.sin(Math.toRadians(player.getRotate())) * speed);
		});
		update(speed);
	}
	//synchronize?
	private  void movePlayer(double speed) {
		Platform.runLater(() -> {
			double x = player.getTranslateX();
			double y = player.getTranslateY();
			player.setTranslateX(x + Math.cos(Math.toRadians(player.getRotate())) * speed);
			player.setTranslateY(y + Math.sin(Math.toRadians(player.getRotate())) * speed);
		});
		update(speed);
	}

	private void shoot() {
		Platform.runLater(() -> {
			if (bulletArray.size() <= 5) {
				bullet = new Bullet();
				root.getChildren().add(bullet.getEllipse());
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
		gameObjects.add(player2);
		System.out.println(gameObjects.size() + " player id " + player2.getName());
		System.out.println(player2.ipAdress + " " + player2.port);
		// player2.setImage(new Image("/secondship.png"));

		addLocalPlayer(player2);
	}

	public void addLocalPlayer(PlayerMP player) {
		Platform.runLater(() -> {
			root.getChildren().add(player);
		});
	}
	//synchronize?
	public void update(double speed) {
		Platform.runLater(() -> {
			player.setRotate(player.getRotate());
			player.setPosX(player.getTranslateX());
			player.setPosY(player.getTranslateY());
			player.setSpeed(speed);

			Packet02Move packet = new Packet02Move(player.getName(), player.getTranslateX(), player.getTranslateY(),
					player.getRotate());
		packet.writeData(gc);
			
			
			//temporary position
			removeExplosions();
		});
	}
	private void updateShoots(Bullet bullet){
		Packet03Shoot packet = new Packet03Shoot(null,bullet.getEllipse().getTranslateX(),bullet.getEllipse().getTranslateY(), bullet.getEllipse().getRotate());
	packet.writeData(gc);
	}
	
	//synchronize?
	private void updateTick(){
		Platform.runLater(()->{
			 Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 25), 
				        ev-> update(0)));
				    timeline.setCycleCount(Animation.INDEFINITE);
				    timeline.play();
			
		});
		
	}

	private int getPlayerMPIndex(String username) {
		int index = -1;
		for (PlayerMP p : gameObjects) {
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
			this.gameObjects.get(index).setTranslateX(x);
			this.gameObjects.get(index).setTranslateY(y);
			this.gameObjects.get(index).setRotate(rotate);
			
		});
		

	}

	public int getRootPlayer(String username) {
		int PlayerIndex = getPlayerMPIndex(username);
		int index = -1;
		for (Node p : root.getChildren()) {
			if (p.equals(gameObjects.get(PlayerIndex))) {
				System.out.println(p.toString() + " " + gameObjects.get(PlayerIndex));
				break;
			}
			index++;
		}
		return index;
	}

	public void removePlayerMP(String username) {
		int index = 0;
		for (PlayerMP p : gameObjects) {
			if (p instanceof PlayerMP && p.getName().equals(username)) {
				break;
			}
			index++;
		}
		root.getChildren().remove(getRootPlayer(username));
		gameObjects.remove(index);

	}
	
	public void updateShoots(double x, double y, double rotate) {
		Bullet bullet = new Bullet(x, y, rotate);
		
		bulletArray.add(bullet);
		Platform.runLater(()->{
			root.getChildren().add(bullet.getEllipse());
			bullet.getEllipse().setTranslateX(x);
			bullet.getEllipse().setTranslateY(y);
			bullet.getEllipse().setRotate(rotate);
			System.out.println("bullet added: " +bullet.getEllipse().getTranslateX()+" " +bullet.getEllipse().getTranslateY());
		});
		
		
	}
	public GameServer getServer(){
		return this.gs;
	}

}
