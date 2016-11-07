package package1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

public class Server_Chat {
    public static ConcurrentHashMap<Integer, Socket> clientMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Socket> getClientMap() {
        return clientMap;
    }

    public static void main(String args[]) throws Exception {

        boolean on = true;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        // Setup reader for user input (in terminal).
        ServerSocket listenSocket = new ServerSocket();
        int clientId = 1;
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
            // Accept new connections.
            // Create a new serverHandler instance for each connection.
            // create new socket/port for client.
            Socket clientSocket = listenSocket.accept();
            clientMap.put(clientId, clientSocket);
            Runnable r = new ServerHandler(clientSocket, clientMap);
            Thread t = new Thread(r);
            // Start new thread
            t.start();
            clientId++;
        }

    }
}

class ServerHandler implements Runnable {
    Socket clientSocket;
    ConcurrentHashMap<Integer, Socket> clientMap = new ConcurrentHashMap<>();
    // Directory to scan for files.

    ServerHandler(Socket incomingSocket, ConcurrentHashMap<Integer, Socket> incomingMap) {
        clientSocket = incomingSocket;
        clientMap = incomingMap;
    }

    private void getCurrentMap() {
        clientMap = Server_Chat.getClientMap();
    }

    @Override
    public void run() {

        boolean on = true;

        // Client now connected
        System.out.println("Client connected.");
        while (on) {
            try {
                BufferedReader inFromClient = null;
                inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = null;
                while ((message = inFromClient.readLine()) != null) {
                    System.out.println("Received: " + message);
                    String sendMessage;
                    int id = Integer.parseInt(message.substring(0, 1));

                    if(id == 0){
                        for (int i = 1; i <= clientMap.size(); i++){
                            Socket currentSocket = clientMap.get(i);
                            PrintWriter outToClient = new PrintWriter(currentSocket.getOutputStream(),true);
                            outToClient.println(message.substring(2, message.length()));
                        }
                        continue;
                    }
                    if(id == 9){
                    	//Send ID's
                    	KeySetView<Integer, Socket> keySet = clientMap.keySet();
                    	StringJoiner joiner = new StringJoiner(",");
                        PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(),true);
                    	for(Integer name : keySet){
                    		joiner.add(name.toString());
                    	}
                        outToClient.println("9" + joiner.toString());
                    	continue;
                    }

                    // Set target client to send message to
                    Socket targetSocket = clientMap.get(id);
                    PrintWriter outToClient = new PrintWriter(targetSocket.getOutputStream(),true);
                    // Set the sending message and send to client
                    sendMessage = message.substring(2, message.length());
                    outToClient.println(sendMessage);

                    if (sendMessage.equals("Kick")) {
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
    }

    private ConcurrentHashMap<Integer, Socket> getMap() {
        return this.clientMap = Server_Chat.clientMap;
    }
}