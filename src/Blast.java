import java.awt.*;

public class Blast {

	double Slope;
	String direction;
	int Dmg, Range;
	double LocX, LocY;
	double srtX, srtY;
	Color color;
	String effect;
	boolean hit = false;
	
	public Blast(double angle, String dir, int dmg, int rng, double x, double y, Color clr, String eff)
	{
		Slope = angle;
		direction = dir;
		Dmg = dmg;
		Range = rng;
		LocX = x;
		LocY = y;
		srtX = x;
		srtY = y;
		color = clr;
		effect = eff;
	}
	
	public void drawBlast(Graphics g, Blast blast)
	{
		g.setColor(blast.color);
		g.fillOval((int)blast.LocX - 5, (int)blast.LocY - 5, 10, 10);
	}
}
