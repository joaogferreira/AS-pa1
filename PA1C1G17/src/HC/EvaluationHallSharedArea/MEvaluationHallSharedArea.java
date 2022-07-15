package HC.EvaluationHallSharedArea;

import HC.ActiveEntities.TPatient;
import HC.FIFO.MFIFO;
import HC.Logger.MLogger;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared Area for the Evaluation Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class MEvaluationHallSharedArea implements IEvaluationHall, IEvaluationHall_CallCentre, IEvaluationHall_Patient, IEvaluationHall_Nurse {
    
    /* Reentrant lock used for synchronization */
    private final ReentrantLock rl;
    
    /* Condition for patients to wait to receive DoS (color) */
    private final Condition waitColor;
    
    /* Condition for call centre to wait for free seat to join  */
    private final Condition waitForFreeSeat;
   
    /*Size  Evaluation Hall*/
    private int size;
    
    /*String color (red, yellow, blue */
    private String color;
    
    /* FIFO for rooms */
    private final MFIFO rooms;
    
    /* Logger */
    MLogger logger;
    
    /**
     * MEvaluationHallSharedArea constructor
     * @param logger
     */
    public MEvaluationHallSharedArea(MLogger logger){
        this.rl = new ReentrantLock(true);
        this.waitColor = rl.newCondition();
        this.waitForFreeSeat = rl.newCondition();
        this.size = 0;
        this.rooms = new MFIFO(4);
        this.logger = logger;
    }
    
     /**
     * Patient joins room - FIFO - put()
     * @param patient
     * @return true if patient joins the first room
     */
    
    @Override
    public boolean joinRoom(TPatient patient){
        try {    
            this.rl.lock();
            
            rooms.put(patient);
           
            
            this.incSize();
            
            patient.waitForNurse();
                        
            patient.setColor( this.getColor() );
            
            return true;
        } finally {
            this.rl.unlock();
        }
    }
    
    @Override
    public void evaluationHallLogger(TPatient patient) throws IOException{
        String room = patient.getEvaluationRoom();
        
        switch(room){
            case "evr1":
                //logger
                logger.writeLogger( "    |             | "+ patient.getPatientName() + "                 |               |                         |         |    \n");
                System.out.print( "    |             | "+ patient.getPatientName() + "                 |               |                         |         |    \n");
                break;
            case "evr2":
                //logger
                logger.writeLogger( "    |             |      " + patient.getPatientName() + "            |               |                         |         |    \n");
                System.out.print( "    |             |      " + patient.getPatientName() + "            |               |                         |         |    \n");
                break;
            case "evr3":
                //logger
                logger.writeLogger( "    |             |           " + patient.getPatientName() + "       |               |                         |         |    \n");
                System.out.print( "    |             |           " + patient.getPatientName() + "       |               |                         |         |    \n");
                break;
            case "evr4":
                //logger
                logger.writeLogger( "    |             |                " + patient.getPatientName() +"  |               |                         |         |    \n");
                System.out.print( "    |             |                " + patient.getPatientName() +"  |               |                         |         |    \n");
                break;
        }
        
    }
    
    @Override
    public void evaluationHallLoggerColor(TPatient patient) throws IOException{
        String room = patient.getEvaluationRoom();
        
        switch(room){
            case "evr1":
                //logger
                logger.writeLogger( "    |             | "+ patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "                |               |                         |         |    \n");
                System.out.print( "    |             | "+ patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "                |               |                         |         |    \n");
                break;
            case "evr2":
                //logger
                logger.writeLogger( "    |             |      " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "           |               |                         |         |    \n");
                System.out.print( "    |             |      " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "           |               |                         |         |    \n");
                break;
            case "evr3":
                //logger
                logger.writeLogger( "    |             |           " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "      |               |                         |         |    \n");
                System.out.print( "    |             |           " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "      |               |                         |         |    \n");
                break;
            case "evr4":
                //logger
                logger.writeLogger( "    |             |                " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) +" |               |                         |         |    \n");
                System.out.print( "    |             |                " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) +" |               |                         |         |    \n");
                break;
        }
        
    }
    
     /**
     * Patient leaves room accordingly to its type 
     * @param patient
     * @return true if patient leaves second room with success 
     */
    
    @Override
    public boolean leaveRoom(TPatient patient) {
        try{
            this.rl.lock();
            
            rooms.get();
            
            this.decSize();
            
            return true;
        } finally {
            this.rl.unlock();
        }
    }
    
     /**
     * Check current size of FIFO 
     * @return size of fifo
     */
    
    @Override
    public int getCurrentSize(){  
        try{
            rl.lock();
            return size;
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Increase size of FIFO 
     */
    @Override 
    public void incSize(){ 
        try {
            rl.lock();
            size++;
        } finally {
            rl.unlock();
        }
    
    }
    /**
     * Decrease size of FIFO 
     */
    @Override 
    public void decSize(){    
        try {
            rl.lock();
            size--;
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Patient waits for DoS (color)
     */
    @Override
    public void waitForColor(){
        try{
            rl.lock();
            
            this.waitColor.await();
                                    
        } catch(InterruptedException ex){} 
        finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre check if any patient is waiting for receive DoS (color)
     * @return true if any patient is waiting for DoS
     */
    @Override
    public boolean lockHasWaitersForColor(){
        try{
            rl.lock();
            return this.rl.hasWaiters(waitColor);
        } finally {
            rl.unlock();
        }
    }
    
    
     
    /**
     * Nurse signals patients receive DoS (color)
     * @param color 
     */
    @Override
    public void signalColor(String color){
        try {
            rl.lock();
            this.color = color;
            this.waitColor.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /** 
     * Get DoS (color) assigned to patient
     * @return DoS assigned to patient
     */
    @Override 
    public String getColor(){
        return color;
    }
    
}
