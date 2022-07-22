package servag.gui.bluebox.action;

import javax.swing.JTextArea;

import servag.ftpserver.tools.LogRecipient;

public class LogTextFieldAction implements LogRecipient 
{
	private JTextArea area;
	
	public LogTextFieldAction( JTextArea area )
	{
		this.area = area;
	}
	
	@Override
	public void inform(String message) 
	{
		this.area.append( message + "\n" );
	}

}
