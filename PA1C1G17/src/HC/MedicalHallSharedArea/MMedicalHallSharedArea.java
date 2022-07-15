package HC.MedicalHallSharedArea;

import HC.ActiveEntities.TPatient;
import HC.FIFO.MFIFO;
import HC.Logger.MLogger;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared Area for the Medical Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */


public class MMedicalHallSharedArea implements IMedicalHall, IMedicalHall_CallCentre, IMedicalHall_Patient, IMedicalHall_Doctor{
    
    /* Reentrant lock used for synchronization */
    private final ReentrantLock rl;
        
    /* Condition for adult patients to go to second room */
    private final Condition adultWaitToGoToSecondRoom;
    
    /* Condition for children patients to go to second room */
    private final Condition childrenWaitToGoToSecondRoom;
    
    /* Condition for adult patients to wait for doctor */
    private final Condition adultWaitForDoctor;
    
    /* Condition for children patients to wait for doctor */
    private final Condition childrenWaitForDoctor;
    
    /* Number of children in first room */
    private int sizeChildrenFirst;
    
    /* Number of adults in first room */
    private int sizeAdultFirst;
    
    /* Number of children in second room */
    private int sizeChildrenSecond;
    
    /* Number of children in second room */
    private int sizeAdultSecond;
    
    /* FIFO for first room (children) */
    private final MFIFO childrenFirstRoom; /* 1 seat */
    
    /* FIFO for first room (adult) */
    private final MFIFO adultFirstRoom; /* 1 seat */
    
    /* FIFO for second room (children) */
    private final MFIFO childrenSecondRoom; /* 2 seats */
    
    /* FIFO for second room (adult) */
    private final MFIFO adultSecondRoom; /* 2 seats */
    
    /* Logger */
    private MLogger logger;
    
    /**
     * MMedicalHallSharedArea constructor
     * @param logger
     */
    public MMedicalHallSharedArea( MLogger logger ){
        this.rl = new ReentrantLock(true);
        this.adultWaitToGoToSecondRoom = rl.newCondition();
        this.childrenWaitToGoToSecondRoom = rl.newCondition();
        this.adultWaitForDoctor = rl.newCondition();
        this.childrenWaitForDoctor = rl.newCondition();
        this.sizeChildrenFirst = 0;
        this.sizeAdultFirst = 0;
        this.sizeChildrenSecond = 0;
        this.sizeAdultSecond = 0;
        this.childrenFirstRoom = new MFIFO(1);
        this.adultFirstRoom = new MFIFO(1);
        this.childrenSecondRoom = new MFIFO(2);
        this.adultSecondRoom = new MFIFO(2);
        this.logger = logger;
    }
    
    /**
     * Patient joins first room 
     * @param patient
     * @return true if patient joined first room with success
     */
    @Override
    public boolean joinFirstRoom(TPatient patient){
        this.rl.lock();
        try {
            
            if (patient.getPatientName().startsWith("C")){ // children FIFO
                this.childrenFirstRoom.put(patient);
                this.incSizeChildrenFirst();
            } else if (patient.getPatientName().startsWith("A")) { // adult FIFO
                this.adultFirstRoom.put(patient);
                this.incSizeAdultFirst();
            }
            
            logger.writeLogger("    |             |                     |               |" + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "                     |         |    \n");
            System.out.print("    |             |                     |               |" + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "                     |         |    \n");
            
            return true;
        } finally {
            this.rl.unlock();
        }        

    }
    
