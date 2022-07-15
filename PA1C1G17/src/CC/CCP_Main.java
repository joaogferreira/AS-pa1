package CC;


import java.io.*;
import java.net.Socket;


public class CCP_Main {
    
    /*
        Socket CLIENT
    */
    static Socket socket;
    static DataInputStream inStream;
    static DataOutputStream outStream;
   
    public static void main(String[] args) throws IOException, InterruptedException {
        CCP_GUI gui = new CCP_GUI();
        gui.setVisible(true);
        
        while (CCP_Main.socket == null){
            try{
                CCP_Main.socket = new Socket("127.0.0.1",8888);

                CCP_Main.inStream=new DataInputStream(socket.getInputStream());
                CCP_Main.outStream=new DataOutputStream(socket.getOutputStream());

                String clientMessage="";
                String serverMessage="";
              }catch(IOException e){
                Thread.sleep(2000); //wait connection
            }
        }
        
    }
    
    public static void afterStart(int nAdults, int nChildren, int nSeats, 
            int ttm, String evt, String mdt, String pyt) throws IOException{
        String text = "CC|CREATE|" + nAdults + "|" 
                + nChildren + "|" + nSeats +"|" + ttm + "|" + evt + "|" + mdt+"|"+ pyt;
        sendMessage(text);
    }
    
    public static void pressedStop() throws IOException{
        String text = "CC|STOP";
        
        sendMessage(text);
    }
    
    public static void pressedSuspend() throws IOException {
        String text = "CC|SUSPEND";
        
        sendMessage(text);
    }
    
    public static void pressedResume() throws IOException {
        String text = "CC|RESUME";
        
        sendMessage(text);
    }
    
    public static void pressedEnd() throws IOException{
        String text = "CC|END";
        
        if(CCP_Main.socket != null) {
            sendMessage(text);
        }
        
        System.exit(0);
    }
    
    public static void sendMessage(String text) throws IOException{
        CCP_Main.outStream.writeUTF(text);
    }

}
