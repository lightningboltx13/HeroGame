import java.awt.*;
import java.util.Random;

public class Minion implements Enemy 
{
	private int health;
	private int speed;
	private int damage;
	//TODO: Why don't minion's have max health?
	private int maxHealth;
	
	private double locationX;
	private double locationY;
	
	//Is length = duration here?
	double length;
	
	private Color color;
	private String effect;
	
	private String status = ".none";
	private String name = "";
	private String world = "";
	
	double slope;
	double wingSlope;
	
	int[] xPoints = new int[3];
	int[] yPoints = new int[3];
	
	Random rand = new Random();
	
	public Minion()
	{
		int temp = rand.nextInt(4);
		
//		if(temp == 0)
//		{
//			health = 15;
//			speed = 8;
//			damage = 7;
//			length = 2;
//			effect = "5.slow";
//			color = Color.blue;
//		}
//		if(temp == 1)
//		{
//			health = 20;
//			speed = 5;
//			damage = 10;
//			length = 1;
//			effect = "5.hurt";
//			color = Color.red;
//		}
//		if(temp == 2)
//		{
//			health = 30;
//			speed = 3;
//			damage = 7;
//			length = .5;
//			effect = "5.stun";
//			color = Color.yellow;
//		}
//		if(temp == 3)
//		{
//			health = 15;
//			speed = 5;
//			damage = 5;
//			length = 0;
//			effect = ".immunity";
//			color = Color.green;
//		}
		switch(temp) {
			case 0:
				this.initialize("minion-" + temp, "", 15, 7, 8, "5.slow", Color.blue);
				length = 2;
				break;
			case 1:
				this.initialize("minion-" + temp, "", 20, 10, 5, "5.hurt", Color.red);
				length = 1;
				break;
			case 2:
				this.initialize("minion-" + temp, "", 30, 7, 3, "5.stun", Color.yellow);
				length = .5;
				break;
			case 3:
				this.initialize("minion-" + temp, "", 15, 5, 5, ".immunity", Color.green);
				length = 0;
				break;
		}
		
		temp = rand.nextInt(4);
		
		switch(temp) {
			case 0:
				this.setLocation(0, rand.nextInt(960));
				break;
			case 1:
				this.setLocation(rand.nextInt(1280), 0);
				break;
			case 2:
				this.setLocation(1280, rand.nextInt(960));
				break;
			case 3:
				this.setLocation(rand.nextInt(1280), 960);
				break;
		}
	}
	@Override
	public void initialize(String name, String world, int health, int damage, int speed, String effect, Color color) {
		this.name = name;
		this.world = world;
		this.health = health;
		this.maxHealth = health;
		this.damage = damage;
		this.effect = effect;
		this.color = color;
	}
	
	@Override
	public void setLocation(int x, int y) {
		this.locationX = x;
		this.locationY = y;
	}
	
	public void draw(Graphics g, int heroX, int heroY, Minion minion)
	{
		xPoints[0] = (int)minion.locationX;
		yPoints[0] = (int)minion.locationY;
		
		if(minion.locationX == heroX)
		{
			if(minion.length == 0)
			{
				xPoints[1] = (int)locationX+7;
				xPoints[2] = (int)locationX-7;
				if(minion.locationY > heroY)
				{
					yPoints[1] = (int)locationY+7;
					yPoints[2] = (int)locationY+7;
				}
				else
				{
					yPoints[1] = (int)locationY-7;
					yPoints[2] = (int)locationY-7;
				}
			}
			else
			{
				xPoints[1] = (int)((1/minion.length)*15)+ (int)locationX;
				xPoints[2] = (int)locationX - (int)((1/minion.length)*15);
				if(minion.locationY > heroY)
				{
					yPoints[1] = (int)(minion.length*15)+ (int)locationY;
					yPoints[2] = (int)(minion.length*15)+(int)locationY;
				}
				else
				{
					yPoints[1] =  (int)locationY - (int)((minion.length)*15);
					yPoints[2] =  (int)locationY - (int)((minion.length)*15);
				}
			}
		}
		else
		{
			slope = Math.atan((double)(heroY- minion.locationY)/(heroX - minion.locationX));
			wingSlope = Math.atan((double)-(heroX- minion.locationX)/(heroY - minion.locationY));
			
			int dir = 1;
			if(minion.locationX < heroX)
				dir = -1;
			if(minion.length == 0)
			{
				xPoints[1] = (int)((dir*Math.cos(slope)*7) + (int)(dir*Math.cos(wingSlope)*7))+ (int)locationX;
				xPoints[2] = (int)((dir*Math.cos(slope)*7) - (int)(dir*Math.cos(wingSlope)*7))+ (int)locationX;
				yPoints[1] = (int)((dir*Math.sin(slope)*7) + (int)(dir*Math.sin(wingSlope)*7))+ (int)locationY;
				yPoints[2] = (int)((dir*Math.sin(slope)*7) - (int)(dir*Math.sin(wingSlope)*7))+ (int)locationY;
			}
			else
			{
				xPoints[1] = (int)((dir*Math.cos(slope)*minion.length*15) + (int)(dir*Math.cos(wingSlope)*(1/minion.length)*15))+ (int) locationX;
				xPoints[2] = (int)((dir*Math.cos(slope)*minion.length*15) - (int)(dir*Math.cos(wingSlope)*(1/minion.length)*15))+ (int)locationX;
				yPoints[1] = (int)((dir*Math.sin(slope)*minion.length*15) + (int)(dir*Math.sin(wingSlope)*(1/minion.length)*15))+ (int)locationY;
				yPoints[2] = (int)((dir*Math.sin(slope)*minion.length*15) - (int)(dir*Math.sin(wingSlope)*(1/minion.length)*15))+ (int)locationY;
			}
		}
		g.setColor(minion.color);
		
		if(minion.health == 1)
			g.setColor(Color.white);
		g.fillPolygon(xPoints, yPoints, 3);
	}
	@Override
	public void setHealth(int health) {
		this.health = health;
	}
	@Override
	public int getHealth() {
		return this.health;
	}
	@Override
	public void lossHealth(int health) {
		//TODO: Check for negative values
		this.health = this.getHealth() - health;
	}
	@Override
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	@Override
	public int getSpeed() {
		return this.speed;
	}
	@Override
	public void setDamage(int damage) {
		this.damage = damage;
	}
	@Override
	public int getDamage() {
		return this.damage;
	}
	@Override
	public void setLocationX(double x) {
		this.locationX = x;
	}
	@Override
	public double getLocationX() {
		return this.locationX;
	}
	@Override
	public void addLocationX(int modifier) {
		this.locationX = this.getLocationX() + modifier;
	}
	
	@Override
	public void addLocationX(double modifier) {
		this.locationX = this.getLocationX() + modifier;
	}
	
	@Override
	public void setLocationY(double y) {
		this.locationY = y;
	}
	@Override
	public double getLocationY() {
		return this.locationY;
	}
	@Override
	public void addLocationY(int modifier) {
		this.locationY = this.getLocationY() + modifier;
	}
	@Override
	public void addLocationY(double modifier) {
		this.locationY = this.getLocationY() + modifier;
	}
	@Override
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String getStatus() {
		return this.status;
	}
	
	@Override
	public void setEffect(String effect) {
		this.effect = effect;
	}

	@Override
	public String getEffect() {
		return this.effect;
	}
}
