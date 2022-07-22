package telnetag.gui.bluebox.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import telnetag.gui.bluebox.action.LogTextFieldAction;
import telnetag.server.Server;
import telnetag.tools.LogAnnunciator;


public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 8673306238656210667L;
	
	private final String APP_TITLE = "telnetAG BlueBox";
	
	private final int WND_WIDTH = 600;
	
	private final int WND_HEIGHT = 400;
	
	private final String MENU_FILE = "menu.xml";
	
	private LogAnnunciator logAnnunciator;
	
	private Server serverHandle;

	public MainFrame()
	{
		serverHandle = new Server();
		logAnnunciator = serverHandle.getAnnunciator();
		
		JMenuBar menu = MenuCreator.createMenu( this, MENU_FILE );
		this.setJMenuBar( menu );
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab( "Loggin", createLogginPanel() );
		
		this.getContentPane().add( tabbedPane );
		
		this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		this.setSize( WND_WIDTH, WND_HEIGHT );
		this.setTitle( APP_TITLE );
	}
	
	private JPanel createLogginPanel()
	{
	    JTextArea logField = new JTextArea( 11, 0 );
	    JPanel logPanel = new JPanel();
	    JScrollPane logSpane = new JScrollPane( logField );
	    
	    logField.setEditable( false );
	    logPanel.setLayout( new BorderLayout() );
	    logPanel.add( logSpane );
		
		this.logAnnunciator.addRecipient( new LogTextFieldAction( logField ) );
		
		return logPanel;
	}
	
	public Server getServer()
	{
		return this.serverHandle;
	}
}
