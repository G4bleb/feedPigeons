package feedPigeons;

import java.util.ArrayList;

//Chaque pigeon est contrôlé par un Thread
public class Pigeon extends GraphicEntity implements Runnable {

	private boolean asleep = true;
	private boolean alive = true;
	private boolean scared = false;
	private double fearThreshold;
	private ArrayList<Food> foodHere;
	private GraphicEntity objective = null;
	private Food foodObjective = null;
	private int foodObjectiveID = 0;

	public Pigeon(int x, int y, int width, int height, ArrayList<Food> foodToWatch) {
		super(x, y, width, height);
		this.fearThreshold = 1d;// Pour le moment, ne peut pas prendre peur
		this.foodHere = foodToWatch;
	}

	public Pigeon(int x, int y, int width, int height, ArrayList<Food> foodToWatch, double fearThreshold) {
		super(x, y, width, height);
		this.fearThreshold = fearThreshold;
		this.foodHere = foodToWatch;
	}

	private class SafeSpace extends GraphicEntity {
		public SafeSpace(int x, int y, int width, int height) {
			super(x, y, width, height);
		}
	}

	/**
	 * Identifie l'envie de la nourriture passée en paramètre
	 * 
	 * @param food
	 */
	private void considerNewFood() {
		int newFoodID = foodHere.size() - 1;
		Food newFood = foodHere.get(newFoodID);
		if (foodObjective != null) {
			if (this.distanceTo(newFood) > this.distanceTo(foodObjective)) {
				// Si la nouvelle nourriture est plus éloignée
				return;
			}
		}
		foodObjective = newFood;
		foodObjectiveID = newFoodID;
	}

	/**
	 * Rend effrayé en fonction du seuil d'éffraiement
	 * 
	 * @param fear la peur subie
	 */
	private void getMaybeScared(double fear) {
		if (fear > fearThreshold) {
			scared = true;
		}
		// TODO trouve un objectif aléatoire (new ?)
	}

	/**
	 * Avance vers l'objectif
	 * 
	 * @return S'il est arrivé ou pas
	 */
	private void stepToObjective() {
		if (objective == null) {
			return;
		}
		// TODO Avance vers l'objectif
		// https://stackoverflow.com/questions/28444491/how-do-i-move-one-point-towards-another-point-in-c/28444694

		// https://gamedev.stackexchange.com/questions/23447/moving-from-ax-y-to-bx1-y1-with-constant-speed
		/*
		 * int speed_per_tick = 1; // constant speed you want the object to move at int
		 * delta_x = objective.x - this.x; int delta_y = objective.y - this.y; double
		 * goal_dist = Math.sqrt((delta_x * delta_x) + (delta_y * delta_y)); if
		 * (this.distanceTo(objective) > speed_per_tick) { double ratio = speed_per_tick
		 * / goal_dist; int x_move = (int) (ratio * delta_x); int y_move = (int) (ratio
		 * * delta_y); this.x = x_move + this.x; this.y = y_move + this.y; } else {
		 * this.x = objective.x; this.y = objective.y; }
		 */

		int speed_per_tick = 1; // constant speed you want the object to move at
		int delta_x = objective.x - this.x;
		int delta_y = objective.y - this.y;
		double goal_dist = Math.sqrt((delta_x * delta_x) + (delta_y * delta_y));
		double ratio = speed_per_tick / goal_dist;
		int x_move = (int) (ratio * delta_x);
		int y_move = (int) (ratio * delta_y);
		this.x = x_move + this.x;
		this.y = y_move + this.y;

		if (collidesWith(objective)) {
			if (objective instanceof Food) {// TODO Vérifier que cette condition fonctionne
				foodHere.remove(foodObjectiveID);
				objective = null;
			} else {
				// C'est un SafeSpace
				objective = foodObjective;
			}
		}
	}

	public void run() {
		int lastKnownFoodArraySize = 0;
		while (alive) {

			if (lastKnownFoodArraySize != foodHere.size()) {// Si de la nourriture vient d'arriver ou de disparaître
				considerNewFood();
				lastKnownFoodArraySize = foodHere.size();
			}
			// Peut prendre peur aléatoirement
			getMaybeScared(Math.random());

			stepToObjective();
		}

	}
}
