package servag.gui.bluebox.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import servag.gui.bluebox.view.MainFrame;

public class ExitAction extends AbstractAction
{	
	private static final long serialVersionUID = 4176352810520048514L;
	
	private MainFrame mainFrame;

	public ExitAction( MainFrame mFrame )
	{
		this.mainFrame = mFrame;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		this.mainFrame.getServer().stop();	
		System.exit( 0 );	
	}
}
