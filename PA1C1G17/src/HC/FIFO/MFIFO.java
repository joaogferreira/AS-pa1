package HC.FIFO;

import HC.ActiveEntities.TPatient;
import HC.ITF.IProducer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MFIFO implements IProducer {
    
    private int idxPut = 0;
    private int idxGet = 0;
    private int count = 0;
    
    private final TPatient fifo[];
    private final int size;
    private final ReentrantLock rl;
    private final Condition cNotFull;
    private final Condition cNotEmpty;
    
    public MFIFO(int size) {
        this.size = size;
        fifo = new TPatient[ size ];
        rl = new ReentrantLock(true);
        cNotEmpty = rl.newCondition();
        cNotFull = rl.newCondition();
    }
    
    @Override
    public void put( TPatient value ) {
        try {
            rl.lock();
            
            while ( isFull() )
                cNotFull.await();
            
            fifo[ idxPut ] = value;
            
            idxPut = (idxPut + 1) % size;
            
            count++;
            
            cNotEmpty.signal();
        } catch ( InterruptedException ex ) {}
        finally {
            rl.unlock();
        }
    }
      
    public TPatient get() {
        try{
            rl.lock();
            try {
                while ( isEmpty() )
                    cNotEmpty.await();
            } catch( InterruptedException ex ) {}
            
            TPatient result = fifo[idxGet];
            
            idxGet = (idxGet + 1) % size;
            
            count--;
            
            cNotFull.signal();
            
            return result;
        }
        finally {
            rl.unlock();
        }
    }

    private boolean isFull() {
        return count == size;
    }

    private boolean isEmpty() {
        return count == 0;
    }
}
