package HC.Logger;

import java.io.FileWriter;
import java.io.IOException; 
import java.util.concurrent.locks.ReentrantLock;

public class MLogger {
    
    FileWriter ficheiro;
    ReentrantLock rl = new ReentrantLock(true);
    
    public MLogger() throws IOException{
        this.ficheiro = new FileWriter("LOG.txt");
    }
    
    
    public void writeFirstLines(int nAdults, int nChildren, int nSeats ) throws IOException{
        ficheiro.write("NoA:"+ nAdults + ", NoC:" + nChildren + ", NoS:" + nSeats + "\n");
        System.out.print("NoA:"+ nAdults + ", NoC:" + nChildren + ", NoS:" + nSeats + "\n");
        
        ficheiro.write("STT | ETH ET1 ET2 | EVR1 EVR2 EVR3 EVR4 | WTH" +
            " WTR1 WTR2 | MDH" +
            " MDR1 MDR2 MDR3 MDR4 | PYH     | OUT \n");
        System.out.print("STT | ETH ET1 ET2 | EVR1 EVR2 EVR3 EVR4 | WTH" +
            " WTR1 WTR2 | MDH" +
            " MDR1 MDR2 MDR3 MDR4 | PYH     | OUT \n");
        
        ficheiro.write("INI |             |                     |               |                         |         |    \n");
        System.out.print("INI |             |                     |               |                         |         |    \n");
    }
    
    
    public void writeLogger(String text) {
        this.rl.lock();
        try {
            this.ficheiro.write(text);
        } catch (IOException e) {
        }finally {
            this.rl.unlock();
        }
    }
    
    public void closeLogger() throws IOException{
        this.rl.lock();
        try {
            this.ficheiro.close();
        } finally {
            this.rl.unlock();
        }
    }
}
