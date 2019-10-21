package feedPigeons;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;

//Chaque pigeon est contrôlé par un Thread
public class Pigeon extends GraphicEntity implements Runnable {
	// Constantes
	private static final Image IMG = new ImageIcon("res/pigeon.png").getImage();
	private static final Image IMGSCARED = new ImageIcon("res/scaredpigeon.png").getImage();
	private static final int WIDTH = 21, HEIGHT = 21;

	private static final int SAFERANGE = 70;// Distance maximale de la zone de fuite
	private static final float DEFAULTFEARTHRESHOLD = 0.995f;// Seuil de peur par défaut
	private static final Random RAND = new Random();

	//Variables
	private boolean alive = true;
	private boolean scared = false;
	private float fearThreshold;// Seuil de peur
	
	private int foodObjectiveID = 0;// Indice de la nourriture ciblée dans foodHere
	private boolean checkFood = false;// Pour actualisation de la nourriture ciblée quand c'est nécessaire

	//Références
	private ArrayList<Food> foodHere;// Tableau des nourritures présentes
	private Semaphore foodLock;// Sémaphore pour opérations sur le tableau de nourriture
	private GraphicEntity objective = null;// Objectif actuel
	private Food foodObjective = null;// Nourriture ciblée
	
	

	public Pigeon(int x, int y, ArrayList<Food> foodToWatch, Semaphore foodLock) {
		super(x, y, WIDTH, HEIGHT);
		this.fearThreshold = DEFAULTFEARTHRESHOLD;
		this.foodHere = foodToWatch;
		this.foodLock = foodLock;
	}

	public Pigeon(int x, int y, ArrayList<Food> foodToWatch, Semaphore foodLock, float fearThreshold) {
		super(x, y, WIDTH, HEIGHT);
		this.fearThreshold = fearThreshold;
		this.foodHere = foodToWatch;
		this.foodLock = foodLock;
	}

	/**
	 * Zone de fuite que créera le pigeon lorsqu'il prendra peur
	 *
	 */
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
	 * Cible la nourriture la plus intéressante
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

	}

	/**
	 * Rend effrayé en fonction du seuil d'éffraiement
	 * 
	 * @param fear la peur subie
	 */
	private void getMaybeScared(float fear) {
		if (fear > fearThreshold) {
			log("J'ai pris peur");
			scared = true;
			objective = new SafeSpace(this.x + RAND.nextInt(SAFERANGE + SAFERANGE) - SAFERANGE,
					this.y + RAND.nextInt(SAFERANGE + SAFERANGE) - SAFERANGE);
			// Le point de fuite se situe dans un carré de côté SAFERANGE autour du pigeon
		}
	}

	/**
	 * Avance vers l'objectif
	 * 
	 * @return S'il est arrivé ou pas
	 */
	private void stepToObjective() {

		if (objective == null) {
			//La nourriture ciblée a disparue
			checkFood = true;
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
			//Arrivée à l'objectif
			if (objective instanceof Food) {
				//C'est de la nourriture
				log("Trying to eat food " + foodObjectiveID);
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

	/**
	 * Mange la nourriture dont l'index est passé en paramètre
	 * @param foodID l'index de la nourriture dans foodHere
	 * @return true si la nourriture a été mangée avec succès
	 */
	private synchronized boolean eat(int foodID) {
		try {
			foodLock.acquire();
			
			if (foodID < foodHere.size()) {
				foodHere.remove(foodID);
				foodObjective = null;
				checkFood = true;
				log("Ate food " + foodObjectiveID);
				foodLock.release();
				return true;
			}
			
			foodLock.release();
		} catch (InterruptedException e) {
		}
		return false;

	}

	/**
	 * Boucle principale du Pigeon
	 * Chaque 60eme de seconde, peut :
	 *  - Considérer la meilleure nourriture
	 *  - Prendre peur
	 *  - Marcher vers son objectif
	 */
	@Override
	public void run() {
		int lastKnownFoodArraySize = 0;
		long frameStartTime;

		while (alive) {
			frameStartTime = System.nanoTime();
			
			if (lastKnownFoodArraySize != foodHere.size() || checkFood) {
				// Si de la nourriture vient d'arriver ou de disparaître
				findBestFood();
				lastKnownFoodArraySize = foodHere.size();
				checkFood = false;
			}
			
			getMaybeScared(RAND.nextFloat());
			
			if(!scared) {
				objective = foodObjective;
			}
			
			stepToObjective();

			//Attendre la fin d'un 60eme de seconde
			while (System.nanoTime() - frameStartTime <= 16666666l);

		}

	}

	/**
	 * Affiche le pigeon
	 */
	@Override
	public void render(Graphics g) {
		if (scared) {
			g.drawImage(IMGSCARED, x, y, null);
		} else {
			g.drawImage(IMG, x, y, null);
		}
	}

	/**
	 * Log un message dans la console, en ajoutant l'id du pigeon
	 * @param msg le message à log
	 */
	private void log(String msg) {
		System.out.println("Pigeon " + Thread.currentThread().getId() + " : " + msg);
	}
}
