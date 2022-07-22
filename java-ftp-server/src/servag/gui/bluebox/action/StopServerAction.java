package servag.gui.bluebox.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import servag.gui.bluebox.view.MainFrame;

public class StopServerAction extends AbstractAction 
{
	private static final long serialVersionUID = 1161344385134508728L;
	
	private MainFrame mFrame;
	
	public StopServerAction( MainFrame mFrame )
	{
		this.mFrame = mFrame;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		mFrame.getServer().stop();
	}

}
