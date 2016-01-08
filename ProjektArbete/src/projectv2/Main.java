package projectv2;

import java.util.Scanner;

import javafx.application.Application;
import javafx.stage.Stage;
import net.GameServer;

public class Main extends Application {


	@Override
	public void start(Stage primaryStage) {
		System.out.println("run server");
		Scanner sc = new Scanner(System.in);
		Game game = new Game(sc.nextLine());
		game.runGame(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	}