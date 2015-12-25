package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class ProjectController implements Initializable {
	
	@FXML AnchorPane root;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		Player player = new Player(3, "simon");
		root.getChildren().add(player.getGraphics()); 
		

	}

}
