package HC.PaymentHallSharedArea;

/**
 * Cashier interface for Payment Hall 
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IPaymentHall_Cashier {
    
    /**
     * Cashier signals Patient to join second room
     */
    public void signalPatient(); 
    
    /**
     * Cashier check if any patient is waiting for pay
     * @return true if any patient is waiting for pay
     */
    public boolean lockHasWaitersToPay();
    
     /**
     * Check current size of FIFO 
     * @return size of fifo
     */
    public int getCurrentSize();
    
    /**
     * Cashier waits to patient to join to pay
     */
    public void waitForPatientToJoin(); 
    
}
