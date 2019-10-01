package feedPigeons;

//Chaque pigeon est contrôlé par un Thread
public class Pigeon implements Runnable{
	
	private int xPos, yPos;
	private boolean asleep;
	private boolean scared;
	private Food [] foodHere;
	
	private boolean isfoodRelevant(Food food) {
		return false;
	}
	
	public void run() {
	//S'il dort (=n'a pas d'objectifs), ne bouge pas
	//S'il a peur, trouve un objectif aléatoire
	//Si de la nourriture vient d'apparaître ou disparaître, isRelevant pour voir si c'est un nouvel objectif
	}
}
