import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class WorldSelector extends Frame implements ActionListener, MouseListener
{
	Panel main = new Panel();
	Panel wrdBtnPane = new Panel();
	Panel infoPane = new Panel();

	boolean[] unlocked;
	String mode;
	Boss boss = new Boss();
	
	Button[] wrdBtnArray = new Button[11];
	
	TextArea wrdInfo = new TextArea(20,75);
	TextArea bossInfo = new TextArea(20,75);
	
	Button backBtn = new Button("Back");
	
	Color btnColor[] = 
	{
		new Color(0, 250, 255),
		new Color(0, 0, 255),
		new Color(255, 0, 0),
		new Color(200, 200, 200),
		new Color(100, 100, 100),
		new Color(255, 255, 0),
		new Color(255, 200, 0),
		new Color(100, 155, 0),
		new Color(155, 100, 0),
		new Color(0, 255, 0),
		new Color(0, 0, 0)
	};
	
	public WorldSelector(String GameMode, boolean[] completed) throws IOException
	{
		unlocked = completed;
		mode = GameMode;
		
		add(main);
			main.setLayout(new BorderLayout());
		main.add(wrdBtnPane, BorderLayout.CENTER);
			wrdBtnPane.setLayout(new GridLayout(11,1));
			
		File file = new File("boss.txt");
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		reader.readLine();
		for(int i = 0; i < 11; i++)
		{
			reader.readLine();
			Button wrdBtn = new Button(reader.readLine());
			wrdBtnArray[i] = wrdBtn;
			wrdBtn.addActionListener(this);
			wrdBtn.addMouseListener(this);
			wrdBtnPane.add(wrdBtn);
			wrdBtn.setBackground(btnColor[i]);
			if(i == 10)
				wrdBtn.setForeground(Color.white);
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
		}
		reader.close();
		for(int i = 0; i < 10; i++)
		{
			if(unlocked[i])
				wrdBtnArray[i].enable(false);
			else
				wrdBtnArray[10].hide();
			if(unlocked[10])
				for(int a = 0; a <10; a++)
					wrdBtnArray[i].enable(true);
		}
		
		main.add(infoPane, BorderLayout.WEST);
			infoPane.setLayout(new BorderLayout());
		infoPane.add(wrdInfo, BorderLayout.NORTH);
		infoPane.add(bossInfo, BorderLayout.CENTER);
		infoPane.add(backBtn, BorderLayout.SOUTH);
			backBtn.addActionListener(this);

			
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});	
	}

	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == backBtn)
		{
			MainMenu window = new MainMenu(unlocked);
			window.setVisible(true);
			window.setResizable(false);
			window.setExtendedState(MAXIMIZED_BOTH);
			this.hide();
		}

		for(int i = 0; i < 11; i++)
		{
			if(e.getSource() == wrdBtnArray[i])
			{
				try{
					File file = new File("boss.txt");
					FileReader fr = new FileReader(file);
					BufferedReader reader = new BufferedReader(fr);
					reader.readLine();
					for(int z = -1; z < i; z++)
					{
						boss.bossName = reader.readLine();
						boss.world = reader.readLine();
						boss.bossHealth = Integer.parseInt(reader.readLine());
						boss.BossMaxHealth = boss.bossHealth;
						boss.bossDmg = Integer.parseInt(reader.readLine());
						boss.bossSpd = Integer.parseInt(reader.readLine());
						boss.bossEffect = reader.readLine();
						boss.bossColor = btnColor[z+1];
					}
					reader.close();
				}catch(IOException e1){
					e1.printStackTrace();
				}
				
				PowerSelector window;
				try{
					window = new PowerSelector(mode, unlocked, i, boss);
					window.setVisible(true);
					window.setResizable(false);
					window.setExtendedState(MAXIMIZED_BOTH);
				}catch(IOException e1){
					e1.printStackTrace();
				}
				this.hide();
			}
		}
	}


	public void mouseEntered(MouseEvent e) 
	{
		for(int i = 0; i < 11; i++)
			if(e.getSource() == wrdBtnArray[i])
				try{
					File file = new File("boss.txt");
					FileReader fr = new FileReader(file);
					BufferedReader reader = new BufferedReader(fr);
					reader.readLine();
					for(int z = -1; z < i; z++)
					{
						String name, hp, dmg, spd, eff;
						
						name = reader.readLine();
						reader.readLine();
						hp = reader.readLine();
						dmg = reader.readLine();
						spd = reader.readLine();
						eff = reader.readLine();
						
						bossInfo.setText("Name: " + name + "\nHealth: " + hp + "\nPower: " + dmg + "\nSpeed: " + spd + "\nEffect: " + eff);
					}
					reader.close();
				}catch(IOException e1){
					e1.printStackTrace();
				}
	}


	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}

	public void mouseClicked(MouseEvent arg0) {}

}
