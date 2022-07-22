package telnetag.gui.bluebox.view;

import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Creates a menu be based on xml document
 * @author SiVikt
 *
 */
public class XMLMenuParser 
{
	private InputSource source;
	
	private SAXParser parser;
	
	private DefaultHandler docementHandler;
	
	private Map<String, JComponent> menuStorage = new HashMap<String, JComponent>();
	
	public XMLMenuParser( InputStream stream )
	{
		try
		{
			Reader reader = new InputStreamReader( stream );
			source = new InputSource( reader );
			parser = SAXParserFactory.newInstance().newSAXParser();
		}
		catch ( Exception e ) {}
		
		docementHandler = new XMLParser();
	}
	
	public void parse()
	{
		try
		{
			parser.parse( source, docementHandler );
		}
		catch ( Exception e ) {}
	}
	
	public JMenuBar getMenuBar( String name )
	{
		return ( JMenuBar )menuStorage.get( name );
	}
	
	public JMenu getMenu( String name )
	{
		return ( JMenu )menuStorage.get( name );
	}
	
	public JMenuItem getMenuItem( String name )
	{
		return ( JMenuItem )menuStorage.get( name );
	}
	
	public void addActionListener( String name, ActionListener listener )
	{
		getMenuItem( name ).addActionListener( listener );
	}
	
	class XMLParser extends DefaultHandler 
	{
		private JMenuBar currMenuBar;
		
		private LinkedList<JMenu> menus = new LinkedList<JMenu>();
		
		public void startElement( String un, String locaName, String qName, Attributes attributes )
		{
			if ( qName.equals( "menubar" ) )
			{
				parseMenuBar( attributes );
			}
			else if ( qName.equals( "menu" ) )
			{
				parseMenu( attributes );
			}
			else if ( qName.equals( "menuitem" ) )
			{
				parseMenuItem( attributes );
			}
		}
		
		public void endElement( String un, String locaName, String qName )
		{
			if ( qName.equals( "menu" ) )
			{
				menus.removeFirst();
			}
		}
		
		protected void parseMenuBar( Attributes attributes )
		{
			JMenuBar menuBar = new JMenuBar();
			
			String name = attributes.getValue( "name" );
			menuStorage.put( name, menuBar );
			currMenuBar = menuBar;
		}
		
		protected void parseMenu( Attributes attributes )
		{
			JMenu menu = new JMenu();
			
			String name = attributes.getValue( "name" );
			adjustProperties( menu, attributes );
			menuStorage.put( name, menu );
			
			if ( menus.size() != 0 ) 
			{
				( ( JMenu )menus.getFirst() ).add( menu );
			}
			else 
			{
				currMenuBar.add( menu );
			}
			
			menus.addFirst( menu );
		}
		
		protected void parseMenuItem( Attributes attributes )
		{	
			String name = attributes.getValue( "name" );
			if ( name.equals( "separator" ) )
			{
				( ( JMenu )menus.getFirst() ).addSeparator();
				return;
			}
			JMenuItem menuItem = new JMenuItem();
			adjustProperties( menuItem, attributes );
			menuStorage.put( name, menuItem );
			( ( JMenu )menus.getFirst() ).add( menuItem );
		}
		
		private void adjustProperties( JMenuItem menuItem, Attributes attributes )
		{
			String text = attributes.getValue( "text" );
			String mnemonic = attributes.getValue( "mnemonic" );
			String accelerator = attributes.getValue( "accelerator" );
			String enabled = attributes.getValue( "enabled" );
			
			menuItem.setText( text );
			if ( mnemonic != null ) 
			{
				menuItem.setMnemonic( mnemonic.charAt( 0 ) );
			}
			if ( accelerator != null ) 
			{
				menuItem.setAccelerator( KeyStroke.getKeyStroke( accelerator ) );	
			}
			if ( enabled != null ) 
			{
				boolean isEnable = true;
				
				if ( enabled.equals( "false" ) )
				{
					isEnable = true;
				}
				menuItem.setEnabled( isEnable );
			}
		}
	}
	
}
