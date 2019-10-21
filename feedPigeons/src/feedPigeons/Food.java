package feedPigeons;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Food extends GraphicEntity{
	//Constantes
	private static final Image IMG = new ImageIcon("res/food.png").getImage();
	private static final int WIDTH = 10, HEIGHT = 10;
	//Variables
	private float freshness = 0;//0 = fresh to 1 = rotten
	private boolean isRotten = false;
	
	public Food(int x, int y) {
		super(x, y, WIDTH, HEIGHT);
	}
	
	public float getFreshness() {
		return freshness;
	}
	
	/**
	 * Affiche la nourriture
	 */
	@Override
	public void render(Graphics g) {
		g.drawImage(IMG, x, y, null);		
	}
	
	/**
	 * Vieillit la nourriture
	 * @return true si la nourriture a pourri, false sinon
	 */
	public boolean age() {
		freshness += 0.004;
		
		if(freshness >= 1) {
			setRotten(true);
			return true;
		}
		return false;
	}

	public boolean isRotten() {
		return isRotten;
	}

	public void setRotten(boolean isRotten) {
		this.isRotten = isRotten;
	}
}
