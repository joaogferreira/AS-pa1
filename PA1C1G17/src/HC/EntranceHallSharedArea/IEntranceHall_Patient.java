package HC.EntranceHallSharedArea;

import HC.ActiveEntities.TPatient;

/**
 * Patient interface for Entrance Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IEntranceHall_Patient {
    
    /**
     * Patient wait for signal from call Centre to go to evaluation wall
     */
    public void waitForCallToEvaluationHall();
    
    /**
     * Patient signals call Centre informing him that he joined entrance hall
     */
    public void wakeUpCallCentre();
    
    /**
     * Patient joins entrance hall first room
     * @param patient
     * @return true if patient joined entrance hall with success (first room)
     */
    public boolean joinFirstRoom(TPatient patient);
    
    /**
     * Patient joins entrance hall second room
     * @param patient
     * @param id
     * @return true if patient joined entrance hall with success (second room)
     */
    public boolean joinSecondRoom(TPatient patient, String id);
    
    /**
     * Patient leaves entrance hall second room 
     * @param patient
     * @param id
     * @return true if patient left entrance hall with success (second room)
     */
    public boolean leaveSecondRoom(TPatient patient, String id);
}
