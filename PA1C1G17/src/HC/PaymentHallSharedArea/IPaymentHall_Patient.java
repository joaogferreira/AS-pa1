package HC.PaymentHallSharedArea;

import HC.ActiveEntities.TPatient;

/**
 * Patient interface for Payment Hall 
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */
public interface IPaymentHall_Patient {
    
    /**
     * Patient waits signal of Cashier
     */
    public void waitForCashier();
    
    /**
     * Patient signals Cashier that he left first room paymentHall
     * and joined secondr room paymentHall
     */
    public void signalCashier();
    
    /**
     * Patient joins first room - FIFO - put()
     * @param patient
     * @return true if patient joins the first room
     */
    public boolean joinFirstRoom(TPatient patient);
    
      /**
     * Patient leaves first room
     * @param patient
     * @return true if patient leaves first room with success 
     */
    public boolean leaveFirstRoom(TPatient patient);
    
    /**
     * Patient joins second room
     * @param patient
     * @return true if patient joins second room
     */
    public boolean joinSecondRoom(TPatient patient);
    
     /**
     * Patient leaves second room
     * @param patient
     * @return true if patient leaves second room with success 
     */
    public boolean leaveSecondRoom(TPatient patient);
}
