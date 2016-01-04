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

	@Override
	public void start(Stage primaryStage) {
		Pane root = new Pane();

		// BackgroundImage bground =

		Player player = new Player(image);
		root.setStyle("-fx-background-color: black;");

		Scene scene = new Scene(root);

		// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();

		root.getChildren().add(player.getGraphics());
		player.getGraphics().setTranslateX(100);
		player.getGraphics().setTranslateY(350);
		
		

		playerLoop = new Timeline(new KeyFrame(Duration.millis(1000 / 15), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				Thread thread = new Thread(()->{
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
								+ (player.getGraphics().getImage().getWidth() / 2));
						bullet.getR().setTranslateY(player.getGraphics().getTranslateY()
								+ (player.getGraphics().getImage().getHeight() / 2));
						bullet.getR().setRotate(player.getGraphics().getRotate());
						bulletArray.add(bullet);

					}

					default:
						break;

					}
				});
				});
				thread.start();
				double x = player.getGraphics().getTranslateX();
				double y = player.getGraphics().getTranslateY();

				if (bullet != null) {
					for (Bullet bullet : bulletArray) {
					
						double bulletX = bullet.getR().getTranslateX();
						double bulletY = bullet.getR().getTranslateY();
						bullet.getR().setTranslateX(bulletX + Math.cos(Math.toRadians(bullet.getR().getRotate())) * 15);
						bullet.getR().setTranslateY(bulletY + Math.sin(Math.toRadians(bullet.getR().getRotate())) * 15);
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
	
	// skapa client här
}
