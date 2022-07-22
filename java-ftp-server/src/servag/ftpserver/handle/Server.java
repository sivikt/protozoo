package servag.ftpserver.handle;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import servag.ftpserver.config.Configuration;
import servag.ftpserver.config.FTPConfigParser;
import servag.ftpserver.tools.FileLocker;
import servag.ftpserver.tools.GuiAnnunciator;
import servag.ftpserver.tools.LogAnnunciator;
import servag.ftpserver.tools.Util;

/**
 * Specified by RFC-959
 * FTP Server startup manage part.
 * 
 * <u>Info about FTP from Wiki</u>
 * <div style="text-align: left">
 * <b>
 * File Transfer Protocol (FTP) is a standard network protocol used to exchange and manipulate files 
 * over a TCP/IP based network, such as the Internet. FTP is built on a client-server architecture and 
 * utilizes separate control and data connections between the client and server applications. Client 
 * applications were originally interactive command-line tools with a standardized command syntax, but 
 * graphical user interfaces have been developed for all desktop operating systems in use today. FTP is 
 * also often used as an application component to automatically transfer files for program internal 
 * functions. FTP can be used with user-based password authentication or with anonymous user access. 
 * The Trivial File Transfer Protocol (TFTP) is a similar, but simplified, not interoperable, and 
 * unauthenticated version of FTP.
 * </b>
 * </div>
 * 
 * @author SiVikt
 */
public class Server implements Runnable
{
	/**
	 * loggin for this class
	 */
	private final GuiAnnunciator log = new GuiAnnunciator( Server.class );
	
	public static final String VERSION = "Version 0.1";
	
	public static final String SERVER_NAME = "FTP server 'servAG'";
	
	public static final int SERVER_PORT = 21;
	
	public static final int SERVER_DATA_PORT = 20;
	
	private final String SERVER_CONFIG_FILE = "src/servag_props.xml";
	
	private int port;
	
	private ServerSocket serverSocket;
	
	public Server()
	{
		this( SERVER_PORT );
	}
	
	public Server( int port )
	{
		this.port = port;
	}
	
	/**
	 * starts the server
	 */
	private void startServer()
	{
		try 
		{
			serverSocket = new ServerSocket( port );
			
			Configuration.setConfigParser( new FTPConfigParser() );
			Configuration.loadConfig( new File( SERVER_CONFIG_FILE ) );
			
			log.info( "Server startup in " + Util.getDateAndTime() );
			while ( true )
			{
				Socket clientSocket = serverSocket.accept();
				ServerPI pi = new ServerPI( clientSocket );
				new Thread( pi ).start();
				log.info( Util.getTime() + " Incoming from " + clientSocket.getLocalAddress().getHostAddress() + "." );
			}
		}
		catch ( IOException e )
		{
			log.error( "Server startup faild or socket has been closed.", e );
			e.printStackTrace();
		}
	}
	
	/**
	 * stops the server
	 */
	public void stop()
	{
		if ( serverSocket != null )
		{
			try
			{
				FileLocker.releaseAll();
				serverSocket.close();
				log.info( "Server stopped at " + Util.getDateAndTime() );
			}
			catch ( IOException e )
			{
				log.error( "Couldn't stop the server.", e );
			}
		}
	}

	/**
	 * run server in the thread
	 */
	@Override
	public void run()
	{
		startServer();
	} 
	
	/**
	 * return logger annunciator
	 * @return
	 */
	public LogAnnunciator getAnnunciator()
	{
		return this.log;
	}
	
	/**
	 * Set server port
	 * @param port
	 */
	public void setPort( int port )
	{
		this.port = port;
	}
}
