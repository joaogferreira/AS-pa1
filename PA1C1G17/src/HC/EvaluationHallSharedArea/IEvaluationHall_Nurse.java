package HC.EvaluationHallSharedArea;

/**
 * Nurse interface for Evaluation Hall 
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IEvaluationHall_Nurse {
    
    /**
     * Call Centre check if any patient is waiting for receive DoS (color)
     * @return true if any patient is waiting for DoS
     */
    public boolean lockHasWaitersForColor();
    
    /**
     * Nurse signals patients receive DoS (color)
     * @param color 
     */
    public void signalColor(String color);
}
