package HC.MedicalHallSharedArea;

/**
 * Call Centre interface for Medical Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IMedicalHall_CallCentre {
    
    /* Get number of children in first room  */
    public int getCurrentSizeChildrenFirst(); 
    
    /* Get number of adults in first room  */
    public int getCurrentSizeAdultFirst(); 
    
    /* Get number of children in second room */
    public int getCurrentSizeChildrenSecond(); 
    
    /* Get number of adults in second room */
    public int getCurrentSizeAdultSecond(); 
    
    /**
     * Check if Lock has children waiting for second room
     * @return true if lock has waiters
     */
    public boolean lockHasChildrenWaitingForSecondRoom(); 
    
    /* Call Centre signals children to go to second room */
    public void signalChildrenToGoToSecondRoom(); 
    
    /**
     * Lock has adults waiting for second room
     * @return true if lock has waiters
     */
    public boolean lockHasAdultWaitingForSecondRoom();
    
    /* Call Centre signals adults to go to second room */
    public void signalAdultToGoToSecondRoom();
}
