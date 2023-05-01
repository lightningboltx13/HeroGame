import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class PowerSelector extends Frame implements ActionListener, MouseListener
{

	boolean[] unlocked;
	int world;
	String mode;
	Boss boss;
	
	Power[] powerSet = 
	{
		new Power(),
		new Power(),
		new Power()
	};
	
	File file = new File("powers.txt");
	
	

	Panel main = new Panel();
	
	Panel pwrBtnPane = new Panel();
	Button[] pwrBtn = new Button[36];
	String[] pwrName = new String[36];

	Panel minor = new Panel();
	
	Panel pwrSlotPane = new Panel();
	
	TextField[] pwrSlot = new TextField[3];
	Button[] removePwrBtn = new Button[3];
	
	TextArea pwrInfo = new TextArea(20,75);
	
	Panel btnPane = new Panel();
	Button backBtn = new Button("Back");
	Button startBtn = new Button("Start");
	
	private final Color btnColor[] = 
	{
		new Color(200, 0, 150),
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
	
	public PowerSelector(String GameMode, boolean[] completed, int place, Boss badGuy) throws IOException
	{
		unlocked = completed;
		world = place;
		mode = GameMode;
		boss = badGuy;
		
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		reader.readLine();
		for(int i = 0; i < 36; i++)
		{
			pwrName[i] = reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
		}
		reader.close();
		
		add(main);
			main.setLayout(new BorderLayout());
		main.add(pwrBtnPane, BorderLayout.EAST);
			pwrBtnPane.setLayout(new GridLayout(12,3,5,5));
		for(int i = 0; i < 36; i++)
		{
			Button tempBtn = new Button(pwrName[i]);
			tempBtn.setBackground(btnColor[i/3]);
			if(i >= 33)
				tempBtn.setForeground(Color.white);
			pwrBtn[i] = tempBtn;
			pwrBtnPane.add(tempBtn);
			tempBtn.addActionListener(this);
			tempBtn.addMouseListener(this);
		}
		for(int i = 0; i < 11; i++)
		{
			if(unlocked[i] == false)
			{
				pwrBtn[(i*3)+3].hide();
				pwrBtn[(i*3)+4].hide();
				pwrBtn[(i*3)+5].hide();
			}
		}
		
		main.add(minor);
			minor.setLayout(new BorderLayout(10,10));
		minor.add(pwrSlotPane, BorderLayout.NORTH);
			pwrSlotPane.setLayout(new GridLayout(3,1,10,10));
			for(int i = 0; i < 3; i++)
			{
				Panel tempPane = new Panel();
				pwrSlotPane.add(tempPane);
				Button tempBtn = new Button("Remove");
				pwrSlotPane.add(tempBtn);
				removePwrBtn[i] = tempBtn;
					tempBtn.addActionListener(this);
				TextField tempField = new TextField();
					tempField.setText("");
				pwrSlotPane.add(tempField);
				pwrSlot[i] = tempField;
				tempField.setEnabled(false);
			}
		minor.add(pwrInfo, BorderLayout.CENTER);
		
		minor.add(btnPane, BorderLayout.SOUTH);
		btnPane.add(backBtn);
			backBtn.addActionListener(this);
		btnPane.add(startBtn);
			startBtn.addActionListener(this);
		

		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});	
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == backBtn)
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
			this.hide();
		}
		if(e.getSource() == startBtn)
		{
			boolean ready = true;
			for(int a = 0; a < 3; a++)
				if(pwrSlot[a].getText().equals(""))
					ready = false;
			if(ready)
			{
				BattleMap window = new BattleMap(mode, unlocked, world, powerSet, boss);
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
				window.setBackground(Color.black);
			}
		}
		
		for(int i = 0; i < 3; i++)
			if(e.getSource() == removePwrBtn[i])
			{
				powerSet[i] = new Power();
				for(int z = 0; z < 36; z++)
					if(!pwrBtn[z].isEnabled())
					{
						pwrBtn[z].setEnabled(true);
						if(!pwrBtn[z].getLabel().equals(pwrSlot[i].getText()))
							pwrBtn[z].setEnabled(false);
					}
				pwrSlot[i].setText("");
			}
		for(int i = 0; i < 36; i++)
		{
			if(e.getSource() == pwrBtn[i])
			{
				for(int a = 0; a < 3; a++)
				{
					if(pwrSlot[a].getText().equals(""))
					{
						try{

							FileReader fr = new FileReader(file);
							BufferedReader reader = new BufferedReader(fr);
							reader.readLine();
							for(int z = 0; z <= i; z++)
							{
								powerSet[a].name = reader.readLine();
									pwrSlot[a].setEnabled(true);
									pwrSlot[a].setText(powerSet[a].name);
									pwrSlot[a].setEnabled(false);
								powerSet[a].damage = Integer.parseInt(reader.readLine());
								powerSet[a].range = Integer.parseInt(reader.readLine());
								powerSet[a].effect = reader.readLine();
								powerSet[a].cost = Integer.parseInt(reader.readLine());
								powerSet[a].shape = reader.readLine();
								
								String clrStr, whichClr = "red";
								int red = 0, green = 0, blue = 0;
								clrStr = reader.readLine();
								for(int x = 0; x < clrStr.length(); x++)
								{
									if(clrStr.charAt(x) == '.')
									{
										if(whichClr.equals("red"))
											whichClr = "green";
										else if(whichClr.equals("green"))
											whichClr = "blue";
									}
									else
									{
										if(whichClr.equals("red"))
											red = (red * 10) + Integer.parseInt(clrStr.charAt(x) + "");
										if(whichClr.equals("green"))
											green = (green * 10) + Integer.parseInt(clrStr.charAt(x) + "");
										if(whichClr.equals("blue"))
											blue = (blue * 10) + Integer.parseInt(clrStr.charAt(x) + "");
									}
								}
								powerSet[a].color = new Color(red, green, blue);
							}
							reader.close();
							pwrBtn[i].setEnabled(false);
							break;
						}catch (IOException e1)
						{
							e1.printStackTrace();
						}
					}
				}
				break;
			}
		}
	}
	
	
	public void mouseEntered(MouseEvent e) 
	{
		for(int i = 0; i < 35; i++)
		{
			if(e.getSource() == pwrBtn[i])
			{
				try{

					FileReader fr = new FileReader(file);
					BufferedReader reader = new BufferedReader(fr);
					reader.readLine();
					for(int z = 0; z <= i; z++)
					{
						pwrInfo.setText("Name: " + reader.readLine() + "\nDamage: " + reader.readLine() + "\nRange: " + reader.readLine() + "\nEffect: " + reader.readLine() + "\nCost: " + reader.readLine() + "\nShape: " + reader.readLine());
						reader.readLine();
					}
					reader.close();
				}catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

	
	
	
}
