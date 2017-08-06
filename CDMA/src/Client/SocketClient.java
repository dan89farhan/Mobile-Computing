package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClient implements Runnable {

	public int port;
	public String serverAddr;
	public Socket socket;
	CDMAClient cdmaClient = null;
	// public PrintWriter Out;
	// public BufferedReader In;
	// public InputStreamReader Ins;

	ObjectOutputStream Out;
	ObjectInputStream In;

	ChipCode cc = null;
        ChipCode cc1 = null;
	// public int chipArr[] = null;

	public SocketClient(CDMAClient cdmaClient, ChipCode cc) throws IOException {

		this.cc = cc;
		this.cdmaClient = cdmaClient;
		this.serverAddr = "localhost";
		this.port = 13000;
		socket = new Socket(InetAddress.getByName(serverAddr), port);
		// Out = new PrintWriter(socket.getOutputStream());
		Out = new ObjectOutputStream(socket.getOutputStream());

		Out.flush();

		// Ins = new InputStreamReader(socket.getInputStream());
		// In =new BufferedReader(Ins);

		In = new ObjectInputStream(socket.getInputStream());

	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
            boolean once_time = false;
		boolean keepRunning = true;
		while (keepRunning) {

			try {

				// String msg = In.readLine();
                                if(!once_time){
                                    cc1 = (ChipCode) In.readObject();
                                    once_time = true;
                                    System.out.println("In if once time");
                                }
                                else{
                                    cc = (ChipCode) In.readObject();
                                    System.out.println("In else once time");
                                }
				
				System.out.println("Incoming : " + cc);
				// String data[] = msg.split(":");
				if (cc.status.equals("signout")) {
					cdmaClient.clientThread.stop();
					System.exit(0);
				} else if (cc.status.equals("test")) {
					System.out.println("Connection Done");
				}
                                else if(cc.status.equals("message")){
                                    
                                }
			} catch (Exception ex) {
				System.out.println("Error in run " + ex);
			}

		}
	}

	public void send(ChipCode cc) {
		try {
			Out.writeObject(cc);
			Out.flush();
			System.out.println("Outgoing : " + cc.toString());

			// String data[] = msg.split(":");
			
		} catch (Exception ex) {
			System.out.println("Exception SocketClient send()");
		}
	}
}