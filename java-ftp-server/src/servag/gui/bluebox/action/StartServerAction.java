package servag.gui.bluebox.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import servag.gui.bluebox.view.MainFrame;

public class StartServerAction extends AbstractAction 
{
	private static final long serialVersionUID = 1161344385134508728L;
	
	private MainFrame mFrame;
	
	public StartServerAction( MainFrame mFrame )
	{
		this.mFrame = mFrame;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		new Thread( mFrame.getServer() ).start();
	}

}
