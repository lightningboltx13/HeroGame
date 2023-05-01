import java.awt.Color;

public class Power {
	
	//TODO: make proper object with accessor/mutator
	String name;
	String effect;
	String shape;
	int damage; 
	int range;
	int cost;
	Color color;
	
	
	@Override
	public String toString() {
		return "Power [name=" + name + ", effect=" + effect + ", shape=" + shape + ", damage=" + damage + ", range="
				+ range + ", cost=" + cost + ", color=" + color + "]";
	}
	
	
}
