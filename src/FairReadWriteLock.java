import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of a fair read and write monitor
 *
 * @author Faniel ghirmay ffg92 and jose garcia jag7235
 *
 */
public class FairReadWriteLock {
	
	private AtomicInteger numOfReaders; // because multiple readers will be updating
	private int numOfWriters;
	AtomicInteger priority;
	AtomicInteger nextPriority;
	int order;
	
	final ReadWriteLockLogger logger = new ReadWriteLockLogger();
	private HashMap<Thread, Integer> mapThreadReqNo = new HashMap<Thread, Integer>();


	FairReadWriteLock() {
		numOfReaders = new AtomicInteger(0);
		numOfWriters = 0;
		priority = new AtomicInteger(0);
		nextPriority = new AtomicInteger(0);
		order = 0;
	}

	/**
	 * @throws InterruptedException
	 *
	 */
	public synchronized void beginRead(){
		logger.logTryToRead();
		mapThreadReqNo.put(Thread.currentThread(), order);
		order++;
		while (numOfWriters == 1
				|| priority.get() != mapThreadReqNo.get(Thread.currentThread())) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		numOfReaders.incrementAndGet();
		priority.incrementAndGet();	
		notifyAll();
		logger.logBeginRead();
	}
	public synchronized void endRead() {
		numOfReaders.decrementAndGet();
		if (numOfReaders.get() == 0) {
			notifyAll();
		}
		logger.logEndRead();
	}

	/**
	 * @throws InterruptedException
	 *
	 */
	public synchronized void beginWrite(){
		logger.logTryToWrite();
		mapThreadReqNo.put(Thread.currentThread(), order);
		order++;
		while (numOfReaders.get() > 0 || numOfWriters > 0
				|| mapThreadReqNo.get(Thread.currentThread()) != priority.get()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		numOfWriters++;
		logger.logBeginWrite();
	}

	public synchronized void endWrite(){
		numOfWriters--;
		priority.incrementAndGet();
		notifyAll();
		logger.logEndWrite();
	}
	public static void main(String args[]) {
		FairReadWriteLock doc = new FairReadWriteLock();
		
		Thread w[] = new Thread[20];
		Thread r[] = new Thread[20];
		for(int i=0; i < 20; i+=1){
			w[i] = new Thread(new Writer(i, i+"", doc));
			r[i] = new Thread(new Reader(i, i+"", doc));
			w[i].start();
			r[i].start();
		}
	}
}

class Writer implements Runnable {
	int x;
	String name;
	FairReadWriteLock file;

	Writer(int x, String n, FairReadWriteLock f) {
		this.x = x;
		name = n;
		file = f;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
			file.beginWrite();
			try {
				Thread.currentThread().sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			file.endWrite();
	}
}

class Reader implements Runnable {
	int x;
	String name;
	FairReadWriteLock file;

	Reader(int x, String n, FairReadWriteLock f) {
		this.x = x;
		name = n;
		file = f;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		file.beginRead();
		try {
			Thread.currentThread().sleep(30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		file.endRead();
	}
}