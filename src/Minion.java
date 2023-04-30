import java.awt.*;
import java.util.Random;

public class Minion implements Enemy 
{
	int HP;
	int Spd;
	int Dmg;
	int maxHP;
	int locX;
	int locY;
	double length;
	Color color;
	String effect;
	String status = ".none";
	String name = "";
	String world = "";
	double slope;
	double wingSlope;
	

	
	int[] xPoints = new int[3];
	int[] yPoints = new int[3];
	
	Random rand = new Random();
	
	public Minion()
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
	public void initialize(String name, String world, int health, int damage, int speed, String effect, Color color) {
		this.name = name;
		this.world = world;
		this.HP = health;
		this.maxHP = health;
		this.Dmg = damage;
		this.effect = effect;
		this.color = color;
	}
	
	public void draw(Graphics g, int heroX, int heroY, Minion minion)
	{
		xPoints[0] = minion.locX;
		yPoints[0] = minion.locY;
		
		if(minion.locX == heroX)
		{
			if(minion.length == 0)
			{
				xPoints[1] = (int)locX+7;
				xPoints[2] = (int)locX-7;
				if(minion.locY > heroY)
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
				xPoints[1] = (int)((1/minion.length)*15)+locX;
				xPoints[2] = locX - (int)((1/minion.length)*15);
				if(minion.locY > heroY)
				{
					yPoints[1] = (int)(minion.length*15)+locY;
					yPoints[2] = (int)(minion.length*15)+locY;
				}
				else
				{
					yPoints[1] =  locY - (int)((minion.length)*15);
					yPoints[2] =  locY - (int)((minion.length)*15);
				}
			}
		}
		else
		{
			slope = Math.atan((double)(heroY- minion.locY)/(heroX - minion.locX));
			wingSlope = Math.atan((double)-(heroX- minion.locX)/(heroY - minion.locY));
			
			int dir = 1;
			if(minion.locX < heroX)
				dir = -1;
			if(minion.length == 0)
			{
				xPoints[1] = (int)((dir*Math.cos(slope)*7) + (int)(dir*Math.cos(wingSlope)*7))+locX;
				xPoints[2] = (int)((dir*Math.cos(slope)*7) - (int)(dir*Math.cos(wingSlope)*7))+locX;
				yPoints[1] = (int)((dir*Math.sin(slope)*7) + (int)(dir*Math.sin(wingSlope)*7))+locY;
				yPoints[2] = (int)((dir*Math.sin(slope)*7) - (int)(dir*Math.sin(wingSlope)*7))+locY;
			}
			else
			{
				xPoints[1] = (int)((dir*Math.cos(slope)*minion.length*15) + (int)(dir*Math.cos(wingSlope)*(1/minion.length)*15))+locX;
				xPoints[2] = (int)((dir*Math.cos(slope)*minion.length*15) - (int)(dir*Math.cos(wingSlope)*(1/minion.length)*15))+locX;
				yPoints[1] = (int)((dir*Math.sin(slope)*minion.length*15) + (int)(dir*Math.sin(wingSlope)*(1/minion.length)*15))+locY;
				yPoints[2] = (int)((dir*Math.sin(slope)*minion.length*15) - (int)(dir*Math.sin(wingSlope)*(1/minion.length)*15))+locY;
			}
		}
		g.setColor(minion.color);
		
		if(minion.HP == 1)
			g.setColor(Color.white);
		g.fillPolygon(xPoints, yPoints, 3);
	}
}
