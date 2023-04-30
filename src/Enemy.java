import java.awt.*;
import java.util.Random;

public class Enemy 
{
	int HP, Spd, Dmg;
	int locX, locY;
	double length;
	Color color;
	String effect, status = ".none";
	double slope, wingSlope;
	
	int[] xPoints = new int[3];
	int[] yPoints = new int[3];
	
	Random rand = new Random();
	
	public Enemy()
	{
		int temp = rand.nextInt(4);
		
		if(temp == 0)
		{
			HP = 15;
			Spd = 8;
			Dmg = 7;
			length = 2;
			effect = "5.slow";
			color = Color.blue;
		}
		if(temp == 1)
		{
			HP = 20;
			Spd = 5;
			Dmg = 10;
			length = 1;
			effect = "5.hurt";
			color = Color.red;
		}
		if(temp == 2)
		{
			HP = 30;
			Spd = 3;
			Dmg = 7;
			length = .5;
			effect = "5.stun";
			color = Color.yellow;
		}
		if(temp == 3)
		{
			HP = 15;
			Spd = 5;
			Dmg = 5;
			length = 0;
			effect = ".immunity";
			color = Color.green;
		}
		
		temp = rand.nextInt(4);
		
		if(temp == 0)
		{
			locX = 0;
			locY = rand.nextInt(960);
		}
		if(temp == 1)
		{
			locX = rand.nextInt(1280);
			locY = 0;
		}
		if(temp == 2)
		{
			locX = 1280;
			locY = rand.nextInt(960);
		}
		if(temp == 3)
		{
			locX = rand.nextInt(1280);
			locY = 960;
		}
	}
	
	public void enemyDraw(Graphics g, int heroX, int heroY, Enemy enemy)
	{
		xPoints[0] = enemy.locX;
		yPoints[0] = enemy.locY;
		
		if(enemy.locX == heroX)
		{
			if(enemy.length == 0)
			{
				xPoints[1] = (int)locX+7;
				xPoints[2] = (int)locX-7;
				if(enemy.locY > heroY)
				{
					yPoints[1] = (int)locY+7;
					yPoints[2] = (int)locY+7;
				}
				else
				{
					yPoints[1] = (int)locY-7;
					yPoints[2] = (int)locY-7;
				}
			}
			else
			{
				xPoints[1] = (int)((1/enemy.length)*15)+locX;
				xPoints[2] = locX - (int)((1/enemy.length)*15);
				if(enemy.locY > heroY)
				{
					yPoints[1] = (int)(enemy.length*15)+locY;
					yPoints[2] = (int)(enemy.length*15)+locY;
				}
				else
				{
					yPoints[1] =  locY - (int)((enemy.length)*15);
					yPoints[2] =  locY - (int)((enemy.length)*15);
				}
			}
		}
		else
		{
			slope = Math.atan((double)(heroY- enemy.locY)/(heroX - enemy.locX));
			wingSlope = Math.atan((double)-(heroX- enemy.locX)/(heroY - enemy.locY));
			
			int dir = 1;
			if(enemy.locX < heroX)
				dir = -1;
			if(enemy.length == 0)
			{
				xPoints[1] = (int)((dir*Math.cos(slope)*7) + (int)(dir*Math.cos(wingSlope)*7))+locX;
				xPoints[2] = (int)((dir*Math.cos(slope)*7) - (int)(dir*Math.cos(wingSlope)*7))+locX;
				yPoints[1] = (int)((dir*Math.sin(slope)*7) + (int)(dir*Math.sin(wingSlope)*7))+locY;
				yPoints[2] = (int)((dir*Math.sin(slope)*7) - (int)(dir*Math.sin(wingSlope)*7))+locY;
			}
			else
			{
				xPoints[1] = (int)((dir*Math.cos(slope)*enemy.length*15) + (int)(dir*Math.cos(wingSlope)*(1/enemy.length)*15))+locX;
				xPoints[2] = (int)((dir*Math.cos(slope)*enemy.length*15) - (int)(dir*Math.cos(wingSlope)*(1/enemy.length)*15))+locX;
				yPoints[1] = (int)((dir*Math.sin(slope)*enemy.length*15) + (int)(dir*Math.sin(wingSlope)*(1/enemy.length)*15))+locY;
				yPoints[2] = (int)((dir*Math.sin(slope)*enemy.length*15) - (int)(dir*Math.sin(wingSlope)*(1/enemy.length)*15))+locY;
			}
		}
		g.setColor(enemy.color);
		
		if(enemy.HP == 1)
			g.setColor(Color.white);
		g.fillPolygon(xPoints, yPoints, 3);
	}
}
