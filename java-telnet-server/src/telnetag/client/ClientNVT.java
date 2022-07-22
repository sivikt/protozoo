package telnetag.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

class ClientNVT implements Runnable
{
	/**
	 * loggin for this class
	 */
	private final Logger log = Logger.getLogger( ClientNVT.class );
	
	@SuppressWarnings("unused")
	/*
	 * server socket
	 */
	private Socket remoteHost;	
	/*
	 * reader
	 */
	private BufferedReader reader;	
	/*
	 * writer
	 */
	private PrintWriter writer;		
	/*
	 * client ip
	 */
	private String CLIENT_IP;
	
	@SuppressWarnings("unused")
	private String CLIENT_HOST;
	
	private Client client;
	
	private boolean connect = true;
	
	public ClientNVT( Socket socket, Client client )
	{
		try 
		{
			this.client = client;
			remoteHost = socket;
			CLIENT_IP = socket.getLocalAddress().getHostAddress();
			CLIENT_HOST = socket.getLocalAddress().getHostName();
			
			reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			writer = new PrintWriter( new OutputStreamWriter( socket.getOutputStream() ) );
		} 
		catch ( IOException e ) 
		{
			log.error( "Couldn't get client ["+ CLIENT_IP +"] intput or output stream.", e );
		}
	}
	
	/**
	 * main loop where gets messages from the server and print them
	 */
	public void listen() throws IOException
	{				
		int msgChar;
		connect = true;
		while ( ( msgChar = reader.read() ) != -1 )
		{		
			client.printMsg( ( char )msgChar );
		}
		connect = false;
	}
	
	/*
	 * send message to client terminal
	 */
	public void sendCmd( String cmd )
	{
		writer.println( cmd );
		writer.flush();
	}
	
	/*
	 * return true if connection exist
	 */
	public boolean isConnect()
	{
		return this.connect;
	}

	@Override
	public void run()
	{
		try 
		{
			listen();
		} 
		catch (IOException e) {}
	}
}
