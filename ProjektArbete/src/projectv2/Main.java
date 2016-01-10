package projectv2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.GameServer;

public class Main extends Application {
	String userName = "Guest";
	String ipAdress ="localhost";
	boolean runServer;

	@Override
	public void start(Stage primaryStage) {
		getLogin(primaryStage);
		//System.out.println("run server");
		//Scanner sc = new Scanner(System.in);
		//Game game = new Game(sc.nextLine());

		//game.runGame(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void getLogin(Stage primaryStage) {
		
		String LocalIP = "This ip is "; 
		try {
			LocalIP += InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		Button start = new Button("Start");
		HBox hbox = new HBox(10);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.getChildren().add(start);
		grid.add(hbox, 1, 4);
		
		Scene scene = new Scene(grid, 300, 275);
		primaryStage.setScene(scene);
		primaryStage.setTitle(LocalIP);	
		primaryStage.show();
		ServerPopUp();		
		
		start.setOnAction(e->{
			userName = userTextField.getText();
			ipAdress = IPTextfield.getText();
			Game game = new Game(runServer);
			game.runGame(primaryStage, userName, ipAdress);
		});
		
		
		
	}
	
	private void ServerPopUp(){
		Stage stage = new Stage();
		FlowPane pane = new FlowPane();
		Text text = new Text("Do you want to run the server?");
		pane.getChildren().add(text);
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		pane.getChildren().addAll(noButton, yesButton);
		yesButton.setOnAction(e->{
			runServer = true;
			//return;
			stage.close();
		});
		noButton.setOnAction(e->{
			runServer = false;
			//return;
			stage.close();
		});
		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
		
	}
}