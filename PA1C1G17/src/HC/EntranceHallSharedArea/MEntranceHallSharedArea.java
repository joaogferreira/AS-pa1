package HC.EntranceHallSharedArea;


import HC.ActiveEntities.TPatient;
import HC.FIFO.MFIFO;
import HC.Logger.MLogger;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared Area for the Entrance Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */


public class MEntranceHallSharedArea implements IEntranceHall_CallCentre, IEntranceHall_Patient {
    
    /* Reentrant lock used for synchronization */
    private final ReentrantLock rl;
    
    /* Condition for patients to wait to go to Evaluation Hall */
    private final Condition waitToGoToEvaluationHall;
    
    /* Condition for call centre to wait for client to join  */
    private final Condition waitForClientToJoin;
    
    /* FIFO for first room */
    private MFIFO firstRoom;
    
    /* FIFO for second room (childrens) */
    private MFIFO secondRoomChildren;
    
    /* FIFO for second room (adults) */
    private MFIFO secondRoomAdults;
    
    /* Sequential number to be assigned to patient */
    private int number;
    
    /* Logger */
    private MLogger logger;
    
    /**
     * MEntranceHallSharedArea constructor
     * @param logger
     */
    public MEntranceHallSharedArea(MLogger logger){
        this.rl = new ReentrantLock(true);
        this.waitToGoToEvaluationHall = rl.newCondition();
        this.waitForClientToJoin = rl.newCondition();
        this.logger = logger;
    }
    
    /**
     * Set size of first room and second rooms (children and adults)
     * @param nPatients
     * @param seats 
     */
    public void setSize(int nPatients, int seats){
        firstRoom = new MFIFO(nPatients);
        secondRoomChildren = new MFIFO(seats);
        secondRoomAdults = new MFIFO(seats);
        this.number = 0;
    }
    
    /**
     * Patient joins first room - FIFO - put()
     * @param patient
     * @return true if patient joins the first room
     */
    @Override
    public boolean joinFirstRoom(TPatient patient){
        this.rl.lock();
        try {
            
            firstRoom.put(patient);
            
            this.logger.writeLogger("    | " + patient.getPatientName() + "         |                     |               |                         |         |    \n" );
            System.out.print("    | " + patient.getPatientName() + "         |                     |               |                         |         |    \n" );
            return true;
        } finally {
            this.rl.unlock();
        }
        
        
    }
    
    /**
     * Patient joins second room accordingly to its type (children or adult)
     * @param patient
     * @param id
     * @return true if patient joins first room
     */
    @Override
    public boolean joinSecondRoom(TPatient patient, String id){
        if ( id.startsWith("A") ){
            secondRoomAdults.put(patient);
            
            
            // ET2
            this.logger.writeLogger("    |         " + patient.getPatientName()+ " |                     |               |                         |         |    \n");
            System.out.print("    |         " + patient.getPatientName()+ " |                     |               |                         |         |    \n");
            
        } else if ( id.startsWith("C") ){
            secondRoomChildren.put(patient);
            
            
            // ET1
            this.logger.writeLogger("    |     "+ patient.getPatientName() + "     |                     |               |                         |         |    \n");
            System.out.print("    |     "+ patient.getPatientName() + "     |                     |               |                         |         |    \n");
        }
        
        patient.setSequentialNumber(number);
            
        number++;
            
        return true;    
    }
    
    /**
     * Patient leaves second room accordingly to its type (children or adult)
     * @param patient
     * @param id
     * @return true if patient leaves second room with success 
     */
    @Override
    public boolean leaveSecondRoom(TPatient patient, String id){
        if ( id.startsWith("A") ){
            secondRoomAdults.get();
        } else if ( id.startsWith("C") ){
            secondRoomChildren.get();
        }
        
        return true;
        
    }
   
    /**
     * Patient waits for signal to go to evaluation hall
     */
    @Override
    public void waitForCallToEvaluationHall(){
        try{
            rl.lock();
            
            this.waitToGoToEvaluationHall.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre check if any patient is waiting for evaluation
     * @return true if any patient is waiting for evaluation
     */
    @Override
    public boolean lockHasWaitersForEvaluation(){
        try{
            rl.lock();
            return this.rl.hasWaiters(waitToGoToEvaluationHall);
        } finally {
            rl.unlock();
        }   
    }
    
    /**
     * Call Centre signals patients to go to evaluation hall
     */
    @Override 
    public void callForEvaluation(){
        try {
            rl.lock();
            this.waitToGoToEvaluationHall.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre waits for patient to join evaluation hall
     */
    @Override
    public void waitForClientToJoin(){
        try{
            rl.lock();
            
            this.waitForClientToJoin.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Patient signals Call Centre that he left entrance hall
     * and joined evaluation hall
     */
    @Override 
    public void wakeUpCallCentre(){
        try {
            rl.lock();
            this.waitForClientToJoin.signal();
                   
        } finally {
            rl.unlock();
        }
    }
        
}
