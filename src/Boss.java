import java.awt.*;

public class Boss implements Enemy
{
	private int health;
	private int speed;
	private int damage;
	private int maxHealth;
	
	private double locationX = 640;
	private double locationY = 480;
	
	//TODO: What's this for/how is it different ? why don't minions have attack?
	int attack = 150;
	int moveCount = 0;
	double tempSlope = 0;
	
	private String name;
	private String effect; 
	private String status = ".none";
	
	//TODO: Why do we store this?
	private String world;
	
	private Color color;
	
	//TODO: boss only thing?
	boolean fighting = false;
	double[] xPoints = new double[4];
	double[] yPoints = new double[4];
	
	public Boss(){
		//blank constructor showing that we acknowledge that we're doing nothing.
	}
	
	@Override
	public void initialize(String name, String world, int health, int damage, int speed, String effect, Color color) {
		this.name = name;
		this.world = world;
		this.health = health;
		this.maxHealth = health;
		this.damage = damage;
		this.speed = speed;
		this.effect = effect;
		this.color = color;
	}
	
	@Override
	public void setLocation(int x, int y) {
		// TODO Auto-generated method stub
		
	}
	
	public void draw(Graphics g, int heroX, int heroY, Boss boss)
	{
		double slope = Math.atan((double)(heroY - boss.locationY)/(heroX - boss.locationX));
		double wingSlope = Math.atan((double)-(heroX - boss.locationX)/(heroY - boss.locationY));

		xPoints[0] = boss.locationX;
		yPoints[0] = boss.locationY;
		
		xPoints[1] = boss.locationX + (Math.cos(slope)*40) + (Math.cos(wingSlope)*25);
		yPoints[1] = boss.locationY + (Math.sin(slope)*40) + (Math.sin(wingSlope)*25);
		
		xPoints[2] = boss.locationX + Math.cos(slope)*25;
		yPoints[2] = boss.locationY + Math.sin(slope)*25;
		
		xPoints[3] = boss.locationX + (Math.cos(slope)*40 - Math.cos(wingSlope)*25);
		yPoints[3] = boss.locationY + (Math.sin(slope)*40 - Math.sin(wingSlope)*25);


		int[] drawPointsX = new int[4];
		int[] drawPointsY = new int[4];
		
		for(int i = 0; i < 4; i++)
		{
			if(boss.locationX < heroX)
			{
				xPoints[i] *= -1;
				xPoints[i] += 2*boss.locationX;
				yPoints[i] *= -1;
				yPoints[i] += 2*boss.locationY;
			}
			drawPointsX[i] = (int) xPoints[i];
			drawPointsY[i] = (int) yPoints[i];
		}
		
		g.setColor(boss.color);
		if(boss.name.equals("Clock"))
			g.setColor(Color.white);
		g.fillPolygon(drawPointsX, drawPointsY, 4);
	}
	
	public void drawArea(Graphics g, double Xloc, double Yloc, Boss boss)
	{

		g.setColor(boss.color);
		if(boss.name.equals("Clock"))
			g.setColor(Color.white);
		g.drawOval((int)Xloc - 75, (int)Yloc - 75, 150, 150);
	}
	
	public void drawBars(Graphics g, Boss boss)
	{
		g.setColor(Color.white);
		g.fillRect(800, 50, boss.maxHealth*2, 25);
		g.setColor(Color.RED);
		g.fillRect(800, 50, boss.health*2, 25);
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
