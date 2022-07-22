package telnetag.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import telnetag.exception.TelnetException;
import telnetag.tools.Util;

/**
 * Client telnet application
 * @author SiVikt
 */
public class Client
{
	private final Logger log = Logger.getLogger( Client.class );
	/*
	 * server port
	 */
	public static final int TELNET_PORT = 23;
	/*
	 * command prompt string
	 */
	public final String CMD_PREFIX = "telnet-> ";
	@SuppressWarnings("rawtypes")
	private final Class perfomerArgTypes[] = { String.class, StringTokenizer.class };
	private Socket remoteHost;
	private Scanner cmdReader;
	private PrintWriter writer;
	
	/**
	 * connect to the server
	 */
	public void init( InputStream cmdIn, OutputStream out )
	{
		cmdReader = new Scanner( cmdIn );
		try 
		{
			writer = new PrintWriter( new OutputStreamWriter( out, "cp1251" ) );
		} 
		catch (UnsupportedEncodingException e1) 
		{
			e1.printStackTrace();
		}
		
		String cmdLine;
		StringTokenizer strTokenizer;
		printMsg( CMD_PREFIX, false );
		while ( ( cmdLine = cmdReader.nextLine() ) != null )
		{		
			try 
			{
				strTokenizer = new StringTokenizer( cmdLine, " " );
				String command = strTokenizer.nextToken().toUpperCase();
				Object args[] = { cmdLine, strTokenizer };
					
				Method commandPerformer = getClass().getMethod( "perform_" + command, perfomerArgTypes );
				int code = ( Integer )commandPerformer.invoke( this, args );
					
				//log.info( Util.getTime() + "	\"" + command + "\" perform." );
				if ( code == -1 )
				{
					return;
				}
				printMsg( CMD_PREFIX, false );
			} 
			catch ( Exception e ) 
			{
				printMsg( "Unknown command \"" + cmdLine + "\".", true );
				//log.error( "Unknown command \"" + cmdLine + "\".", e );
				printMsg( CMD_PREFIX, false );
			}
		}
	}
	
	/**
	 * perfomr open command(open new connection)
	 * @param cmd - command string
	 * @param cmdLine - command tokens
	 * @return performed code
	 * @throws TelnetException
	 */
	public int perform_O( String cmd, StringTokenizer cmdLine ) throws TelnetException
	{
		String host = cmdLine.nextToken();
		int port = Integer.valueOf( cmdLine.nextToken() );
		
		try 
		{
			remoteHost = new Socket( host, port );
			log.info( "Connect to the server " + host + " in " + Util.getDateAndTime() );
			
			ClientNVT clientNVT = new ClientNVT( remoteHost, this );
			new Thread( clientNVT ).start();
			
			printMsg( "connected...", true );
			String command;
			while ( clientNVT.isConnect() && ( command = cmdReader.nextLine() ) != null ) 
			{
				clientNVT.sendCmd( command );
			}
			printMsg( "connection lost...", true );
		} 
		catch (IOException e)
		{
			printMsg( "not connect...", true );
			return 0;
		}	
		
		return 0;
	}
	
	/**
	 * perfomr exit command
	 * @param cmd - command string
	 * @param cmdLine - command tokens
	 * @return performed code
	 * @throws TelnetException
	 */
	public int perform_EXIT( String cmd, StringTokenizer cmdLine ) throws TelnetException
	{
		return -1;
	}

	/**
	 * print string
	 * @param msg - string
	 * @param nl - print CRLF?
	 */
	public void printMsg( String msg, boolean nl )
	{
		if ( nl )
		{
			writer.println( msg );
		}
		else
		{
			writer.print( msg );
		}
		writer.flush();
	}
	
	/**
	 * print char
	 * @param c
	 */
	public void printMsg( char c )
	{
		writer.print( c ); 
		writer.flush();
	}
}

