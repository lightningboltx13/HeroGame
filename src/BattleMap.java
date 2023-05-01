import java.awt.Frame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BattleMap extends Frame implements KeyListener, MouseListener, FocusListener
{
	boolean[] unlocked;
	String mode;
	int world;
	
	int HeroHealth = 100, HeroEnergy = 100;
	int regen = 0;
	boolean HeroPosition = false;
	int HeroLocX = 640, HeroLocY = 480;
	String HeroStatus = ".none";
	Player drawer = new Player();
	
	boolean gameEnd = false;
	
	int powerIndex = 0;
	Power[] powerSet;
	
	boolean up = false, down = false, left = false, right = false;
	
	Blast blasts[] = new Blast[0];
	Mine mines[] = new Mine[0];
	
	int minionCount;
	int spawnReady = 0;
	Minion minions[] = {new Minion()};
	Boss boss;
	boolean Bright = false;
	
	Timer timer;
	Random rand = new Random();
	
	public BattleMap(String GameMode, boolean[] completed, int place, Power[] powers, Boss badGuy)
	{
		for(int i = 0; i < completed.length; i++)
			if(completed[i])
				minionCount += 10;
		
		powerSet = powers;
		mode = GameMode;
		unlocked = completed;
		world = place;
		boss = badGuy;
		
		addKeyListener(this);
		addMouseListener(this);
		addFocusListener(this);
		
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});	
	}
	
	class testEvent extends TimerTask
	{
		public void run()
		{
			update(getGraphics());
			
			boolean statusRead = true;
			String heroStatus = "";
			int duration = 0;
			for(int i = 0; i < HeroStatus.length(); i++)
			{
				if(HeroStatus.charAt(i) == '.')
					statusRead = false;
				else if(statusRead)
					duration = (duration * 10) + Integer.parseInt(HeroStatus.charAt(i) + "");
				else if(!statusRead)
					heroStatus = heroStatus + HeroStatus.charAt(i);
			}
			
			
			int speed = 5;
			speed = checkHeroStatus(speed, duration, heroStatus);
			
			
			if(up)
				HeroLocY -= speed;
			if(down)
				HeroLocY += speed;
			if(left)
				HeroLocX -= speed;
			if(right)
				HeroLocX += speed;
			
			drawer.drawHero(getGraphics(), HeroLocX, HeroLocY, HeroPosition, heroStatus);
			
			HeroPosition = false;
			
			//run blast moves
			if(blasts.length > 0)
			{
				thenIStartedBlasting();
			}
			//minion spawn
			if(!boss.fighting)
			{
				spawnMinions();
			}
			
			
			//minion move, status updater
			moveMinions(heroStatus);
			
			
			//attack with mines
			attackWithMines();
			
			//boss actions
			if(boss.fighting)
			{
				//boss move
				if(boss.attack <= 0)
				{
					//boss runs at hero till he hits him with his area attack.
					if(!(boss.getLocationX() == HeroLocX))
					{
						boss.tempSlope = Math.atan((boss.getLocationY() - HeroLocY)/(boss.getLocationX() - HeroLocX));
						if(HeroLocX < boss.getLocationX())
							Bright = false;
						else
							Bright = true;
					}
				}
				else
				{
					//boss wonders around till ready to attack again
					if(boss.moveCount == 0)
					{
						Random rand = new Random();
						Bright = rand.nextBoolean();
						int temp = 1;
						if(rand.nextBoolean())
							temp = -1;
						boss.tempSlope =  Math.atan(rand.nextDouble()+temp*(rand.nextDouble()/2));
						if(rand.nextBoolean())
							boss.tempSlope += -1;
						boss.moveCount = rand.nextInt(25)+25;
					}
					else
						boss.moveCount--;
				}
				
				double Bspeed = boss.getSpeed();
				if(boss.getStatus().contains("slow"))//slow
					Bspeed /= 2;
				else if(boss.getStatus().contains("stun"))//stun
					Bspeed /= 4;
				

				//System.out.println(Bright);
				//System.out.println(boss.moveCount);
				if(Bright)
				{
					boss.addLocationY(Math.sin(boss.tempSlope)*Bspeed);
					boss.addLocationX(Math.cos(boss.tempSlope)*Bspeed);
				}
				else
				{
					boss.addLocationY(-(Math.sin(boss.tempSlope)*Bspeed));
					boss.addLocationX(-(Math.cos(boss.tempSlope)*Bspeed));
				}
				
				boss.attack--;
				
				//boss attack
				int atkTime = 50;
				if(boss.attack <= 0)
					atkTime = 10;
				if(boss.attack%atkTime == 0)
				{
					boss.drawArea(getGraphics(), boss.getLocationX(), boss.getLocationY(), boss);
					double temp1, temp2;
					temp1 = Math.pow(boss.getLocationX() - HeroLocX, 2);
					temp2 = Math.pow(boss.getLocationY() - HeroLocY, 2);
					if(Math.sqrt(temp1 + temp2) <=75)
					{
						int damage = boss.getDamage();
						if(heroStatus.contains(".defend"))
							damage /= 2;
						HeroHealth -= damage;
						
						if(HeroStatus.equals(".none"))
							if(boss.getEffect().equals("20.knockback"))
							{
								if(HeroLocX > boss.getLocationX())
								{
									HeroLocX += Math.cos(boss.tempSlope)*200;
									HeroLocY += Math.sin(boss.tempSlope)*200;
								}
								else if(HeroLocX < boss.getLocationX())
								{
									HeroLocX -= Math.cos(boss.tempSlope)*200;
									HeroLocY -= Math.sin(boss.tempSlope)*200;
								}
								else
								{
									if(HeroLocY > boss.getLocationY())
										HeroLocY += 200;
									else
										HeroLocY -= 200;
								}
							}
							else
								HeroStatus = boss.getEffect();
						boss.attack = 150;
					}
				}
				
				//boss status
				String bossStatus = boss.getStatusText();
				duration = boss.getDuration();
				
				
				if(duration > 0)
				{
					duration--;
					if(bossStatus.equals("hurt")) //TODO shouldn't this be related to damage?
						boss.loseHealth(1);
					boss.setStatus(duration + "." + bossStatus);
				}
				else if(duration == 0)
					boss.setStatus(".none");
				
				//boss drawer
				boss.draw(getGraphics(), HeroLocX, HeroLocY, boss);
				boss.drawBars(getGraphics(), boss);
			}
			
			if(HeroEnergy < 100)
				regen++;
			if(regen == 10)
			{
				HeroEnergy++;
				//HeroEnergy+=100;
				regen = 0;
			}
			
			//draw health and energy bars
			drawer.drawTest(getGraphics(), powerSet[powerIndex].name);
			drawer.drawBars(getGraphics(), HeroHealth, HeroEnergy);

			//TODO: break into its own methods?
			//death checks
			if(HeroHealth <= 0)
			{
				if(!gameEnd)
				{
					WorldSelector window;
					try{
						window = new WorldSelector(mode, unlocked);
						window.setVisible(true);
						window.setResizable(false);
						window.setExtendedState(MAXIMIZED_BOTH);
					}catch (IOException e1){
						e1.printStackTrace();
					}
					gameEnd = true;
					close();
				}
			}
			
			if(boss.getHealth() <= 0)
			{
				if(!gameEnd)
				{
					unlocked[world] = true;
					WorldSelector window;
					try{
						window = new WorldSelector(mode, unlocked);
						window.setVisible(true);
						window.setResizable(false);
						window.setExtendedState(MAXIMIZED_BOTH);
					}catch (IOException e1){
						e1.printStackTrace();
					}
					gameEnd = true;
					close();
				}
			}

//			update(getGraphics());
			timer.schedule(new testEvent(), 20);
//			update(getGraphics());
		}
	}
	public int checkHeroStatus(int speed, int duration, String heroStatus) {
		switch (heroStatus) {
		case "boost":
			speed = 10;
			break;
		case "slow":
			speed = 2;
			break;
		case "stun":
			speed = 0;
			break;
		case "none":
			//do nothing---none
			break;
		case "hurt":
			HeroHealth--;
			break;
			//need to account for immunity and defend still?
		default:
			System.err.println("Unknown Hero Status in Momvement: " + heroStatus);
		}
		//Hero status
		if(duration > 0)
		{
			duration--;
			HeroStatus = duration + "." + heroStatus;
		}
		if(duration == 0)
			HeroStatus = ".none"; 
			//TODO: if stacking statuses need to modify to not remove all statuses here.

		return speed;
	}
	
	public void spawnMinions() {
		Minion[] tempArray = minions;
		Minion[] tempArray2;
		int lifeCount = 0;
		
		try
		{
			for(int i = 0; i < tempArray.length; i++)
				if(tempArray[i].getHealth() > 0)
					lifeCount++;
		}catch (NullPointerException ex){}
		
		if(minionCount > 0)
			tempArray2 = new Minion[lifeCount + 1];
		else
			tempArray2 = new Minion[lifeCount];
		
		try{
			int count = 0;
			for(int i = 0; i < tempArray.length; i++)
				if(tempArray[i].getHealth() > 0)
				{
					tempArray2[count] = tempArray[i];
					count++;
				}
		}catch (NullPointerException ex){}
		
		if(minionCount > 0)
		{
			if(spawnReady == 10)
			{
				minionCount--;
				Minion tempMinion = new Minion();
				tempArray2[tempArray2.length - 1] = tempMinion;
				spawnReady -= 10;
			}
			else
			{
				spawnReady++;
			}
		}
		minions = tempArray2;
		if(minions.length == 0)
			boss.fighting = true;
	}
	
	public void moveMinions(String status) {
		try{
			for(Minion minion: minions)
			{
				String Estatus = minion.getStatusText();
				int Eduration = minion.getDuration();
				
				double minionSpeed = minion.getSpeed();
				if(Estatus.contains("slow"))
					minionSpeed /= 2;
				if(Estatus.contains("stun"))
					minionSpeed = 0;
				if(Estatus.contains("hurt"))
					minion.loseHealth(1);
				if(!Estatus.contains("none") && !Estatus.contains("immunity"))
				{
					Eduration--;
					if(Eduration == 0)
						minion.setStatus(".none");
					else
						minion.setStatus(Eduration + "." + Estatus);
				}
				if(minion.getLocationX() == HeroLocX)
				{
					if(minion.getLocationY() > HeroLocY)
						minion.addLocationY(-(minionSpeed));
					else if(minion.getLocationY() < HeroLocY)
						minion.addLocationY(minionSpeed);
					minion.addLocationX(1);
				}
				else
				{
					int dir = 1;
					if(minion.getLocationX() > HeroLocX)
						dir = -1;
					minion.addLocationX(dir*(Math.cos(minion.slope)*minionSpeed));
					minion.addLocationY(dir*(Math.sin(minion.slope)*minionSpeed));
				}
				
				double temp1, temp2;
				temp1 = Math.pow(minion.getLocationX() - HeroLocX,2);
				temp2 = Math.pow(minion.getLocationY() - HeroLocY,2);
				if(Math.sqrt(temp1 + temp2) <= 10)
					minionHit(minion, status);
				
				minion.draw(getGraphics(), HeroLocX, HeroLocY, minion);
			}
		}catch (NullPointerException ex){}
	}
	public void thenIStartedBlasting() {
		try{
			//Blast Movement, collision, and end of life
			for(Blast blast: blasts)
			{
				if(blast.direction.equals("right"))
				{
					blast.locationX += Math.cos(blast.slope)*5;
					blast.locationY += Math.sin(blast.slope)*5;
				}
				else if(blast.direction.equals("left"))
				{
					blast.locationX -= Math.cos(blast.slope)*5;
					blast.locationY -= Math.sin(blast.slope)*5;
				}
				else if(blast.direction.equals("up"))
				{
					blast.locationY -= 5;
				}
				else if(blast.direction.equals("down"))
				{
					blast.locationY += 5;
				}
				
				if(boss.fighting)
				{
					//collision detection
					double blastDis = Math.sqrt(Math.pow(boss.getLocationX() - blast.locationX, 2) + Math.pow(boss.getLocationY() - blast.locationY, 2));
					if(blastDis <= 10)
					{
						blast.hit = true;
						boss.loseHealth(blast.damage);
						bossEffectStat(powerSet[powerIndex].effect, boss);
					}
				}
				else
				{
					for(Minion minion : minions)
					{
						double blastDis = Math.sqrt(Math.pow(minion.getLocationX() - blast.locationX, 2) + Math.pow(minion.getLocationY() - blast.locationY, 2));
						if(blastDis <= 5)
						{
							blast.hit = true;
							minion.loseHealth(blast.damage);
							applyHeroEffectToMinion(blast.effect, minion);
						}
					}
				}
				
				double temp1, temp2;
				temp1 = Math.pow(blast.locationX - blast.srtX, 2);
				temp2 = Math.pow(blast.locationY - blast.srtY, 2);
				double travled = Math.sqrt(temp1 + temp2);
				if(travled >= blast.range*50)
					blast.hit = true;
			}
			
			//blast array resizer
			int blastCount = 0;
			for(int i = 0; i < blasts.length; i++)
				//counts blasts that aren't dead
				if(!blasts[i].hit)
					blastCount++;
			for(int i = 0; i < blasts.length; i++)
			{
				//create temporary array to store blasts
				Blast[] tempArray = new Blast[blastCount];
				int tempCount = 0;
				int numBlast = blasts.length;
						
				for(int m = 0; m < numBlast; m++)
					if(!blasts[m].hit)
					{
						try{
							tempArray[tempCount] = blasts[m];
						}catch (ArrayIndexOutOfBoundsException ex){}
					tempCount++;
					}
				blasts = tempArray;
			}
			for(int i = 0; i < blasts.length; i++)
				blasts[i].drawBlast(getGraphics(), blasts[i]);
		}catch (NullPointerException ex){}
	}
	
	public void attackWithMines() {
		if(mines.length > 0)
		{
			int mineCount = 0;
			double mine1, mine2;
			
			if(!boss.fighting)
			{
				for(Minion minion: minions)
				{
					try{
						for(Mine mine: mines)
						{
							//enemies is null!!!
							mine1 = Math.pow(minion.getLocationX() - mine.locX, 2);
							mine2 = Math.pow(minion.getLocationY() - mine.locY, 2);
							if(Math.sqrt(mine1 + mine2) <= 10)
							{
								minion.loseHealth(10);
								mine.exploded = true;
							}
							else
								mineCount++;
						}
					}catch (NullPointerException ex){}
				}
			}
			else
			{
				for(Mine mine: mines)
				{
					mine1 = Math.pow(boss.getLocationX() - mine.locX, 2);
					mine2 = Math.pow(boss.getLocationY() - mine.locY, 2);
					if(Math.sqrt(mine1 + mine2) <= 10)
					{
						boss.loseHealth(10);
						mine.exploded = true;
					}
					else
						mineCount++;
				}
			}
			Mine[] tempArray = new Mine[mineCount];
			int tempCount = 0;
			for(Mine mine: mines)
			{
				try{
					if(!mine.exploded)
					{
						//TODO: Might have an array index out of bounds, need to look into
						//System.out.println("mineCount: " + mineCount);
						//System.out.println("tempArray Index: " + tempCount);
						//System.out.println("mines Index: " + m);
						tempArray[tempCount] = mine;
						tempCount++;
					}
				}catch (NullPointerException ex){}
			}
			mines = tempArray;
		}
		
		//draw mines
		try{
			for(Mine mine: mines)
				mine.drawMine(getGraphics(),  mine);
		}catch (NullPointerException ex){}
		
	}

	public void close()
	{
		this.hide();
	}
	
	public void minionHit(Minion minion, String heroStatus)
	{
		int damage = minion.getDamage();
		if(heroStatus.contains("defend"))
			damage /= 2;
		HeroHealth -= damage;
		minion.setHealth(0);
		if(HeroStatus.equals(".none") && !minion.getEffect().contains("immunity"))
			HeroStatus = minion.getEffect();
	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		//Movement
		if(key == KeyEvent.VK_W)
			up = true;
		else if(key == KeyEvent.VK_A)
			left = true;
		else if(key == KeyEvent.VK_S)
			down= true;
		else if(key == KeyEvent.VK_D)
			right = true;

		if(key == KeyEvent.VK_1)//Power Switch
			powerIndex = 0;
		else if(key == KeyEvent.VK_2)//Power Switch
			powerIndex = 1;
		else if(key == KeyEvent.VK_3)//Power Switch
			powerIndex = 2;

		if(key == KeyEvent.VK_P)//Pause
		{
			PauseMenu window = new PauseMenu();
			window.setVisible(true);
			window.setBounds(550, 375, 200, 200);
		}
		
		if(key == KeyEvent.VK_SPACE)//Update Graphics
			update(getGraphics());
		
	}
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		//Movement
		if(key == KeyEvent.VK_W)
			up = false;
		else if(key == KeyEvent.VK_A)
			left = false;
		else if(key == KeyEvent.VK_S)
			down= false;
		else if(key == KeyEvent.VK_D)
			right = false;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		try{
			if(HeroEnergy > powerSet[powerIndex].cost)
			{
				HeroPosition = true;
				HeroEnergy -= powerSet[powerIndex].cost;
				
				switch(powerSet[powerIndex].shape) {
				case "beam":
					this.doBeam((int)e.getLocationOnScreen().getX(), (int)e.getLocationOnScreen().getY());
					break;
				case "blast":
					this.doBlast((int)e.getLocationOnScreen().getX(), (int)e.getLocationOnScreen().getY());
					break;
				case "area":
					this.doArea();
					break;
				case "melee":
					this.doMelee((int)e.getLocationOnScreen().getX(),(int)e.getLocationOnScreen().getY());
					break;
				case "self":
					this.doSelf((int)e.getLocationOnScreen().getX(), (int)e.getLocationOnScreen().getY());
					break;
				default:
					System.err.println("Unknown Power: " + powerSet[powerIndex].shape);
				}
			}
		}catch(NullPointerException e1){}
	}
	
	public void doMelee(int mouseX, int mouseY) {
		drawer.drawMelee(getGraphics(), HeroLocX, HeroLocY, powerSet[powerIndex]);
		double slope = Math.atan((double)(mouseY - HeroLocY)/(double)(mouseX - HeroLocX));
		
		int fistX, fistY;
		if(mouseX > HeroLocX)
		{
			fistX = HeroLocX + (int)(Math.cos(slope)*25);
			fistY = HeroLocY + (int)(Math.sin(slope)*25);
		}
		else if(mouseX< HeroLocX)
		{
			fistX = HeroLocX - (int)(Math.cos(slope)*25);
			fistY = HeroLocY - (int)(Math.sin(slope)*25);
		}
		else
			if(mouseY > HeroLocY)
			{
				fistX = HeroLocX;
				fistY = HeroLocY + 25;
			}
			else
			{
				fistX = HeroLocX;
				fistY = HeroLocY - 25;
			}

		double temp1, temp2;
		if(boss.fighting)
		{
			temp1 = Math.pow(boss.getLocationX() - fistX,  2);
			temp2 = Math.pow(boss.getLocationY() - fistY,  2);
			if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].range*10)
			{
				boss.loseHealth(powerSet[powerIndex].damage);
				bossEffectStat(powerSet[powerIndex].effect, boss);
			}
		}
		else
		{
			for(Minion minion : minions)
			{
				temp1 = Math.pow(minion.getLocationX() - fistX,  2);
				temp2 = Math.pow(minion.getLocationY() - fistY,  2);
				if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].range*10)
				{
					minion.loseHealth(powerSet[powerIndex].damage);
					applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
				}
			}
		}
	}
	
	public void doSelf(int mouseX, int mouseY) {
		boolean powerRead = false;
		String power = "";
		for(int i = 0; i < powerSet[powerIndex].effect.length(); i++)
		{
			if(powerSet[powerIndex].effect.charAt(i) == '.')
				powerRead = true;
			else if(powerRead)
				power = power + powerSet[powerIndex].effect.charAt(i);
		}
		
		if(power.contains("mine"))
		{
			Mine tempMine = new Mine(HeroLocX, HeroLocY);
			Mine[] tempArray = new Mine[mines.length + 1];
			for(int i = 0; i < mines.length; i++)
				tempArray[i] = mines[i];
			tempArray[mines.length] = tempMine;
			mines = tempArray;
		}
		
		else if(power.contains("tele"))
		{
			HeroLocX = mouseX;
			HeroLocY = mouseY;
		}
		
		else if(power.contains("boost"))
		{
			HeroStatus = "30.boost";
		}
		
		else if(power.contains("defend"))
		{
			HeroStatus = powerSet[powerIndex].effect;
		}else {
			System.err.println("Unknown Hero Power: " + power);
		}
	}
	
	public void doArea() {
		drawer.drawArea(getGraphics(), HeroLocX, HeroLocY, powerSet[powerIndex]);
		double temp1, temp2;
		if(boss.fighting)
		{
			temp1 = Math.pow(boss.getLocationX() - HeroLocX,  2);
			temp2 = Math.pow(boss.getLocationY() - HeroLocY,  2);
			if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].range*50)
			{
				boss.loseHealth(powerSet[powerIndex].damage);
				bossEffectStat(powerSet[powerIndex].effect, boss);
			}
		}
		else
		{
			for(Minion minion : minions)
			{
				temp1 = Math.pow(minion.getLocationX() - HeroLocX,  2);
				temp2 = Math.pow(minion.getLocationY() - HeroLocY,  2);
				if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].range*50)
				{
					minion.loseHealth(powerSet[powerIndex].damage);
					applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
				}
			}
		}
	}
	
	public void doBeam(int mouseX, int mouseY) {
		double slope = drawer.drawBeam(getGraphics(), HeroLocX, HeroLocY, powerSet[powerIndex]);
		
		if(boss.fighting)
		{
			double Eslope1, Eslope2, distance;
			distance = Math.sqrt(Math.pow(boss.getLocationY() - HeroLocY, 2) + Math.pow(boss.getLocationX() - HeroLocX, 2));
			
			if(distance <= powerSet[powerIndex].range*100)
			{
				if(mouseY > HeroLocY)
				{
					Eslope1 = Math.atan(((double)boss.yPoints[1] - HeroLocY)/((double)boss.xPoints[1] - HeroLocX));
					Eslope2 = Math.atan(((double)boss.yPoints[3] - HeroLocY)/((double)boss.xPoints[3] - HeroLocX));
					
					//right
					if(mouseX > HeroLocX && boss.getLocationX() > HeroLocX)
					{
						if(Eslope1 <= slope && Eslope2 >= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					//left
					else if(mouseX < HeroLocX && boss.getLocationX() < HeroLocX)
					{
						if(Eslope1 >= slope && Eslope2 <= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					else {
						System.out.println("[BEAM]- Y+ -BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
					}
				}
				else
				{
					Eslope1 = Math.atan((HeroLocY - (double)boss.yPoints[1])/(HeroLocX - (double)boss.xPoints[1]));
					Eslope2 = Math.atan((HeroLocY - (double)boss.yPoints[3])/(HeroLocX - (double)boss.xPoints[3]));
					
					//right
					if(mouseX> HeroLocX && boss.getLocationX() > HeroLocX)
					{
						if(Eslope1 >= slope && Eslope2 <= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					//left
					else if(mouseX < HeroLocX && boss.getLocationX() < HeroLocX)
					{
						if(Eslope1 <= slope && Eslope2 >= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					else {
						System.out.println("[BEAM]- Y- -BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
					}
				}
			}
		}
		if(!boss.fighting)
		{
			double Eslope1, Eslope2, distance;
			for(Minion minion : minions)
			{
				distance = Math.sqrt(Math.pow(minion.getLocationY() - HeroLocY, 2) + Math.pow(minion.getLocationX() - HeroLocX, 2));
				
				if(distance <= powerSet[powerIndex].range*100)
				{
					if(mouseY > HeroLocY)
					{
						Eslope1 = Math.atan(((double)minion.yPoints[1] - HeroLocY)/((double)minion.xPoints[1] - HeroLocX));
						Eslope2 = Math.atan(((double)minion.yPoints[2] - HeroLocY)/((double)minion.xPoints[2] - HeroLocX));
						
						//right
						if(mouseX > HeroLocX && minion.getLocationX() > HeroLocX)
						{
							if(Eslope1 <= slope && Eslope2 >= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//left
						if(mouseX < HeroLocX && boss.getLocationX() < HeroLocX)
						{
							if(Eslope1 >= slope && Eslope2 <= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//TODO: what happens when they're equal? can they ever be equal? 
						else {
							System.out.println("[BEAM] - Y+ - NOT--BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
						}
					}
					else
					{
						Eslope1 = Math.atan((HeroLocY - (double)minion.yPoints[1])/(HeroLocX - (double)minion.xPoints[1]));
						Eslope2 = Math.atan((HeroLocY - (double)minion.yPoints[2])/(HeroLocX - (double)minion.xPoints[2]));
						
						//right
						if(mouseX > HeroLocX && minion.getLocationX() > HeroLocX)
						{
							if(Eslope1 >= slope && Eslope2 <= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//left
						else if(mouseX < HeroLocX && minion.getLocationX() < HeroLocX)
						{
							if(Eslope1 <= slope && Eslope2 >= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//TODO: what happens when they're equal? can they ever be equal? 
						else {
							System.out.println("[BEAM] - Y- - NOT--BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
						}
					}
				}
			}
		}
	}
	
	public void doBlast(int mouseX, int mouseY) {
		double slope = Math.atan((double)(mouseY - HeroLocY)/(double)(mouseX - HeroLocX));
		String dir = "";
		if(mouseX > HeroLocX) {
			dir = "right";
			System.out.println("SHOOTING RIGHT: HeroLocX: " + HeroLocX + " Mouse LocX: " + mouseX);
		}else if(mouseX < HeroLocX) {
			dir = "left";
			System.out.println("SHOOTING LEFT: HeroLocX: " + HeroLocX + " Mouse LocX: " + mouseX);
		}else { //equal (meaning center)
			System.out.println("SHOOTING EQUAL: HeroLocX: " + HeroLocX + " Mouse LocX: " + mouseX);
			if(mouseY > HeroLocY) {
				dir = "down";
				System.out.println("SHOOTING DOWN: HeroLocY: " + HeroLocY + " Mouse LocY: " + mouseY);
			}else{
				dir = "up";
				System.out.println("SHOOTING UP: HeroLocY: " + HeroLocY + " Mouse LocY: " + mouseY);
			}
		}
		Blast tempBlast = new Blast(slope, dir, powerSet[powerIndex].damage, powerSet[powerIndex].range, HeroLocX, HeroLocY, powerSet[powerIndex].color, powerSet[powerIndex].effect);
		Blast[] tempArray = new Blast[blasts.length + 1];
		for(int i = 0; i < blasts.length; i++)
			tempArray[i] = blasts[i];
		tempArray[blasts.length] = tempBlast;
		blasts = tempArray;
	}
			
	public void applyHeroEffectToMinion(String effect, Minion minion)
	{
		if(!minion.getStatus().equals(".immunity"))
		{
			boolean statusRead = true;
			String heroStatus = "";
			int duration = 0;
			for(int i = 0; i < effect.length(); i++)
			{
				if(effect.charAt(i) == '.')
					statusRead = false;
				else if(statusRead)
					duration = (duration * 10) + Integer.parseInt(effect.charAt(i) + "");
				else if(!statusRead)
					heroStatus = heroStatus + effect.charAt(i);
			}
			if(heroStatus.equals("knock"))
			{
	
				double slope = Math.atan((double)(HeroLocY - minion.getLocationY())/(double)(HeroLocX - minion.getLocationX()));
				if(minion.getLocationX() > HeroLocX)
				{
					minion.addLocationX(Math.cos(slope)*duration);
					minion.addLocationY(Math.sin(slope)*duration);
				}
				if(minion.getLocationX() < HeroLocX)
				{
					minion.addLocationX(-(Math.cos(slope)*duration));
					minion.addLocationY(-(Math.sin(slope)*duration));
				}
				if(minion.getLocationX() == HeroLocX)
				{
					if(minion.getLocationY() > HeroLocY)
						minion.addLocationY(Math.sin(slope)*duration);
					else
						minion.addLocationY(-(Math.sin(slope)*duration));
				}
			}
			else
			{
				minion.setStatus(effect);
			}
		}
	}
	
	public void bossEffectStat(String effect, Boss boss)
	{
		boolean statusRead = true;
		String status = "";
		int duration = 0;
		for(int i = 0; i < effect.length(); i++)
		{
			if(effect.charAt(i) == '.')
				statusRead = false;
			else if(statusRead)
				duration = (duration * 10) + Integer.parseInt(effect.charAt(i) + "");
			else if(!statusRead)
				status = status + effect.charAt(i);
		}
		if(status.equals("knock"))
		{

			double slope = Math.atan((double)(HeroLocY - boss.getLocationY())/(double)(HeroLocX - boss.getLocationX()));
			if(boss.getLocationX() > HeroLocX)
			{
				boss.addLocationX(Math.cos(slope)*duration);
				boss.addLocationY(Math.sin(slope)*duration);
			}
			if(boss.getLocationX() < HeroLocX)
			{
				boss.addLocationX(-(Math.cos(slope)*duration));
				boss.addLocationY(-(Math.sin(slope)*duration));
			}
			if(boss.getLocationX() == HeroLocX)
			{
				if(boss.getLocationY() > HeroLocY)
					boss.addLocationY(Math.sin(slope)*duration);
				else
					boss.addLocationY(-(Math.sin(slope)*duration));
			}
		}
		else
		{
			boss.setStatus(effect);
		}
	}
	
	public void focusGained(FocusEvent e)
	{
		timer = new Timer();
		timer.schedule(new testEvent(), 20);
	}
	
	public void focusLost(FocusEvent e)
	{
		timer.cancel();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
