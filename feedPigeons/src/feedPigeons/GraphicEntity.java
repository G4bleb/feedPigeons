package feedPigeons;

abstract class GraphicEntity{
	protected int width, height;
	protected int x, y;
	
	public GraphicEntity(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	protected boolean collidesWith(GraphicEntity entity) {
		//TODO
		return false;
	}
}
