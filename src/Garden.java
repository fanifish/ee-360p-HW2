import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Garden implements GardenCounts {
	int MAX;

	AtomicInteger numOfUnfilledHoles;
	AtomicInteger numOfDugHoles;
	AtomicInteger numOfSeededHoles;
	AtomicInteger numOfFilledHoles;
	

	final ReentrantLock gardenLock = new ReentrantLock(); // to enter garden
															// more like to
															// control shre of
															// the shovel

	final Condition maryIsFar = gardenLock.newCondition();
	final Condition noAvailableSeededHole = gardenLock.newCondition();
	final Condition noAvailableDugHole = gardenLock.newCondition();

	public Garden(int MAX) {
		this.MAX = MAX;
		numOfUnfilledHoles = new AtomicInteger(0);
		numOfDugHoles = new AtomicInteger(0);
		numOfSeededHoles = new AtomicInteger(0);
		numOfFilledHoles = new AtomicInteger(0);
	}

	public void startDigging() {
		gardenLock.lock();
		while (numOfDugHoles.get() - numOfFilledHoles.get() >= this.MAX) {
			try {
				maryIsFar.await();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}finally{
				
			}
		}
		this.dig();
	}

	public void doneDigging() {
		if (numOfDugHoles.get() > numOfSeededHoles.get()) {
			noAvailableDugHole.signal();
		}
		gardenLock.unlock();
	}

	public void startSeeding() {
		gardenLock.lock();
		while (numOfDugHoles.get() <= numOfSeededHoles.get()) {
			try {
				noAvailableDugHole.await();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}finally{
			}
		}
		this.plantSeed();
	}

	public void doneSeeding() {
		if (numOfSeededHoles.get() - numOfFilledHoles.get() > 0) {
			noAvailableSeededHole.signal();
		}
		gardenLock.unlock();
	}

	public void startFilling() {
		gardenLock.lock();
		while (numOfSeededHoles.get() - numOfFilledHoles.get() <= 0) {
			try {
				noAvailableSeededHole.await();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}finally{
				
			}
		}
		this.Fill();
	}

	public void doneFilling() {
		if (numOfDugHoles.get() - numOfFilledHoles.get() == 0) {
			maryIsFar.signal();
		}
		gardenLock.unlock();
	}

	public  int dig() {
		return numOfDugHoles.incrementAndGet();
	}

	public int plantSeed() {
		return numOfSeededHoles.incrementAndGet();
	}

	public int Fill() {
		return numOfFilledHoles.incrementAndGet();
	}

	public int totalHolesDugByNewton() {
		return numOfDugHoles.get();
	}

	public int totalHolesSeededByBenjamin() {
		return numOfSeededHoles.get();
	}

	public int totalHolesFilledByMary() {
		return numOfFilledHoles.get();
	}
	public void printGardenState(){
		System.out.println("Number of holes dug are " + numOfDugHoles);
		System.out.println("Number of holes Seeded are " + numOfSeededHoles);
		System.out.println("Number of holes Filled are " + numOfFilledHoles);
	}
	public static void main(String args[]) {
		Garden garden = new Garden(10);
		Thread w1 = new Thread(new Newton(garden));
		Thread w2 = new Thread(new Benjamin(garden));
		Thread w3 = new Thread(new Mary(garden));
		w2.start();
		w1.start();
		w3.start();
		garden.printGardenState();
	}
}

class Newton implements Runnable {
	Garden g;

	Newton(Garden g) {
		this.g = g;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				g.startDigging();

			} finally {
				System.out.println(" Newton Digging");
				g.printGardenState();
				g.doneDigging();
			}
		}
	}

}

class Benjamin implements Runnable {
	Garden g;

	Benjamin(Garden g) {
		this.g = g;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				g.startSeeding();
			} finally {
				System.out.println(" Benjamin Seeding");
				g.printGardenState();
				g.doneSeeding();
			}
		}
	}

}

class Mary implements Runnable {
	Garden g;

	Mary(Garden g) {
		this.g = g;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				g.startFilling();

			} finally {
				System.out.println("Mary is filling ");
				g.printGardenState();
				g.doneFilling();
			}
		}
	}
}
