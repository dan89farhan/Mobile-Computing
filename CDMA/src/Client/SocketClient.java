package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class SocketClient implements Runnable{

    public int port;
    public String serverAddr;
    public Socket socket;
    CDMAClient cdmaClient = null;
    public PrintWriter Out;
    public BufferedReader In;
    public InputStreamReader Ins;
    
    public SocketClient(CDMAClient cdmaClient) throws IOException {
        
        this.cdmaClient = cdmaClient;
        this.serverAddr = "localhost";
        this.port = 13000;
        socket = new Socket(InetAddress.getByName(serverAddr), port);
        Out = new PrintWriter(socket.getOutputStream());
        Out.flush();
        
        Ins = new InputStreamReader(socket.getInputStream());
        In =new BufferedReader(Ins);
        
    }
    
    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            
            try {
                
                String msg = In.readLine();
                System.out.println("Incoming : "+msg);
                String data[] = msg.split(":");
                if(data[0].equals("signout")){
                    cdmaClient.clientThread.stop();
                    System.exit(0);
                }
                else if(data[0].equals("test")){
                    System.out.println("Connection Done");
                }
            }
            catch(Exception ex){
                System.out.println("Error in run "+ex);
            }
            
        }
    }
    
    public void send(String msg) {
        try {
            Out.println(msg);
            Out.flush();
            System.out.println("Outgoing : "+msg);
            
            String data[] = msg.split(":");
            if(data[0].equals("message") && !data[0].equals(".bye")){
                String msgTime = (new Date()).toString();
                
            }
        } 
        catch (Exception ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
}