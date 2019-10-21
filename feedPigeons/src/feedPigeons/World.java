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

public class World {//TODO
	
	//https://stackoverflow.com/questions/35571395/how-to-access-running-threads-inside-threadpoolexecutor
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
	
	private ArrayList<Food> foodArray = new ArrayList<Food>();
	private Semaphore foodLock = new Semaphore(1);
	
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(300));

	public World() {
		//for (int i = 0; i < 2; i++) {
			//addPigeon((int) ((Math.random()+1)*200), (int) ((Math.random()+1)*200), 10, 10);
		//}
		addPigeon(600, 400, 21, 21);
		addPigeon(100, 100, 21, 21);
		
		
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	for (int i = 0; i < 10; i++) {
		        			foodArray.add(new Food((int) ((Math.random()+1)*200), (int) ((Math.random()+1)*200), 16, 16));
		        			try {
		        				Thread.sleep(200);
		        			} catch (InterruptedException e) {
		        				// TODO Auto-generated catch block
		        				e.printStackTrace();
		        			}
		        		}
		            }
		        }, 
		        500 
		);
	}
	
    public void addPigeon(int x, int y, int width, int height) {
        executor.execute(new MonitorPigeon(new Pigeon(x, y, width, height, foodArray, foodLock)));
    }
    
    public void addFood(int x, int y, int width, int height) {
    	try {
			foodLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	foodArray.add(new Food(x, y, width, height));
    	foodLock.release();
    }

    public void renderWorld(Graphics g) {
        synchronized (MonitorPigeon.activePigeons) {
            for (Pigeon p : MonitorPigeon.activePigeons) {
            	p.render(g);
            }
        }
        try {
        	Food f;
			foodLock.acquire();
			for(int i=0 ; i < foodArray.size() ; i++) {
				f = foodArray.get(i);
				f.render(g);
				if(f.isRotten()) {
					foodArray.remove(i);
				}
			}
			
	        foodLock.release();
		} catch (InterruptedException | ConcurrentModificationException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
   }
    
    

}
