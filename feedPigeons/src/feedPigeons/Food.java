package feedPigeons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Food extends GraphicEntity{
	private static final Image img = new ImageIcon("res/food.png").getImage();
	private float freshness = 0;//0 = fresh to 1 = rotten
	
	public Food(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public float getFreshness() {
		return freshness;
	}
	
	@Override
	public void render(Graphics g) {
		//g.setColor(Color.PINK);
		//g.fillRect(x, y, width, height);
		g.drawImage(img, x, y, null);
	}
}
