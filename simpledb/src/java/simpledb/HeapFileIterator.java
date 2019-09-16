package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFileIterator is an implementation of an iterator for HeapFiles.
 */

public class HeapFileIterator implements DbFileIterator {

	private HeapFile f;
	private TransactionId tid;
	private boolean open;
	private int curPage;
	private Iterator<Tuple> curHeapFileIterator;

	public HeapFileIterator(HeapFile file, TransactionId tid) {
		this.f = file;
		this.tid = tid;

		this.open = false;

	}

	@Override
	public void open() throws DbException, TransactionAbortedException {
		// set open to true and init the iterator
		this.open = true;
		this.curPage = 0;
		HeapPageId pageId = new HeapPageId(this.f.getId(), this.curPage);
		this.curHeapFileIterator = ((HeapPage) (Database.getBufferPool().getPage(this.tid, pageId,
				Permissions.READ_ONLY))).iterator();
	}

	@Override
	public boolean hasNext() throws DbException, TransactionAbortedException {
		/*
		 * checks if it has next by checking if current iterator has next and if not
		 * checks for a next page if no next page returns false.
		 */

		if (!this.open) {
			return false;
		} else {
			while (!(this.curHeapFileIterator.hasNext())) {
				// loop through in case there is a page with no tuples
				if (this.curPage == (this.f.numPages() - 1)) {
					// careful here for off by one bugs
					return false;
				}
				this.curPage++;
				HeapPageId pageId = new HeapPageId(this.f.getId(), this.curPage);
				this.curHeapFileIterator = ((HeapPage) Database.getBufferPool().getPage(this.tid, pageId,
						Permissions.READ_ONLY)).iterator();

			}
			return true;
		}
	}

	@Override
	public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
		/*
		 * Returns the next item in the iterator or raises noSuchElement if there is not
		 * next element
		 */
		
		if (!this.open) {
			// if not open def not an element
			throw new NoSuchElementException("not open");
		}
		if (!this.hasNext()) {
			// call hasNext because could be current iterator has no next but the next page does
			throw new NoSuchElementException("No more items");
		}
		return this.curHeapFileIterator.next(); // get next item
	}

	@Override
	public void rewind() throws DbException, TransactionAbortedException {
		// TODO Auto-generated method stub
		this.close();
		this.open();

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		// clean up method
		this.open = false;

	}

}