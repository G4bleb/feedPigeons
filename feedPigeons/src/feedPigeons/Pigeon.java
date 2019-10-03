package feedPigeons;

import java.util.ArrayList;

//Chaque pigeon est contrôlé par un Thread
public class Pigeon extends GraphicEntity implements Runnable{
	
	
	private boolean asleep = true;
	private boolean alive = true;
	private boolean scared = false;
	private double fearThreshold;
	private GraphicEntity objective = null;
	private Food foodObjective = null;
	private int foodObjectiveID = 0;
	private ArrayList<Food> foodHere;
	
	
	public Pigeon(int x, int y, int width, int height, ArrayList<Food> foodToWatch) {
		super(x, y, width, height);
		this.fearThreshold = 1d;//Pour le moment, ne peut pas prendre peur
		this.foodHere = foodToWatch;
	}
	
	public Pigeon(int x, int y, int width, int height, ArrayList<Food> foodToWatch, double fearThreshold) {
		super(x, y, width, height);
		this.fearThreshold = fearThreshold;
		this.foodHere = foodToWatch;
	}
	
	/**
	 * Identifie l'envie de la nourriture passée en paramètre
	 * @param food
	 */
	private void considerNewFood() {
		int newFoodID = foodHere.size()-1;
		Food newFood = foodHere.get(newFoodID);
		//TODO Vérifier si la nourriture devient le nouvel objectif
		foodObjective = newFood;
		foodObjectiveID = newFoodID;
	}
	
	/**
	 * Rend effrayé en fonction du seuil d'éffraiement
	 * @param fear la peur subie
	 */
	private void getMaybeScared(double fear) {
		if(fear > fearThreshold) {
			scared = true;
		}
		//TODO trouve un objectif aléatoire (new ?)
	}
	
	/**
	 * Avance vers l'objectif
	 * @return S'il est arrivé ou pas
	 */
	private void stepToObjective() {
		if (objective == null) {
			return;
		}
		//TODO Avance vers l'objectif
		if(collidesWith(objective)) {
			//Si l'objectif est de la nourriture, la manger 
			//foodHere.remove(foodObjective);
			objective = null;
		}
	}


	public void run() {
		int lastKnownFoodArraySize = 0;
		while(alive){
			
			if(lastKnownFoodArraySize != foodHere.size()) {//Si de la nourriture vient d'arriver ou de disparaître
				considerNewFood();			
				lastKnownFoodArraySize = foodHere.size();
			}
			//Peut prendre peur aléatoirement
			getMaybeScared(Math.random());
			
			stepToObjective();
		}
	
	}
}
