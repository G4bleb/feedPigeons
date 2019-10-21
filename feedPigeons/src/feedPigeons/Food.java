package feedPigeons;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Food extends GraphicEntity{
	private static final Image freshFood = new ImageIcon("res/food.png").getImage();
	private float freshness = 0;//0 = fresh to 1 = rotten
	private boolean isRotten = false;
	
	public Food(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public float getFreshness() {
		return freshness;
	}
	
	@Override
	public void render(Graphics g) {
		freshness += 0.004;
		
		if(freshness < 1) {
			g.drawImage(freshFood, x, y, null);		
			setRotten(true);
		}
	}

	public boolean isRotten() {
		return isRotten;
	}

	public void setRotten(boolean isRotten) {
		this.isRotten = isRotten;
	}
}
