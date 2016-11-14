package package1;

import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Client_Chat {
	private PublicKey pubKey;
	DataOutputStream outToServer;
	byte encryptedsecret[];
	byte sendData[] = new byte[1024];
	byte IV[] = new byte[16];
	byte[] dataLength = new byte[1];
	private SecretKey key;

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
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			//outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			key = generateAESKey();
			SecureRandom r = new SecureRandom();
			encryptedsecret = RSAEncrypt(key.getEncoded());
			Integer length = encryptedsecret.length;
			dataLength[1] = length.byteValue();

			System.arraycopy(dataLength, 0, sendData, 0, 1);
			System.arraycopy(encryptedsecret, 0, sendData, 1, length);

			outToServer.write(sendData, 0, sendData.length);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public Boolean sendMessage(byte[] toSend){
		try{
			outToServer.write(toSend, 0, toSend.length);
		}
		catch (Exception e){
			//
		}
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

	// Creates symmetric key
	public SecretKey generateAESKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			SecretKey secKey = keyGen.generateKey();
			return secKey;
		} catch (Exception e) {
			System.out.println("Key Generation Exception");
			System.exit(1);
			return null;
		}
	}

	// Read public key from file
	public void setPublicKey(String filename) {
		try {
			File f = new File(filename);
			FileInputStream fs = new FileInputStream(f);
			byte[] keybytes = new byte[(int) f.length()];
			fs.read(keybytes);
			fs.close();
			X509EncodedKeySpec keyspec = new X509EncodedKeySpec(keybytes);
			KeyFactory rsafactory = KeyFactory.getInstance("RSA");
			pubKey = rsafactory.generatePublic(keyspec);
		} catch (Exception e) {
			System.out.println("Public Key Exception");
			System.exit(1);
		}
	}

	// Asymmetric Encryption
	public byte[] RSAEncrypt(byte[] plaintext) {
		try {
			Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
			c.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] ciphertext = c.doFinal(plaintext);
			return ciphertext;
		} catch (Exception e) {
			System.out.println("RSA Encrypt Exception");
			System.exit(1);
			return null;
		}
	}

	public SecretKey getKey() {
		return key;
	}
}