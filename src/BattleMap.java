import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

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
					if(!(boss.bossLocX == HeroLocX))
					{
						boss.tempSlope = Math.atan((boss.bossLocY - HeroLocY)/(boss.bossLocX - HeroLocX));
						if(HeroLocX < boss.bossLocX)
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
				
				double Bspeed = boss.bossSpd;
				if(boss.bossStatus.contains("slow"))//slow
					Bspeed /= 2;
				else if(boss.bossStatus.contains("stun"))//stun
					Bspeed /= 4;
				

				//System.out.println(Bright);
				//System.out.println(boss.moveCount);
				if(Bright)
				{
					boss.bossLocY += Math.sin(boss.tempSlope)*Bspeed;
					boss.bossLocX += Math.cos(boss.tempSlope)*Bspeed;
				}
				else
				{
					boss.bossLocY -= Math.sin(boss.tempSlope)*Bspeed;
					boss.bossLocX -= Math.cos(boss.tempSlope)*Bspeed;
				}
				
				boss.attack--;
				
				//boss attack
				int atkTime = 50;
				if(boss.attack <= 0)
					atkTime = 10;
				if(boss.attack%atkTime == 0)
				{
					boss.drawArea(getGraphics(), boss.bossLocX, boss.bossLocY, boss);
					double temp1, temp2;
					temp1 = Math.pow(boss.bossLocX - HeroLocX, 2);
					temp2 = Math.pow(boss.bossLocY - HeroLocY, 2);
					if(Math.sqrt(temp1 + temp2) <=75)
					{
						int damage = boss.bossDmg;
						if(heroStatus.contains(".defend"))
							damage /= 2;
						HeroHealth -= damage;
						
						if(HeroStatus.equals(".none"))
							if(boss.bossEffect.equals("20.knockback"))
							{
								if(HeroLocX > boss.bossLocX)
								{
									HeroLocX += Math.cos(boss.tempSlope)*200;
									HeroLocY += Math.sin(boss.tempSlope)*200;
								}
								else if(HeroLocX < boss.bossLocX)
								{
									HeroLocX -= Math.cos(boss.tempSlope)*200;
									HeroLocY -= Math.sin(boss.tempSlope)*200;
								}
								else
								{
									if(HeroLocY > boss.bossLocY)
										HeroLocY += 200;
									else
										HeroLocY -= 200;
								}
							}
							else
								HeroStatus = boss.bossEffect;
						boss.attack = 150;
					}
				}
				
				//boss status
				statusRead = true;
				String bossStatus = "";
				duration = 0;
				System.out.println("Boss Status: " + boss.bossStatus);
				for(int i = 0; i < boss.bossStatus.length(); i++)
				{
					if(boss.bossStatus.charAt(i) == '.')
						statusRead = false;
					else if(statusRead)
						duration = (duration * 10) + Integer.parseInt(boss.bossStatus.charAt(i) + "");
					else if(!statusRead)
						bossStatus = bossStatus + boss.bossStatus.charAt(i);
				}
				
				if(duration > 0)
				{
					duration--;
					if(bossStatus.equals("hurt"))
						boss.bossHealth--;
					boss.bossStatus = duration + "." + bossStatus;
				}
				else if(duration == 0)
					boss.bossStatus = ".none";
				
				//boss drawer
				boss.drawBoss(getGraphics(), HeroLocX, HeroLocY, boss);
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
			drawer.drawBars(getGraphics(), HeroHealth, HeroEnergy);
			drawer.drawTest(getGraphics(), powerSet[powerIndex].Name);

			
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
			
			if(boss.bossHealth <= 0)
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
			timer.schedule(new testEvent(), 60);
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
				if(tempArray[i].HP > 0)
					lifeCount++;
		}catch (NullPointerException ex){}
		
		if(minionCount > 0)
			tempArray2 = new Minion[lifeCount + 1];
		else
			tempArray2 = new Minion[lifeCount];
		
		try{
			int count = 0;
			for(int i = 0; i < tempArray.length; i++)
				if(tempArray[i].HP > 0)
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
			for(int i = 0; i < minions.length; i++)
			{
				boolean EstatusRead = true;
				String Estatus = "";
				int Eduration = 0;
				for(int a = 0; a < minions[i].status.length(); a++)
				{
					if(minions[i].status.charAt(a) == '.')
						EstatusRead = false;
					else if(EstatusRead)
						Eduration = (Eduration * 10) + Integer.parseInt(minions[i].status.charAt(a) + "");
					else if(!EstatusRead)
						Estatus = Estatus + minions[i].status.charAt(a);
				}
				
				double minionSpeed = minions[i].Spd;
				if(Estatus.contains("slow"))
					minionSpeed /= 2;
				if(Estatus.contains("stun"))
					minionSpeed = 0;
				if(Estatus.contains("hurt"))
					minions[i].HP--;
				if(!Estatus.contains("none") && !Estatus.contains("immunity"))
				{
					Eduration--;
					if(Eduration == 0)
						minions[i].status = ".none";
					else
						minions[i].status = Eduration + "." + Estatus;
				}
				if(minions[i].locX == HeroLocX)
				{
					if(minions[i].locY > HeroLocY)
						minions[i].locY -= minionSpeed;
					else if(minions[i].locY < HeroLocY)
						minions[i].locY += minionSpeed;
					minions[i].locX++;
				}
				else
				{
					int dir = 1;
					if(minions[i].locX > HeroLocX)
						dir = -1;
					minions[i].locX = (int)(minions[i].locX + dir*(Math.cos(minions[i].slope)*minionSpeed));
					minions[i].locY = (int)(minions[i].locY + dir*(Math.sin(minions[i].slope)*minionSpeed));
				}
				
				double temp1, temp2;
				temp1 = Math.pow(minions[i].locX - HeroLocX,2);
				temp2 = Math.pow(minions[i].locY - HeroLocY,2);
				if(Math.sqrt(temp1 + temp2) <= 10)
					minionHit(minions[i], i, status);
				
				minions[i].draw(getGraphics(), HeroLocX, HeroLocY, minions[i]);
			}
		}catch (NullPointerException ex){}
	}
	public void thenIStartedBlasting() {
		try{
			//Blast Movement, collision, and end of life
			for(int i = 0; i < blasts.length; i++)
			{
				if(blasts[i].direction.equals("right"))
				{
					blasts[i].LocX += Math.cos(blasts[i].Slope)*5;
					blasts[i].LocY += Math.sin(blasts[i].Slope)*5;
				}
				else if(blasts[i].direction.equals("left"))
				{
					blasts[i].LocX -= Math.cos(blasts[i].Slope)*5;
					blasts[i].LocY -= Math.sin(blasts[i].Slope)*5;
				}
				else if(blasts[i].direction.equals("up"))
				{
					blasts[i].LocY -= 5;
				}
				else if(blasts[i].direction.equals("down"))
				{
					blasts[i].LocY += 5;
				}
				
				if(boss.fighting)
				{
					//collision detection
					double blastDis = Math.sqrt(Math.pow(boss.bossLocX - blasts[i].LocX, 2) + Math.pow(boss.bossLocY - blasts[i].LocY, 2));
					if(blastDis <= 10)
					{
						blasts[i].hit = true;
						boss.bossHealth -= blasts[i].Dmg;
						bossEffectStat(powerSet[powerIndex].Effect, boss);
					}
				}
				else
				{
					for(int a = 0; a < minions.length; a++)
					{
						double blastDis = Math.sqrt(Math.pow(minions[a].locX - blasts[i].LocX, 2) + Math.pow(minions[a].locY - blasts[i].LocY, 2));
						if(blastDis <= 5)
						{
							blasts[i].hit = true;
							minions[a].HP -= blasts[i].Dmg;
							applyHeroEffectToMinion(blasts[i].effect, minions[a]);
						}
					}
				}
				
				double temp1, temp2;
				temp1 = Math.pow(blasts[i].LocX - blasts[i].srtX, 2);
				temp2 = Math.pow(blasts[i].LocY - blasts[i].srtY, 2);
				double travled = Math.sqrt(temp1 + temp2);
				if(travled >= blasts[i].Range*50)
					blasts[i].hit = true;
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
				for(int i = 0; i < minions.length; i++)
				{
					try{
						for(int m = 0; m < mines.length; m++)
						{
							//enemies is null!!!
							mine1 = Math.pow(minions[i].locX - mines[m].locX, 2);
							mine2 = Math.pow(minions[i].locY - mines[m].locY, 2);
							if(Math.sqrt(mine1 + mine2) <= 10)
							{
								minions[i].HP -= 10;
								mines[m].exploded = true;
							}
							else
								mineCount++;
						}
					}catch (NullPointerException ex){}
				}
			}
			else
			{
				for(int m = 0; m < mines.length; m++)
				{
					mine1 = Math.pow(boss.bossLocX - mines[m].locX, 2);
					mine2 = Math.pow(boss.bossLocY - mines[m].locY, 2);
					if(Math.sqrt(mine1 + mine2) <= 10)
					{
						boss.bossHealth -= 10;
						mines[m].exploded = true;
					}
					else
						mineCount++;
				}
			}
			Mine[] tempArray = new Mine[mineCount];
			int tempCount = 0;
			for(int m = 0; m < mines.length; m++)
			{
				try{
					if(!mines[m].exploded)
					{
						//TODO: Might have an array index out of bounds, need to look into
						//System.out.println("mineCount: " + mineCount);
						//System.out.println("tempArray Index: " + tempCount);
						//System.out.println("mines Index: " + m);
						tempArray[tempCount] = mines[m];
						tempCount++;
					}
				}catch (NullPointerException ex){}
			}
			mines = tempArray;
		}
		
		//draw mines
		try{
			for(int m = 0; m < mines.length; m++)
				mines[m].drawMine(getGraphics(),  mines[m]);
		}catch (NullPointerException ex){}
		
	}

	public void close()
	{
		this.hide();
	}
	
	public void minionHit(Minion minion, int index, String heroStatus)
	{
		int damage = minion.Dmg;
		if(heroStatus.contains("defend"))
			damage /= 2;
		HeroHealth -= damage;
		minions[index].HP = 0;
		if(HeroStatus.equals(".none") && !minion.effect.contains("immunity"))
			HeroStatus = minion.effect;
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
			if(HeroEnergy > powerSet[powerIndex].Cost)
			{
				HeroPosition = true;
				HeroEnergy -= powerSet[powerIndex].Cost;
				
				switch(powerSet[powerIndex].Shape) {
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
					System.err.println("Unknown Power: " + powerSet[powerIndex].Shape);
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
			temp1 = Math.pow(boss.bossLocX - fistX,  2);
			temp2 = Math.pow(boss.bossLocY - fistY,  2);
			if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].Range*10)
			{
				boss.bossHealth -= powerSet[powerIndex].Dmg;
				bossEffectStat(powerSet[powerIndex].Effect, boss);
			}
		}
		else
		{
			for(int i = 0; i < minions.length; i++)
			{
				temp1 = Math.pow(minions[i].locX - fistX,  2);
				temp2 = Math.pow(minions[i].locY - fistY,  2);
				if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].Range*10)
				{
					minions[i].HP -= powerSet[powerIndex].Dmg;
					applyHeroEffectToMinion(powerSet[powerIndex].Effect, minions[i]);
				}
			}
		}
	}
	
	public void doSelf(int mouseX, int mouseY) {
		boolean powerRead = false;
		String power = "";
		for(int i = 0; i < powerSet[powerIndex].Effect.length(); i++)
		{
			if(powerSet[powerIndex].Effect.charAt(i) == '.')
				powerRead = true;
			else if(powerRead)
				power = power + powerSet[powerIndex].Effect.charAt(i);
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
			HeroStatus = powerSet[powerIndex].Effect;
		}else {
			System.err.println("Unknown Hero Power: " + power);
		}
	}
	
	public void doArea() {
		drawer.drawArea(getGraphics(), HeroLocX, HeroLocY, powerSet[powerIndex]);
		double temp1, temp2;
		if(boss.fighting)
		{
			temp1 = Math.pow(boss.bossLocX - HeroLocX,  2);
			temp2 = Math.pow(boss.bossLocY - HeroLocY,  2);
			if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].Range*50)
			{
				boss.bossHealth -= powerSet[powerIndex].Dmg;
				bossEffectStat(powerSet[powerIndex].Effect, boss);
			}
		}
		else
		{
			for(int i = 0; i < minions.length; i++)
			{
				temp1 = Math.pow(minions[i].locX - HeroLocX,  2);
				temp2 = Math.pow(minions[i].locY - HeroLocY,  2);
				if(Math.sqrt(temp1 + temp2) <= powerSet[powerIndex].Range*50)
				{
					minions[i].HP -= powerSet[powerIndex].Dmg;
					applyHeroEffectToMinion(powerSet[powerIndex].Effect, minions[i]);
				}
			}
		}
	}
	
	public void doBeam(int mouseX, int mouseY) {
		double slope = drawer.drawBeam(getGraphics(), HeroLocX, HeroLocY, powerSet[powerIndex]);
		
		if(boss.fighting)
		{
			double Eslope1, Eslope2, distance;
			distance = Math.sqrt(Math.pow(boss.bossLocY - HeroLocY, 2) + Math.pow(boss.bossLocX - HeroLocX, 2));
			
			if(distance <= powerSet[powerIndex].Range*100)
			{
				if(mouseY > HeroLocY)
				{
					Eslope1 = Math.atan(((double)boss.yPoints[1] - HeroLocY)/((double)boss.xPoints[1] - HeroLocX));
					Eslope2 = Math.atan(((double)boss.yPoints[3] - HeroLocY)/((double)boss.xPoints[3] - HeroLocX));
					
					//right
					if(mouseX > HeroLocX && boss.bossLocX > HeroLocX)
					{
						if(Eslope1 <= slope && Eslope2 >= slope)
						{
							boss.bossHealth -= powerSet[powerIndex].Dmg;
							bossEffectStat(powerSet[powerIndex].Effect, boss);
						}
					}
					//left
					else if(mouseX < HeroLocX && boss.bossLocX < HeroLocX)
					{
						if(Eslope1 >= slope && Eslope2 <= slope)
						{
							boss.bossHealth -= powerSet[powerIndex].Dmg;
							bossEffectStat(powerSet[powerIndex].Effect, boss);
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
					if(mouseX> HeroLocX && boss.bossLocX > HeroLocX)
					{
						if(Eslope1 >= slope && Eslope2 <= slope)
						{
							boss.bossHealth -= powerSet[powerIndex].Dmg;
							bossEffectStat(powerSet[powerIndex].Effect, boss);
						}
					}
					//left
					else if(mouseX < HeroLocX && boss.bossLocX < HeroLocX)
					{
						if(Eslope1 <= slope && Eslope2 >= slope)
						{
							boss.bossHealth -= powerSet[powerIndex].Dmg;
							bossEffectStat(powerSet[powerIndex].Effect, boss);
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
			for(int i = 0; i < minions.length; i++)
			{
				distance = Math.sqrt(Math.pow(minions[i].locY - HeroLocY, 2) + Math.pow(minions[i].locX - HeroLocX, 2));
				
				if(distance <= powerSet[powerIndex].Range*100)
				{
					if(mouseY > HeroLocY)
					{
						Eslope1 = Math.atan(((double)minions[i].yPoints[1] - HeroLocY)/((double)minions[i].xPoints[1] - HeroLocX));
						Eslope2 = Math.atan(((double)minions[i].yPoints[2] - HeroLocY)/((double)minions[i].xPoints[2] - HeroLocX));
						
						//right
						if(mouseX > HeroLocX && minions[i].locX > HeroLocX)
						{
							if(Eslope1 <= slope && Eslope2 >= slope)
							{
								minions[i].HP -= powerSet[powerIndex].Dmg;
								applyHeroEffectToMinion(powerSet[powerIndex].Effect, minions[i]);
							}
						}
						//left
						if(mouseX < HeroLocX && boss.bossLocX < HeroLocX)
						{
							if(Eslope1 >= slope && Eslope2 <= slope)
							{
								minions[i].HP -= powerSet[powerIndex].Dmg;
								applyHeroEffectToMinion(powerSet[powerIndex].Effect, minions[i]);
							}
						}
						//TODO: what happens when they're equal? can they ever be equal? 
						else {
							System.out.println("[BEAM] - Y+ - NOT--BOSS FIGHTING -- HERO LOC AND MOUSE ARE EQUAL");
						}
					}
					else
					{
						Eslope1 = Math.atan((HeroLocY - (double)minions[i].yPoints[1])/(HeroLocX - (double)minions[i].xPoints[1]));
						Eslope2 = Math.atan((HeroLocY - (double)minions[i].yPoints[2])/(HeroLocX - (double)minions[i].xPoints[2]));
						
						//right
						if(mouseX > HeroLocX && minions[i].locX > HeroLocX)
						{
							if(Eslope1 >= slope && Eslope2 <= slope)
							{
								minions[i].HP -= powerSet[powerIndex].Dmg;
								applyHeroEffectToMinion(powerSet[powerIndex].Effect, minions[i]);
							}
						}
						//left
						else if(mouseX < HeroLocX && minions[i].locX < HeroLocX)
						{
							if(Eslope1 <= slope && Eslope2 >= slope)
							{
								minions[i].HP -= powerSet[powerIndex].Dmg;
								applyHeroEffectToMinion(powerSet[powerIndex].Effect, minions[i]);
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
			if(mouseY < HeroLocY) {
				dir = "down";
				System.out.println("SHOOTING DOWN: HeroLocY: " + HeroLocY + " Mouse LocY: " + mouseY);
			}else if(mouseY > HeroLocY) {
				dir = "up";
				System.out.println("SHOOTING UP: HeroLocY: " + HeroLocY + " Mouse LocY: " + mouseY);
			}
		}
		Blast tempBlast = new Blast(slope, dir, powerSet[powerIndex].Dmg, powerSet[powerIndex].Range, HeroLocX, HeroLocY, powerSet[powerIndex].color, powerSet[powerIndex].Effect);
		Blast[] tempArray = new Blast[blasts.length + 1];
		for(int i = 0; i < blasts.length; i++)
			tempArray[i] = blasts[i];
		tempArray[blasts.length] = tempBlast;
		blasts = tempArray;
	}
			
	public void applyHeroEffectToMinion(String effect, Minion minion)
	{
		if(!minion.status.equals(".immunity"))
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
	
				double slope = Math.atan((double)(HeroLocY - minion.locY)/(double)(HeroLocX - minion.locX));
				if(minion.locX > HeroLocX)
				{
					minion.locX += Math.cos(slope)*duration;
					minion.locY += Math.sin(slope)*duration;
				}
				if(minion.locX < HeroLocX)
				{
					minion.locX -= Math.cos(slope)*duration;
					minion.locY -= Math.sin(slope)*duration;
				}
				if(minion.locX == HeroLocX)
				{
					if(minion.locY > HeroLocY)
						minion.locY += Math.sin(slope)*duration;
					else
						minion.locY -= Math.sin(slope)*duration;
				}
			}
			else
			{
				minion.status = effect;
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

			double slope = Math.atan((double)(HeroLocY - boss.bossLocY)/(double)(HeroLocX - boss.bossLocX));
			if(boss.bossLocX > HeroLocX)
			{
				boss.bossLocX += Math.cos(slope)*duration;
				boss.bossLocY += Math.sin(slope)*duration;
			}
			if(boss.bossLocX < HeroLocX)
			{
				boss.bossLocX -= Math.cos(slope)*duration;
				boss.bossLocY -= Math.sin(slope)*duration;
			}
			if(boss.bossLocX == HeroLocX)
			{
				if(boss.bossLocY > HeroLocY)
					boss.bossLocY += Math.sin(slope)*duration;
				else
					boss.bossLocY -= Math.sin(slope)*duration;
			}
		}
		else
		{
			boss.bossStatus = effect;
		}
	}
	
	public void focusGained(FocusEvent e)
	{
		timer = new Timer();
		timer.schedule(new testEvent(), 60);
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
