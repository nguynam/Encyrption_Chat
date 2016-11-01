package package1;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class GUI extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		final Client_Chat client = new Client_Chat();
		
		final TextField ipField = new TextField();
		ipField.setPromptText("Server IP:");
		final TextField portField = new TextField();
		portField.setPromptText("Server Port:");
		Button connectBtn = new Button();
		connectBtn.setText("Connect");
		connectBtn.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				// TODO Handle connect
				if(ipField.getText().isEmpty() || portField.getText().isEmpty()){
					displayPopup("The fields cannot be blank");
					return;
				}
				Boolean connectionSuccess = client.connect(ipField.getText(), portField.getText());
				if (!connectionSuccess){
					displayPopup("Could not connect to specified server");
					return;
				}
			}
			
			
		});
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25,25,25,25));
		grid.add(ipField, 1, 0);
		grid.add(portField, 1, 1);
		grid.add(connectBtn, 1, 2);

		
		Scene loginScene = new Scene(grid,300, 250);
		primaryStage.setTitle("Secure Chat");
		primaryStage.setScene(loginScene);
		primaryStage.show();
		
	}
	private void displayPopup(String message){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}
	public static void main(String[] args){
		launch(args);
	}

}
