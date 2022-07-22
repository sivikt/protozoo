package telnetag.tools;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Class that logging process and inform about it a GUI part of aplication
 * @author SiVikt
 *
 */
public class GuiAnnunciator implements LogAnnunciator 
{
	/**
	 * collection of the recipients
	 */
	private static ArrayList<LogRecipient> recipients = new ArrayList<LogRecipient>();
	
	/**
	 * message for loggin and sending to the recipients
	 */
	private String lastMsg;
	
	/**
	 * logger
	 */
	private Logger log;
	
	/**
	 * Create a logger for specific class
	 * @param logFor - class is must be loggin
	 */
	@SuppressWarnings("rawtypes")
	public GuiAnnunciator(  Class logFor )
	{
		this.log = Logger.getLogger( logFor );
	}
	
	/**
	 * an info message
	 * @param m
	 */
	public void info( String m )
	{
		log.info( m );
		lastMsg = m;
		informAboutEvent();
	}
	
	/**
	 * an info message with exception
	 * @param m
	 * @param th
	 */
	public void info( String m, Throwable th )
	{
		log.info( m, th );
		lastMsg = m;
		informAboutEvent();
	}
	
	/**
	 * an error message
	 * @param m
	 */
	public void error( String m )
	{
		log.error( m );
		lastMsg = m;
		informAboutEvent();
	}
	
	/**
	 * an error message with exection
	 * @param m
	 * @param th
	 */
	public void error( String m, Throwable th )
	{
		log.error( m, th );
		lastMsg = m;
		informAboutEvent();
	}
	
	/**
	 * a warn message
	 * @param m
	 */
	public void warn( String m )
	{
		log.warn( m );
		lastMsg = m;
		informAboutEvent();
	}
	
	/**
	 * a warn message with exception
	 * @param m
	 * @param th
	 */
	public void warn( String m, Throwable th )
	{
		log.warn( m, th );
		lastMsg = m;
		informAboutEvent();
	}
	
	@Override
	public void informAboutEvent() 
	{
		for ( LogRecipient res : recipients )
		{
			res.inform( log.getName() + " say | " + lastMsg );
		}
	}

	@Override
	public void addRecipient(LogRecipient recipient)
	{
		recipients.add( recipient );
	}

	@Override
	public void removeRecipient(LogRecipient recipient) 
	{
		recipients.remove( recipient );
	}

}
