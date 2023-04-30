import java.awt.*;

public class Boss 
{
	int bossHealth, bossSpd, bossDmg, BossMaxHealth;
	double bossLocX = 640, bossLocY = 480;
	int attack = 150;
	int moveCount = 0;
	double tempSlope = 0;
	String bossName, bossEffect, bossStatus = ".none", world;
	Color bossColor;
	boolean fighting = false;
	double[] xPoints = new double[4];
	double[] yPoints = new double[4];
	
	public void drawBoss(Graphics g, int heroX, int heroY, Boss boss)
	{
		double slope = Math.atan((double)(heroY - boss.bossLocY)/(heroX - boss.bossLocX));
		double wingSlope = Math.atan((double)-(heroX - boss.bossLocX)/(heroY - boss.bossLocY));

		xPoints[0] = boss.bossLocX;
		yPoints[0] = boss.bossLocY;
		
		xPoints[1] = boss.bossLocX + (Math.cos(slope)*40) + (Math.cos(wingSlope)*25);
		yPoints[1] = boss.bossLocY + (Math.sin(slope)*40) + (Math.sin(wingSlope)*25);
		
		xPoints[2] = boss.bossLocX + Math.cos(slope)*25;
		yPoints[2] = boss.bossLocY + Math.sin(slope)*25;
		
		xPoints[3] = boss.bossLocX + (Math.cos(slope)*40 - Math.cos(wingSlope)*25);
		yPoints[3] = boss.bossLocY + (Math.sin(slope)*40 - Math.sin(wingSlope)*25);


		int[] drawPointsX = new int[4];
		int[] drawPointsY = new int[4];
		
		for(int i = 0; i < 4; i++)
		{
			if(boss.bossLocX < heroX)
			{
				xPoints[i] *= -1;
				xPoints[i] += 2*boss.bossLocX;
				yPoints[i] *= -1;
				yPoints[i] += 2*boss.bossLocY;
			}
			drawPointsX[i] = (int) xPoints[i];
			drawPointsY[i] = (int) yPoints[i];
		}
		
		g.setColor(boss.bossColor);
		if(boss.bossName.equals("Clock"))
			g.setColor(Color.white);
		g.fillPolygon(drawPointsX, drawPointsY, 4);
	}
	
	public void drawArea(Graphics g, double Xloc, double Yloc, Boss boss)
	{

		g.setColor(boss.bossColor);
		if(boss.bossName.equals("Clock"))
			g.setColor(Color.white);
		g.drawOval((int)Xloc - 75, (int)Yloc - 75, 150, 150);
	}
	
	public void drawBars(Graphics g, Boss boss)
	{
		g.setColor(Color.white);
		g.fillRect(800, 50, boss.BossMaxHealth*2, 25);
		g.setColor(Color.RED);
		g.fillRect(800, 50, boss.bossHealth*2, 25);
	}
	
}
