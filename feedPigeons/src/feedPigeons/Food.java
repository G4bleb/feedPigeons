package feedPigeons;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Food extends GraphicEntity{
	private static final Image IMG = new ImageIcon("res/food.png").getImage();
	private static final int WIDTH = 10, HEIGHT = 10;
	private float freshness = 0;//0 = fresh to 1 = rotten
	
	public Food(int x, int y) {
		super(x, y, WIDTH, HEIGHT);
	}
	
	public float getFreshness() {
		return freshness;
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(IMG, x, y, null);
		freshness += 0.004;
	}
}
