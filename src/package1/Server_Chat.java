package package1;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

public class Server_Chat {
    public static ConcurrentHashMap<Integer, Socket> clientMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Thread> threadMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, SecretKey> keyMap = new ConcurrentHashMap<>();

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
            Runnable r = new ServerHandler(clientSocket, clientId);
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
    private PrivateKey privKey;
    Socket clientSocket;
    int currentId;

    ServerHandler(Socket incomingSocket, int clientId) {
        clientSocket = incomingSocket;
        currentId = clientId;
    }

    @Override
    public void run() {

        boolean on = true;
        byte encryptedKey[] = new byte[1024];
        byte encryptedText[] = new byte[1024];
        byte decryptedText[];
        int dataLength;
        BufferedInputStream inFromClient = null;

        try{
            inFromClient.read(encryptedKey, 0 , 1024);
            dataLength = encryptedKey[1];
            System.arraycopy(encryptedKey, 1, encryptedKey, 1, dataLength);

            byte decryptedKey[] = RSADecrypt(encryptedKey);
            SecretKey ds = new SecretKeySpec(decryptedKey, "AES");
            Server_Chat.keyMap.put(currentId, ds);
        }
        catch (Exception e){
            //
        }

        // Client now connected
        System.out.println("Client connected.");
        while (on) {
            try {

                BufferedReader inFromClient2 = null;
                inFromClient = new BufferedInputStream(clientSocket.getInputStream());
                inFromClient2 = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = null;

                while ((inFromClient.read(encryptedText, 0, 1024)) > 0) {

                    String sendMessage;
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
                        Server_Chat.clientMap.get(targetId).close();
                        Thread.currentThread().interrupt();
                        on = false;
                        return;
                    }

                    // Set target client to send message to
                    Socket targetSocket = Server_Chat.clientMap.get(id);
                    PrintWriter outToClient = new PrintWriter(targetSocket.getOutputStream(), true);
                    // Set the sending message and send to client
                    sendMessage = message.substring(2, message.length());

                    //Kick a client
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

    // Read prviate key from file
    public void setPrivateKey(String filename) {
        try {
            File f = new File(filename);
            FileInputStream fs = new FileInputStream(f);
            byte[] keybytes = new byte[(int) f.length()];
            fs.read(keybytes);
            fs.close();
            PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(keybytes);
            KeyFactory rsafactory = KeyFactory.getInstance("RSA");
            privKey = rsafactory.generatePrivate(keyspec);
        } catch (Exception e) {
            System.out.println("Private Key Exception");
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }

    // Asymmetric Decryption
    public byte[] RSADecrypt(byte[] ciphertext) {
        try {
            Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            c.init(Cipher.DECRYPT_MODE, privKey);
            byte[] plaintext = c.doFinal(ciphertext);
            return plaintext;
        } catch (Exception e) {
            System.out.println("RSA Decrypt Exception");
            System.exit(1);
            return null;
        }
    }

    // Symmetric encryption
    public byte[] encrypt(byte[] plaintext, SecretKey secKey, IvParameterSpec iv) {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, secKey, iv);
            byte[] ciphertext = c.doFinal(plaintext);
            return ciphertext;
        } catch (Exception e) {
            System.out.println("AES Encrypt Exception");
            System.exit(1);
            return null;
        }
    }

    // Symmetric Decryption
    public byte[] decrypt(byte[] ciphertext, SecretKey secKey, IvParameterSpec iv) {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secKey, iv);
            byte[] plaintext = c.doFinal(ciphertext);
            return plaintext;
        } catch (Exception e) {
            System.out.println("AES Decrypt Exception");
            System.exit(1);
            return null;
        }
    }
}