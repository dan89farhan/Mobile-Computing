package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class SocketClient implements Runnable{

    public int port;
    public String serverAddr;
    public Socket socket;
    CDMAClient cdmaClient = null;
    //public PrintWriter Out;
    //public BufferedReader In;
    //public InputStreamReader Ins;
    
    ObjectOutputStream Out;
    ObjectInputStream In;
    
    ChipCode cc = null;
    //public int chipArr[] = null;
    
    public SocketClient(CDMAClient cdmaClient, ChipCode cc) throws IOException {
        
        
        
        this.cc = cc;
        this.cdmaClient = cdmaClient;
        this.serverAddr = "localhost";
        this.port = 13000;
        socket = new Socket(InetAddress.getByName(serverAddr), port);
        //Out = new PrintWriter(socket.getOutputStream());
        Out = new ObjectOutputStream(socket.getOutputStream());
        
        Out.flush();
        
        //Ins = new InputStreamReader(socket.getInputStream());
        //In =new BufferedReader(Ins);
        
        In = new ObjectInputStream(socket.getInputStream());
        
    }
    
    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            
            try {
                
                //String msg = In.readLine();
                
                cc = (ChipCode)In.readObject();
                System.out.println("Incoming : "+cc);
                //String data[] = msg.split(":");
                if(cc.status.equals("signout")){
                    cdmaClient.clientThread.stop();
                    System.exit(0);
                }
                else if(cc.status.equals("test")){
                    System.out.println("Connection Done");
                }
            }
            catch(Exception ex){
                System.out.println("Error in run "+ex);
            }
            
        }
    }
    
    public void send(ChipCode cc) {
        try {
            System.out.println("Outgoing : "+cc.message);
            Out.writeObject(cc);
            Out.flush();
            System.out.println("Outgoing : "+cc.toString());
            
            //String data[] = msg.split(":");
            if(cc.status.equals("message") && !cc.status.equals(".bye")){
                String msgTime = (new Date()).toString();
                
            }
        } 
        catch (Exception ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
}