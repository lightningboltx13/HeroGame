import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PauseMenu extends Frame implements ActionListener, FocusListener
{

	Panel main = new Panel();
	
	Button resume = new Button("RESUME");
	Button quit = new Button("QUIT");
	
	public PauseMenu()
	{
		add(main);
		main.add(resume);
			resume.addActionListener(this);
		main.add(quit);
			quit.addActionListener(this);
				
		addFocusListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == resume)
			this.hide();
		if(e.getSource() == quit)
			System.exit(0);
	}
	
	public void focusLost(FocusEvent e)
	{
		this.setFocusableWindowState(true);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
