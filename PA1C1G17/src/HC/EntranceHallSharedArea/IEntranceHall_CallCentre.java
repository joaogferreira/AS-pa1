package HC.EntranceHallSharedArea;

/**
 * Call Centre interface for Entrance Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IEntranceHall_CallCentre {
    
    /**
     * Call Centre checks if there are any patients waiting to go to
     * entrance hall
     * @return true if the correspondent lock has waiters (patient)
     */
    public boolean lockHasWaitersForEvaluation();
    
    /**
     * Call Centre signals a patient to go to evaluation hall
     */
    public void callForEvaluation(); 
    
    /**
     * Call Centre waits for client to leave entrance hall and 
     * join evaluation hall
     */
    public void waitForClientToJoin(); 
}
