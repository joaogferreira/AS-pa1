package HC.WaitingHallSharedArea;

import HC.ActiveEntities.TPatient;
import HC.FIFO.MFIFO;
import HC.Logger.MLogger;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Shared Area for the Waiting Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class MWaitingHallSharedArea implements IWaitingHall_CallCentre, IWaitingHall_Patient{
    
    /* Reentrant lock used for synchronization */
    private final ReentrantLock rl;
    
    /* Condition for call centre to wait for children patients to join first room  */
    private final Condition waitForChildrenPatientToJoin;
    
    /* Condition for call centre to wait for adult patients to join first room  */
    private final Condition waitForAdultPatientToJoin;
    
    /* Condition for blue children patients to wait for call Centre signal to go to medical hall first room   */
    private final Condition blueChildrenWaitToGoToMedicalHall;
    
    /* Condition for red children patients to wait for call Centre signal to go to medical hall first room*/
    private final Condition redChildrenWaitToGoToMedicalHall;
    
    /* Condition for yellow children patients to wait for call Centre signal to go to medical hall first room*/
    private final Condition yellowChildrenWaitToGoToMedicalHall;
    
    /* Condition for blue adultspatients to wait for call Centre signal to go to medical hall first room   */
    private final Condition blueAdultWaitToGoToMedicalHall;
    
    /* Condition for red adults patients to wait for call Centre signal to go to medical hall first room   */
    private final Condition redAdultWaitToGoToMedicalHall;
    
    /* Condition for yellow adults patients to wait for call Centre signal to go to medical hall first room   */
    private final Condition yellowAdultWaitToGoToMedicalHall;
    
    /* FIFO for first room */
    private MFIFO firstRoom;
    
    /* FIFO for children second room */
    private MFIFO secondRoomChildren;
    
    /* FIFO for adult second room  */
    private MFIFO secondRoomAdults;
    
    /* Number to be assigned to each patient */
    private int number;
    
    /* Logger */
    private MLogger logger;
    /**
     * MWaitingHallSharedArea constructor
     * @param logger
     */
    public MWaitingHallSharedArea(MLogger logger){
        this.rl = new ReentrantLock(true);
        this.waitForChildrenPatientToJoin = rl.newCondition();
        this.waitForAdultPatientToJoin = rl.newCondition();
        this.blueChildrenWaitToGoToMedicalHall = rl.newCondition();
        this.redChildrenWaitToGoToMedicalHall = rl.newCondition();
        this.yellowChildrenWaitToGoToMedicalHall = rl.newCondition();
        this.blueAdultWaitToGoToMedicalHall = rl.newCondition();
        this.redAdultWaitToGoToMedicalHall = rl.newCondition();
        this.yellowAdultWaitToGoToMedicalHall = rl.newCondition();
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
            
            this.logger.writeLogger("    |             |                     |" + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "           |                         |         |    \n" );
            System.out.print("    |             |                     |" + patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "           |                         |         |    \n" );
            
            return true;
        } finally {
            this.rl.unlock();
        }
        
        
    }
    
    /**
     * Patient joins second room - FIFO - put()
     * @param patient
     * @param id
     * @return true if patient joins the first room
     */
    @Override
    public boolean joinSecondRoom(TPatient patient, String id){
        if ( id.startsWith("A") ){ // adults second room 
            
            secondRoomAdults.put(patient);
            
            
            //WTR2
            logger.writeLogger("    |             |                     |          "+patient.getPatientName()+patient.getColor().toUpperCase().charAt(0)+" |                         |         |    \n");
            System.out.print("    |             |                     |          "+patient.getPatientName()+patient.getColor().toUpperCase().charAt(0)+" |                         |         |    \n");

        } else if ( id.startsWith("C") ){ // children second room
            secondRoomChildren.put(patient);
            
            //WTR1
            logger.writeLogger("    |             |                     |     "+patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "      |                         |         |    \n" );
            System.out.print("    |             |                     |     "+patient.getPatientName()+patient.getColor().toUpperCase().charAt(0) + "      |                         |         |    \n" );
            
        }
        
        patient.setWaitingNumber(number);
            
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
     * Call Centre checks if any red children patient is waiting for evaluation
     * @return true if any red children patient is waiting for evaluation
     */
    @Override
    public boolean lockHasRedChildrenWaitersForMedicalHall(){
        try{
            rl.lock();
            return this.rl.hasWaiters(redChildrenWaitToGoToMedicalHall);
        } finally {
            rl.unlock();
        } 
    }
    
    /**
     * Call Centre checks if any yellow children patient is waiting for evaluation
     * @return true if any yellow children patient is waiting for evaluation
     */
    @Override
    public boolean lockHasYellowChildrenWaitersForMedicalHall(){
        try{
            rl.lock();
            return this.rl.hasWaiters(yellowChildrenWaitToGoToMedicalHall);
        } finally {
            rl.unlock();
        } 
    }
    
    /**
     * Call Centre checks if any blue children patient is waiting for evaluation
     * @return true if any blue children patient is waiting for evaluation
     */
    @Override
    public boolean lockHasBlueChildrenWaitersForMedicalHall(){
        try{
            rl.lock();
            return this.rl.hasWaiters(blueChildrenWaitToGoToMedicalHall);
        } finally {
            rl.unlock();
        } 
    }
    
    /**
     * Call Centre checks if any red adult patient is waiting for evaluation
     * @return true if any red adult patient is waiting for evaluation
     */
    @Override
    public boolean lockHasRedAdultWaitersForMedicalHall(){
        try{
            rl.lock();
            return this.rl.hasWaiters(redAdultWaitToGoToMedicalHall);
        } finally {
            rl.unlock();
        } 
    }
    
    /**
     * Call Centre checks if any yellow adult patient is waiting for evaluation
     * @return true if any yellow adult patient is waiting for evaluation
     */
    @Override
    public boolean lockHasYellowAdultWaitersForMedicalHall(){
        try{
            rl.lock();
            return this.rl.hasWaiters(yellowAdultWaitToGoToMedicalHall);
        } finally {
            rl.unlock();
        } 
    }
    
    /**
     * Call Centre checks if any blue adult patient is waiting for evaluation
     * @return true if any blue adult patient is waiting for evaluation
     */
    @Override
    public boolean lockHasBlueAdultWaitersForMedicalHall(){
        try{
            rl.lock();
            return this.rl.hasWaiters(blueAdultWaitToGoToMedicalHall);
        } finally {
            rl.unlock();
        } 
    }
    
    
    /**
     * Call Centre signals red children patients to go to medical hall
     */
    @Override
    public void signalRedChildrenToGoToMedicalHall(){
         try {
            rl.lock();
            this.redChildrenWaitToGoToMedicalHall.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre signals blue children patients to go to medical hall
     */
    @Override
    public void signalBlueChildrenToGoToMedicalHall(){
         try {
            rl.lock();
            this.blueChildrenWaitToGoToMedicalHall.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre signals yellow children patients to go to medical hall
     */
    @Override
    public void signalYellowChildrenToGoToMedicalHall(){
         try {
            rl.lock();
            this.yellowChildrenWaitToGoToMedicalHall.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    
    /**
     * Call Centre signals red adult patients to go to medical hall
     */    
    @Override
    public void signalRedAdultToGoToMedicalHall(){
         try {
            rl.lock();
            this.redAdultWaitToGoToMedicalHall.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre signals blue adult patients to go to medical hall
     */  
    @Override
    public void signalBlueAdultToGoToMedicalHall(){
         try {
            rl.lock();
            this.blueAdultWaitToGoToMedicalHall.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre signals yellow adult patients to go to medical hall
     */  
    @Override
    public void signalYellowAdultToGoToMedicalHall(){
         try {
            rl.lock();
            this.yellowAdultWaitToGoToMedicalHall.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    
    /**
     * Call Centre waits for child patient to join medical hall first room
     */    
    @Override
    public void waitForChildPatientToJoinMedicalHall(){
        try{
            rl.lock();
            
            this.waitForChildrenPatientToJoin.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Call Centre waits for adult patient to join medical hall first room
     */
    @Override
    public void waitForAdultPatientToJoinMedicalHall(){
        try{
            rl.lock();
            
            this.waitForAdultPatientToJoin.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Children patient signals call Centre (joined medical hall)
     */
    @Override
    public void childrenPatientInMedicalHall(){
        try {
            rl.lock();
            this.waitForChildrenPatientToJoin.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Adult patient signals call Centre (joined medical hall)
     */
    @Override
    public void adultPatientInMedicalHall(){
        try {
            rl.lock();
            this.waitForAdultPatientToJoin.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    
    /**
     * Red Children waits for signal from call Centre to go to medical hall first room
     */
    @Override
    public void redChildrenWaitToGoToMedicalHall(){
        try{
            rl.lock();
            
            this.redChildrenWaitToGoToMedicalHall.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Yellow Children waits for signal from call Centre to go to medical hall first room
     */
    @Override 
    public void yellowChildrenWaitToGoToMedicalHall() {
        try{
            rl.lock();
            
            this.yellowChildrenWaitToGoToMedicalHall.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Blue Children waits for signal from call Centre to go to medical hall first room
     */
    @Override
    public void blueChildrenWaitToGoToMedicalHall() {
        try{
            rl.lock();
            
            this.blueChildrenWaitToGoToMedicalHall.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Red Adult waits for signal from call Centre to go to medical hall first room
     */
    @Override
    public void redAdultWaitToGoToMedicalHall(){
        try{
            rl.lock();
            
            this.redAdultWaitToGoToMedicalHall.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Yellow Adult waits for signal from call Centre to go to medical hall first room
     */
    @Override 
    public void yellowAdultWaitToGoToMedicalHall() {
        try{
            rl.lock();
            
            this.yellowAdultWaitToGoToMedicalHall.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Blue Adult waits for signal from call Centre to go to medical hall first room
     */
    @Override
    public void blueAdultWaitToGoToMedicalHall() {
        try{
            rl.lock();
            
            this.blueAdultWaitToGoToMedicalHall.await();
                        
        } catch(InterruptedException ex){
        } finally {
            rl.unlock();
        }
    }

    

       
}
