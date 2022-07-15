package HC.ActiveEntities;


import HC.MedicalHallSharedArea.IMedicalHall_Doctor;

/**
 * Represents the Doctor Thread
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class TDoctor extends Thread {
    
    /* Shared Area Medical Hall */
    private final IMedicalHall_Doctor iMedicalHall;
    
    /* Number of patients until the simulation is finished */
    private int nPatients;
    
    /**
     * Doctor constructor
     * @param iMedicalHall Medical Hall Shared Area (interface)
     * @param nPatients Number of patients
     */
    public TDoctor(IMedicalHall_Doctor iMedicalHall, int nPatients){
        this.iMedicalHall = iMedicalHall;
        this.nPatients = nPatients;
    }
    
    /**
     * Doctor Life Cycle
     */
    @Override
    public void run(){
        
        while(true){
            
            /**
             * The doctor checks if there are ADULT patients waiting 
             * to join the medical hall 
             */
            if(this.iMedicalHall.lockHasAdultWaitersForAppointment()){   
                
                /* signal ADULT patient to join the medical hall */
                this.iMedicalHall.signalAdultForAppointment();
                
                /* decrease the number of patients */
                this.nPatients--;
                
            }
            
            /**
             * The doctor checks if there are CHILDREN patients waiting 
             * to join the medical hall 
             */
            if(this.iMedicalHall.lockHasChildrenWaitersForAppointment()){
                
                /* signal ADULT patient to join the medical hall */
                this.iMedicalHall.signalChildrenForAppointment();
                
                /* decrease the number of patients */
                this.nPatients--;
                
            }
            
             /* if the number of patients is equal to 0 it means that the simulation ended */
            if (this.nPatients == 0){
                break;
            }
            
        }
        
        /* Doctor end of simulation */
       // System.out.println("DOCTOR: END OF SIMULATION");
    }
    
}
