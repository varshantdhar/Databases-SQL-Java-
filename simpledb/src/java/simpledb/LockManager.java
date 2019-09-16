package simpledb;

import java.util.*;

public class LockManager {

    private Map<PageId, Lock> pagesToLocks;
    private Map<TransactionId, Set<TransactionId>> depGraph;
    private Map<TransactionId, Set<PageId>> pagesHeld;

    public LockManager() {
        pagesToLocks = new HashMap<PageId, Lock>();
        depGraph = new HashMap<TransactionId, Set<TransactionId>>();
        pagesHeld = new HashMap<TransactionId, Set<PageId>>();
    }
    
    private Set<PageId> addGetHeldPages(TransactionId tid) {
        if (!pagesHeld.containsKey(tid))
            pagesHeld.put(tid, new HashSet<PageId>());
        return pagesHeld.get(tid);
    }

    public void acquireReadLock(TransactionId tid, PageId pid) throws TransactionAbortedException {
        Lock lock;
        
        synchronized (this) {
            lock = getOrCreateLock(pid);
            if (lock.heldBy(tid)) 
            	return;
            if (!lock.holders().isEmpty() && lock.isExclusive()) {
                depGraph.put(tid, lock.holders());
                if (hasDeadlock(tid)) {
                    depGraph.remove(tid);
                    throw new TransactionAbortedException();
                }
            }
        }
        lock.readLock(tid);
        synchronized (this) {
            depGraph.remove(tid);
            addGetHeldPages(tid).add(pid);
        }
    }

    public void acquireWriteLock(TransactionId tid, PageId pid) throws TransactionAbortedException {
        Lock lock;
        
        synchronized (this) {
            lock = getOrCreateLock(pid);
            if (lock.isExclusive() && lock.heldBy(tid))
                return;
            if (!lock.holders().isEmpty()) {
                depGraph.put(tid, lock.holders());
                if (hasDeadlock(tid)) {
                    depGraph.remove(tid);
                    throw new TransactionAbortedException();
                }
            }
        }
        lock.writeLock(tid);
        synchronized (this) {
            depGraph.remove(tid);
            addGetHeldPages(tid).add(pid);
        }
    }

    public synchronized void releaseLock(TransactionId tid, PageId pid) {
        if (!pagesToLocks.containsKey(pid)) return;
        Lock lock = pagesToLocks.get(pid);
        pagesHeld.get(tid).remove(pid);
        lock.unlock(tid);
    }

    public synchronized void releaseAllLocks(TransactionId tid) {
        if (!pagesHeld.containsKey(tid)) 
        	return;
        Set<PageId> pages = pagesHeld.get(tid);
        
        for (Object pageId: pages.toArray()) {
            releaseLock(tid, ((PageId) pageId));
        }
        pagesHeld.remove(tid);
    }

    private Lock getOrCreateLock(PageId pageId) {
        if (!pagesToLocks.containsKey(pageId))
            pagesToLocks.put(pageId, new Lock());
        return pagesToLocks.get(pageId);
    }


    private boolean hasDeadlock(TransactionId tid) {
        Set<TransactionId> visited = new HashSet<TransactionId>();
        Queue<TransactionId> q = new LinkedList<TransactionId>();
        
        visited.add(tid);
        q.offer(tid);
        while (!q.isEmpty()) {
            TransactionId head = q.poll();
            if (!depGraph.containsKey(head)) 
            	continue;
            for (TransactionId adj: depGraph.get(head)) {
                if (adj.equals(head)) 
                	continue;
                if (!visited.contains(adj)) {
                    visited.add(adj);
                    q.offer(adj);
                } else 
                    return true;
            }
        }
        return false;
    }

    public boolean holdsLock(TransactionId tid, PageId pid) {
        return (pagesHeld.containsKey(tid)
                && pagesHeld.get(tid).contains(pid));
    }

    public Set<PageId> getPagesHeldBy(TransactionId tid) {
        if (pagesHeld.containsKey(tid))
            return pagesHeld.get(tid);
        return null;
    }
}
