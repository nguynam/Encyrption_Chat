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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GUI extends Application{

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		final Client_Chat client = new Client_Chat();
		
		final TextField ipField = new TextField();
		ipField.setPromptText("Server IP:");
		final TextField portField = new TextField();
		portField.setPromptText("Server Port:");
		Button connectBtn = new Button();
		Text ipText = new Text();
		ipText.setText("IP Address");
		Text portText = new Text();
		portText.setText("Port");
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
				else{
					//Connection successful switch to new view
					
					GridPane chatGridGui = new GridPane();
					chatGridGui.setAlignment(Pos.CENTER);
					chatGridGui.setHgap(10);
					chatGridGui.setVgap(10);
					chatGridGui.setPadding(new Insets(25,25,25,25));
					TextArea idBox = new TextArea();
					TextArea chatBox = new TextArea();
					Button refreshBtn = new Button();
					refreshBtn.setText("Refresh ID's");
					Button sendBtn = new Button();
					sendBtn.setText("Send");
					TextField inputText = new TextField();
					inputText.setPromptText("Enter a message: ");
					sendBtn.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							String sending = inputText.getText();
							String messageSubstring = sending.substring(2, sending.length());
							client.sendMessage(messageSubstring);
							String chatBoxMessage = "Sent: " + messageSubstring + "\n";
							chatBox.appendText(chatBoxMessage);
							inputText.clear();
						}

					});
					Text idText = new Text();
					idText.setText("Client ID's");
					Text chatText = new Text();
					chatText.setText("Chat Box");
					chatGridGui.add(idText, 0, 0);
					chatGridGui.add(idBox, 0, 1);
					chatGridGui.add(refreshBtn, 0, 2);
					chatGridGui.add(chatText, 1, 0);
					chatGridGui.add(chatBox, 1, 1, 2, 1);
					chatGridGui.add(inputText, 1, 2);
					chatGridGui.add(sendBtn, 2, 2);
					Scene chatScene = new Scene(chatGridGui, 400, 300);
					idBox.setPrefWidth(50);
					primaryStage.setScene(chatScene);
				}
				
			}
			
			
		});
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25,25,25,25));
		grid.add(ipText, 0, 0);
		grid.add(ipField, 0, 1);
		grid.add(portText, 0, 2);
		grid.add(portField, 0, 3);
		grid.add(connectBtn, 0, 4);

		
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
