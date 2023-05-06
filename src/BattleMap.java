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
	
	int regen = 0;

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
			
			//set speed to 5, not sure why but replicating existing code
			//TODO: Probably need to initialize to 5 and then keep it as that?
			drawer.setSpeed(5);
			
			//TODO: Maybe move this to the player class? idk
			checkHeroStatus();
	
			
			if(up)
				drawer.setLocationY(drawer.getLocationY() - drawer.getSpeed());
			if(down)
				drawer.setLocationY(drawer.getLocationY() + drawer.getSpeed());
			if(left)
				drawer.setLocationX(drawer.getLocationX() - drawer.getSpeed());
			if(right)
				drawer.setLocationX(drawer.getLocationX() + drawer.getSpeed());
			
			//TODO: Modify to use values from class instead of passing them in.
			drawer.drawHero(getGraphics(), drawer.getLocationX(), drawer.getLocationY(), drawer.isHeroPosition(), drawer.getStatusText());

			
			//TODO: Why do we set hero position? what is it used for?
			drawer.setHeroPosition(false);
			
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
			moveMinions();

			
			
			//attack with mines
			attackWithMines();
			
			//boss actions
			if(boss.fighting)
			{
				//boss move
				if(boss.attack <= 0)
				{
					//boss runs at hero till he hits him with his area attack.
					//TODO: Why not just make it != instead of complicating it with the !(x==y)?
					if(!(boss.getLocationX() == drawer.getLocationX()))
					{
						boss.tempSlope = Math.atan(
								(boss.getLocationY() - drawer.getLocationY())
								/
								(boss.getLocationX() - drawer.getLocationX()));
						if(drawer.getLocationX() < boss.getLocationX())
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
					temp1 = Math.pow(boss.getLocationX() - drawer.getLocationX(), 2);
					temp2 = Math.pow(boss.getLocationY() - drawer.getLocationY(), 2);
					if(Math.sqrt(temp1 + temp2) <=75)
					{
						int damage = boss.getDamage();
						if(drawer.getStatus().contains(".defend"))
							damage /= 2;
						drawer.loseHealth(damage);
						
						if(drawer.getStatus().equals(".none"))
							if(boss.getEffect().equals("20.knockback"))
							{
								if(drawer.getLocationX() > boss.getLocationX())
								{	
									//TODO once you change to double remove type-casting here
									drawer.setLocationX((int)(drawer.getLocationX() + Math.cos(boss.tempSlope)*200));
									drawer.setLocationY((int)(drawer.getLocationY() + Math.sin(boss.tempSlope)*200));
								}
								else if(drawer.getLocationX() < boss.getLocationX())
								{
									//TODO once you change to double remove type-casting here
									drawer.setLocationX((int)(drawer.getLocationX() - Math.cos(boss.tempSlope)*200));
									drawer.setLocationY((int)(drawer.getLocationY() - Math.sin(boss.tempSlope)*200));
								}
								else
								{
									if(drawer.getLocationY() > boss.getLocationY())
										drawer.setLocationY(drawer.getLocationY() + 200);
									else
										drawer.setLocationY(drawer.getLocationY() - 200);
								}
							}
							else
								drawer.setStatus(boss.getEffect());
						boss.attack = 150;
					}
				}
				
				//boss status
				String bossStatus = boss.getStatusText();
				int d1 = boss.getDuration();
				
				
				if(d1 > 0)
				{
					d1--;
					if(bossStatus.equals("hurt")) //TODO shouldn't this be related to damage?
						boss.loseHealth(1);
					boss.setStatus(d1 + "." + bossStatus);
				}
				else if(d1 == 0)
					boss.setStatus(".none");
				
				//boss drawer
				boss.draw(getGraphics(), drawer.getLocationX(), drawer.getLocationY(), boss);
				boss.drawBars(getGraphics(), boss);
			}
			
			regenHealth();
			
			//draw health and energy bars
			drawer.drawTest(getGraphics(), powerSet[powerIndex].name);
			//TODO: Change to use internal properties instead of passing them in
			drawer.drawBars(getGraphics(), drawer.getHealth(), drawer.getEnergy());

			//TODO: break into its own methods?
			//death checks
			if(drawer.getHealth() <= 0)
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
	
	public void regenHealth() {
		if(drawer.getEnergy() < 100)
			regen++;
		if(regen == 10)
		{
			drawer.setEnergy(drawer.getEnergy() + 1);
			//HeroEnergy+=100;
			regen = 0;
		}
	}
	
	public void checkHeroStatus() {
		String hsval = drawer.getStatusText();
		int dur = drawer.getDuration();
		switch (hsval) {
		case "boost":
			drawer.setSpeed(10);
			break;
		case "slow":
			drawer.setSpeed(2);
			break;
		case "stun":
			drawer.setSpeed(0);
			break;
		case "none":
			//do nothing---none
			break;
		case "hurt":
			drawer.loseHealth(1);
			break;
			//need to account for immunity and defend still?
		default:
			System.err.println("Unknown Hero Status in Momvement: " + hsval);
		}
		//Hero status
		if(dur > 0)
		{
			dur--;
			drawer.setStatus(dur + "." + hsval);
		}
		if(dur == 0)
			drawer.setStatus(".none"); 
			//TODO: if stacking statuses need to modify to not remove all statuses here.
	}
	
	public void spawnMinions() {
		Minion[] tempArray = minions;
		Minion[] tempArray2;
		int lifeCount = 0;
		
		try
		{
			for(Minion tMinion : tempArray)
				if(tMinion.getHealth() > 0)
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
	
	public void moveMinions() {
		try{
			for(Minion minion: minions)
			{
				String mStatus = minion.getStatusText();
				int mDuration = minion.getDuration();
				
				double minionSpeed = minion.getSpeed();
				if(mStatus.contains("slow"))
					minionSpeed /= 2;
				if(mStatus.contains("stun"))
					minionSpeed = 0;
				if(mStatus.contains("hurt"))
					minion.loseHealth(1);
				if(!mStatus.contains("none") && !mStatus.contains("immunity"))
				{
					mDuration--;
					if(mDuration == 0)
						minion.setStatus(".none");
					else
						minion.setStatus(mDuration + "." + mStatus);
				}
				if(minion.getLocationX() == drawer.getLocationX())
				{
					if(minion.getLocationY() > drawer.getLocationY())
						minion.addLocationY(-(minionSpeed));
					else if(minion.getLocationY() < drawer.getLocationY())
						minion.addLocationY(minionSpeed);
					minion.addLocationX(1);
				}
				else
				{
					int dir = 1;
					if(minion.getLocationX() > drawer.getLocationX())
						dir = -1;
					minion.addLocationX(dir*(Math.cos(minion.slope)*minionSpeed));
					minion.addLocationY(dir*(Math.sin(minion.slope)*minionSpeed));
				}
				
				double temp1, temp2;
				temp1 = Math.pow(minion.getLocationX() - drawer.getLocationX(),2);
				temp2 = Math.pow(minion.getLocationY() - drawer.getLocationY(),2);
				if(Math.sqrt(temp1 + temp2) <= 10)
					minionHit(minion);
				
				minion.draw(getGraphics(), drawer.getLocationX(), drawer.getLocationY(), minion);
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
				//TODO: Merge these into 1 and call ENEMY instead of each individual
				if(boss.fighting)
				{
					//collision detection
					double blastDis = Math.sqrt(
							Math.pow(boss.getLocationX() - blast.locationX, 2) 
							+ Math.pow(boss.getLocationY() - blast.locationY, 2));
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
						double blastDis = Math.sqrt(
								Math.pow(minion.getLocationX() - blast.locationX, 2) 
								+ Math.pow(minion.getLocationY() - blast.locationY, 2));
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
	
	public void minionHit(Minion minion)
	{
		int damage = minion.getDamage();
		if(drawer.getStatusText().contains("defend"))
			damage /= 2;
		drawer.loseHealth(damage);
		minion.setHealth(0);
		if(drawer.getStatus().equals(".none") && !minion.getEffect().contains("immunity"))
			drawer.setStatus(minion.getEffect());
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
			if(drawer.getEnergy() > powerSet[powerIndex].cost)
			{
				drawer.setHeroPosition(true);
				 drawer.setEnergy(drawer.getEnergy() - powerSet[powerIndex].cost);
				
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
		drawer.drawMelee(getGraphics(), drawer.getLocationX(), drawer.getLocationY(), powerSet[powerIndex]);
		double slope = Math.atan(
				(double)(mouseY - drawer.getLocationY())
				/
				(double)(mouseX - drawer.getLocationX()));
		
		int fistX, fistY;
		if(mouseX > drawer.getLocationX())
		{
			fistX = drawer.getLocationX() + (int)(Math.cos(slope)*25);
			fistY = drawer.getLocationY() + (int)(Math.sin(slope)*25);
		}
		else if(mouseX< drawer.getLocationX())
		{
			fistX = drawer.getLocationX() - (int)(Math.cos(slope)*25);
			fistY = drawer.getLocationY() - (int)(Math.sin(slope)*25);
		}
		else
			if(mouseY > drawer.getLocationY())
			{
				fistX = drawer.getLocationX();
				fistY = drawer.getLocationY() + 25;
			}
			else
			{
				fistX = drawer.getLocationX();
				fistY = drawer.getLocationY() - 25;
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
			Mine tempMine = new Mine(drawer.getLocationX(), drawer.getLocationY());
			Mine[] tempArray = new Mine[mines.length + 1];
			for(int i = 0; i < mines.length; i++)
				tempArray[i] = mines[i];
			tempArray[mines.length] = tempMine;
			mines = tempArray;
		}
		
		else if(power.contains("tele"))
		{
			drawer.setLocationX(mouseX);
			drawer.setLocationY(mouseY);
		}
		
		else if(power.contains("boost"))
		{
			drawer.setStatus("30.boost");
		}
		
		else if(power.contains("defend"))
		{
			drawer.setStatus(powerSet[powerIndex].effect);
		}else {
			System.err.println("Unknown Hero Power: " + power);
		}
	}
	
	public void doArea() {
		drawer.drawArea(getGraphics(), drawer.getLocationX(), drawer.getLocationY(), powerSet[powerIndex]);
		double temp1, temp2;
		if(boss.fighting)
		{
			temp1 = Math.pow(boss.getLocationX() - drawer.getLocationX(),  2);
			temp2 = Math.pow(boss.getLocationY() - drawer.getLocationY(),  2);
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
				temp1 = Math.pow(minion.getLocationX() - drawer.getLocationX(),  2);
				temp2 = Math.pow(minion.getLocationY() - drawer.getLocationY(),  2);
				if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].range*50)
				{
					minion.loseHealth(powerSet[powerIndex].damage);
					applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
				}
			}
		}
	}
	
	public void doBeam(int mouseX, int mouseY) {
		double slope = drawer.drawBeam(getGraphics(), drawer.getLocationX(), drawer.getLocationY(), powerSet[powerIndex]);
		System.out.println("Beam slope:" + slope);
		
		//TODO: should be able to clean this code up a lot. This should interact with Enemies and not minions and boss separately.
		//Still getting a horrible graphic bug where the Beam is not being rendered.
		
		if(boss.fighting)
		{
			double Eslope1, Eslope2, distance;
			distance = Math.sqrt(Math.pow(boss.getLocationY() - drawer.getLocationY(), 2) + Math.pow(boss.getLocationX() - drawer.getLocationX(), 2));
			
			if(distance <= powerSet[powerIndex].range*100)
			{
				if(mouseY > drawer.getLocationY())
				{
					Eslope1 = Math.atan(
							(boss.yPoints[1] - drawer.getLocationY())
							/
							(boss.xPoints[1] - drawer.getLocationX()));
					Eslope2 = Math.atan(
							(boss.yPoints[3] - drawer.getLocationY())
							/
							(boss.xPoints[3] - drawer.getLocationX()));
					
					//right
					if(mouseX > drawer.getLocationX() && boss.getLocationX() > drawer.getLocationX())
					{
						if(Eslope1 <= slope && Eslope2 >= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					//left
					else if(mouseX < drawer.getLocationX() && boss.getLocationX() < drawer.getLocationX())
					{
						if(Eslope1 >= slope && Eslope2 <= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					else {
						//System.out.println("[BEAM]- Y+ -BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
					}
				}
				else
				{
					Eslope1 = Math.atan(
							(drawer.getLocationY() - boss.yPoints[1])
							/
							(drawer.getLocationX() - boss.xPoints[1]));
					Eslope2 = Math.atan(
							(drawer.getLocationY() - boss.yPoints[3])
							/
							(drawer.getLocationX() - boss.xPoints[3]));
					
					//right
					if(mouseX> drawer.getLocationX() && boss.getLocationX() > drawer.getLocationX())
					{
						if(Eslope1 >= slope && Eslope2 <= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					//left
					else if(mouseX < drawer.getLocationX() && boss.getLocationX() < drawer.getLocationX())
					{
						if(Eslope1 <= slope && Eslope2 >= slope)
						{
							boss.loseHealth(powerSet[powerIndex].damage);
							bossEffectStat(powerSet[powerIndex].effect, boss);
						}
					}
					else {
						//System.out.println("[BEAM]- Y- -BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
					}
				}
			}
		}
		if(!boss.fighting)
		{
			double Eslope1, Eslope2, distance;
			for(Minion minion : minions)
			{
				distance = Math.sqrt(
						Math.pow(minion.getLocationY() - drawer.getLocationY(), 2) 
						+ Math.pow(minion.getLocationX() - drawer.getLocationX(), 2));
				
				if(distance <= powerSet[powerIndex].range*100)
				{
					if(mouseY > drawer.getLocationY())
					{
						Eslope1 = Math.atan(
								(minion.yPoints[1] - drawer.getLocationY())
								/
								(minion.xPoints[1] - drawer.getLocationX()));
						Eslope2 = Math.atan(
								(minion.yPoints[2] - drawer.getLocationY())
								/
								(minion.xPoints[2] - drawer.getLocationX()));
						
						//right
						if(mouseX > drawer.getLocationX() && minion.getLocationX() > drawer.getLocationX())
						{
							if(Eslope1 <= slope && Eslope2 >= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//left
						if(mouseX < drawer.getLocationX() && boss.getLocationX() < drawer.getLocationX())
						{
							if(Eslope1 >= slope && Eslope2 <= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//TODO: what happens when they're equal? can they ever be equal? 
						else {
							//System.out.println("[BEAM] - Y+ - NOT--BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
						}
					}
					else
					{
						Eslope1 = Math.atan(
								(drawer.getLocationY() - minion.yPoints[1])
								/
								(drawer.getLocationX() - minion.xPoints[1]));
						Eslope2 = Math.atan(
								(drawer.getLocationY() - minion.yPoints[2])
								/
								(drawer.getLocationX() - minion.xPoints[2]));
						
						//right
						if(mouseX > drawer.getLocationX() && minion.getLocationX() > drawer.getLocationX())
						{
							if(Eslope1 >= slope && Eslope2 <= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//left
						else if(mouseX < drawer.getLocationX() && minion.getLocationX() < drawer.getLocationX())
						{
							if(Eslope1 <= slope && Eslope2 >= slope)
							{
								minion.loseHealth(powerSet[powerIndex].damage);
								applyHeroEffectToMinion(powerSet[powerIndex].effect, minion);
							}
						}
						//TODO: what happens when they're equal? can they ever be equal? 
						else {
							//System.out.println("[BEAM] - Y- - NOT--BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
						}
					}
				}
			}
		}
	}
	
	public void doBlast(int mouseX, int mouseY) {
		double slope = Math.atan((double)(mouseY - drawer.getLocationY())/(double)(mouseX - drawer.getLocationX()));
		String dir = "";
		if(mouseX > drawer.getLocationX()) {
			dir = "right";
			//System.out.println("SHOOTING RIGHT: HeroLocX: " + drawer.getLocationX() + " Mouse LocX: " + mouseX);
		}else if(mouseX < drawer.getLocationX()) {
			dir = "left";
			//System.out.println("SHOOTING LEFT: HeroLocX: " + drawer.getLocationX() + " Mouse LocX: " + mouseX);
		}else { //equal (meaning center)
			//System.out.println("SHOOTING EQUAL: HeroLocX: " + drawer.getLocationX() + " Mouse LocX: " + mouseX);
			if(mouseY > drawer.getLocationY()) {
				dir = "down";
				//System.out.println("SHOOTING DOWN: HeroLocY: " + drawer.getLocationY() + " Mouse LocY: " + mouseY);
			}else{
				dir = "up";
				//System.out.println("SHOOTING UP: HeroLocY: " + drawer.getLocationY() + " Mouse LocY: " + mouseY);
			}
		}
		Blast tempBlast = 
				new Blast(slope, 
						dir, 
						powerSet[powerIndex].damage, 
						powerSet[powerIndex].range, 
						drawer.getLocationX(), 
						drawer.getLocationY(), 
						powerSet[powerIndex].color, 
						powerSet[powerIndex].effect);
		
		Blast[] tempArray = new Blast[blasts.length + 1];
		for(int i = 0; i < blasts.length; i++)
			tempArray[i] = blasts[i];
		tempArray[blasts.length] = tempBlast;
		blasts = tempArray;
	}
	
	//TODO: Combine apply boss and minion methods they are practically identical
	public void applyHeroEffectToMinion(String effect, Minion minion)
	{
		if(!minion.getStatus().equals(".immunity"))
		{
			//TODO: Prettify
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
	
				double slope = Math.atan(
						(double)(drawer.getLocationY() - minion.getLocationY())
						/
						(double)(drawer.getLocationX() - minion.getLocationX()));
				if(minion.getLocationX() > drawer.getLocationX())
				{
					minion.addLocationX(Math.cos(slope)*duration);
					minion.addLocationY(Math.sin(slope)*duration);
				}
				if(minion.getLocationX() < drawer.getLocationX())
				{
					minion.addLocationX(-(Math.cos(slope)*duration));
					minion.addLocationY(-(Math.sin(slope)*duration));
				}
				if(minion.getLocationX() == drawer.getLocationX())
				{
					if(minion.getLocationY() > drawer.getLocationY())
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
		//TODO: Prettify
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

			double slope = Math.atan(
					(double)(drawer.getLocationY() - boss.getLocationY())
					/
					(double)(drawer.getLocationX() - boss.getLocationX()));
			if(boss.getLocationX() > drawer.getLocationX())
			{
				boss.addLocationX(Math.cos(slope)*duration);
				boss.addLocationY(Math.sin(slope)*duration);
			}
			if(boss.getLocationX() < drawer.getLocationX())
			{
				boss.addLocationX(-(Math.cos(slope)*duration));
				boss.addLocationY(-(Math.sin(slope)*duration));
			}
			if(boss.getLocationX() == drawer.getLocationX())
			{
				if(boss.getLocationY() > drawer.getLocationY())
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
