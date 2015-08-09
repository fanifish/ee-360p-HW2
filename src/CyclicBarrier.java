import java.util.concurrent.Semaphore;





public class CyclicBarrier {
	int numOfThreads;
	int permits;
	int numOfThreadsOut;
	Semaphore mutex;
	Semaphore sync;
	Semaphore exitMutex;

	CyclicBarrier(int permits) {
		numOfThreads = permits;
		this.permits = permits;
		numOfThreadsOut = 0;
		mutex = new Semaphore(1, true);
		exitMutex = new Semaphore(1, true);
	}

	int await() throws InterruptedException {
		int id;
		mutex.acquire();
		numOfThreads--;
		id = numOfThreads;
		if (id == permits - 1) {
			sync = new Semaphore(-1 * (permits - 1));
		}
		sync.release();
		if (id != 0) {
			mutex.release();
		}
		sync.acquire();
		sync.release();
		exitMutex.acquire();
		if (numOfThreadsOut == permits - 1) {
			numOfThreadsOut = 0;
			numOfThreads = permits;
			mutex.release(); // to synchronize the cycle
		} else {
			numOfThreadsOut++;
		}
		exitMutex.release();
		return id;
	}
	public static void main(String args[]) throws InterruptedException {
		CyclicBarrier sync = new CyclicBarrier(5);
		Thread t[] = new Thread[10];
		int y = 0;
	//	while (true) {
			for (int i = 0; i < 10; i += 1) {
				t[i] = new Thread(new po(y, sync));
				t[i].start();
				y++;
			}
			System.out.println("Next phase");

			for (int i = 0; i < 1000000000; i += 1) {

			}
			System.out.println("all released");
	//}
	}

}

class po implements Runnable {
	int x;
	CyclicBarrier w;

	po(int n, CyclicBarrier wb) {
		x = n;
		w = wb;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("This is thread num " + x);

		try {
			System.out.println("The barries release num for "+ x+ " is " + w.await());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
