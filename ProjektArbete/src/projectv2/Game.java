package projectv2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.GameClient;
import packets.Packet00Login;

public class Game {
	public Game(){
		
	}
	
	//Image image = new Image("projectv2/untitled.png");
	private Timeline playerLoop;
	private Bullet bullet;
	private List<Bullet> bulletArray = new LinkedList<Bullet>();
	private ArrayList<Player> gameObjects = new ArrayList<Player>();
	Scene scene;
	Player player;
	Pane root;
	GameClient gc;
	
	
	public synchronized void runGame(Stage primaryStage) {
		root = new Pane();
		player = new Player();
		root.setStyle("-fx-background-color: black;");
		scene = new Scene(root);
		Player player2 = new Player();
		primaryStage.setScene(scene);
		primaryStage.show();

		root.getChildren().add(player.getGraphics());
		// root.getChildren().add(player2.getGraphics());
		
		
		gc = new GameClient("localhost");
		gc.start();

		Packet00Login loginPacket = new Packet00Login("00ghjälp");
		loginPacket.writeData(gc);
		
		//gc.sendData("ping".getBytes());
		if (!gameObjects.isEmpty())
		for (Player mp : gameObjects) {
			mp.setImage(new Image("projectv2/untitled.png"));
			root.getChildren().add(mp.getGraphics());
			mp.getGraphics().setTranslateX(150);
			mp.getGraphics().setTranslateY(400);
		}
		player.getGraphics().setTranslateX(100);
		player.getGraphics().setTranslateY(350);
		
		

		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 60), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!gameObjects.isEmpty())
					for (Player mp : gameObjects) 
						System.out.println(mp.toString());

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
			
				if (bullet != null) {
					for (int i = 0; i < bulletArray.size(); i++) {
						moveBullet(bulletArray.get(i));
						checkHit();
					}
				}
				movePlayer(1.5);
			}
		}));
		playerLoop.setCycleCount(-1);
		playerLoop.play();
	}

	private void checkHit() {
		if (!bulletArray.isEmpty())
			for (int i = 0; i < bulletArray.size(); i++) {
				if (bulletArray.get(i).getR().getTranslateX() <= -scene.getWidth()
						|| bulletArray.get(i).getR().getTranslateX() >= scene.getWidth()
						|| bulletArray.get(i).getR().getTranslateY() <= -scene.getHeight()
						|| bulletArray.get(i).getR().getTranslateY() >= scene.getHeight()) {
					root.getChildren().remove(bulletArray.get(i).getR());
					bulletArray.remove(bulletArray.get(i));
				}

				else if (bulletArray.get(i).getR().getBoundsInParent()
						.intersects(player.getGraphics().getBoundsInParent())) {
					System.out.println("hit");
					player.setLives(player.getLives() - 1);
					System.out.println(player.getLives());

					root.getChildren().remove(bulletArray.get(i).getR());
					bulletArray.remove(0);
				}
			}
	}

	private void moveBullet(Bullet bullet) {

		double bulletX = bullet.getR().getTranslateX();
		double bulletY = bullet.getR().getTranslateY();
		bullet.getR().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getR().getRotate())) * 25);
		bullet.getR().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getR().getRotate())) * 25);

	}

	private void moveBulletFirst(Bullet bullet) {
		double bulletX = bullet.getR().getTranslateX();
		double bulletY = bullet.getR().getTranslateY();
		bullet.getR().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getR().getRotate())) * 100);
		bullet.getR().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getR().getRotate())) * 100);
	}

	private void movePlayer(int turn, double speed) {
		double x = player.getGraphics().getTranslateX();
		double y = player.getGraphics().getTranslateY();
		player.getGraphics().setRotate(player.getGraphics().getRotate() + turn);
		player.getGraphics().setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * speed);
		player.getGraphics().setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * speed);

	}
	private void movePlayer(double speed) {
		double x = player.getGraphics().getTranslateX();
		double y = player.getGraphics().getTranslateY();
		player.getGraphics().setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * speed);
		player.getGraphics().setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * speed);

	}

	private void shoot() {
		if (bulletArray.size() <= 5) {
			bullet = new Bullet();
			root.getChildren().add(bullet.getR());
			bullet.getR().setTranslateX(
					player.getGraphics().getTranslateX() + (player.getGraphics().getImage().getWidth()) / 2);
			bullet.getR().setTranslateY(
					player.getGraphics().getTranslateY() + (player.getGraphics().getImage().getHeight() / 2));
			bullet.getR().setRotate(player.getGraphics().getRotate());
			bulletArray.add(bullet);

			moveBulletFirst(bullet);
		}
	}
	public void addPlayer(PlayerMP player2){
		System.out.println("before");
			System.out.println(player2.port + " " + player2.ipAdress);
			this.gameObjects.add(player2);
			System.out.println("worked");
			this.player = player2;
		
		
	}
}




