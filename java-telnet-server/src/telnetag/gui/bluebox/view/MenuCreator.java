package telnetag.gui.bluebox.view;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JMenuBar;

import telnetag.gui.bluebox.action.ExitAction;
import telnetag.gui.bluebox.action.StartServerAction;
import telnetag.gui.bluebox.action.StopServerAction;

/**
 * Creates a menu help by {@link XMLMenuParser} 
 * and adds listeners to menu items
 * @author SiVikt
 *
 */
public class MenuCreator
{
	/**
	 * do something
	 * @param mFrame - main window of gui
	 * @param path - path to the menu xml file
	 * @return menu
	 */
	public static JMenuBar createMenu( MainFrame mFrame, String path )
	{
		XMLMenuParser loader;
		
		try
		{
			InputStream stream = new FileInputStream( path );		
			loader = new XMLMenuParser( stream );
			loader.parse();
			
			loader.addActionListener( "start", new StartServerAction(mFrame) );
			loader.addActionListener( "stop", new StopServerAction(mFrame) );
			loader.addActionListener( "exit", new ExitAction(mFrame) );
			
			return loader.getMenuBar( "mainMenu" );
		}
		catch ( IOException e ) {}
		
		return null;
	}
}
