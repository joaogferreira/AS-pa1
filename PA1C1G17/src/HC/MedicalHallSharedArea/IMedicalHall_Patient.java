package HC.MedicalHallSharedArea;

import HC.ActiveEntities.TPatient;
import java.io.IOException;

/**
 * Patient interface for Medical Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IMedicalHall_Patient {
    
    /* Adult patients wait to go to medical hall second room */
    public void adultWaitToGoToSecondRoom();
    
    /* Children atients wait to go to medical hall second room */
    public void childrenWaitToGoToSecondRoom();
    
    /* Adult patients wait for doctor for appointment */
    public void adultWaitForDoctor();  
    
    /* Children patients wait for doctor for appointment */
    public void childrenWaitForDoctor();
    
    /**
     * Patient joins first room 
     * @param patient
     * @return true if patient joined first room with success
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
     * @return true if patient joined first room with success
     */
    public boolean joinSecondRoom(TPatient patient);
    
    /**
     * Patient leaves second room 
     * @param patient
     * @return true if patient leaves first room with success
     */
    public boolean leaveSecondRoom(TPatient patient);
    
    public void medicalHallLogger(TPatient patient) throws IOException;
}
