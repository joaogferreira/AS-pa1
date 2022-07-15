package HC.WaitingHallSharedArea;


public interface IWaitingHall_CallCentre {
    
    /* Call Centre waits for child patient to join medical hall first room */
    public void waitForChildPatientToJoinMedicalHall(); 
    
    /* Call Centre waits for adult patient to join medical hall first room */
    public void waitForAdultPatientToJoinMedicalHall(); 
     
    /**
     * Call Centre checks if any red children patient is waiting for evaluation
     * @return true if any red children patient is waiting for evaluation
     */
    public boolean lockHasRedChildrenWaitersForMedicalHall(); 
    
    /**
     * Call Centre checks if any yellow children patient is waiting for evaluation
     * @return true if any yellow children patient is waiting for evaluation
     */
    public boolean lockHasYellowChildrenWaitersForMedicalHall(); 
    
    /**
     * Call Centre checks if any blue children patient is waiting for evaluation
     * @return true if any blue children patient is waiting for evaluation
     */
    public boolean lockHasBlueChildrenWaitersForMedicalHall(); 
    
    /* Call Centre signals red children patients to go to medical hall */
    public void signalRedChildrenToGoToMedicalHall(); 
    
    /* Call Centre signals yellow children patients to go to medical hall */
    public void signalYellowChildrenToGoToMedicalHall(); 
    
    /* Call Centre signals blue children patients to go to medical hall */
    public void signalBlueChildrenToGoToMedicalHall(); 
    
    /**
     * Call Centre checks if any red adult patient is waiting for evaluation
     * @return true if any red adult patient is waiting for evaluation
     */
    public boolean lockHasRedAdultWaitersForMedicalHall(); 
    
    /**
     * Call Centre checks if any yellow adult patient is waiting for evaluation
     * @return true if any yellow adult patient is waiting for evaluation
     */
    public boolean lockHasYellowAdultWaitersForMedicalHall(); 
    
    /**
     * Call Centre checks if any blue adult patient is waiting for evaluation
     * @return true if any blue adult patient is waiting for evaluation
     */
    public boolean lockHasBlueAdultWaitersForMedicalHall(); 
    
    /* Call Centre signals red adult patients to go to medical hall */
    public void signalRedAdultToGoToMedicalHall(); 
    
    /* Call Centre signals yellow adult patients to go to medical hall */
    public void signalYellowAdultToGoToMedicalHall(); 
    
    /* Call Centre signals blue adult patients to go to medical hall */
    public void signalBlueAdultToGoToMedicalHall();
    
            
}
