package projectv2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

	Image image = new Image("projectv2/images.jpg");
	private Timeline playerLoop;
	private Bullet bullet;
	private List<Bullet> bulletArray = new LinkedList<Bullet>();
	Scene scene;
	Player player;
	Player player2 = new Player(image);
	Pane root;
	DataInputStream in;

	@Override
	public void start(Stage primaryStage) {
		root = new Pane();

		// BackgroundImage bground =

		player = new Player(image);
		root.setStyle("-fx-background-color: black;");

		scene = new Scene(root);

		// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

		root.getChildren().add(player.getGraphics());
		root.getChildren().add(player2.getGraphics());
		player.getGraphics().setTranslateX(100);
		player.getGraphics().setTranslateY(350);

		Thread thread = new Thread(() -> {
			connectToServer();
			System.out.println("här");
		});
		thread.start();

		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 15), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				scene.setOnKeyPressed(e -> {

					switch (e.getCode()) {
					case RIGHT: {
						double x = player.getGraphics().getTranslateX();
						double y = player.getGraphics().getTranslateY();
						player.getGraphics().setRotate(player.getGraphics().getRotate() + 5);
						player.getGraphics()
								.setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * 10);
						player.getGraphics()
								.setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * 10);
						break;
					}
					case LEFT: {
						double x = player.getGraphics().getTranslateX();
						double y = player.getGraphics().getTranslateY();
						player.getGraphics().setRotate(player.getGraphics().getRotate() - 5);
						player.getGraphics()
								.setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * 10);
						player.getGraphics()
								.setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * 10);
						break;
					}
					case UP: {
						double x = player.getGraphics().getTranslateX();
						double y = player.getGraphics().getTranslateY();
						player.getGraphics()
								.setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * 10);
						player.getGraphics()
								.setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * 10);
						break;
					}

					default:
						break;

					}
				});
				scene.setOnKeyReleased(e -> {
					switch (e.getCode()) {
					case SPACE: {
						bullet = new Bullet();
						root.getChildren().add(bullet.getR());
						bullet.getR().setTranslateX(player.getGraphics().getTranslateX()
								+ (player.getGraphics().getImage().getWidth()) / 2);
						bullet.getR().setTranslateY(player.getGraphics().getTranslateY()
								+ (player.getGraphics().getImage().getHeight() / 2));
						bullet.getR().setRotate(player.getGraphics().getRotate());
						bulletArray.add(bullet);

						moveBulletFirst(bullet);

					}
					default:
						break;

					}
				});

				double x = player.getGraphics().getTranslateX();
				double y = player.getGraphics().getTranslateY();

				if (bullet != null) {
					for (int i = 0; i < bulletArray.size(); i++) {
						moveBullet(bulletArray.get(i));
						moveBullet(bulletArray.get(i));

						checkHit();
					}

				}

				player.getGraphics().setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * 3);
				player.getGraphics().setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * 3);

			}
		}));

		playerLoop.setCycleCount(-1);
		playerLoop.play();
	}

	public static void main(String[] args) {
		launch(args);
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
					// System.out.println(bulletArray.get(0));

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

	// skapa client här
	private void connectToServer() {
		try {
			Socket socket = new Socket("LocalHost", 8016);
			in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			while (true) {
				out.writeDouble(player.getGraphics().getTranslateX());
				out.flush();
				out.writeDouble(player.getGraphics().getTranslateY());
				out.flush();
				player2.getGraphics().setTranslateX(in.readDouble());
				player2.getGraphics().setTranslateY(in.readDouble());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
