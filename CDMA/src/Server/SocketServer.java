package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable {
	public ServerSocket server = null;
	public ServerThread clients[];
	public Thread thread = null;
	public int clientCount = 0, port = 13000;

	public SocketServer() {
		clients = new ServerThread[50];
		try {
			server = new ServerSocket(port);
			port = server.getLocalPort();
			System.out.println(
					"Server startet. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort());
			start();
		} catch (IOException ioe) {
			System.out.println("Can not bind to port : " + port + "\nRetrying");
			// ui.RetryStart(0);
		}

	}

	private void start() {
		if (thread == null) {

			thread = new Thread(this);
			thread.start();

		}
	}

	@Override
	public void run() {
		while (thread != null) {
			try {
				System.out.println("\nWaiting for a client ...\n");
				addThread(server.accept());
			} catch (Exception ioe) {
				System.out.println("\nServer accept error: ");

			}
		}

	}

	private void addThread(Socket socket) {
		if (clientCount < clients.length) {
			System.out.println("\nClient accepted: " + socket);
			clients[clientCount] = new ServerThread(this, socket);
			try {
				clients[clientCount].open();
				clients[clientCount].start();
				clientCount++;
			} catch (IOException ioe) {
				System.out.println("\nError opening thread: " + ioe);
			}
		} else {
			System.out.println("\nClient refused: maximum " + clients.length + " reached.");
		}
	}

	public void handle(int ID, String[] data) {
		if (data[2].equals(".bye")) {
			Announce("signout", "SERVER", data[1]);
			remove(ID);
		}
                else if(data[2].equals("connection")){
                    clients[findClient(ID)].send("test:SERVER:OK:"+data[1]);
                    
                }
                //else if(data[])

	}

	public void remove(int ID) {
		int pos = findClient(ID);
		if (pos >= 0) {
			ServerThread toTerminate = clients[pos];
			System.out.println("\nRemoving client thread " + ID + " at " + pos);
			if (pos < clientCount - 1) {
				for (int i = pos + 1; i < clientCount; i++) {
					clients[i - 1] = clients[i];
				}
			}
			clientCount--;
			try {
				toTerminate.close();
			} catch (IOException ioe) {
				System.out.println("\nError closing thread: " + ioe);
			}
			toTerminate.stop();
		}
	}

	
	public void Announce(String type, String sender, String content) {
		String msg = type + ":" + sender + ":" + content + ":" + "All";
		// Message msg = new Message(type, sender, content, "All");
		for (int i = 0; i < clientCount; i++) {
			clients[i].send(msg);
		}
	}

	private int findClient(int ID) {
		for (int i = 0; i < clientCount; i++) {
			if (clients[i].getID() == ID) {
				return i;
			}
		}
		return -1;
	}
}
