package package1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class Server_Chat {
    public static ConcurrentHashMap<Integer, Socket> clientMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Thread> threadMap = new ConcurrentHashMap<>();

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
            Runnable r = new ServerHandler(clientSocket);
            Thread t = new Thread(r);
            clientMap.put(clientId, clientSocket);
            threadMap.put(clientId, t);

            // Start new thread
            t.start();
            clientId++;
        }

    }
}

class ServerHandler implements Runnable {
    Socket clientSocket;
    SecretKey secretKey = null;
    byte[] iv = {(byte) 141,42,58,(byte) 172,66,(byte) 129,49,77,(byte) 218,21,116,26,(byte) 241,44,34,2};
    cryptotest crypto = new cryptotest();
    
    ServerHandler(Socket incomingSocket) {
        clientSocket = incomingSocket;
        
        //Set encryption keys
        crypto.setPrivateKey("RSApriv.der");
		crypto.setPublicKey("RSApub.der");
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
                	byte[] stringToBytes = Base64.decode(message);
                	if(secretKey == null){
                    	//Secret Key not established
                    	byte[] secretKeyBytes = crypto.RSADecrypt(stringToBytes);
                    	secretKey = new SecretKeySpec(secretKeyBytes, "AES");
                    	continue;
                    }
                	//Decrypt message
                	byte[] ivBytes = Arrays.copyOfRange(stringToBytes,0,15);
                	byte[] encryptedMessage = Arrays.copyOfRange(stringToBytes, 15, stringToBytes.length);
                	byte[] decryptedMessage = crypto.decrypt(encryptedMessage, secretKey, new IvParameterSpec(ivBytes));
                	String sendMessage = new String(decryptedMessage);

                    int id = Integer.parseInt(message.substring(0, 1));

                    if (id == 0) {
                        for (int i = 1; i <= Server_Chat.clientMap.size(); i++) {
                            Socket currentSocket = Server_Chat.clientMap.get(i);
                            PrintWriter outToClient = new PrintWriter(currentSocket.getOutputStream(), true);
                            outToClient.println(message.substring(2, message.length()));
                        }
                        continue;
                    }
                    if(id == 9){
                    	//Send ID's
                    	KeySetView<Integer, Socket> keySet = Server_Chat.clientMap.keySet();
                    	StringJoiner joiner = new StringJoiner(",");
                        PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(),true);
                    	for(Integer name : keySet){
                    		joiner.add(name.toString());
                    	}
                        outToClient.println("9" + joiner.toString());
                    	continue;
                    }
                    if(id == 8){
                    	//Client is exiting
                    	Integer targetId = null;
                    	for(Integer x : Server_Chat.clientMap.keySet()){
                    		if(Server_Chat.clientMap.get(x).equals(clientSocket)){
                    			//Found user id;
                    			targetId = x;
                    			break;
                    		}
                    	}
                    	System.out.println("Client " + targetId.toString() + " Disconnected.");
                        //Thread closingThread = Server_Chat.threadMap.get(targetId);
                        Server_Chat.clientMap.remove(targetId);
                        Server_Chat.threadMap.remove(targetId);
                        //Server_Chat.clientMap.get(targetId).close();
                        Thread.currentThread().interrupt();
                        return;
                    }

                    // Set target client to send message to
                    Socket targetSocket = Server_Chat.clientMap.get(id);
                    PrintWriter outToClient = new PrintWriter(targetSocket.getOutputStream(), true);
                    // Set the sending message and send to client
                    sendMessage = message.substring(2, message.length());

                    if (sendMessage.equals("Kick")) {
                        System.out.println("Client " + id + " Disconnected.");
                        Thread closingThread = Server_Chat.threadMap.get(id);
                        Server_Chat.clientMap.get(id).close();
                        closingThread.interrupt();
                        Server_Chat.clientMap.remove(id);
                        Server_Chat.threadMap.remove(id);
                        continue;
                    }
                    outToClient.println(sendMessage);
                }
            } catch (Exception e) {
            }

        }
    }
}