import java.awt.*;

public class Player {

	public Player(){}
	
	public void drawHero(Graphics g, int Xloc, int Yloc, boolean attacking, String status)
	{
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		
		g.setColor(Color.white);
		if(status.equals("boost"))
			g.setColor(new Color(200, 0, 150));
		if(status.equals("defend"))
			g.setColor(new Color(200,200,200));
		
		g.fillOval(Xloc - 10,  Yloc - 10, 20, 20);
		double slope = 0, handSlope = 0;
		slope = Math.atan((b.getY() - Yloc)/(b.getX() - Xloc));
		handSlope = Math.atan(-(b.getX() - Xloc)/(b.getY() - Yloc));
		
		if(b.getX()>Xloc) //mouse on right side
		{
			g.fillOval((int)(Math.cos(slope)*20) + (int)(Math.cos(handSlope)*10) + Xloc-2, (int)(Math.sin(slope)*20) + (int)(Math.sin(handSlope)*10) + Yloc-2, 5, 5);
			if(attacking)
				g.fillOval((int)(Math.cos(slope)*25) + Xloc-2, (int)(Math.sin(slope)*25) + Yloc-2, 5, 5);
			else
				g.fillOval((int)(Math.cos(slope)*20) - (int)(Math.cos(handSlope)*10) + Xloc-2, (int)(Math.sin(slope)*20) - (int)(Math.sin(handSlope)*10) + Yloc-2, 5, 5);
		}
		else if(b.getX() < Xloc) //mouse on left side
		{
			g.fillOval((int)(-1*Math.cos(slope)*20) + (int)(Math.cos(handSlope)*10) + Xloc-2, (int)(-1*Math.sin(slope)*20) + (int)(Math.sin(handSlope)*10) + Yloc-2, 5, 5);
			if(attacking)
				g.fillOval((int)(-1*Math.cos(slope)*25) + Xloc-2, (int)(-1*Math.sin(slope)*25) + Yloc-2, 5, 5);
			else
				g.fillOval((int)(-1*Math.cos(slope)*20) - (int)(Math.cos(handSlope)*10) + Xloc-2, (int)(-1*Math.sin(slope)*20) - (int)(Math.sin(handSlope)*10) + Yloc-2, 5, 5);
		}
		else //mouse = here
		{
			if(b.getY()<Yloc)
			{
				g.fillOval(Xloc-12, Yloc-22, 5, 5);
				g.fillOval(Xloc+8, Yloc-22, 5, 5);
			}
			else
			{
				g.fillOval(Xloc-12, Yloc+18, 5, 5);
				g.fillOval(Xloc+8, Yloc+18, 5, 5);
			}
		}
	}
	
	public double drawBeam(Graphics g, int Xloc, int Yloc, Power power)
	{
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		
		g.setColor(power.color);
		
		double slope = Math.atan((b.getY() - Yloc)/(b.getX() - Xloc));
		if(b.getX() > Xloc)
			g.drawLine(Xloc, Yloc, Xloc + (int)(Math.cos(slope) * (power.range*100)), Yloc + (int)(Math.sin(slope) * (power.range*100)));
		else if(b.getX() > Xloc)
			g.drawLine(Xloc, Yloc, Xloc + (int)(-1*Math.cos(slope) * (power.range*100)), Yloc + (int)(-1*Math.sin(slope)) * (power.range*100));
		else
			if(b.getY() > Yloc)
				g.drawLine(Xloc, Yloc, Xloc, Yloc + power.range*100);
			else
				g.drawLine(Xloc, Yloc, Xloc, Yloc - power.range*100);
		return slope;
	}
	
	public void drawArea(Graphics g, int Xloc, int Yloc, Power power)
	{
		g.setColor(power.color);
		g.drawOval(Xloc - (power.range*50), Yloc - (power.range*50), power.range*100, power.range*100);
	}
	
	public void drawMelee(Graphics g, int Xloc, int Yloc, Power power)
	{
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		g.setColor(power.color);
		
		double slope = Math.atan((b.getY() - Yloc)/(b.getX()- Xloc));
		
		if(b.getX()>Xloc)
			g.fillOval((int)(Math.cos(slope)*25) + Xloc-2, (int)(Math.sin(slope)*25) + Yloc-2, 5, 5);
		else
			g.fillOval((int)(-1*Math.cos(slope)*25) + Xloc-2, (int)(-1*Math.sin(slope)*25) + Yloc-2, 5, 5);
	}
	
	public void drawBars(Graphics g, int hp, int mp)
	{
		g.setColor(Color.white);
		g.fillRect(20, 50, 200, 25);
		g.fillRect(240, 50, 200, 25);
		g.setColor(Color.RED);
		g.fillRect(20, 50, hp*2, 25);
		g.setColor(Color.BLUE);
		g.fillRect(240, 50, mp*2, 25);
	}

	public void drawTest (Graphics g, String test)
	{
		g.setColor(Color.WHITE);
		g.drawString(test,  450,  65);

	}
}
