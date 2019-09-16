package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

	private File f;
	private TupleDesc td;
	private int tableId;

	/**
	 * Constructs a heap file backed by the specified file.
	 * 
	 * @param f the file that stores the on-disk backing store for this heap file.
	 */
	public HeapFile(File f, TupleDesc td) {
		// some code goes here
		this.f = f;
		this.td = td;
		this.tableId = f.getAbsoluteFile().hashCode();
	}

	/**
	 * Returns the File backing this HeapFile on disk.
	 * 
	 * @return the File backing this HeapFile on disk.
	 */
	public File getFile() {
		// some code goes here
		return this.f;
	}

	/**
	 * Returns an ID uniquely identifying this HeapFile. Implementation note: you
	 * will need to generate this tableid somewhere to ensure that each HeapFile has
	 * a "unique id," and that you always return the same value for a particular
	 * HeapFile. We suggest hashing the absolute file name of the file underlying
	 * the heapfile, i.e. f.getAbsoluteFile().hashCode().
	 * 
	 * @return an ID uniquely identifying this HeapFile.
	 */
	public int getId() {
		// some code goes here
		return this.tableId;
	}

	/**
	 * Returns the TupleDesc of the table stored in this DbFile.
	 * 
	 * @return TupleDesc of this DbFile.
	 */
	public TupleDesc getTupleDesc() {
		// some code goes here
		return this.td;

	}

	 // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        if (this.getId() != pid.getTableId()) 
        	return null;
        if (pid.getPageNumber() < 0 || pid.getPageNumber() >= this.numPages()) 
        	return null;
        
        try (RandomAccessFile hf = new RandomAccessFile(this.f, "r")) {
			long pageOffset = pid.getPageNumber() * (long) BufferPool.getPageSize();
			hf.seek(pageOffset);
			byte[] page = new byte[BufferPool.getPageSize()];
			hf.read(page);
			return new HeapPage(new HeapPageId(pid.getTableId(), pid.getPageNumber()), page);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    	try (RandomAccessFile hf = new RandomAccessFile(this.f, "rw")) {
			PageId pid = page.getId();
			long pageOffset = pid.getPageNumber() * (long) BufferPool.getPageSize();
			hf.seek(pageOffset);
			hf.write(page.getPageData(), 0, BufferPool.getPageSize());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int) Math.ceil((double)f.length() / BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
        if (!td.equals(t.getTupleDesc())) throw new DbException("TupleDesc does not match.");
        int i = 0;
        HeapPage hp = null;
        for (i = 0; i < numPages(); i ++) {
            if (((HeapPage)(Database.getBufferPool()
            		.getPage(tid, new HeapPageId(tableId, i), Permissions.READ_ONLY)))
            		.getNumEmptySlots() > 0)
                break;
        }
        if (i == numPages()) {
            synchronized(this) {
                i = numPages();
                // All files are full
                hp = new HeapPage(new HeapPageId(tableId, i), HeapPage.createEmptyPageData());
                try {
                    int pageSize = BufferPool.getPageSize();
                    byte[] byteStream = hp.getPageData();
                    RandomAccessFile hf = new RandomAccessFile(f, "rw");
                    hf.seek(pageSize * i);
                    hf.write(byteStream);
                    hf.close();
                }
                catch (IOException e) {
                    throw e;
                }
            }
        }
        hp = (HeapPage)(Database.getBufferPool().getPage(tid, new HeapPageId(tableId, i), Permissions.READ_WRITE));
        hp.insertTuple(t);
        ArrayList<Page> pageList = new ArrayList<Page>();
        pageList.add(hp);
        return pageList;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
    	ArrayList<Page> pageList = new ArrayList<Page>();
    	
        if (tableId != t.getRecordId().getPageId().getTableId()) 
        	throw new DbException("Table ID does not match.");
        int pageno = t.getRecordId().getPageId().getPageNumber();
        if (pageno < 0 || pageno >= numPages()) 
        	throw new DbException("Page number is illegal.");
        HeapPage hp = (HeapPage)(Database.getBufferPool().getPage(tid, t.getRecordId().getPageId(), Permissions.READ_WRITE));
        hp.deleteTuple(t);
        pageList.add(hp);
        return pageList;
    }

	// see DbFile.java for javadocs
	public DbFileIterator iterator(TransactionId tid) {
		// some code goes here
		return new HeapFileIterator(this, tid);
	}

}
