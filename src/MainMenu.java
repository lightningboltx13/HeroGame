import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class MainMenu extends Frame implements ActionListener 
{
	Panel main = new Panel();
	Panel buttonPane = new Panel();
	boolean[] unlocked;

	private static String GUIspace = "                                                                    ";
	
	Button saveBtn = new Button("Save Game");
	Button recordBtn = new Button("Records");
	Button exitBtn = new Button("Exit Game");
	Button advBtn = new Button("Adventure");
	Button surBtn = new Button("Survival");
	Button bbBtn = new Button("Boss Battle");
	
	
	public MainMenu(boolean[] completed)
	{
		unlocked = completed;
		add(main);
			main.setLayout(new BorderLayout());
		main.add(buttonPane, BorderLayout.CENTER);
			buttonPane.setLayout(new GridLayout(3,2,100,100));
		buttonPane.add(saveBtn);
			saveBtn.addActionListener(this);
		buttonPane.add(advBtn);
			advBtn.addActionListener(this);
		buttonPane.add(recordBtn);
			recordBtn.addActionListener(this);
		buttonPane.add(surBtn);
			surBtn.addActionListener(this);
		buttonPane.add(exitBtn);
			exitBtn.addActionListener(this);
		buttonPane.add(bbBtn);
			bbBtn.addActionListener(this);
		
			
		if(unlocked[10] == false)
		{
			surBtn.hide();
			bbBtn.hide();
		}

		main.add(new Label(GUIspace), BorderLayout.NORTH);
		main.add(new Label(GUIspace), BorderLayout.SOUTH);
		main.add(new Label(GUIspace), BorderLayout.EAST);
		main.add(new Label(GUIspace), BorderLayout.WEST);
		
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == saveBtn)
		{
			try {
				MemoryMenu window = new MemoryMenu(true, unlocked);
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
				this.hide();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	
		if(e.getSource() == recordBtn)
		{
			
		}
		
		if(e.getSource() == exitBtn)
		{
			this.hide();
		}
		
		if(e.getSource() == advBtn)
		{
			WorldSelector window;
			try {
				window = new WorldSelector("Adventure", unlocked);
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
			} catch (IOException e1){
				e1.printStackTrace();
			}
			this.hide();
		}
		
		if(e.getSource() == surBtn)
		{
			PowerSelector window;
			try {
				window = new PowerSelector("Survival", unlocked, 0, null);
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
			} catch (IOException e1){
				e1.printStackTrace();
			}
			this.hide();
		}
		
		if(e.getSource() == bbBtn)
		{
			PowerSelector window;
			try {
				window = new PowerSelector("BossBattle", unlocked, 0, null);
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
			} catch (IOException e1){
				e1.printStackTrace();
			}
			this.hide();
		}
	}
}
