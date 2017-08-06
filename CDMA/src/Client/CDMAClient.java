
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CDMAClient implements Runnable {

	public SocketClient client;
	public int port = 13000;
	public String serverAddr = "localhost";
	public Thread clientThread;

	public String number = "";
	ChipCode cc = null;
	public boolean serverConnect = false;

	InputStreamReader Ins = null;
	BufferedReader In = null;
	
	CDMAClient() {
		try {
			Ins = new InputStreamReader(System.in);
			In = new BufferedReader(Ins);
			
			cc = new ChipCode();
			client = new SocketClient(this, cc);

			clientThread = new Thread(client);
			clientThread.start();
			
			cc.status = "connection";
			cc.message = "assign chip code";
			cc.to = "SERVER";
			client.send(cc);
			
			setMobileNumber();
			
		} catch (Exception ex) {
			System.out.println("[Application > Me] : Server not found\n " + ex);
			ex.printStackTrace();
		}
	}
	
	void setMobileNumber(){
		try {
			System.out.println("Enter Your mobile number");
			number = In.readLine();
			cc.status = "reg";
			cc.message = number;
			cc.to = "SERVER";
			
			client.send(cc);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public static void main(String args[]) {
		CDMAClient cdmaClient = new CDMAClient();
		
		Thread t = new Thread(cdmaClient);
		t.start();
	}

	@Override
	public void run() {
		cc.from = number;
		cc.status = "message";
		while (true) {
			try {
				System.out.println("Enter your mssg.");
				String msg = In.readLine();
				
				System.out.println("To whome you want to send message.");
				String toWhome = In.readLine();
				//int data = Integer.parseInt(msg);
				
				cc.message = msg;
				cc.to = toWhome;
				client.send(cc);
				System.out.println("Your message " + msg);
			} catch (IOException ex) {
				System.out.println("Error in reading "+ex);
				ex.printStackTrace();
			}
		}

	}
}
