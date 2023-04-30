import java.awt.Color;

public interface Enemy {

	public void initialize(String name, String world, int health, int damage, int speed, String effect, Color color);
	public void setLocation(int x, int y);
	
	
	public void setHealth(int health);
	public int getHealth();
	public void loseHealth(int health);
	
	public void setSpeed(int speed);
	public int getSpeed();
	
	public void setDamage(int damage);
	public int getDamage();
	
	public void setLocationX(double x);
	public double getLocationX();
	public void addLocationX(int modifier);
	public void addLocationX(double modifier);
	
	public void setLocationY(double y);
	public double getLocationY();
	public void addLocationY(int modifier);
	public void addLocationY(double modifier);
	
	public void setStatus(String status);
	public String getStatus();
	
	public void setEffect(String effect);
	public String getEffect();
	
	public String toString();

	//public void draw(Graphics g, int heroX, int heroY, Enemy enemy);
		

}
