package HC.EvaluationHallSharedArea;

/**
 * Interface for the Evaluation Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IEvaluationHall {
    /**
     * Method to increase size of FIFO when Patient get in 
     */
    public void incSize();
    
    /**
     * Method to decrease size of FIFO when Patient get out
     */
    public void decSize(); 
    
    /**
     * Method to get color assigned to Patient
     * @return Color assigned to Patient
     */
    public String getColor();
}
