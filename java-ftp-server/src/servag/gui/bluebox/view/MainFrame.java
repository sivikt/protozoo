package servag.gui.bluebox.view;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import servag.ftpserver.handle.Server;
import servag.ftpserver.tools.LogAnnunciator;
import servag.gui.bluebox.action.LogTextFieldAction;

public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 8673306238656210667L;
	
	private final String APP_TITLE = "servAG BlueBox";
	
	private final int WND_WIDTH = 600;
	
	private final int WND_HEIGHT = 400;
	
	private final String MENU_FILE = "src/servag/gui/bluebox/view/menu.xml";
	
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
		tabbedPane.addTab( "General", createGeneralPanel() );
		tabbedPane.addTab( "Users", createUsersPanel() );
		
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
	
	private JPanel createUsersPanel()
	{
		JPanel usersPanel = new JPanel();
		return usersPanel;
	}
	
	private JPanel createGeneralPanel()
	{
		JPanel generalPanel = new JPanel();
		generalPanel.setLayout( new BorderLayout() );
		
		Box vBox = Box.createVerticalBox();
		
		Box hBox1 = Box.createHorizontalBox();
		hBox1.add( new JLabel( "Welcome message:" ) );
		hBox1.add( Box.createHorizontalStrut( 30 ) );
		JTextField welMsg = new JTextField( 20 );
		welMsg.setMaximumSize( welMsg.getPreferredSize() );
		hBox1.add( welMsg );
		
		Box hBox2 = Box.createHorizontalBox();
		hBox2.add( new JLabel( "port:" ) );
		hBox2.add( Box.createHorizontalStrut( 30 ) );
		JTextField port = new JTextField( 20 );
		port.setMaximumSize( port.getPreferredSize() );
		hBox2.add( port );
		
		vBox.add( hBox1  );
		vBox.add( hBox2 );
		
		generalPanel.add( vBox );
		
		return generalPanel;
	}
	
	public Server getServer()
	{
		return this.serverHandle;
	}
}
