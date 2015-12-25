package projectv2;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	Image image = new Image("projectv2/images.jpg");

	@Override
	public void start(Stage primaryStage) {
		Group root = new Group();
		Player player = new Player(image);
		
		
		Scene scene = new Scene(root,500,500);
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		
		root.getChildren().add(player.getGraphics());
		player.getGraphics().setTranslateX(100);
		player.getGraphics().setTranslateY(350);
		
		
		scene.setOnKeyPressed(e->{
			player.getGraphics().setTranslateX(player.getGraphics().getTranslateX()+5);
		});
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
