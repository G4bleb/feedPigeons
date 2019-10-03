package feedPigeons;

public class Food extends GraphicEntity{
	private float freshness = 0;//0 = fresh to 1 = rotten
	
	public Food(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public float getFreshness() {
		return freshness;
	}
}
