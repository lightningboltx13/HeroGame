import java.awt.Color;
import java.awt.Graphics;

public interface Enemy {

	public void initialize(String name, String world, int health, int damage, int speed, String effect, Color color);
	//public void draw(Graphics g, int heroX, int heroY, Enemy enemy);
		

}
