package HC.ActiveEntities; 


import HC.EntranceHallSharedArea.MEntranceHallSharedArea;
import HC.EvaluationHallSharedArea.MEvaluationHallSharedArea;
import HC.Main.HCP_GUI;
import HC.MedicalHallSharedArea.MMedicalHallSharedArea;
import HC.PaymentHallSharedArea.MPaymentHallSharedArea;
import HC.WaitingHallSharedArea.MWaitingHallSharedArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import HC.EntranceHallSharedArea.IEntranceHall_CallCentre;
import HC.EvaluationHallSharedArea.IEvaluationHall_CallCentre;
import HC.EvaluationHallSharedArea.IEvaluationHall_Nurse;
import HC.Logger.MLogger;
import HC.MedicalHallSharedArea.IMedicalHall_CallCentre;
import HC.MedicalHallSharedArea.IMedicalHall_Doctor;
import HC.PaymentHallSharedArea.IPaymentHall_Cashier;
import HC.WaitingHallSharedArea.IWaitingHall_CallCentre;
import java.io.IOException;

/**
 * Represents the thread that is created whenever a client connects to the socket
 * @author João Ferreira 80041
 * @author João Magalhães 79923
 */

public class TServerClient extends Thread {
    /* client socket (CCP) */
    private final Socket client;
    
    /* Number assigned to each new client */
    private final int clientNo;
    
    private final HCP_GUI gui;
        
    /* Shared Area Payment Hall */
    private final MEntranceHallSharedArea sharedETH;
    
    /* Shared Area Payment Hall */
    private final MEvaluationHallSharedArea sharedEVH;
    
    /* Shared Area Payment Hall */
    private final MWaitingHallSharedArea sharedWTH;
    
    /* Shared Area Payment Hall */
    private final MMedicalHallSharedArea sharedMDH;
    
    /* Shared Area Payment Hall */
    private final MPaymentHallSharedArea sharedPYH;
    
    /* Logger Monitor*/
    private final MLogger logger;
    
    /* Call Centre thread */
    private TCallCentre callCentre;
    
    /* Nurses threads */
    private TNurse nurse1;
    private TNurse nurse2;
    private TNurse nurse3;
    private TNurse nurse4;
    
    /* Doctors Threads */
    private TDoctor doctor1;
    private TDoctor doctor2;
    private TDoctor doctor3;
    private TDoctor doctor4;
    
    /* Cashier Thread */
    private TCashier cashier;
    
    /**
     * TServerClient constructor
     * @param inSocket Communication Socket
     * @param counter Client counter 
     * @param gui HCP GUI
     * @param sharedETH Entrance Hall Shared Area
     * @param sharedEVH Evaluation Hall Shared Area
     * @param sharedWTH Waiting Hall Shared Area
     * @param sharedMDH Medical Hall Shared Area
     * @param sharedPYH Payment Hall Shared Area
     * @param logger Logger file
     */
    public TServerClient(Socket inSocket,int counter, HCP_GUI gui,
            MEntranceHallSharedArea sharedETH, 
            MEvaluationHallSharedArea sharedEVH, 
            MWaitingHallSharedArea sharedWTH, 
            MMedicalHallSharedArea sharedMDH,
            MPaymentHallSharedArea sharedPYH, MLogger logger){
      client = inSocket;
      clientNo=counter;
      this.gui = gui;
      this.sharedETH = sharedETH;
      this.sharedEVH = sharedEVH;
      this.sharedWTH = sharedWTH;
      this.sharedMDH = sharedMDH;
      this.sharedPYH = sharedPYH;
      this.logger = logger;
     
    }
    
