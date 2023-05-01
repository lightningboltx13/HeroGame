import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MemoryMenu extends Frame implements ActionListener, FocusListener
{
	boolean[] unlocked = new boolean[11];
	boolean svLd; //true = save, false = load;
	
	Panel main = new Panel();
	Panel slotPane = new Panel();
	Panel btnPane = new Panel();
	
	File[] slots = {
			new File("slot1.txt"),
			new File("slot2.txt"),
			new File("slot3.txt")
	};
	
	TextArea[] memorySlots = {
			new TextArea(),
			new TextArea(),
			new TextArea()
	};
	
	Button svLdBtn = new Button();
	Button backBtn = new Button("Back");
	
	
	public MemoryMenu(boolean mode, boolean[] completed) throws IOException
	{
		svLd= mode;
		if(svLd)
			unlocked = completed;
		
		add(main);
		
		main.add(slotPane);
		
		slotDisplay();
		
		main.add(btnPane);
		
		btnPane.add(svLdBtn);
		if(svLd)
			svLdBtn.setLabel("Save");
		else
			svLdBtn.setLabel("Load");
		svLdBtn.addActionListener(this);
		
		btnPane.add(backBtn);
			backBtn.addActionListener(this);
		
		
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	public void slotDisplay() throws IOException
	{
		for(int i = 0; i < 3; i++)
		{
			slotPane.add(memorySlots[i]);
			memorySlots[i].addFocusListener(this);
			FileReader fr = new FileReader(slots[i]);
			BufferedReader reader = new BufferedReader(fr);
			int count = 0; 
			for(int a = 0; a < 13; a++)
			{
				if(a < 11)
				{
					if(Boolean.parseBoolean(reader.readLine()))
						count++;
				}
				else
				{
					memorySlots[i].setText("Adventure: " + (double)((count*100)/11) + "%\nSurvival: " + reader.readLine() + "\nBoss Battle: " + reader.readLine());
				}
			}
			reader.close();
		}
		
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == svLdBtn)
		{
			if(svLd)
			{
				for(int i = 0; i < 3; i++)
				{
					if(memorySlots[i].getBackground() == Color.blue)
					{
						try
						{
							FileWriter writer = new FileWriter(slots[i]);
							for(int a = 0; a < 13; a++)
							{
								if(a < 11)
								{
									if(unlocked[a])
										writer.write("true\n");
									else
										writer.write("false\n");
								}
								else
								{
									//survival, boss battle stuff
									writer.write("00.00\n");
								}
							}
							writer.close();
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
						try {
							slotDisplay();
						} catch (IOException e1){
							e1.printStackTrace();
						}
					}
				}
			}
			else //svLd
			{
				//TODO: maybe iterate thru slots and memory slots instead?
				for(int i = 0; i < 3; i++)
				{
					if(memorySlots[i].getBackground() == Color.blue)
					{
						try
						{
							FileReader fr = new FileReader(slots[i]);
							BufferedReader reader = new BufferedReader(fr);
							for(int a = 0; a < 11; a++)
							{
								unlocked[a] = Boolean.parseBoolean(reader.readLine());
							}
							reader.close();
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
						
						MainMenu window = new MainMenu(unlocked);
						window.setVisible(true);
						window.setResizable(false);
						window.setExtendedState(MAXIMIZED_BOTH);
						this.hide();
					}
				}
			}
		}
		
		if(e.getSource() == backBtn)
		{
			if(svLd)
			{
				MainMenu window = new MainMenu(unlocked);
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
				this.hide();
			}
			else
			{
				StartMenu window = new StartMenu();
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
				this.hide();
			}
		}
	}
	@Override
	public void focusGained(FocusEvent e) 
	{
		for(int i = 0; i < 3; i++)
		{
			memorySlots[i].setBackground(Color.white);
			if(e.getSource() == memorySlots[i])
			{
				memorySlots[i].setBackground(Color.blue);
			}
		}
	}

	public void focusLost(FocusEvent arg0) {}
}
