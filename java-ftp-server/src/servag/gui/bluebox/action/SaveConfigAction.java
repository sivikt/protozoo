package servag.gui.bluebox.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import servag.gui.bluebox.view.MainFrame;

public class SaveConfigAction extends AbstractAction
{
	private static final long serialVersionUID = -4679039885577566602L;

	@SuppressWarnings("unused")
	private MainFrame mFrame;
	
	public SaveConfigAction( MainFrame mFrame )
	{
		this.mFrame = mFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		
	}

}
