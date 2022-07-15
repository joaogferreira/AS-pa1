package HC.PaymentHallSharedArea;

/**
 * Interface for the Payment Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */
public interface IPaymentHall {
    
    /**
     * Method to increase size of FIFO when Patient get in 
     */
    public void incSize();
    
     /**
     * Method to decrease size of FIFO when Patient get in 
     */
    public void decSize();
}
