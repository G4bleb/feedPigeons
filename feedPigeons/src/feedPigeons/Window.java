package feedPigeons;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Window extends MouseAdapter {

	//private static final long serialVersionUID = 224995615333633900L;
	private Game game;

	public Window(int width, int height, String title, Game game) {
		
		this.game = game;
		
		JFrame frame = new JFrame(title);
		
		frame.setPreferredSize(new Dimension(width, height));
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		
		try {
			frame.setIconImage(ImageIO.read(new File("res/pigeon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(game);
		frame.setVisible(true);
		
		game.start();
	}
	
	public void mousePressed(MouseEvent e) {
		System.out.println("Mouse pressed at position x=" + e.getX() + " y=" + e.getY());
		
		game.getWorld().addFood(e.getX(), e.getY(), 16, 16);
	}
	
	public void mouseReleased(MouseEvent e) {
		
	}

}
