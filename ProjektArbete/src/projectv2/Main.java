package projectv2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.GameServer;
/**
 * This class is used to instantiate GameServer and/or the Game.
 * @author Simon Borgström
 *@version 1.0
 */
public class Main extends Application {
	private String userName = "Guest";
	private String ipAdress = "localhost";
	private double posX, posY;
	private String LocalIP;
	private GameServer gs;

	@Override
	public void start(Stage primaryStage) {
		getLogin(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void getLogin(Stage primaryStage) {

		LocalIP = "This ip is ";
		try {
			LocalIP += InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("could not identify ip.");
		}
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		Text scenetitle = new Text("Welcome");
		grid.add(scenetitle, 0, 0, 2, 1);
		Label userNameLabel = new Label("User Name:");
		grid.add(userNameLabel, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label ip = new Label("IP Adress:");
		grid.add(ip, 0, 2);
		TextField IPTextfield = new TextField();
		grid.add(IPTextfield, 1, 2);

		Label slideXLabel = new Label("Startposition X");
		Slider posXSlide = new Slider(0, 1500, 500);
		grid.add(posXSlide, 1, 3);
		grid.add(slideXLabel, 0, 3);

		Label slideYLabel = new Label("Startposition Y");
		Slider posYSlide = new Slider(0, 1000, 500);
		grid.add(posYSlide, 1, 4);
		grid.add(slideYLabel, 0, 4);

		Button start = new Button("Start");
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.getChildren().add(start);
		grid.add(hbox, 1, 5);

		Scene scene = new Scene(grid, 300, 275);
		primaryStage.setScene(scene);
		primaryStage.setTitle(LocalIP);
		primaryStage.show();
		ServerPopUp();

		start.setOnAction(e -> {
			userName = userTextField.getText();
			ipAdress = IPTextfield.getText();
			System.out.println(ipAdress);
			posX = posXSlide.getValue();
			posY = posYSlide.getValue();
			Game game = new Game(ipAdress, userName, posX, posY);
			game.runGame(primaryStage);
		});
	}

	private void ServerPopUp() {
		Stage stage = new Stage();
		stage.initStyle(StageStyle.UTILITY);
		stage.sizeToScene();
		FlowPane pane = new FlowPane(50,50);
		Text text = new Text("Do you want to run the server?");
		pane.getChildren().add(text);
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		pane.getChildren().addAll(noButton, yesButton);
		yesButton.setOnAction(e -> {
			gs = new GameServer(stage);
			gs.start();
			pane.getChildren().removeAll(pane.getChildren());
			Label ipLabel = new Label(LocalIP);
			pane.getChildren().add(ipLabel);
		});
		noButton.setOnAction(e -> {
			stage.close();
		});
		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(e -> {
			closeServer();
			});

	}
	private void closeServer(){
		if (gs != null) {
			try {
				gs.setRunning(false);
				gs.shutDownServer();
				gs.join();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// gs.setRunning(false);

		}
	}
}