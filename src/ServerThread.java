import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

	public SocketServer server = null;
	public Socket socket = null;
	public int ID = -1;
	public String username = "";
	public PrintWriter Out;
	public BufferedReader In;
	public InputStreamReader Ins;

	public ServerThread(SocketServer _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort();

	}

	public void open() throws IOException {
		Out = new PrintWriter(socket.getOutputStream());
		Out.flush();

		Ins = new InputStreamReader(socket.getInputStream());
		In = new BufferedReader(Ins);

	}

	public void run() {
		System.out.println("\nServer Thread " + ID + " running.\n");
		while (true) {
			try {
				String msg = In.readLine();
				System.out.println(msg);
				String data[] = msg.split(":");
				server.handle(ID, data);

			} catch (Exception ioe) {
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}

	public void send(String msg) {
		try {
			Out.println(msg);
			Out.flush();
			System.out.println("Outgoing : " + msg);
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

	public int getID(){  
	    return ID;
    }


}
