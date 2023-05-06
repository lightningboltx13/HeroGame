import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

public class Player {
	//enhance to store data in properties like enemies
	private String status = ".none";
	private int energy=100;
	private int health=100;
	private int regen=0;
	private int speed;
	private boolean heroPosition = false;
	
	//TODO: Change to double
	private int locationX = 640;
	private int locationY = 480;
	

	public Player(){}
	
	public void drawHero(Graphics g, int Xloc, int Yloc, boolean attacking, String status)
	{
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		
		g.setColor(Color.white);
		if(status.contains("boost"))
			g.setColor(new Color(200, 0, 150));
		if(status.contains("defend"))
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
			g.drawLine(Xloc, Yloc, (int)(Xloc + (Math.cos(slope) * (power.range*100))), (int)(Yloc + (Math.sin(slope)) * (power.range*100)));
		else if(b.getX() < Xloc)
			g.drawLine(Xloc, Yloc, (int)(Xloc + (-1*Math.cos(slope) * (power.range*100))), (int)(Yloc + (-1*Math.sin(slope)) * (power.range*100)));
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatusText() {
		System.out.println("Player StatusTextCalled: " + this.status);
		int index = this.status.indexOf(".");
		//get the RIGHT of the "." 
		//even if the "." is the first character, 
		//it will exclude it and any duration that proceeds it.
		//returns -1 if there is no "."
		if(index > -1){
		return this.status.substring(index  + 1, this.status.length());
		}
		return this.status;
	}
	
	public int getDuration() {
		int index = this.status.indexOf(".");
		//get the LEFT of the "." if "." is not the first char
		//returns -1 if there is no "."
		if(index > 0){
			return Integer.parseInt(this.status.substring(0, index));
		}
		return 0;
	}
	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public void loseHealth(int damageDone) {
		this.health = this.health - damageDone ;
	}

	public int getRegen() {
		return regen;
	}

	public void setRegen(int regen) {
		this.regen = regen;
	}

	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public void setLocationY(int locationY) {
		this.locationY = locationY;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isHeroPosition() {
		return heroPosition;
	}

	public void setHeroPosition(boolean heroPosition) {
		this.heroPosition = heroPosition;
	}
	
	
	
	
	
	
	
	
	
	
}
