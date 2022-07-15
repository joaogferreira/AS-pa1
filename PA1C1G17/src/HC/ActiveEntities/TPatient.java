package HC.ActiveEntities;

import HC.EntranceHallSharedArea.IEntranceHall_Patient;
import HC.EvaluationHallSharedArea.IEvaluationHall_Patient;
import HC.Main.HCP_Main;
import java.io.IOException;
import HC.MedicalHallSharedArea.IMedicalHall_Patient;
import HC.PaymentHallSharedArea.IPaymentHall_Patient;
import HC.WaitingHallSharedArea.IWaitingHall_Patient;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;


/**
 * Represents the Patient Thread
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class TPatient extends Thread {
    
    /* Name of the patient
       Adults - A0, A1, ...
       Children - C0, C1, ...
    */
    private final String name;
    
    /* Assigned color in evaluation hall */
    private String color;
    
    /* Time to move */
    private final int ttm;
    
    /* Shared Area Entrance Hall */
    private final IEntranceHall_Patient sharedETH;
    
    /* Shared Area Evaluation Hall */
    private final IEvaluationHall_Patient sharedEVH;
    
    /* Shared Area Waiting Hall */
    private final IWaitingHall_Patient sharedWTH;
    
    /* Shared Area Medical Hall */
    private final IMedicalHall_Patient sharedMDH;
    
    /* Shared Area Payment Hall */
    private final IPaymentHall_Patient sharedPYH;
    
    /* Sequential Number - Entrance Hall  */
    private int sequentialNumber;
    
    /* Evaluation time */
    private int evtime;
    
    /* Evaluation room */
    private String evaluationRoom;
    
    /* Waiting number - Waiting Hall */
    private int waitingNumber;
    
    /* Payment number - Payment Hall */
    private int paymentNumber;
    
    /* Medical Time - Medical Hall */
    private int mdtime;
    
    /* Medical room */
    private String medicalRoom;
    
    /* Payment Time - Payment Hall */
    private int pytime;
    
    /* Suspend / Resume Variables */
    private boolean isPaused = false;
    private final ReentrantLock suspendLock = new ReentrantLock();
    private final Condition suspend = suspendLock.newCondition();
    
    /* Stop Variables */
    private boolean isStopped = false;
    private final ReentrantLock stopLock = new ReentrantLock();

    /**
     * Patient constructor
     * @param name Name of the patient (Ax, Cy)
     * @param ttm Time to move
     * @param sharedETH Entrance Hall Shared Area (interface)
     * @param sharedEVH Evaluation Hall Shared Area (interface)
     * @param sharedWTH Waiting Hall Shared Area (interface)
     * @param sharedMDH Medical Hall Shared Area (interface)
     * @param sharedPYH Payment Hall Shared Area (interface)
     * @throws java.io.IOException
     */
    public TPatient(String name, int ttm, IEntranceHall_Patient sharedETH, 
            IEvaluationHall_Patient sharedEVH, 
            IWaitingHall_Patient sharedWTH,
            IMedicalHall_Patient sharedMDH,
            IPaymentHall_Patient sharedPYH) throws IOException {
        this.name = name;
        this.ttm = ttm;
        this.sharedETH = sharedETH;
        this.sharedEVH = sharedEVH;
        this.sharedWTH = sharedWTH;
        this.sharedMDH = sharedMDH;
        this.sharedPYH = sharedPYH;
        this.isPaused = false;
        this.isStopped = false;
    }
    
    /**
     * Patient Life Cycle
     */
    @Override
    public void run(){
        
        /* Time to move to first room in entrance hall */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        
        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;

        
        /* Join Entrance Hall first room */
        if ( this.sharedETH.joinFirstRoom(this) ){
            /* add text to GUI - Entrance Hall First Room */
            HCP_Main.appendPatientToEntranceHallFirstRoom(this.name);
        }
        
        /* Time to move to second Room in entrance hall */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        
        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        /* Join Entrance Hall second room (limited seats) */
        if(this.sharedETH.joinSecondRoom(this, this.name)){
            /* add text to GUI - Entrnace Hall Second room */
            HCP_Main.appendPatientToEntranceHallSecondRoom(this.name+",etn:"+this.sequentialNumber);
        }
        
        /* Time to move to Evaluation Hall */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        
        /* Check if simulation is suspended or stopped  */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        /* wait for signal from call centre to join evaluation hall */
        this.sharedETH.waitForCallToEvaluationHall();
        
        
        /* Leave entrance Hall second room */
        if (this.sharedETH.leaveSecondRoom(this, this.name) ){ 
            /* Clear text from GUI */
            HCP_Main.clearEntranceHallSecondRoom(this.name+",etn:"+this.sequentialNumber);
        }
        
        /* Time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
   
        
        /* Join Evaluation Hall */
        try {
            if ( this.sharedEVH.joinRoom(this) ){        
                /* add patient to gui */
                HCP_Main.appendPatientToEvaluationHall(this, this.name+",etn:"+this.sequentialNumber);
                
                this.sharedEVH.evaluationHallLogger(this);
                
                
                /* signal call centre that patient left entrance hall and joined evaluation hall*/
                this.sharedETH.wakeUpCallCentre();

                /* Time spent on evaluation - EVT */
                Thread.sleep( this.getEVT() );
                
                this.sharedEVH.evaluationHallLoggerColor(this);

            }
        } catch(InterruptedException ex) {} catch (IOException ex) {
            Logger.getLogger(TPatient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* Time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;

        /* Leave Evaluation Hall */  
        this.sharedEVH.leaveRoom(this);
        
        /* clear text from GUI */
        HCP_Main.clearEvaluationHall(this.name+",etn:"+this.sequentialNumber);

        try {
            /* Join Waiting Hall first room */
            if(this.sharedWTH.joinFirstRoom(this)){
                /* add text to GUI */
                HCP_Main.appendPatientToWaitingHallFirstRoom(this.name+
                    ",etn:"+this.sequentialNumber+","+this.getColor() );
            }    
        } catch (BadLocationException ex) {
            Logger.getLogger(TPatient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /* Time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        
        /* Join waiting hall second room (limited seats) */
        if(this.sharedWTH.joinSecondRoom(this, this.name)){
            try {
                /* add text to GUI */
                HCP_Main.appendPatientToWaitingHallSecondRoom(this.name+",etn:"+this.sequentialNumber+","
                        +this.color+","+this.waitingNumber);
            } catch (BadLocationException ex) {
                Logger.getLogger(TPatient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /* Time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}

        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        /** 
         * wait for signal from call Centre to join medical hall 
         * each patient waits in a specific condition accordingly to his color 
         * and type (adult or child)
        */
        if (this.name.startsWith("C")){
            if (this.color.startsWith("red")){
                this.sharedWTH.redChildrenWaitToGoToMedicalHall();
            } else if (this.color.startsWith("yellow")){
                this.sharedWTH.yellowChildrenWaitToGoToMedicalHall();
            } else if (this.color.startsWith("blue")){
                this.sharedWTH.blueChildrenWaitToGoToMedicalHall();
            }
        } else {
            if (this.color.startsWith("red")){
                this.sharedWTH.redAdultWaitToGoToMedicalHall();
            } else if (this.color.startsWith("yellow")){
                this.sharedWTH.yellowAdultWaitToGoToMedicalHall();
            } else if (this.color.startsWith("blue")){
                this.sharedWTH.blueAdultWaitToGoToMedicalHall();
            }
        }
        
        
        /* leave waiting hall second room */
        if (this.sharedWTH.leaveSecondRoom(this, this.name) ){ 
            HCP_Main.clearWaitingHallSecondRoom(this.name+",etn:"+this.sequentialNumber+","+this.color+","+this.waitingNumber);
        }
        
        /* time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        /* join medical hall first room */
        if ( this.sharedMDH.joinFirstRoom(this) ) {  
            
            if (this.name.startsWith("C")){
                
                /* signal call centre that patient left waiting hall and joined medical hall */
                this.sharedWTH.childrenPatientInMedicalHall(); 

                /* add text to GUI */ 
                HCP_Main.appendPatientToMedicallHallFirstRoom(this.name+",etn:"+this.sequentialNumber+","+this.color+","+this.waitingNumber);

            } else {
                
                /* signal call centre that patient left waiting hall and joined medical hall */
                this.sharedWTH.adultPatientInMedicalHall();

                /* add text to GUI */ 
                HCP_Main.appendPatientToMedicallHallFirstRoom(this.name+",etn:"+this.sequentialNumber+","+this.color+","+this.waitingNumber);
            }
        }
        
        /* time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        
        /* Check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        /* wait for signal from call centre to go to second room in medical hall */
        if ( this.name.startsWith("A") ){
            this.sharedMDH.adultWaitToGoToSecondRoom();
        } else if ( this.name.startsWith("C") ){
            this.sharedMDH.childrenWaitToGoToSecondRoom();
        }
        

        /* leave first room from medical hall */
        this.sharedMDH.leaveFirstRoom(this);
        
        /* clear text from GUI */
        HCP_Main.clearMedicalHallFirstRoom(this.name+",etn:"+this.sequentialNumber+","+this.color+","+this.waitingNumber);
        
        
        /* time to move*/
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        /* check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        /* join second room in medical hall */
        if (this.sharedMDH.joinSecondRoom(this) ){
            /* add text to GUI */
            HCP_Main.appendPatientToMedicallHallSecondRoom(this, this.name+",etn:"+this.sequentialNumber+","+this.color+","+this.waitingNumber);
            
            try {
                this.sharedMDH.medicalHallLogger(this);
            } catch (IOException ex) {
                Logger.getLogger(TPatient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                

        /* wait for signal from doctor */
        if ( this.name.startsWith("A") ){
            this.sharedMDH.adultWaitForDoctor();
        } else if ( this.name.startsWith("C") ){
            this.sharedMDH.childrenWaitForDoctor();
        }
        
        
        try {
            /* Time spent with doctor - Medical time */
            Thread.sleep( this.getMDT() );
        } catch (InterruptedException ex) {}
        
        
        /* Leave second room from medical hall */
        if (this.sharedMDH.leaveSecondRoom(this)){
            /* clear text from GUI */
            HCP_Main.clearMedicalHallSecondRoom(this.name+",etn:"+this.sequentialNumber+","+this.color+","+this.waitingNumber);            
        }
        
        /* time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}
        
        
        /* check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        /* join payment hall first room */
        if ( this.sharedPYH.joinFirstRoom(this) ){
            /* add text to GUI */
            HCP_Main.appendPatientToPaymentHallFirstRoom(this.name+",etn:"+this.sequentialNumber+",pyn:"+this.paymentNumber);
        }
        
        /* wait from signal from cashier */
        this.sharedPYH.waitForCashier();
        
        
        /* left payment hall first room */
        this.sharedPYH.leaveFirstRoom(this);
        
        /* clear text GUI */
        HCP_Main.clearPaymentHallFirstRoom(this.name+",etn:"+this.sequentialNumber+",pyn:"+this.paymentNumber);
        
        /* time to move */
        try {
            Thread.sleep(ttm);
        } catch (InterruptedException ex) {}

        
        /* check if simulation is suspended or stopped */
        this.checkSuspend();
        if(this.checkStop())
            return;
        
        
        /* join payment hall second room */
        if ( this.sharedPYH.joinSecondRoom(this) ){
            /* add text do second room */
            HCP_Main.appendPatientToPaymentHallSecondRoom(this.name+",etn:"+this.sequentialNumber+",pyn:"+this.paymentNumber);
            
            /* signal cashier that patient joined payment hall second room */
            this.sharedPYH.signalCashier();
        }
        
        /* Payment time */
        try {
            /* time spent to pay - PYT */
            Thread.sleep( this.getPYT() );
        } catch (InterruptedException ex) {}
        
        
        /* LEAVE SECOND ROOM */ 
        if( this.sharedPYH.leaveSecondRoom(this) ){
            HCP_Main.clearPaymentHallSecondRoom(this.name+",etn:"+this.sequentialNumber+",pyn:"+this.paymentNumber);
        }
        
        /* Patient end of simulation */
        //System.out.println(this.name+" END OF SIMULATION");
        
        
        
    }
    
    /**
     * Assign color to patient
     * @param color 
     */
    public void setColor(String color){
        this.color = color;
    }
    
    
    public void setEvaluationRoom(String room){
        this.evaluationRoom = room;
    }
     
    public void setMedicalRoom(String room){
        this.medicalRoom = room;
    }
    
    public String getEvaluationRoom(){
        return this.evaluationRoom;
    }
    
    public String getMedicalRoom(){
        return this.medicalRoom;
    }
    
    public int getSequentialNumber(){
        return this.sequentialNumber;
    }
    
    public int getPYN(){
        return this.paymentNumber;
    }
    
    
    /**
     * Get patient color
     * @return color
     */
    public String getColor(){
        return color;
    }
    
    
    /**
     * Get patient name
     * @return name
     */
    public String getPatientName(){
        return name;
    }
    
    /**
     * Assign sequential number to patient
     * @param number 
     */
    public void setSequentialNumber(int number) {
        this.sequentialNumber = number;
    }
    
    /**
     * Assign waiting number to patient
     * @param number 
     */
    public void setWaitingNumber(int number){
        this.waitingNumber = number;
    }
    
    /**
     * Assign payment number to patient
     * @param number 
     */
    public void setPaymentNumber(int number){
        this.paymentNumber = number;
    }
    
    /**
     * Wait for Nurse to return color
     */
    public void waitForNurse(){
        this.sharedEVH.waitForColor();
    }
    
    /**
     * Get Evaluation Time 
     * @return evaluation time
     */
    public int getEVT(){
        return this.evtime;
    }
    
    /**
     * Get Payment time
     * @return payment time
     */
    public int getPYT(){
        return this.pytime;
    }
    
    /**
     * Get Medical Time
     * @return medical time
     */
    private long getMDT() {
        return this.mdtime;
    }
    
    /**
     * Set Evaluation Time accordingly to CCP (0, random(0;100), etc)
     * @param evt
     */
    public void setEvaluationTime(String evt){
        int high = 0;
        int low = 0;
        
        Random r = new Random();
        
        switch(evt){
            case "0":
                this.evtime = 0; break;
            case "100":
                this.evtime = 100; break;
            case "random (0;100)":
                high = 100;
                this.evtime = r.nextInt(high-low) + low; break;
            case "random(0;250)":
                high = 250;
                this.evtime = r.nextInt(high-low) + low; break;
            case "random(0;500)":
                high = 500;
                this.evtime = r.nextInt(high-low) + low; break;
            case "random(0;1000)":
                high = 1000;
                this.evtime = r.nextInt(high-low) + low; break;
        }
    }
    
    /**
     * Set Medical Time accordingly to CCP (0, random(0;100), etc)
     * @param mdt
     */
    public void setMedicalTime(String mdt){
        int high = 0;
        int low = 0;
        
        Random r = new Random();
        
        
        switch(mdt){
            case "0":
                this.mdtime = 0; break;
            case "100":
                this.mdtime = 100; break;
            case "random (0; 100)":
                high = 100;
                this.mdtime  = r.nextInt(high-low) + low; break;
            case "random(0; 250)":
                high = 250;
                this.mdtime  = r.nextInt(high-low) + low; break;
            case "random(0; 500)":
                high = 500;
                this.mdtime  = r.nextInt(high-low) + low; break;
            case "random(0; 1000)":
                high = 1000;
                this.mdtime  = r.nextInt(high-low) + low; break;
        }
        
        //this.mdtime = 3000;
        
    }
    
    /**
     * Set Payment Time accordingly to CCP (0, random(0;100), etc)
     * @param pyt
     */
    public void setPaymentTime(String pyt){
        int high = 0;
        int low = 0;
        
        Random r = new Random();
        
        
        switch(pyt){
            case "0":
                this.pytime = 0; break;
            case "100":
                this.pytime = 100; break;
            case "random (0; 100)":
                high = 100;
                this.pytime  = r.nextInt(high-low) + low; break;
            case "random(0; 250)":
                high = 250;
                this.pytime  = r.nextInt(high-low) + low; break;
            case "random(0; 500)":
                high = 500;
                this.pytime  = r.nextInt(high-low) + low; break;
            case "random(0; 1000)":
                high = 1000;
                this.pytime  = r.nextInt(high-low) + low; break;
        }
        
        //this.pytime = 3000;
        
    }
    
    /* Suspend button - set flag to TRUE */
    public void suspendPatient(){
        this.suspendLock.lock();
        try {
            this.isPaused = true;
        } finally {
            this.suspendLock.unlock();
        }
    }
    
    /* Check if simulation is suspended */
    public void checkSuspend(){
        this.suspendLock.lock();
        try {
            if (this.isPaused) {
                try {
                    this.suspend.await();
                } catch (InterruptedException ex) {}
            }
        } finally {
            this.suspendLock.unlock();
        }
    }
    
    /* Suspend button - set flag to FALSE */
    public void resumePatient(){
        this.suspendLock.lock();
        try {
            this.isPaused = false;
            this.suspend.signalAll();
        } finally {
            this.suspendLock.unlock();
        }
    }
    
    /* Stop button - set flag to TRUE */
    public void stopPatient(){
        this.stopLock.lock();
        try {
            this.isStopped = true;
        } finally {
            this.stopLock.unlock();
        }
    }

    /* Check if simulation is suspended */
    public boolean checkStop(){
        this.stopLock.lock();
        try {
            return this.isStopped;
        } finally {
            this.stopLock.unlock();
        }
    }
    
    
}