    /**
     * Life cycle (whenever a client connects to the socket)
     */
    @Override
    public void run(){
        
        try{
        
            /* ArrayList Patients that contains all patients - used for buttons STOP, SUSPEND, RESUME */
            ArrayList<TPatient> patients = new ArrayList<>();
            
            /* Socket in / out */
            DataInputStream inStream = new DataInputStream(client.getInputStream());
            DataOutputStream outStream = new DataOutputStream(client.getOutputStream());
            
            /* message received */
            String clientMessage="";
            
            /* message to send */
            String serverMessage="";
            
            while(true){
              
                /* Message received */
              clientMessage=inStream.readUTF();
              
              /* The message will contain all the simulation parameters */
              //System.out.println("From Client: " +clientNo+ ": "+clientMessage);
              
              /* Message to send */
              serverMessage = "OK";
                
              /* Create Entities */
                if (clientMessage.startsWith("CC|CREATE|")){
                    /* Message format: CC| CREATE | nAdults | nChildren | nSeats | ttm | evt | mdt | pyt */
                    
                    
                    /* default gui */ 
                    gui.restore();
                    
                    /* clear previous patients */
                    patients = new ArrayList<>();
                    
                    /* split message */
                    String[] info = clientMessage.split("\\|");
                    
                    /* number of adults */
                    int nAdults = Integer.valueOf(info[2]);
                    
                    /* number of children */
                    int nChildren = Integer.valueOf(info[3]);
                    
                    /* number of seats */
                    int nSeats = Integer.valueOf(info[4]);
                    
                    /* time to move */
                    int ttm = Integer.valueOf(info[5]);
                    
                    /* evaluation time */
                    String evt = info[6];
                    
                    /* medical time */
                    String mdt = info[7];
                    
                    /* payment time */
                    String pyt = info[8];
                    
                    /* update gui with number of seats available */                    
                    gui.setNumberSeats(nSeats);
                    
                    /* set size of fifo in entrance hall */
                    this.sharedETH.setSize(nAdults + nChildren, nSeats / 2);
                    
                    /* set size of fifo in waiting hall */
                    this.sharedWTH.setSize(nAdults + nChildren, nSeats / 2);
                    
                    /* set size of fifo in payment hall */
                    this.sharedPYH.setSize(nAdults + nChildren);
                    
                    /* logger */
                    this.logger.writeFirstLines(nAdults, nChildren, nSeats);
                    
                    this.logger.writeLogger("RUN |             |                     |               |                         |         |    \n");
                    System.out.print("RUN |             |                     |               |                         |         |    \n");
                    
                    /* Call Centre Thread */
                    callCentre = new TCallCentre( (IEntranceHall_CallCentre) sharedETH, 
                            (IEvaluationHall_CallCentre) sharedEVH, 
                            (IWaitingHall_CallCentre) sharedWTH,
                            (IMedicalHall_CallCentre) sharedMDH, nAdults + nChildren);
                    callCentre.start();
                    
                    
                    /* Nurse threads start */
                    nurse1 = new TNurse( (IEvaluationHall_Nurse) sharedEVH, nChildren + nAdults);
                    nurse1.start();
                    
                    nurse2 = new TNurse( (IEvaluationHall_Nurse) sharedEVH, nChildren + nAdults);
                    nurse2.start();
                    
                    nurse3 = new TNurse( (IEvaluationHall_Nurse) sharedEVH, nChildren + nAdults);
                    nurse3.start();
                    
                    nurse4 = new TNurse( (IEvaluationHall_Nurse) sharedEVH, nChildren + nAdults);
                    nurse4.start();
                    
                    
                    /* Doctor threads start */
                    doctor1 = new TDoctor( (IMedicalHall_Doctor) sharedMDH, nChildren + nAdults);
                    doctor1.start();
                    
                    doctor2 = new TDoctor( (IMedicalHall_Doctor) sharedMDH, nChildren + nAdults);
                    doctor2.start();
                    
                    doctor3 = new TDoctor( (IMedicalHall_Doctor) sharedMDH, nChildren + nAdults);
                    doctor3.start();
                    
                    doctor4 = new TDoctor( (IMedicalHall_Doctor) sharedMDH, nChildren + nAdults);
                    doctor4.start();
                    
                    /* Cashier thread start */
                    cashier = new TCashier( (IPaymentHall_Cashier) sharedPYH, nChildren + nAdults);
                    cashier.start();
                    
                    /* Adult thread starts */
                    for(int i = 0; i < nAdults; i++){
                        TPatient p;
                        
                        if ( i >= 10){
                            p = new TPatient("A"+i, ttm, sharedETH, sharedEVH, sharedWTH, sharedMDH, sharedPYH);
                        }
                        else{
                            p = new TPatient("A0"+i, ttm, sharedETH, sharedEVH, sharedWTH, sharedMDH, sharedPYH);
                        
                        }
                        
                        /* evaluation time */
                        p.setEvaluationTime(evt);
                        
                        /* medical time */
                        p.setMedicalTime(mdt);
                        
                        /* payment time */
                        p.setPaymentTime(pyt);
                        
                        /* add patient to arraylist */
                        patients.add(p);
                        
                        /* start */
                        p.start();
                    }

                    for(int j = 0; j < nChildren; j++) {
                        TPatient p;
                        
                        if ( j >= 10){
                            p = new TPatient("C"+j, ttm, sharedETH, sharedEVH, sharedWTH, sharedMDH, sharedPYH);
                        } else {
                            p = new TPatient("C0"+j, ttm, sharedETH, sharedEVH, sharedWTH, sharedMDH, sharedPYH);
                        }
                        
                        
                        /* evaluation time */
                        p.setEvaluationTime(evt);
                        
                        /* medical time */
                        p.setMedicalTime(mdt);
                        
                        /* payment time */
                        p.setPaymentTime(pyt);
                        
                        /* add patient to arraylist */
                        patients.add(p); 
                        
                        /* start */
                        p.start();
                    }
                }
                else if ( clientMessage.equals("CC|SUSPEND") ){ /* suspend simulation */
                  for(TPatient pat: patients) {
                      pat.suspendPatient();
                  }
                  
                  this.logger.writeLogger("SUS |             |                     |               |                         |         |    \n");
                  System.out.print("SUS |             |                     |               |                         |         |    \n");
                }
                else if ( clientMessage.equals("CC|RESUME") ){ /* resume simulation */
                    
                    this.logger.writeLogger("RUN |             |                     |               |                         |         |    \n");
                    System.out.print("RUN |             |                     |               |                         |         |    \n");
                    
                    for(TPatient pat: patients) {
                        pat.resumePatient();
                    }
                    
                    
                }
                else if ( clientMessage.equals("CC|STOP") ){ /* stop simulation */
                    
                    this.logger.writeLogger("STO |             |                     |               |                         |         |    \n");
                    System.out.print("STO |             |                     |               |                         |         |    \n");
                    //this.logger.closeLogger();
                    
                    for(TPatient pat: patients) {
                        pat.stopPatient();
                        pat.interrupt();
                    }
                    
                    
                }
                else if ( clientMessage.equals("CC|END") ){ /* end simulation */
                    //System.out.println("aqui2");
                    this.logger.writeLogger("END |             |                     |               |                         |         |    \n");
                    this.logger.closeLogger();
                    System.out.print("END |             |                     |               |                         |         |    \n");
                    System.exit(0);
                   
                }
                outStream.writeUTF(serverMessage);
                outStream.flush();
              }

            }catch(IOException | NumberFormatException ex){ /* client ends connection */
              System.out.println(ex);
            }finally{
              System.out.println("Client -" + clientNo + " exit!! ");
            }
        }
}