package feedPigeons;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class World {

	private ArrayList<Food> foodArray = new ArrayList<Food>();
	private Semaphore foodLock = new Semaphore(1);
	
	// https://stackoverflow.com/questions/35571395/how-to-access-running-threads-inside-threadpoolexecutor
	static class MonitorPigeon implements Runnable {

		static final List<Pigeon> activePigeons = Collections.synchronizedList(new ArrayList<>());

		private final Pigeon pigeon;

		public MonitorPigeon(Pigeon pigeon) {
			this.pigeon = pigeon;
		}

		@Override
		public void run() {
			activePigeons.add(pigeon);
			pigeon.run();
			activePigeons.remove(pigeon);
		}
	}


	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 20, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(300));

	public World(int width, int height, int initialPigeonsNumber) {
		for (int i = 0; i < initialPigeonsNumber; i++) {
			addPigeon((int) (Math.random() * width), (int) (Math.random() * height));
		}
	}

	public void addPigeon(int x, int y) {
		executor.execute(new MonitorPigeon(new Pigeon(x, y, foodArray, foodLock)));
	}

	public void addFood(int x, int y, int width, int height) {
		try {
			foodLock.acquire();
		} catch (InterruptedException e) {
		}
		foodArray.add(new Food(x, y));
		foodLock.release();
	}

	public void renderWorld(Graphics g) {
		synchronized (MonitorPigeon.activePigeons) {
			for (Pigeon p : MonitorPigeon.activePigeons) {
				p.render(g);
			}
		}
		try {
			foodLock.acquire();
			for (Food f : foodArray) {
				f.render(g);
			}
			foodLock.release();
		} catch (InterruptedException | ConcurrentModificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
