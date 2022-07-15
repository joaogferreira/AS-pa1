package HC.Main;

import HC.ActiveEntities.TServerClient;
import HC.ActiveEntities.TCallCentre;
import HC.ActiveEntities.TCashier;
import HC.ActiveEntities.TDoctor;
import HC.ActiveEntities.TNurse;
import HC.ActiveEntities.TPatient;
import HC.EntranceHallSharedArea.MEntranceHallSharedArea;
import HC.EvaluationHallSharedArea.MEvaluationHallSharedArea;
import HC.Logger.MLogger;
import HC.MedicalHallSharedArea.MMedicalHallSharedArea;
import HC.PaymentHallSharedArea.MPaymentHallSharedArea;
import HC.WaitingHallSharedArea.MWaitingHallSharedArea;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.text.BadLocationException;


public class HCP_Main {
    
    /*
        Socket SERVER
    */
    static int port = 2345;
    static HCP_GUI gui;
    static TNurse nurse;
    static TDoctor doctor;
    static TCashier cashier;
    static ReentrantLock rl = new ReentrantLock();
   

    
    public static void main(String[] args) throws IOException{
        
        gui = new HCP_GUI();
        gui.setVisible(true);
        
        
        // Logger
        MLogger logger = new MLogger();
                
        
        // Shared Areas
        MEntranceHallSharedArea sharedETH = new MEntranceHallSharedArea(logger);
        MEvaluationHallSharedArea sharedEVH = new MEvaluationHallSharedArea(logger);
        MWaitingHallSharedArea sharedWTH = new MWaitingHallSharedArea(logger);
        MMedicalHallSharedArea sharedMDH = new MMedicalHallSharedArea(logger);
        MPaymentHallSharedArea sharedPYH = new MPaymentHallSharedArea(logger);
        
        
        // receive new messages
        try{
            ServerSocket server=new ServerSocket(8888);
            int counter=0;
            System.out.println("Server Started ...");
            while(true){
              counter++;
              Socket serverClient = server.accept();  //server accept the client connection request
              //System.out.println(" >> " + "Client No:" + counter + " started!");
              
              TServerClient sct = new TServerClient(serverClient, counter, gui, 
                     sharedETH, sharedEVH, sharedWTH, sharedMDH, sharedPYH, logger); //send  the request to a separate thread
              
              sct.start();
            }
          }catch(IOException e){
            System.out.println(e);
          }

    }

    public static void appendPatientToEntranceHallFirstRoom(String id){
        rl.lock();
        try {
            gui.addTextToEntranceHallFirstRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void appendPatientToEntranceHallSecondRoom(String id){
        rl.lock();
        try {
            gui.addTextToEntranceHallSecondRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void clearEntranceHallSecondRoom(String id){
        rl.lock();
        try {
            gui.clearTextEntranceHallSecondRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void appendPatientToEvaluationHall(TPatient patient, String id){
        rl.lock();
        try {
            String room = gui.addTextToEvaluationHall(id);
            patient.setEvaluationRoom(room);
        } finally {
            rl.unlock();
        }
    }

    public static void clearEvaluationHall(String id) {
        rl.lock();
        try {
            gui.clearTextEvaluationHall(id);
        } finally {
            rl.unlock();
        }
    }

    public static void appendPatientToWaitingHallFirstRoom(String id) throws BadLocationException {
        rl.lock();
        try {
            gui.appendTextToWaitingHallFirstRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void appendPatientToWaitingHallSecondRoom(String id) throws BadLocationException{
        rl.lock();
        try {
            gui.addTextToWaitingHallSecondRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void clearWaitingHallSecondRoom(String id){
        rl.lock();
        try {
            gui.clearTextWaitingHallSecondRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void appendPatientToMedicallHallFirstRoom(String id){
        rl.lock();
        try {
            gui.addTextToMedicalHallFirstRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void clearMedicalHallFirstRoom(String id){
        rl.lock();
        try{
            gui.clearTextMedicalHallFirstRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void appendPatientToMedicallHallSecondRoom(TPatient patient, String id){
        rl.lock();
        try{
            String room = gui.addTextToMedicalHallSecondRoom(id);
            patient.setMedicalRoom(room);
        } finally {
            rl.unlock();
        }
    }
    
    public static void clearMedicalHallSecondRoom(String id){
        rl.lock();
        try{
            gui.clearTextMedicalHallSecondRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void appendPatientToPaymentHallFirstRoom(String id){
        rl.lock();
        try{
            gui.addTextToPaymentHallFirstRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void clearPaymentHallFirstRoom(String id){
        rl.lock();
        try{
            gui.clearTextPaymentHallFirstRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void appendPatientToPaymentHallSecondRoom(String id){
        rl.lock();
        try{
            gui.addTextToPaymentHallSecondRoom(id);
        } finally {
            rl.unlock();
        }
    }
    
    public static void clearPaymentHallSecondRoom(String id){
        rl.lock();
        try{
            gui.clearTextPaymentHallSecondRoom(id);
        } finally {
            rl.unlock();
        }
    }
}
