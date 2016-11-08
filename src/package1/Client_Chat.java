package package1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class Client_Chat {
	PrintWriter outToServer;
	//BufferedInputStream bis;
	BufferedReader inFromServer;
	Socket clientSocket;
	boolean on = true;
	
	public void closeSocket() throws IOException{
		clientSocket.close();
	}
	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public Boolean connect (String ServerIp, String ServerPort){
		//Return true for successful connect otherwise false
		int port = Integer.parseInt(ServerPort);
		if(port > 65535){
			return false;
		}
		try {
			clientSocket = new Socket(ServerIp, port);
			//outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			//outToServer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public Boolean sendMessage(String toSend){
		outToServer.println(toSend);
		//outToServer.newLine();
		//outToServer.flush();
		return true;
	}

	public String getLine(){
		String currentText;
		try {
			while((currentText = inFromServer.readLine()) != null && on){
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