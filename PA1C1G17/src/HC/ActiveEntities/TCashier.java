package HC.ActiveEntities;

import HC.PaymentHallSharedArea.IPaymentHall_Cashier;

/**
 * Represents the Cashier Thread
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class TCashier extends Thread {
    
    /* Shared Area Payment Hall */
    private final IPaymentHall_Cashier iPaymentHall;
    
    /* Number of patients until the simulation is finished */
    private int nPatients;
    
    /**
     * Cashier constructor
     * @param iPaymentHall Payment Hall Shared Area (interface)
     * @param nPatients Number of patients
     */
    public TCashier( IPaymentHall_Cashier iPaymentHall, int nPatients){
        this.iPaymentHall = iPaymentHall;
        this.nPatients = nPatients;
    }
    
    /**
     * Cashier Life Cycle
     */
    @Override
    public void run(){
        
        while(true){
            
            /**
             * The cashier checks if there are patients waiting to pay 
             * and if the second room in payment hall (only 1 place) is available
             */
            if(this.iPaymentHall.lockHasWaitersToPay() && this.iPaymentHall.getCurrentSize() == 0){ // patient is waiting and the second room is free
                
                /* signal patient that he can join the second room */
                this.iPaymentHall.signalPatient(); 
                
                /* wait for patient to join the second room */
                this.iPaymentHall.waitForPatientToJoin();
                
                /* decrease the number of patients who have already paid */
                this.nPatients--;                
            }
            
            /* if the number of patients is equal to 0 it means that the simulation ended */
            if(this.nPatients == 0){
                break;
            }
            
        }
        
        /* Cashier end of simulation */
       // System.out.println("CASHIER: END OF SIMULATION");
    }
    
}
