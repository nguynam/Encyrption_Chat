package package1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client_Chat {
	DataOutputStream outToServer;
	BufferedInputStream bis;
	Socket clientSocket;

	public void run(){
		boolean on = true;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// Setup input and output streams
		while (on) {
			// Code goes here
		}
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
			bis = new BufferedInputStream(clientSocket.getInputStream());
			outToServer.close();
			bis.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
}
