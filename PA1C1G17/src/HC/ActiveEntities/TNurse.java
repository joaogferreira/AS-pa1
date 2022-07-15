package HC.ActiveEntities;

import HC.EvaluationHallSharedArea.IEvaluationHall_Nurse;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents the Nurse Thread
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class TNurse extends Thread {
    
    /* Shared Area Evaluation Hall */
    private final IEvaluationHall_Nurse iEvaluationHall;
    
    /* Color to be assigned to each patient */
    private String color;
    
    /* Possible colors */
    private final String[] colors = { "red", "yellow", "blue"};
    
    /* Stop simulation variables */
    private final ReentrantLock stopLock = new ReentrantLock(true);
    private boolean isStopped = false;
    
    /* Number of patients until the simulation is finished */
    private int nPatients;
    
    /**
     * Nurse constructor
     * @param iEvaluationHall Evaluation Hall Shared Area (interface)
     * @param nPatients Number of patients
     */
    public TNurse(IEvaluationHall_Nurse iEvaluationHall, int nPatients){
        this.iEvaluationHall = iEvaluationHall;
        this.nPatients = nPatients;
    }
    
    /**
     * Nurse Life Cycle
     */
    @Override
    public void run(){
        
        while(true){
            
            /**
             * The nurse checks if there are patients waiting to get a color 
             */
            if(this.iEvaluationHall.lockHasWaitersForColor()){
                
                /* pick a random color from Red, Yellow, Blue */
                int rnd = new Random().nextInt(colors.length);
                color = colors[rnd];
                
                /* Signal the patient with the color assigned to him */
                this.iEvaluationHall.signalColor(color);
                
                /* decrease the number of patients */
                this.nPatients--;                
            }
            
            /* if the number of patients is equal to 0 it means that the simulation ended */
            if( this.nPatients == 0){
                break;
            }
            
            /* check if the simulation was stopped */
            if(this.checkStop())
                break;
        }
        
        /* Nurse end of simulation */
        //System.out.println("NURSE: END OF SIMULATION");
    }
    
    
    /* Check if simulation is stopped */
    public boolean checkStop(){
        this.stopLock.lock();
        try {
            return this.isStopped;
        } finally {
            this.stopLock.unlock();
        }
    }
    
    
    /* Stop button - set flag to TRUE */
    public void stopNurse(){
        this.stopLock.lock();
        try {
            this.isStopped = true;
        } finally {
            this.stopLock.unlock();
        }
    }
}
