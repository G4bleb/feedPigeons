package feedPigeons;

import java.awt.Graphics;

abstract class GraphicEntity {
	
	protected int width, height;
	protected int x, y;

	public GraphicEntity(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public class Point{
		public int x, y;
		Point(int x, int y){
			this.x = x;
			this.y = y;
		}
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

	/**
	 * Calcule l'épicentre d'une entité
	 * 
	 * @return un tableau de deux entiers contenant les coordonées de l'épicentre
	 */
	public Point epicenter() {
		return new Point(x + width / 2, y + height / 2);
	}

	/**
	 * Teste si l'entité est en collision avec une autre
	 * @param entity l'entité avec qui vérifier la collision
	 * @return true s'il y a collision
	 */
	protected boolean collidesWith(GraphicEntity entity) {
		if(this.x + this.width < entity.x) 
			return false;
		if(this.x > entity.x + entity.width)
			return false;
		if(this.y + this.height < entity.y) 
			return false;
		if(this.y > entity.y + entity.height)
			return false;
		return true;
	}
	
	protected double distanceTo(GraphicEntity entity) {
		double term1 = Math.pow(entity.epicenter().x-this.epicenter().x, 2);
		double term2 = Math.pow(entity.epicenter().y-this.epicenter().y, 2);
		return Math.sqrt(term1+term2);
	}
	
	public abstract void render(Graphics g);

}
