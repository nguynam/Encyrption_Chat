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
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class Client_Chat {
	PrintWriter outToServer;
	// BufferedInputStream bis;
	BufferedReader inFromServer;
	Socket clientSocket;
	boolean on = true;
	cryptotest crypto = new cryptotest();
	SecretKey secretKey;
	public void closeSocket() throws IOException {
		clientSocket.close();
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public Boolean connect(String ServerIp, String ServerPort) {
		// Return true for successful connect otherwise false
		int port = Integer.parseInt(ServerPort);
		if (port > 65535) {
			return false;
		}
		try {
			clientSocket = new Socket(ServerIp, port);
			outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// Send new private key as encoded string
			sendMessage(Base64.encode(crypto.RSAEncrypt(getNewPrivateKey().getEncoded())),false);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	public Boolean sendMessage(String toSend, Boolean addEncryption) {
		if (addEncryption) {
			SecureRandom r = new SecureRandom();
			byte ivbytes[] = new byte[16];
			r.nextBytes(ivbytes);
			IvParameterSpec iv = new IvParameterSpec(ivbytes);
			byte[] encryptedMessage = crypto.encrypt(toSend.getBytes(), secretKey, iv);
			byte[] sendingBytes = new byte[encryptedMessage.length + 16];
			for(int index = 0; index < sendingBytes.length; index++){
				//Create new byte[] with first 16 bytes containing the IV, and rest is encrypted message
				if(index < 16){
					//Add IV byte
					sendingBytes[index] = ivbytes[index];
				}else{
					//Add encrypted message byte
					sendingBytes[index] = encryptedMessage[index-16];
				}
			}
			//Send completed byte array as string.
			outToServer.println(Base64.encode(sendingBytes));

		} else {
			outToServer.println(toSend);
		}
		return true;
	}

	private SecretKey getNewPrivateKey() {
		crypto.setPublicKey("RSApub.der");
		secretKey = crypto.generateAESKey();
		return secretKey;
	}

	public String getLine() {
		String currentText;
		try {
			while ((currentText = inFromServer.readLine()) != null && on) {
				// Display received text.
				// Decrypt message
				byte[] stringAsBytes = Base64.decode(currentText);
				byte[] ivBytes = Arrays.copyOfRange(stringAsBytes, 0, 16);
				byte[] encryptedMessage = Arrays.copyOfRange(stringAsBytes, 16, stringAsBytes.length);
				byte[] decryptedMessage = crypto.decrypt(encryptedMessage, secretKey, new IvParameterSpec(ivBytes));
				String recievedMessage = new String(decryptedMessage);
				return recievedMessage;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return null;
	}
}