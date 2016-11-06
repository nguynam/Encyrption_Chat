package package1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client_Chat {
	DataOutputStream outToServer;
<<<<<<< Updated upstream
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
=======
	// BufferedInputStream bis;
	BufferedReader inFromServer;
	Socket clientSocket;

	public void run() {
		boolean on = true;
		// BufferedReader inFromUser = new BufferedReader(new
		// InputStreamReader(System.in));
>>>>>>> Stashed changes
		// Setup input and output streams
		while (on) {
			// Code goes here
		}*/
	}
<<<<<<< Updated upstream
	public Boolean connect (String ServerIp, String ServerPort){
		//Return true for successful connect otherwise false
=======

	public Boolean connect(String ServerIp, String ServerPort) {
		// Return true for successful connect otherwise false
>>>>>>> Stashed changes
		int port = Integer.parseInt(ServerPort);
		if (port > 65535) {
			return false;
		}
		try {
			Socket clientSocket = new Socket(ServerIp, port);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
<<<<<<< Updated upstream
			bis = new BufferedInputStream(clientSocket.getInputStream());
=======
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// outToServer.close();
			return true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public Boolean sendMessage(String toSend) {
		try {
			outToServer.writeBytes(toSend);
>>>>>>> Stashed changes
			return true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
<<<<<<< Updated upstream
}
=======

	public String getLine() {
		String currentText;
		try {
			while ((currentText = inFromServer.readLine()) != null) {
				// Display received text.
				System.out.println(currentText);
				return currentText;
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return null;
	}
}
>>>>>>> Stashed changes
