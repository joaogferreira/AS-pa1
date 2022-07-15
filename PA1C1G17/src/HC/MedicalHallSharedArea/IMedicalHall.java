package HC.MedicalHallSharedArea;

/**
 * Interface for Medical Hall
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public interface IMedicalHall {
    
    /* Increase size children first room */
    public void incSizeChildrenFirst(); 
    
    /* Increase size adults first room */
    public void incSizeAdultFirst(); 
    
    /* Decrease size children first room */
    public void decSizeChildrenFirst(); 
    
    /* Decrease size adults first room */
    public void decSizeAdultFirst(); 
    
    /* Increase size children second room */
    public void incSizeChildrenSecond(); 
    
    /* Increase size adults second room */
    public void incSizeAdultSecond(); 
    
    /* Decrease size children second room */
    public void decSizeChildrenSecond();
    
    /* Decrease size adults second room */
    public void decSizeAdultSecond();     
}
