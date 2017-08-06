
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


 public class CDMAClient implements Runnable {
    
    public SocketClient client;
    public int port=13000;
    public String serverAddr="localhost";
    public Thread clientThread;
    
    ChipCode cc = null;
    public boolean serverConnect = false;
    
    InputStreamReader Ins = null;
    BufferedReader In = null;
    CDMAClient(){
        try{
            cc = new ChipCode();
            client = new SocketClient(this, cc);

            clientThread = new Thread(client);
            clientThread.start();
            serverConnect = true;
            //client.send("test:testUser:connection:SERVER");
            cc.status = "connection";
            cc.message="assign chip code";
            cc.to = "SERVER";
            client.send(cc);
            }
            catch(Exception ex){
                System.out.println("[Application > Me] : Server not found\n "+ex);
                ex.printStackTrace();
            }
    }
    
    public static void main(String args[]){
        CDMAClient cdmaClient = new CDMAClient();
        Thread t = new Thread(cdmaClient);
        t.start();
    }

    @Override
    public void run() {
        Ins = new InputStreamReader(System.in);
        In =new BufferedReader(Ins);
        while(true){
            try {
                String msg = In.readLine();
                int data = Integer.parseInt(msg);
                cc.status = "message";
                cc.message = msg;
                cc.to = "SERVER";
                client.send(cc);
                System.out.println("Your message "+msg);
            } catch (IOException ex) {
                //System.out.println("Error in reading "+ex);
            }
        }
        
    }
}