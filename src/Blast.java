import java.awt.Color;
import java.awt.Graphics;

public class Blast {
	//TODO: make values private and create getters/setters
	//TODO: Do more "blast" logic in here
	double slope;
	String direction;
	int damage;
	int range;
	double locationX;
	double locationY;
	double srtX;
	double srtY;
	Color color;
	String effect;
	boolean hit = false;
	
	public Blast(double angle, String dir, int dmg, int rng, double x, double y, Color clr, String eff)
	{
		slope = angle;
		direction = dir;
		damage = dmg;
		range = rng;
		locationX = x;
		locationY = y;
		srtX = x;
		srtY = y;
		color = clr;
		effect = eff;
	}
	
	public void drawBlast(Graphics g, Blast blast)
	{
		g.setColor(blast.color);
		g.fillOval((int)blast.locationX - 5, (int)blast.locationY - 5, 10, 10);
	}

	@Override
	public String toString() {
		return "Blast [slope=" + slope + ", direction=" + direction + ", damage=" + damage + ", range=" + range
				+ ", locationX=" + locationX + ", locationY=" + locationY + ", srtX=" + srtX + ", srtY=" + srtY
				+ ", color=" + color + ", effect=" + effect + ", hit=" + hit + "]";
	}
	
	
}
