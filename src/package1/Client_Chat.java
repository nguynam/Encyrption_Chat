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
	public static void main(String args[]) throws Exception {
		/*String correctAddress = "127.0.0.1";
		boolean on = true;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter server address: ");
		String address = inFromUser.readLine();
		System.out.println("Enter server port: ");
		int port = Integer.parseInt(inFromUser.readLine());
		if (port > 65535) {
			System.out.println("Invalid port number.");
		}
		if (!address.equals(correctAddress)) {
			System.out.println("Invalid address.");
		}
		Socket clientSocket = new Socket(address, port);
		// Create socket with new connection to server.
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
		// Setup input and output streams
		while (on) {
			// Code goes here
		}*/
	}
	public Boolean connect (String ServerIp, String ServerPort){
		//Return true for successful connect otherwise false
		int port = Integer.parseInt(ServerPort);
		if(port > 65535){
			return false;
		}
		try {
			Socket clientSocket = new Socket(ServerIp, port);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			bis = new BufferedInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return false;
	}
}
