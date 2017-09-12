import java.io.*;
import java.util.*;
import java.net.*;

public class ChatServer {
	ArrayList outputStream;

	public class ClientService implements Runnable {
		BufferedReader reader;
		Socket socket;

		public ClientService(Socket clientSocket) {
			try {
				socket = clientSocket;
				InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
				reader = new BufferedReader(isReader);
				System.out.println(reader);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("Odczytano: " + message);
					sendToAll(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer().go();
	}

	public void go() {
		outputStream = new ArrayList();
		try {
			ServerSocket serverSock = new ServerSocket(5000);

			while (true) {
				Socket clientSocket = serverSock.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				outputStream.add(writer);

				Thread thread = new Thread(new ClientService(clientSocket));
				thread.start();
				System.out.println("connection established");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendToAll(String message) {
		Iterator it = outputStream.iterator();
		while (it.hasNext()) {
			try {
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				writer.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
