package feedPigeons;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;

//Chaque pigeon est contrôlé par un Thread
public class Pigeon extends GraphicEntity implements Runnable {
	private static final Image IMG = new ImageIcon("res/pigeon.png").getImage();
	private static final int WIDTH = 21, HEIGHT = 21;
	private static final Image IMGSCARED = new ImageIcon("res/scaredpigeon.png").getImage();
	private static final int SAFERANGE = 50;
	private static final float DEFAULTFEARTHRESHOLD = 0.995f;
	private static final Random RAND = new Random();

	private boolean alive = true;
	private boolean scared = false;

	private float fearThreshold;

	private ArrayList<Food> foodHere;
	private Semaphore foodLock;

	private GraphicEntity objective = null;
	private Food foodObjective = null;
	private int foodObjectiveID = 0;
	private boolean checkFood = false;

	public Pigeon(int x, int y, ArrayList<Food> foodToWatch, Semaphore foodLock) {
		super(x, y, WIDTH, HEIGHT);
		this.fearThreshold = DEFAULTFEARTHRESHOLD;
		this.foodHere = foodToWatch;
		this.foodLock = foodLock;
	}

	public Pigeon(int x, int y, ArrayList<Food> foodToWatch, Semaphore foodLock,
			float fearThreshold) {
		super(x, y, WIDTH, HEIGHT);
		this.fearThreshold = fearThreshold;
		this.foodHere = foodToWatch;
		this.foodLock = foodLock;
	}

	private class SafeSpace extends GraphicEntity {
		public SafeSpace(int x, int y) {
			super(x, y, 1, 1);
		}

		@Override
		public void render(Graphics g) {
			// Invisible
		}
	}

	/**
	 * Identifie l'envie de la nourriture passée en paramètre
	 * 
	 * @param food
	 */
	private void findBestFood() {
		switch (foodHere.size()) {
		case 0:
			foodObjective = null;
			return;
		case 1:
			foodObjective = foodHere.get(0);
			foodObjectiveID = 0;
			return;
		default:
			foodObjective = foodHere.get(0);
			foodObjectiveID = 0;
			for (int foodID = 1; foodID < foodHere.size(); foodID++) {
				if (this.distanceTo(foodHere.get(foodID)) < this.distanceTo(foodObjective)) {
					foodObjective = foodHere.get(foodID);
					foodObjectiveID = foodID;
				}
			}
			break;
		}

		// log("Now I like food "+foodObjectiveID);
	}

	/**
	 * Rend effrayé en fonction du seuil d'éffraiement
	 * 
	 * @param fear la peur subie
	 */
	private void getMaybeScared(double fear) {
		if (fear > fearThreshold) {
			System.out.println("Am scared");
			scared = true;
			objective = new SafeSpace(this.x + RAND.nextInt(SAFERANGE + SAFERANGE) - SAFERANGE,
					this.y + RAND.nextInt(SAFERANGE + SAFERANGE) - SAFERANGE);
			//Le point de fuite se situe dans un carré de côté SAFERANGE autour du pigeon
		}

		if (!scared) {
			objective = foodObjective;
		}
	}

	/**
	 * Avance vers l'objectif
	 * 
	 * @return S'il est arrivé ou pas
	 */
	private void stepToObjective() {

		if (objective == null) {
			if (objective instanceof Food) {
				foodObjective = null;
				checkFood = true;
				log("oh no, the food " + foodObjectiveID + " i was aiming got eaten");
			}
			return;
		}

		int speed_per_tick = 2; // constant speed you want the object to move at
		int delta_x = objective.x - this.x;
		int delta_y = objective.y - this.y;
		double goal_dist = Math.sqrt((delta_x * delta_x) + (delta_y * delta_y));
		double ratio = speed_per_tick / goal_dist;
		int x_move = (int) (ratio * delta_x);
		int y_move = (int) (ratio * delta_y);
		this.x = x_move + this.x;
		this.y = y_move + this.y;

		if (collidesWith(objective)) {
			if (objective instanceof Food) {
				log("Let's try to eat food " + foodObjectiveID);
				eat(foodObjectiveID);
				foodObjective = null;
				objective = null;
			} else {
				// C'est un SafeSpace
				scared = false;
				objective = foodObjective;
			}
		}
	}

	private synchronized boolean eat(int foodID) {
		try {
			foodLock.acquire();

			if (foodHere.size() > 0) {
				foodHere.remove(foodID);
				foodObjective = null;
				checkFood = true;
				log("Yum ! Ate food " + foodObjectiveID);
				foodLock.release();
				return true;
			}
			foodLock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public void run() {
		int lastKnownFoodArraySize = 0;
		long frameStartTime;

		while (alive) {

			frameStartTime = System.nanoTime();
			if (lastKnownFoodArraySize != foodHere.size() || checkFood) {// Si de la nourriture vient d'arriver ou de
																			// disparaître

				findBestFood();
				lastKnownFoodArraySize = foodHere.size();
				checkFood = false;
			}
			getMaybeScared(RAND.nextFloat());
			stepToObjective();

			while (System.nanoTime() - frameStartTime <= 16666666l);

		}

	}

	@Override
	public void render(Graphics g) {
		if(scared) {
			g.drawImage(IMGSCARED, x, y, null);
		}else{
			g.drawImage(IMG, x, y, null);
		}
	}

	private void log(String msg) {
		System.out.println("Pigeon " + Thread.currentThread().getId() + " : " + msg);
	}
}
