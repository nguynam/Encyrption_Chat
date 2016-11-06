package package1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.concurrent.ConcurrentHashMap;

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
            BufferedReader inFromClient = null;
            try {
                inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String message = null;
            try {
                while ((message = inFromClient.readLine()) != null) {
                    System.out.println("Recieved: " + message);
                    String sendMessage;
                    int id = Integer.parseInt(message.substring(0, 1));
                    System.out.println(id);
                    // Set target client to send message to
                    Socket targetSocket = clientMap.get(id);
                    DataOutputStream outToClient = new DataOutputStream(targetSocket.getOutputStream());
                    // Set the sending message and send to client
                    sendMessage = message.substring(2, message.length());
                    outToClient.writeBytes(sendMessage);

                    if (sendMessage.equals("Kick")) {
                        on = false;
                        System.out.println("Client Disconnected.");
                        clientSocket.close();
                        break;
                    }
                }
            } catch (NumberFormatException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private ConcurrentHashMap<Integer, Socket> getMap() {
        return this.clientMap = Server_Chat.clientMap;
    }
}