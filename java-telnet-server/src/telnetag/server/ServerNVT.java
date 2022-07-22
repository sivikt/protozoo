package telnetag.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import telnetag.exception.TelnetException;
import telnetag.tools.GuiAnnunciator;
import telnetag.tools.Util;

import config.Configuration;

class ServerNVT implements Runnable, TelnetServerListener
{
	/**
	 * loggin for this class
	 */
	private final GuiAnnunciator log = new GuiAnnunciator( ServerNVT.class );
	/*
	 * input user name prompt
	 */
	private final String LOGIN_STRING = "Login: ";
	/*
	 * input passwrd prompt
	 */
	private final String PASSW_STRING = "Password: ";
	private Socket clientSocket;	
	private BufferedReader reader;
	/*
	 * login
	 */
	private String login;
	/*
	 * password
	 */
	private String pass;	
	private String CLIENT_IP;
	/*
	 * command handler
	 */
	private TelnetCommandHandler commandHandler;
	
	public ServerNVT( Socket socket, TelnetCommandHandler commandHandler )
	{
		try 
		{
			this.commandHandler = commandHandler;
			
			clientSocket = socket;
			CLIENT_IP = socket.getInetAddress().getHostAddress();
			
			reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			
			login = null;
			pass = null;
		} 
		catch ( IOException e ) 
		{
			log.error( "Couldn't get client ["+ CLIENT_IP +"] intput or output stream.", e );
		}
	}
	
	@Override
	public void run() 
	{
		try
		{
			performerLoop();
		}
		catch ( Exception e ) 
		{
			log.info( "Client ["+ CLIENT_IP +"] close connection or there is any problems with reader stream.", e );
		}
		finally
		{
			try 
			{
				clientSocket.close();
			} 
			catch ( IOException e ) 
			{
				log.error( "Client ["+ CLIENT_IP +"] socket closing abortive attempt.", e );
			}
		}
	}
	
	/**
	 * main loop where gets client messages and perfom it
	 */
	private void performerLoop() throws IOException, TelnetException
	{
		commandHandler.serverReady();
		
		try2Login( reader );
				
		commandHandler.commandPrompt();
		
		String cmdLine;
		while ( ( cmdLine = reader.readLine() ) != null )
		{
			cmdLine = Util.applyBackspace( cmdLine );
			logClientAction( cmdLine );
			try
			{
				if ( commandHandler.doCommand( cmdLine ) == ServerConstants.EXIT_CODE )
				{
					return;
				}
			}
			catch ( TelnetException ex )
			{
				log.error( Util.getTime() + " \"" + cmdLine + "\" [" + CLIENT_IP + "]", ex );
			}
		}
	}
	
	/**
	 * sets new command handler
	 * @param cHandler
	 */
	public void setCommandHandler( TelnetCommandHandler cHandler )
	{
		this.commandHandler = cHandler;
	}
	
	/**
	 * loggin procedure
	 * check for correct login and password
	 * and close connection, if it is incorrect
	 * @param reader
	 * @throws IOException
	 * @throws TelnetException
	 */
	private void try2Login( BufferedReader reader ) throws IOException, TelnetException
	{	
		if ( Configuration.getInt( "props\nneed_login", "\n", 0 ) == 1 )
		{
			commandHandler.reply2Client( LOGIN_STRING, false );
			
			String cmdLine;
			while ( !clientSocket.isClosed() && ( cmdLine = reader.readLine() ) != null )
			{
				cmdLine = Util.applyBackspace( cmdLine );
				logClientAction( cmdLine );
				if ( login == null )
				{
					login = cmdLine;
					commandHandler.reply2Client( PASSW_STRING, false );
					continue;
				}
				
				if ( pass == null )
				{
					pass = cmdLine;
					checkLogin( login, pass );
					break;
				}
			}
		}
	}
	
	/**
	 * check loggin data
	 * if incorrect throw exception
	 * @param login
	 * @param pass
	 * @throws TelnetException
	 */
	private void checkLogin( String login, String pass ) throws TelnetException
	{
		if ( (Configuration.get( "users\n" + login, "\n" ) == null || 
			 !Configuration.get( "users\n" + login + "\npass", "\n", "" ).equals( pass ) ) )
		{
			pass = null;
			login = null;
			throw new TelnetException( "Not logged in." );
		}
	}

	@Override
	public void serverStoped()
	{
		try
		{
			clientSocket.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * log info message
	 * @param cmdLine
	 */
	private void logClientAction( String cmdLine )
	{
		log.info( Util.getTime() + " \"" + cmdLine + "\" from [" + CLIENT_IP + "]" );
	}
}
