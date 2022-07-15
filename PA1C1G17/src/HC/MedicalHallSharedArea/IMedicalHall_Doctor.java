package HC.MedicalHallSharedArea;


/**
 * Doctor interface for Medical Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IMedicalHall_Doctor {
    
    /**
     * Check if Lock has children waiting for appointment
     * @return true if lock has waiters
     */
    public boolean lockHasAdultWaitersForAppointment(); 
    
    /**
     * Check if Lock has children waiting for appointment
     * @return true if lock has waiters
     */
    public boolean lockHasChildrenWaitersForAppointment();
    
    /* Doctor signals adult patients to go medical hall second room  */
    public void signalAdultForAppointment();
    
    /* Doctor signals children patients to go medical hall second room */
    public void signalChildrenForAppointment(); 
}
