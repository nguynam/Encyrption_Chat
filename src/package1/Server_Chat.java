package package1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class Server_Chat {
	public static ConcurrentHashMap <Integer, Socket> idMap = new ConcurrentHashMap<>();

	public static ConcurrentHashMap<Integer, Socket> getIdMap(){
		return idMap;
	}

	public static void main(String args[]) throws Exception {
		boolean on = true;

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		// Setup reader for user input (in terminal).
		ServerSocket listenSocket = new ServerSocket();

		// Continue prompting user for new port if binding is unsuccessful
		while (listenSocket.isBound() == false) {
			try {
				System.out.println("Enter a port for the server: ");
				int listenPort = Integer.parseInt(inFromUser.readLine());
				listenSocket = new ServerSocket(listenPort);
			} catch (Exception b) {
				System.out.println("Port invalid or in use. Try a new port.");
			}

		}
		while (on) {
			int id = 1;

			// create new socket/port for client.
			Socket clientSocket = listenSocket.accept();
			idMap.put(id,clientSocket);

			ServerHandler r = new ServerHandler(clientSocket);
			Thread t = new Thread(r);
			// Start new thread
			t.start();
			id++;
		}

	}
}

class ServerHandler implements Runnable {
	Socket clientSocket;
	ConcurrentHashMap <Integer, Socket> threadMap = new ConcurrentHashMap<>();

	ServerHandler(Socket incomingSocket) {
		clientSocket = incomingSocket;
	}

	@Override
	public void run() {
		try {
			boolean on = true;

			// Client now connected
			System.out.println("Client connected.");
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
			while (on) {
				String requestedFile = inFromClient.readLine();
				// Capture requested file name.
				if (requestedFile.equals("Exit")) {
					on = false;
					System.out.println("Client Disconnected.");
					clientSocket.close();
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	private ConcurrentHashMap<Integer, Socket> getMap(){
		return this.threadMap = Server_Chat.idMap;
	}
}
