package serveur;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import utilisateur.Message;

public class Tube implements Runnable {
	private Serveur server;
	private Socket socket;
	private ObjectInputStream inputFromClient;
	private ObjectOutputStream outputToClient;

	public Tube(Serveur server, Socket s) {
		this.server = server;
		this.socket = s;
	}

	public void checkConnection() {
		List<Socket> allSocketsTemp = server.getAllSockets();
		if (!socket.isConnected())
			for (Socket stemp : allSocketsTemp)
				if (stemp.equals(socket))
					allSocketsTemp.remove(stemp);
	}

	@Override
	public void run() {
		try {
			inputFromClient = new ObjectInputStream(socket.getInputStream());
			while (true) {
				checkConnection();

				Object temp = inputFromClient.readObject();
				if (temp != null) {
					if (temp instanceof Message) {
						Message message = (Message) temp;
						broadcast(server.getAllSockets(), message);
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void broadcast(List<Socket> list, Message tobroad)
			throws IOException {
		for (Socket stemp : list) {
			outputToClient = new ObjectOutputStream(stemp.getOutputStream());
			outputToClient.writeObject(tobroad);
			outputToClient.flush();
		}
	}
}
