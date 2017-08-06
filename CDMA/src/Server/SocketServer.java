package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.IntStream;

import Client.ChipCode;

public class SocketServer implements Runnable {
	public ServerSocket server = null;
	public ServerThread clients[];
	public Thread thread = null;
	public int port = 13000;
	static int clientCount = 0;

	int mssgCount = 0;

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
				System.out.println("\nServer accept error: " + ioe);

			}
		}

	}

	private void addThread(Socket socket) {
		if (clientCount < clients.length) {
			System.out.println("\nClient accepted: " + socket);
			clients[clientCount] = new ServerThread(this, socket);
			try {
				clientCount++;
				clients[clientCount - 1].open();
				clients[clientCount - 1].start();

			} catch (IOException ioe) {
				System.out.println("\nError opening thread: " + ioe);
			}
		} else {
			System.out.println("\nClient refused: maximum " + clients.length + " reached.");
		}
	}

	public void handle(int ID, ChipCode cc) {
		// System.out.println("In Server Handle "+cc.toString());
		// System.out.println("cc Status "+cc.status);
		if (cc.status.equals(".bye")) {
			// Announce("signout", "SERVER", cc.message);
			remove(ID);
		} else if (cc.status.equals("connection")) {
			// clients[findClient(ID)].send("test:SERVER:OK:"+data[1]);
			clients[findClient(ID)].send(cc);

		} else if (cc.status.equals("reg")) {

			clients[findClient(ID)].mobile = cc.message;

			cc.from = cc.message;
			cc.message = "register Successfull";
			cc.to = "All";

			Announce(cc);

		} else if (cc.status.equals("message")) {
			// Announce("signout", "SERVER", cc.message);

			// cc.message = "wait";

			// findUserThread(cc.to).send(cc);
			mssgCount++;

			calMatrix(cc);

			// clients[findClient(ID)].send(cc);
			// Announce(cc);
			synchronized (clients[findClient(ID)]) {
				try {
					if (mssgCount == 2) {
						awakeAllThread();
						mssgCount = 0;
						addClientsChipcode();
						Announce(cc);
					}
					else{
						clients[findClient(ID)].wait();
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}

			// findUserThread(cc.to).send(cc);
		}
		// else if(data[])

	}

	private void awakeAllThread() {

		try {
			for (int i = 0; i < clientCount; i++) {
				if (clients[i].getState() == Thread.State.WAITING)
					clients[i].notify();
			}
		} catch (Exception ex) {
			System.out.println("Exception in awakeAllThread : " + ex);
			ex.printStackTrace();
		}

	}

	void calMatrix(ChipCode cc) {
		// cc = multiply(cc);
		// int sum = IntStream.of(multiply(cc).chipCode).sum();
		// System.out.println("Sum is " + sum);
		System.out.println("After Multiply " + multiply(cc));
		// addClientsChipcode();

	}

	void addClientsChipcode() {
		int sum = 0;
		for (int i = 0; i < clientCount; i++) {
			sum += IntStream.of(multiply(clients[i].cc).chipCode).sum();
		}
		System.out.println("The sum is " + sum);
	}

	ChipCode multiply(ChipCode cc) {

		try {
			int mssg = Integer.parseInt(cc.message);
			for (int i = 0; i < cc.chipCode.length; i++) {
				cc.chipCode[i] = mssg * cc.chipCode[i];
			}
		} catch (Exception ex) {
			System.out.println("Error in SocketServer.SocketServer " + ex);
			ex.printStackTrace();
		}

		return cc;
	}

	public ServerThread findUserThread(String usr) {
		for (int i = 0; i < clientCount; i++) {
			if (clients[i].mobile.equals(usr)) {
				return clients[i];
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
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

	public void Announce(ChipCode cc) {
		// String msg = type + ":" + sender + ":" + content + ":" + "All";
		// Message msg = new Message(type, sender, content, "All");
		for (int i = 0; i < clientCount; i++) {

			clients[i].send(cc);
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
