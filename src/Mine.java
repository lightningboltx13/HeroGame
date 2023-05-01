import java.awt.Color;
import java.awt.Graphics;

public class Mine {

	int locX, locY;
	boolean exploded = false;
	//TODO: make private and have getters/setters to retrieve data
	//TODO: do more "mine" logic in here instead of battle map
	
	public Mine(int x, int y)
	{
		locX = x;
		locY = y;
	}
	
	public void drawMine(Graphics g, Mine mine)
	{
		g.setColor(new Color(255,200,0));
		g.fillOval(mine.locX - 5, mine.locY - 5, 10, 10);
		g.setColor(Color.red);
		g.fillOval(mine.locX - 2, mine.locY - 2, 4, 4);
	}
	
}
