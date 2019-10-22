package feedPigeons;

import java.awt.Graphics;
import java.awt.Image;

public class Food extends GraphicEntity {
	// Constantes
	private static final Image IMG = createImageIcon("res/food.png").getImage();
	public static final int WIDTH = 16, HEIGHT = 16;
	// Variables
	private float freshness = 0;// 0 = fresh to 1 = rotten
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
	 * 
	 * @return true si la nourriture a pourri, false sinon
	 */
	public boolean age() {
		freshness += 0.004;

		if (freshness >= 1) {
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
