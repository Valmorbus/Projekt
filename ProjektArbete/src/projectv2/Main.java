package projectv2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.stage.Stage;
import net.GameServer;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		getLogin(primaryStage);
		System.out.println("run server");
		Scanner sc = new Scanner(System.in);
		Game game = new Game(sc.nextLine());

		game.runGame(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void getLogin(Stage primaryStage) {
		String ip = "This ip is "; 
		try {
			ip += InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		primaryStage.setTitle(ip);
		
		
		
		
	}
}