
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CDMAClient implements Runnable {
    
    public SocketClient client;
    public int port=13000;
    public String serverAddr="localhost";
    public Thread clientThread;
    
    public boolean serverConnect = false;
    
    InputStreamReader Ins = null;
    BufferedReader In = null;
    CDMAClient(){
        try{
                client = new SocketClient(this);
                clientThread = new Thread(client);
                clientThread.start();
                serverConnect = true;
                client.send("test:testUser:connection:SERVER");
            }
            catch(Exception ex){
                System.out.println("[Application > Me] : Server not found\n "+ex);
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
                System.out.println("Your message ");
            } catch (IOException ex) {
                System.out.println("Error in reading "+ex);
            }
        }
        
    }
}
