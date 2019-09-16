package simpledb;

import java.util.*;

/*
 * Designed my own Lock class to represent a lock. 
 */

public class Lock {
    private Map<TransactionId, Boolean> acquires;
    private Set<TransactionId> holds;
    private boolean exclusive;
    private int readCount;
    private int writeCount;

    public Lock() {
        holds = new HashSet<TransactionId>();
        acquires = new HashMap<TransactionId, Boolean>();
        exclusive = false;
        readCount = 0;
        writeCount = 0;
    }

    public void readLock(TransactionId tid) {
        if (holds.contains(tid) && !exclusive) 
        	return;
        acquires.put(tid, false);
        
        synchronized (this) {
            try {
                while (writeCount != 0) 
                	this.wait();
                readCount++;
                holds.add(tid);
                exclusive = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        acquires.remove(tid);
    }
    
    private void readUnlockWithoutNotifying(TransactionId tid) {
        if (!holds.contains(tid)) 
        	return;
        
        synchronized (this) {
            --readCount;
            holds.remove(tid);
        }
    }

    public void writeLock(TransactionId tid) {
        if (holds.contains(tid) && exclusive) 
        	return;
        if (acquires.containsKey(tid) && acquires.get(tid)) 
        	return;
        acquires.put(tid, true);
        
        synchronized (this) {
            try {
                if (holds.contains(tid)) {
                    while (holds.size() > 1)
                        this.wait();
                    readUnlockWithoutNotifying(tid);
                }
                while (readCount != 0 || writeCount != 0) 
                	this.wait();
                writeCount++;
                holds.add(tid);
                exclusive = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        acquires.remove(tid);
    }

    public void readUnlock(TransactionId tid) {
        if (!holds.contains(tid)) 
        	return;
        
        synchronized (this) {
            readCount--;
            holds.remove(tid);
            notifyAll();
        }
    }

    public void writeUnlock(TransactionId tid) {
        if (!holds.contains(tid)) 
        	return;
        if (!exclusive)
        	return;
        
        synchronized (this) {
            writeCount--;
            holds.remove(tid);
            notifyAll();
        }
    }

    public void unlock(TransactionId tid) {
        if (!exclusive)
            readUnlock(tid);
        else 
        	writeUnlock(tid);
    }

    public Set<TransactionId> holders() {
        return holds;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public Set<TransactionId> acquirers() {
        return acquires.keySet();
    }

    public boolean heldBy(TransactionId tid) {
        return holders().contains(tid);
    }

}
