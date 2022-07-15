package HC.WaitingHallSharedArea;

import HC.ActiveEntities.TPatient;


public interface IWaitingHall_Patient {
    
    /* Children patient signals call Centre (joined medical hall) */
    public void childrenPatientInMedicalHall();        
    
    /* Adult patient signals call Centre (joined medical hall) */
    public void adultPatientInMedicalHall(); 
    
    /* Red Children waits for signal from call Centre to go to medical hall first room */
    public void redChildrenWaitToGoToMedicalHall(); 
    
    /* Yellow Children waits for signal from call Centre to go to medical hall first room */
    public void yellowChildrenWaitToGoToMedicalHall(); 
    
    /* Blue Children waits for signal from call Centre to go to medical hall first room */
    public void blueChildrenWaitToGoToMedicalHall(); 
    
    /* Red Adult waits for signal from call Centre to go to medical hall first room */
    public void redAdultWaitToGoToMedicalHall(); 
    
    /* Yellow Adult waits for signal from call Centre to go to medical hall first room */
    public void yellowAdultWaitToGoToMedicalHall(); 
    
    /* Blue Adult waits for signal from call Centre to go to medical hall first room */
    public void blueAdultWaitToGoToMedicalHall(); 
    
    /**
     * Patient joins first room - FIFO - put()
     * @param patient
     * @return true if patient joins the first room
     */
    public boolean joinFirstRoom(TPatient patient);
    
   /**
     * Patient joins second room - FIFO - put()
     * @param patient
     * @param id
     * @return true if patient joins the first room
     */
    public boolean joinSecondRoom(TPatient patient, String id);
    
    /**
     * Patient leaves second room accordingly to its type (children or adult)
     * @param patient
     * @param id
     * @return true if patient leaves second room with success 
     */
    public boolean leaveSecondRoom(TPatient patient, String id);
    
}
