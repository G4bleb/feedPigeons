package feedPigeons;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = -1750302489705385150L;
	
	public static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
	private Thread thread;
	private boolean running = false;
	private Window window;
	
	//Partie jeu
	private World world;
	private static final int PIGEONSNUMBER = 4;
	
	public Game() {
		
		window = new Window(WIDTH, HEIGHT, "Pigeon Game", this);
		
		this.addMouseListener(window); //On ajoute le listener qui traite les clics
	}

	public synchronized void start() {
		//Lancement du jeu, on met en place notre monde et on lance le thread principal
		setWorld(new World(WIDTH, HEIGHT, PIGEONSNUMBER));
		thread = new Thread(this);
		thread.start();
		running = true;		
	}
	
	public synchronized void stop() {
		//Arret du jeu, on ferme le thread et la boucle principale
		try {
			running = false;
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long frameStartTime;
		
		// Boucle principale faisant tourner le jeu
		while(running) {
			frameStartTime = System.nanoTime();
			
			tick();
			if(running) {
				render();
			}
			
			//Attente de la fin d'un 60ème de seconde
			while(System.nanoTime()-frameStartTime <= 16666666l);
		}
		
		stop();
	}
	
	
	private void tick() {
		world.ageFood(); //On viellit la nourriture
	}
	
	private void render() {
		// A chaque tour, on render notre monde
		BufferStrategy bs = this.getBufferStrategy();
		
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		getWorld().renderWorld(g);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new Game();
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