    /**
     * Patient leaves first room 
     * @param patient
     * @return true if patient leaves first room with success
     */
    @Override
    public boolean leaveFirstRoom(TPatient patient){
        this.rl.lock();
        
        try{
                
            if (patient.getPatientName().startsWith("C")){ // children FIFO
                this.childrenFirstRoom.get();
                this.decSizeChildrenFirst();
            } else if (patient.getPatientName().startsWith("A")){ // adult FIFO
                this.adultFirstRoom.get();
                this.decSizeAdultFirst();
            }
            
            return true;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Patient joins second room 
     * @param patient
     * @return true if patient joined second room with success
     */
    @Override
    public boolean joinSecondRoom(TPatient patient){
        this.rl.lock();
        try {
            
            if (patient.getPatientName().startsWith("C")){ //children FIFO
                childrenSecondRoom.put(patient);
                this.incSizeChildrenSecond();
            } else{
                adultSecondRoom.put(patient); // adult FIFO
                this.incSizeAdultSecond();
            }
            
            return true;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Patient leaves second room 
     * @param patient
     * @return true if patient leaves first room with success
     */
    @Override
    public boolean leaveSecondRoom(TPatient patient){
        if ( patient.getPatientName().startsWith("A") ){ //adult FIFO
            adultSecondRoom.get();
            this.decSizeAdultSecond();
        } else if ( patient.getPatientName().startsWith("C") ){ //children FIFO
            childrenSecondRoom.get();
            this.decSizeChildrenSecond();
        }
        
        return true;
        
    }
    
    /**
     * Get number of adults in first room 
     * @return adults count
     */
    @Override
    public int getCurrentSizeAdultFirst(){  
        this.rl.lock();
        try{
            return this.sizeAdultFirst;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Get number of adults in second room
     * @return adults count
     */
    @Override
    public int getCurrentSizeAdultSecond(){  
        this.rl.lock();
        try{
            return this.sizeAdultSecond;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Get number of children in first room 
     * @return children count
     */
    @Override
    public int getCurrentSizeChildrenFirst(){  
        this.rl.lock();
        try{
            return this.sizeChildrenFirst;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Get number of children in first room
     * @return children count
     */
    @Override
    public int getCurrentSizeChildrenSecond(){  
        this.rl.lock();
        try{
            return this.sizeChildrenSecond;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Increase size adults first room
     */
    @Override 
    public void incSizeAdultFirst(){
        this.rl.lock();
        try {
            this.sizeAdultFirst++;
        } finally {
            this.rl.unlock();
        }
    
    }
    
    /**
     * Increase size adults second room
     */
    @Override 
    public void incSizeAdultSecond(){
        this.rl.lock();
        try {
            this.sizeAdultSecond++;
        } finally {
            this.rl.unlock();
        }
    
    }
    
    /**
     * Decrease size adults first room
     */
    @Override 
    public void decSizeAdultFirst(){ 
        this.rl.lock();
        try {
            this.sizeAdultFirst--;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Decrease size adults second room
     */
    @Override 
    public void decSizeAdultSecond(){ 
        this.rl.lock();
        try {
            this.sizeAdultSecond--;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Increase size children first room
     */
    @Override 
    public void incSizeChildrenFirst(){
        this.rl.lock();
        try {
            this.sizeChildrenFirst++;
        } finally {
            this.rl.unlock();
        }
    
    }
    
    /**
     * Increase size children second room
     */
    @Override 
    public void incSizeChildrenSecond(){
        this.rl.lock();
        try {
            this.sizeChildrenSecond++;
        } finally {
            this.rl.unlock();
        }
    
    }
    
    
    /**
     * Decrease size children first room
     */
    @Override 
    public void decSizeChildrenFirst(){
        this.rl.lock();
        try {
            this.sizeChildrenFirst--;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Decrease size children second room
     */
    @Override 
    public void decSizeChildrenSecond(){
        this.rl.lock();
        try {
            this.sizeChildrenSecond--;
        } finally {
            this.rl.unlock();
        }
    }
    

    /**
     * Adult patients wait to go to medical hall second room 
     */
    @Override
    public void adultWaitToGoToSecondRoom(){
        try{
            this.rl.lock();
            
            this.adultWaitToGoToSecondRoom.await();
                        
        } catch(InterruptedException ex){} 
        finally {
            rl.unlock();
        }
    }
    
    /**
     * Lock has adults waiting for second room
     * @return true if lock has adults patients waiting
     */
    @Override
    public boolean lockHasAdultWaitingForSecondRoom(){
        try{
            rl.lock();
            return this.rl.hasWaiters(adultWaitToGoToSecondRoom);
        } finally {
            rl.unlock();
        } 
    }
    
    /**
     * Call Centre signals adult patients to go medical hall second room 
     */
    @Override
    public void signalAdultToGoToSecondRoom(){
        try {
            rl.lock();
            this.adultWaitToGoToSecondRoom.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Children patients wait to go to medical hall second room 
     */
    @Override
    public void childrenWaitToGoToSecondRoom(){
        try{
            this.rl.lock();
            
            this.childrenWaitToGoToSecondRoom.await();
                        
        } catch(InterruptedException ex){} 
        finally {
            rl.unlock();
        }
    }
    
    /**
     * Lock has children waiting for second room
     * @return true if lock has children patients waiting
     */
    @Override
    public boolean lockHasChildrenWaitingForSecondRoom(){
        try{
            rl.lock();
            return this.rl.hasWaiters(childrenWaitToGoToSecondRoom);
        } finally {
            rl.unlock();
        } 
    }
    
    /**
     * Call Centre signals children patients to go medical hall second room 
     */
    @Override
    public void signalChildrenToGoToSecondRoom(){
        try {
            rl.lock();
            this.childrenWaitToGoToSecondRoom.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Adult patients wait for doctor 
     */
    @Override
    public void adultWaitForDoctor(){
        try{
            this.rl.lock();
            
            this.adultWaitForDoctor.await();
                        
        } catch(InterruptedException ex){} 
        finally {
            rl.unlock();
        }
    }
    
    /**
     * Children patients wait for doctor 
     */
    @Override
    public void childrenWaitForDoctor(){
        try{
            this.rl.lock();
            
            this.childrenWaitForDoctor.await();
                        
        } catch(InterruptedException ex){} 
        finally {
            rl.unlock();
        }
    }
    
    /**
     * Lock has adults waiting for doctor 
     * @return true if lock has adults patients waiting
     */
    @Override
    public boolean lockHasAdultWaitersForAppointment(){
        try{
            rl.lock();
            return this.rl.hasWaiters(adultWaitForDoctor);
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Lock has children waiting for doctor
     * @return true if lock has children patients waiting
     */
    @Override
    public boolean lockHasChildrenWaitersForAppointment(){
        try{
            rl.lock();
            return this.rl.hasWaiters(childrenWaitForDoctor);
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Doctor signals adult patients for appointment 
     */
    @Override 
    public void signalAdultForAppointment(){
        try {
            rl.lock();
            this.adultWaitForDoctor.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Doctor signals children patients for appointment 
     */
    @Override 
    public void signalChildrenForAppointment(){
        try {
            rl.lock();
            this.childrenWaitForDoctor.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    
    @Override
    public void medicalHallLogger(TPatient patient) throws IOException{
        String room = patient.getMedicalRoom();
        
        switch(room){
            case "mdr1":
                //logger
                logger.writeLogger( "    |             |                     |               |     " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "                |         |    \n");
                System.out.print( "    |             |                     |               |     " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "                |         |    \n");
                break;
            case "mdr2":
                //logger
                logger.writeLogger( "    |             |                     |               |          " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "           |         |    \n");
                System.out.print( "    |             |                     |               |          " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "           |         |    \n");
                break;
            case "mdr3":
                //logger
                logger.writeLogger( "    |             |                     |               |               " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "      |         |    \n");
                System.out.print( "    |             |                     |               |               " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "      |         |    \n");
                break;
            case "mdr4":
                //logger
                logger.writeLogger( "    |             |                     |               |                    " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + " |         |    \n");
                System.out.print( "    |             |                     |               |                    " + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + " |         |    \n");
                break;
        }
        
    }
        
}
