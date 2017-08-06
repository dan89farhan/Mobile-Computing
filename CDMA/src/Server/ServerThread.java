package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Client.ChipCode;

public class ServerThread extends Thread {

	public SocketServer server = null;
	public Socket socket = null;
	public int ID = -1;
	public String mobile = "";
	// public PrintWriter Out;
	// public BufferedReader In;
	// public InputStreamReader Ins;

	
	
	ObjectOutputStream Out;
	ObjectInputStream In;

	ChipCode cc = null;
	boolean assignChipCode = false;
	
	public ServerThread(SocketServer _server, Socket _socket) {
		super();
		// cc = new ChipCode();

		server = _server;
		socket = _socket;
		ID = socket.getPort();

	}

	public void open() throws IOException {
		// Out = new PrintWriter(socket.getOutputStream());

		Out = new ObjectOutputStream(socket.getOutputStream());
		Out.flush();

		// Ins = new InputStreamReader(socket.getInputStream());
		// In = new BufferedReader(Ins);

		In = new ObjectInputStream(socket.getInputStream());

	}

	@SuppressWarnings("deprecation")
	public void run() {
		System.out.println("\nServer Thread " + ID + " running.\n");
		while (true) {
			try {
				// String msg = In.readLine();
				cc = (ChipCode) In.readObject();
				
				System.out.println("Incomming: " + cc+"\n\n");
				if(!assignChipCode){
					switch (SocketServer.clientCount) {
					case 1:
						cc.chipCode[0] = 1;
						cc.chipCode[1] = 1;
						break;
					case 2:
						cc.chipCode[0] = 1;
						cc.chipCode[1] = -1;
						break;

					}

					assignChipCode = true;
				}
				
				// System.out.println(cc.toString());
				// String data[] = msg.split(":");
				server.handle(ID, cc);

			} catch (Exception ioe) {
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				ioe.printStackTrace();
				SocketServer.clientCount--;
				server.remove(ID);

				stop();
			}
		}
	}

	public void send(ChipCode cc) {
		try {
			// Out.println(msg);
			Out.reset();
			Out.writeObject(cc);
			Out.flush();
			System.out.println("Outgoing : " + cc+"\n\n");
		} catch (Exception ex) {
			System.out.println("Exception [SocketClient : send(...)]");
		}
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (In != null)
			In.close();
		if (Out != null)
			Out.close();
	}

	public int getID() {
		return ID;
	}

}
