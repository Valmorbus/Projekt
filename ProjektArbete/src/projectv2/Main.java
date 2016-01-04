package projectv2;

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
	Pane root;

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
		player.getGraphics().setTranslateX(100);
		player.getGraphics().setTranslateY(350);

		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 15), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				scene.setOnKeyPressed(e -> {

					switch (e.getCode()) {
					case RIGHT:
						player.getGraphics().setRotate(player.getGraphics().getRotate() + 5);
						break;
					case LEFT:
						player.getGraphics().setRotate(player.getGraphics().getRotate() - 5);
						break;
					case UP: {
						double x = player.getGraphics().getTranslateX();
						double y = player.getGraphics().getTranslateY();
						player.getGraphics()
								.setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * 5);
						player.getGraphics()
								.setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * 5);
						break;
					}
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
					for (Bullet bullet : bulletArray) {
						moveBullet(bullet);
						moveBullet(bullet);
						checkHit();
					}
					
				}

				player.getGraphics().setTranslateX(x + Math.cos(Math.toRadians(player.getGraphics().getRotate())) * 2);
				player.getGraphics().setTranslateY(y + Math.sin(Math.toRadians(player.getGraphics().getRotate())) * 2);

			}
		}));

		playerLoop.setCycleCount(-1);
		playerLoop.play();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void checkHit() {

		if (bulletArray.get(0).getR().getTranslateX() <= -scene.getWidth()
				|| bulletArray.get(0).getR().getTranslateX() >= scene.getWidth()
				|| bulletArray.get(0).getR().getTranslateY() <= -scene.getHeight()
				|| bulletArray.get(0).getR().getTranslateY() >= scene.getHeight()) {
			root.getChildren().remove(bulletArray.get(0).getR());
			bulletArray.remove(0);
			System.out.println(bulletArray.get(0));
		
		}

		else if (bulletArray.get(0).getR().getBoundsInParent().intersects(player.getGraphics().getBoundsInParent())) {
			System.out.println("hit");
			player.setLives(player.getLives() - 1);
			System.out.println(player.getLives());

			root.getChildren().remove(bulletArray.get(0).getR());
			bulletArray.remove(0);
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
		bullet.getR().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getR().getRotate())) * 75);
		bullet.getR().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getR().getRotate())) * 75);
	}

	// skapa client här
}
