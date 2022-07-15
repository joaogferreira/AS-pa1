package HC.EvaluationHallSharedArea;

import HC.ActiveEntities.TPatient;
import java.io.IOException;

/**
 * Patient interface for Evaluation Hall 
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IEvaluationHall_Patient {
    
    /**
     * Patient waits for DoS (color)
     */
    public void waitForColor();
    
     /**
     * Patient joins room - FIFO - put()
     * @param patient
     * @return true if patient joins the first room
     */
    public boolean joinRoom(TPatient patient);
    
    /**
     * Patient leaves room accordingly to its type
     * @param patient
     * @return true if patient leaves second room with success  
     */
    public boolean leaveRoom(TPatient patient);
    
    public void evaluationHallLogger(TPatient patient) throws IOException;
    
    public void evaluationHallLoggerColor(TPatient patient) throws IOException;
}
