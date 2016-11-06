package package1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class Client_Chat {
	DataOutputStream outToServer;
	//BufferedInputStream bis;
	BufferedReader inFromServer;
	Socket clientSocket;

	public void run(){
		boolean on = true;
		//BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		// Setup input and output streams

	}

	public Boolean connect (String ServerIp, String ServerPort){
		//Return true for successful connect otherwise false
		int port = Integer.parseInt(ServerPort);
		if(port > 65535){
			return false;
		}
		try {
			clientSocket = new Socket(ServerIp, port);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			//outToServer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public Boolean sendMessage(String toSend){
		try {
			outToServer.write(toSend.getBytes());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public String getLine(){
		String currentText;
		try {
			while((currentText = inFromServer.readLine()) != null){
				//Display received text.
				return currentText;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return null;
	}
}