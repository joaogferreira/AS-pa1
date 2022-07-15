package HC.ActiveEntities;

import HC.EntranceHallSharedArea.IEntranceHall_CallCentre;
import HC.EvaluationHallSharedArea.IEvaluationHall_CallCentre;
import HC.MedicalHallSharedArea.IMedicalHall_CallCentre;
import HC.WaitingHallSharedArea.IWaitingHall_CallCentre;


/**
 * Represents the Call Centre Thread
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */
public class TCallCentre extends Thread {
    
    /* Shared Area Entrance Hall */
    private final IEntranceHall_CallCentre iEntranceHall;
    
    /* Shared Area Evaluation Hall */
    private final IEvaluationHall_CallCentre iEvaluationHall;
    
    /* Shared Area Waiting Hall */
    private final IWaitingHall_CallCentre iWaitingHall;
    
    /* Shared Area Medical Hall */
    private final IMedicalHall_CallCentre iMedicalHall;
    
    /* Number of patients until the simulation is finished */
    private int nPatients;
    
    /**
     * Call Centre constructor
     * @param iEntranceHall Entrance Hall Shared Area (interface)
     * @param iEvaluationHall Evaluation Hall Shared Area (interface)
     * @param iWaitingHall Waiting Hall Shared Area (interface)
     * @param iMedicalHall Medical Hall Shared Area (interface)
     * @param nPatients Number of patients
     */
    public TCallCentre(IEntranceHall_CallCentre iEntranceHall, 
            IEvaluationHall_CallCentre iEvaluationHall, IWaitingHall_CallCentre iWaitingHall, IMedicalHall_CallCentre iMedicalHall, int nPatients){
        this.iEntranceHall = iEntranceHall;
        this.iEvaluationHall = iEvaluationHall;
        this.iWaitingHall = iWaitingHall;
        this.iMedicalHall = iMedicalHall;
        this.nPatients = nPatients;
    }
    
    /**
     * Call Centre Life Cycle
     */
    @Override
    public void run(){

        while(true){
            
            /**
             * The call Centre checks if there are patients waiting for evaluation 
             * and if there is space available (4 seats)
             */
            if(iEntranceHall.lockHasWaitersForEvaluation() && iEvaluationHall.getCurrentSize() < 4){
                
                /*signal patient to go to evaluation */
                iEntranceHall.callForEvaluation();
                
                /* wait for client to join */
                iEntranceHall.waitForClientToJoin();
            }

            
            /**
             * The call Centre checks if there are CHILDREN patients waiting to go to Medical Hall
             * and if there is space available (only 1 seat)
             * The call Centre calls the patients by the following order: RED, YELLOW, BLUE
             */
            if (iWaitingHall.lockHasRedChildrenWaitersForMedicalHall() && iMedicalHall.getCurrentSizeChildrenFirst() < 1 ){
                
                /* signal children RED to go to medical hall */
                iWaitingHall.signalRedChildrenToGoToMedicalHall();
                
                /* wait for children to join the medical hall */
                iWaitingHall.waitForChildPatientToJoinMedicalHall();
                
            } else if (iWaitingHall.lockHasYellowChildrenWaitersForMedicalHall() && iMedicalHall.getCurrentSizeChildrenFirst() < 1 ){
                
                /* signal children YELLOW to go to medical hall */
                iWaitingHall.signalYellowChildrenToGoToMedicalHall();
                
                /* wait for children to join the medical hall */
                iWaitingHall.waitForChildPatientToJoinMedicalHall();
                
            } else if (iWaitingHall.lockHasBlueChildrenWaitersForMedicalHall() && iMedicalHall.getCurrentSizeChildrenFirst() < 1 ){
                
                /* signal children BLUE to go to medical hall */
                iWaitingHall.signalBlueChildrenToGoToMedicalHall();
                
                /* wait for children to join the medical hall */
                iWaitingHall.waitForChildPatientToJoinMedicalHall();     
            }
            
            /**
             * The call Centre checks if there are ADULT patients waiting to go to Medical Hall
             * and if there is space available (only 1 seat)
             * The call Centre calls the patients by the following order: RED, YELLOW, BLUE
             */
            if (iWaitingHall.lockHasRedAdultWaitersForMedicalHall() && iMedicalHall.getCurrentSizeAdultFirst() < 1 ){
                
                /* signal adult RED to go to medical hall */
                iWaitingHall.signalRedAdultToGoToMedicalHall();
                
                /* wait for adult to join the medical hall */
                iWaitingHall.waitForAdultPatientToJoinMedicalHall();
                
            } else if (iWaitingHall.lockHasYellowAdultWaitersForMedicalHall() && iMedicalHall.getCurrentSizeAdultFirst() < 1 ){
                
                /* signal adult YELLOW to go to medical hall */
                iWaitingHall.signalYellowAdultToGoToMedicalHall();
                
                /* wait for adult to join the medical hall */
                iWaitingHall.waitForAdultPatientToJoinMedicalHall();
                
            } else if (iWaitingHall.lockHasBlueAdultWaitersForMedicalHall() && iMedicalHall.getCurrentSizeAdultFirst() < 1 ){
                
                /* signal adult BLUE to go to medical hall */
                iWaitingHall.signalBlueAdultToGoToMedicalHall();

                /* wait for adult to join the medical hall */                
                iWaitingHall.waitForAdultPatientToJoinMedicalHall();
            }
            
            
            /**
             * The call Centre checks if there are ADULT patients waiting to go to 
             * the second rooms of the Medical Hall
             * and if there is space available (2 seats for adults, 2 seats for children)
             */
            if ( iMedicalHall.lockHasAdultWaitingForSecondRoom() && iMedicalHall.getCurrentSizeAdultSecond() < 2) {
                
                /* signal ADULT patient to go to second hall medical hall */
                iMedicalHall.signalAdultToGoToSecondRoom();
                
                /* decrease the number of patients who have already paid */
                this.nPatients--;
            }
            
            
            /**
             * The call Centre checks if there are CHILDREN patients waiting to go to 
             * the second rooms of the Medical Hall
             * and if there is space available (2 seats for adults, 2 seats for children)
             */
            if ( iMedicalHall.lockHasChildrenWaitingForSecondRoom() && iMedicalHall.getCurrentSizeChildrenSecond() < 2){
                
                /* signal CHILDREN patient to go to second hall medical hall */
                iMedicalHall.signalChildrenToGoToSecondRoom();
                
                /* decrease the number of patients who have already paid */
                this.nPatients--;
            }
            
            /* if the number of patients is equal to 0 it means that the simulation ended */
            if(this.nPatients == 0){
                break;
            }
        }
        
        /* Call Centre end of simulation */
        //System.out.println("CALL CENTRE: END OF SIMULATION");
    }
    
}
