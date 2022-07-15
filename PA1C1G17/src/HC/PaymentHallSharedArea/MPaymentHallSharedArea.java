package HC.PaymentHallSharedArea;

import HC.ActiveEntities.TPatient;
import HC.FIFO.MFIFO;
import HC.Logger.MLogger;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared Area for the Payment Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class MPaymentHallSharedArea implements IPaymentHall, IPaymentHall_Cashier, IPaymentHall_Patient{
    
    /* Reentrant lock used for synchronization */
    private final ReentrantLock rl;
    
    /* Condition for patients to wait for cashier */
    private final Condition waitForCashier;
    
    /* Condition for cashier to wait for patient*/
    private final Condition waitForPatient;
    
    /*size of PaymentHall*/
    private int sizePaymentHall;
    
    /* FIFO for first room */
    private MFIFO firstRoom;
    
    /* FIFO for second room */
    private MFIFO secondRoom;
    
    /* Sequential number to be assigned to patient */
    private int number;
    
    /* Log file */
    private MLogger logger;
    
    /* Count of patients that already paid - used to close the log file */
    private int count;
     
    /**
     * MPaymentHallSharedArea constructor
     * @param logger
     */
    public MPaymentHallSharedArea(MLogger logger){
        this.rl = new ReentrantLock(true);
        this.waitForCashier = rl.newCondition();
        this.waitForPatient = rl.newCondition();
        this.sizePaymentHall = 0;
        this.logger = logger;
    }
    
        /**
     * Set size of first room and second rooms (children and adults)
     * @param nPatients
     * @param seats 
     */
    public void setSize(int nPatients){
        firstRoom = new MFIFO(nPatients);
        secondRoom = new MFIFO(1);
        this.number = 0;
        this.count = nPatients;
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
            
            patient.setPaymentNumber(number);

            
            number++;            
            
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
        firstRoom.get();
        
        return true;        
    }
    
    /**
     * Patient joins second room
     * @param patient
     * @return true if patient joins second room
     */
    @Override
    public boolean joinSecondRoom(TPatient patient){
        this.rl.lock();
        try {
            secondRoom.put(patient);
            
            this.incSize();
            
            if(patient.getPYN() >= 10){
                logger.writeLogger( "    |             |                     |               |                         | " + patient.getPatientName() + "-" +patient.getPYN() +"  |    \n" );
                System.out.print( "    |             |                     |               |                         | " + patient.getPatientName() + "-" +patient.getPYN() +"  |    \n" );
            }
            else {
                logger.writeLogger( "    |             |                     |               |                         | " + patient.getPatientName() +"-" + patient.getPYN() +"   |    \n" );
                System.out.print( "    |             |                     |               |                         | " + patient.getPatientName() +"-" + patient.getPYN() +"   |    \n" );
            }
            
            
            return true;
        } finally {
            this.rl.unlock();
        }  

    }
    
     /**
     * Patient leaves second room
     * @param patient
     * @return true if patient leaves second room with success 
     */
    @Override
    public boolean leaveSecondRoom(TPatient patient){
        this.rl.lock();
        try {
            secondRoom.get();
            
            this.decSize();
            
            this.count--;
            
            if(patient.getPYN() >= 10){
                logger.writeLogger( "    |             |                     |               |                         |         | " + patient.getPatientName() + "-" +patient.getPYN() + "\n" );
                System.out.print( "    |             |                     |               |                         |         | " + patient.getPatientName() + "-" +patient.getPYN() + "\n" );
            }
            else {
                logger.writeLogger( "    |             |                     |               |                         |         | " + patient.getPatientName() + "-" +patient.getPYN() +  "\n" );
                System.out.print( "    |             |                     |               |                         |         | " + patient.getPatientName() + "-" +patient.getPYN() +  "\n" );
            }
            
            /* Close log file if there are no more patients */
            if(this.count == 0){
                try {
                    this.logger.closeLogger();
                } catch (IOException ex) {
                    Logger.getLogger(MPaymentHallSharedArea.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            return true;
        } finally {
            this.rl.unlock();
        }
        
    }
    
    /**
     * Patient waits signal of Cashier
     */
    @Override
    public void waitForCashier() {
        try{
            this.rl.lock();
            
            this.waitForCashier.await();
                        
        } catch(InterruptedException ex){} 
        finally {
            rl.unlock();
        }
    }
    
    /**
     * Cashier signals Patient to join second room
     */
    @Override
    public void signalPatient() {
        try {
            rl.lock();
            this.waitForCashier.signal();
                   
        } finally {
            rl.unlock();
        }
    }
    
    /**
     * Cashier check if any patient is waiting for pay
     * @return true if any patient is waiting for pay
     */
    @Override
    public boolean lockHasWaitersToPay(){
        try{
            rl.lock();
            return this.rl.hasWaiters(this.waitForCashier);
        } finally {
            rl.unlock();
        }
    }
    
     /**
     * Check current size of FIFO 
     * @return size of fifo
     */
    @Override
    public int getCurrentSize(){  
        this.rl.lock();
        try{
            return this.sizePaymentHall;
        } finally {
            this.rl.unlock();
        }
    }
    
     /**
     * Increase size of FIFO 
     */
    @Override 
    public void incSize(){
        this.rl.lock();
        try {
            this.sizePaymentHall++;
        } finally {
            this.rl.unlock();
        }
    
    }
    
     /**
     * Decrease size of FIFO 
     */
    @Override 
    public void decSize(){ 
        this.rl.lock();
        try {
            this.sizePaymentHall--;
        } finally {
            this.rl.unlock();
        }
    }
    
    /**
     * Cashier waits to patient to join to pay
     */
    @Override
    public void waitForPatientToJoin(){
        try{
            this.rl.lock();
            
            this.waitForPatient.await();
                        
        } catch(InterruptedException ex){} 
        finally {
            rl.unlock();
        
        }
    }
    
    /**
     * Patient signals Cashier that he left first room paymentHall
     * and joined secondr room paymentHall
     */
    @Override 
    public void signalCashier(){
        try {
            rl.lock();
            this.waitForPatient.signal();
                   
        } finally {
            rl.unlock();
        }
    }

}
