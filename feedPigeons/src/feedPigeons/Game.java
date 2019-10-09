package feedPigeons;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -1750302489705385150L;
	
	private static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
	private Thread thread;
	private boolean running = false;
	
	//Partie jeu
	private World world;
	
	public Game() {
		new Window(WIDTH, HEIGHT, "Pigeon Game", this);
	}

	public synchronized void start() {
		world = new World();
		thread = new Thread(this);
		thread.start();
		running = true;	
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {

		long timer = System.currentTimeMillis();
		int frames = 0;
		long frameStartTime;
		while(running) {
			frameStartTime = System.nanoTime();
			
			
			tick();
			if(running) {
				render();
			}
			//long timeElapsed = System.nanoTime()-t1;
			
			while(System.nanoTime()-frameStartTime <= 16666666l);
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: "+ frames);
				frames = 0;
				
			}
		}
		
		stop();
	}
	
	private void tick() {
		
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		world.renderWorld(g);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new Game();
	}
}
