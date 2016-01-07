package projectv2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.GameClient;
import net.GameServer;
import packets.Packet;
import packets.Packet00Login;
import packets.Packet01Disconnect;
import packets.Packet.PacketTypes;

public class Game extends Thread{
	public Game() {

	}

	// Image image = new Image("projectv2/untitled.png");
	private Timeline playerLoop;
	private Bullet bullet;
	private List<Bullet> bulletArray = new LinkedList<Bullet>();
	private ArrayList<PlayerMP> gameObjects = new ArrayList<PlayerMP>();
	Scene scene;
	public PlayerMP player;
	Pane root;
	GameClient gc;
	GameServer gs; 
	//Label label;

	public synchronized void runGame(Stage primaryStage) {
		System.out.println("name: ");
		Scanner sc = new Scanner(System.in);
		String userName = sc.nextLine();
		root = new Pane();
		player = new PlayerMP(userName, null, 0);
		root.setStyle("-fx-background-color: black;");
		scene = new Scene(root);
		

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e->{
			Packet01Disconnect packet = new Packet01Disconnect(player.getName());
			packet.writeData(gc);
		});
		root.getChildren().add(player.getGraphics());
		
		System.out.println("run server");
		if (sc.nextLine().equalsIgnoreCase("y")) {
			gs = new GameServer(this);
			gs.start();
		}
		Packet00Login loginPacket = new Packet00Login(("00"+player.getName()).getBytes());
		
		gc = new GameClient(this, "localhost");
		gc.start();
		
		
		if (gs != null){
			gs.addConnection((PlayerMP)player, loginPacket);
		}
		loginPacket.writeData(gc);
		

		//loginPacket.writeData(gc);
		
		player.getGraphics().setTranslateX(100);
		player.getGraphics().setTranslateY(350);

		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 60), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

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
				//movePlayer(1.5);

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
		update(speed);
	}

	private void movePlayer(double speed) {
		double x = player.getGraphics().getTranslateX();
		double y = player.getGraphics().getTranslateY();
		player.getGraphics().setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * speed);
		player.getGraphics().setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * speed);
		update(speed);
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

	public void addPlayer(PlayerMP player2) {
		
		gameObjects.add(player2);
		System.out.println(gameObjects.size() +" player id "+ player2.getName());
		System.out.println(player.ipAdress +" " +player.port);
		System.out.println(player2.ipAdress + " " +player2.port);
		player2.setGraphics(new Image("/secondship.png"));
		Platform.runLater(()->{
			root.getChildren().add(player2.getGraphics());

			//player.getGraphics().setTranslateX(500);
			//root.getChildren().add(player2.getGraphics());
			//player2.getGraphics().setTranslateX(400);
			//player2.getGraphics().setTranslateY(400);
		});
	}
	public void update(double speed){
		player.setRotate(player.getGraphics().getRotate());
		player.setPosX(player.getGraphics().getTranslateX());
		player.setPosyY(player.getGraphics().getTranslateY());
		player.setSpeed(speed);
	}

	public void removePlayerMP(String username) {
		int index=0;
		for (PlayerMP p : gameObjects) {
			if (p instanceof PlayerMP && p.getName().equals(username)){
				break;
			}
			index++;
		}
		root.getChildren().remove(gameObjects.get(index));
		gameObjects.remove(index);
		
	}
	
}
