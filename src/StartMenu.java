import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class StartMenu extends Frame implements ActionListener 
{

	Panel main = new Panel();
	Panel buttonPane = new Panel();
	
	Label Title = new Label("       HERO II");
	
	Button newGame = new Button("New Game");
	Button loadGame = new Button("Load Game");
	
	public StartMenu()
	{
		add(main);
			main.setLayout(new BorderLayout());
		main.add(Title, BorderLayout.NORTH);
			Title.setFont(new Font("sansserif", Font.BOLD, 256));
		main.add(buttonPane, BorderLayout.CENTER);
		buttonPane.add(newGame);
			newGame.addActionListener(this);
		buttonPane.add(loadGame);
			loadGame.addActionListener(this);
			
			
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
	}
	
	public static void main(String[] args) 
	{
		StartMenu window = new StartMenu();
		window.setVisible(true);
		window.setResizable(false);
		window.setExtendedState(MAXIMIZED_BOTH);
		

	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == newGame)
		{
			boolean[] unlocked = {false,false,false,false,false,false,false,false,false,false,false};
			//boolean[] unlocked = {true,true,true,true,true,true,true,true,true,true,true}; 
			
			MainMenu window = new MainMenu(unlocked);
			window.setVisible(true);
			window.setResizable(false);
			window.setExtendedState(MAXIMIZED_BOTH);
			this.hide();
		}
		if(e.getSource() == loadGame)
		{
			try{
				MemoryMenu window = new MemoryMenu(false, null);
				window.setVisible(true);
				window.setResizable(false);
				window.setExtendedState(MAXIMIZED_BOTH);
				this.hide();
			} catch (IOException e1){
				e1.printStackTrace();
			}
		}
	}

}
