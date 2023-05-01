import java.awt.*;
import java.util.Arrays;
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
	
	//TODO: Change to double
	int[] xPoints = new int[3];
	int[] yPoints = new int[3];
	
	
	
	public Minion()
	{
		Random rand = new Random();
		int temp = rand.nextInt(4);
	
		switch(temp) {
			case 0:
				this.initialize("BLUE-MINION", "WORLD", 15, 7, 8, "5.slow", Color.blue);
				length = 2;
				break;
			case 1:	
				this.initialize("RED-MINION", "WORLD", 20, 10, 5, "5.hurt", Color.red);
				length = 1;
				break;
			case 2:
				this.initialize("YELLOW-MINION", "WORLD", 30, 7, 3, "5.stun", Color.yellow);
				length = .5;
				break;
			case 3:
				this.initialize("GREEN-MINION", "WORLD", 15, 5, 5, ".immunity", Color.green);
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
		//this.name = name;
		//this.world = world;
		this.health = health;
		this.maxHealth = health;
		this.damage = damage;
		this.speed = speed;
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
		xPoints[0] = (int)minion.getLocationX();
		yPoints[0] = (int)minion.getLocationY();
		
		if(minion.getLocationX() == heroX)
		{
			if(minion.length == 0)
			{
				xPoints[1] = (int)locationX+7;
				xPoints[2] = (int)locationX-7;
				if(minion.getLocationY() > heroY)
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
				if(minion.getLocationY() > heroY)
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
			double slope = Math.atan((double)(heroY- minion.getLocationY())/(heroX - minion.getLocationX()));
			double wingSlope = Math.atan((double)-(heroX- minion.getLocationX())/(heroY - minion.getLocationY()));
			
			int dir = 1;
			if(minion.getLocationX() < heroX)
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
		//TODO: Once you change to double then do casting to int's because fillPolygon(int[],int[],int)
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
	public void loseHealth(int health) {
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
	public String getStatusText() {
		int index = this.status.indexOf(".");
		if(index > 0){
		return this.status.substring(index  + 1, this.status.length());
		}
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
	
	@Override
	public int getDuration() {
		int index = this.status.indexOf(".");
		if(index > 0){
			return Integer.parseInt(this.status.substring(0, index));
		}
		return 0;
	}
	@Override
	public String toString() {
		return "Minion [health=" + health + ", speed=" + speed + ", damage=" + damage + ", maxHealth=" + maxHealth
				+ ", locationX=" + locationX + ", locationY=" + locationY + ", length=" + length + ", color=" + color
				+ ", effect=" + effect + ", status=" + status + ", name=" + name + ", world=" + world + ", slope="
				+ slope + ", xPoints=" + Arrays.toString(xPoints) + ", yPoints="
				+ Arrays.toString(yPoints) + "]";
	}
	
	
}
