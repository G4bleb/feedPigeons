package feedPigeons;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class World {//TODO
	
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
	
	//private Pigeon[] pigeons;
	private ArrayList<Food> foodArray = new ArrayList<Food>();

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(300));

	public World() {
		//for (int i = 0; i < 2; i++) {
			//addPigeon((int) ((Math.random()+1)*200), (int) ((Math.random()+1)*200), 10, 10);
		//}
		addPigeon(600, 400, 64, 64);
		addPigeon(100, 100, 64, 64);
		
		
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
        executor.execute(new MonitorPigeon(new Pigeon(x, y, width, height, foodArray)));
    }

    public void renderWorld(Graphics g) {
        synchronized (MonitorPigeon.activePigeons) {
            for (Pigeon p : MonitorPigeon.activePigeons) {
            	p.render(g);
            }
            try {
            	for(Food f : foodArray) {
                	f.render(g);
                }
            }catch(ConcurrentModificationException e) {
            	System.err.println("ERROR TIME : ConcurrentModificationException :(");
            }
            
        }
        
   }
    
    

}
