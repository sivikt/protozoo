package telnetag.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import telnetag.exception.TelnetException;
import telnetag.tools.Util;
import config.Configuration;

/**
 * Telnet command handler for MS Windows
 * @author root
 *
 */
class MSCommandHandler implements TelnetCommandHandler
{
	@SuppressWarnings("unused")
	/*
	 * logginf for this class
	 */
	private final Logger log = Logger.getLogger( MSCommandHandler.class );
	/*
	 * postfix, that finished path string
	 */
	private final String PATH_POSTFIX = "> ";
	/*
	 * parametres for reflections
	 */
	@SuppressWarnings("rawtypes")
	private final Class[] perfParametres = { String.class, StringTokenizer.class };
	/*
	 * buffer size for transfer files
	 */
	private final int BUF_SIZE = 1024;
	@SuppressWarnings("unused")
	/*
	 * client socket
	 */
	private Socket clientSocket;
	private PrintWriter writer;
	@SuppressWarnings("unused")
	private ProcessBuilder procBuilder;
	private String currentPath;
	
	/*
	 * constructor - init variables
	 */
	public MSCommandHandler( Socket clientSocket )
	{
		this.clientSocket = clientSocket;
		
		try 
		{
			writer = new PrintWriter( clientSocket.getOutputStream() );
		} 
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		
		File currDir = new File( "" );
		currentPath = currDir.getAbsolutePath();
		//procBuilder = new ProcessBuilder();
		//procBuilder.directory( new File( "." ) );
	}
	
	@Override
	public void serverReady()
	{
		reply2Client( Configuration.getStr( "props\ntn_banner", "\n", "telnet server ready..." ), true );	
	}
	
	/*@Override
	public int doCommand(String cmd) throws TelnetException
	{	
		if ( cmd.equals( ServerConstants.EXIT_CMD ) )
		{
			return ServerConstants.EXIT_CODE;
		}
		
		String outMsg = null; 
		String errMsg = null;
		String[] perfCmds = { "cmd.exe", "/C", cmd }; 
		
		procBuilder.command( perfCmds ); 
		Process process;
		try 
		{
			process = procBuilder.start();
		
			outMsg = readFromProc( process.getInputStream() ); 
			errMsg = readFromProc( process.getErrorStream() );	
		
			process.waitFor();
		}
		catch ( Exception e )
		{
			log.error( "Error in doCommand [" + cmd + "]", e );
			e.printStackTrace();
		} 
    	
		if ( !cmd.split( " " )[ 0 ].equalsIgnoreCase( "cd" ) )
		{
			reply2Client( outMsg + errMsg, true );
			commandPrompt();
		}
		else
		{
			if ( outMsg.length() != 0 )
			{
				currentPath = outMsg.substring( 0, outMsg.lastIndexOf( "\r\n" ) );
				procBuilder.directory( new File( currentPath ) );
			}
			commandPrompt();
		}
    	
    	return 0;
	}*/

	@Override
	public int doCommand(String cmd) throws TelnetException {
		if ( cmd.equalsIgnoreCase( ServerConstants.EXIT_CMD ) )
		{
			reply2Client( "Bye!", true );
			return ServerConstants.EXIT_CODE;
		}
		
		try 
		{
			StringTokenizer strTokenizer = new StringTokenizer( cmd, " " );
			if ( strTokenizer.hasMoreTokens() )
			{
				String command = strTokenizer.nextToken().toUpperCase();
				Object args[] = { cmd, strTokenizer };	
				Method commandPerformer = getClass().getMethod( "perform_" + command, perfParametres );
				String retMsg = ( String )commandPerformer.invoke( this, args );
				reply2Client( retMsg, true );
			}
			commandPrompt();
		}
		catch ( Exception ex )
		{
			reply2Client( "\"" + cmd + "\" - Error in arguments or command unrecognized!", true );
			commandPrompt();
		}
		
		return 0;
	}
	
	/*private String readFromProc( InputStream inStream ) throws IOException
	{
        StringBuffer rzText = new StringBuffer();   
        BufferedReader in = new BufferedReader( new InputStreamReader( inStream ) );  
        
        String line;   
        while ( ( line = in.readLine() ) != null )
        {   
            rzText.append( line + "\r\n" );   
        }   
        
        return rzText.toString(); 
	}*/

	@Override
	public void doOption(String option) throws TelnetException
	{
	}

	@Override
	public void dontOption(String option) throws TelnetException 
	{	
	}

	@Override
	public void willOption(String option) throws TelnetException
	{	
	}

	@Override
	public void wontOption(String option) throws TelnetException 
	{	
	}
	
	@Override
	public void reply2Client( String msg, boolean nl )
	{
		if ( nl ) 
		{
			writer.write( msg );
			writer.write( "\r\n" );
		}
		else
		{
			writer.write( msg );
		}
		writer.flush();
	}

	@Override
	public void commandPrompt() 
	{
		writer.write( currentPath );
		writer.write( PATH_POSTFIX );
		writer.flush();
	}
	
	/**
	 * perform CD command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_CD( String cmd, StringTokenizer st ) throws TelnetException
	{	 
		st.nextElement();
		
		String resultPath = currentPath;
		String relDir = cmd.substring( 3 );
		resultPath = Util.relativeToAbsolute( currentPath, relDir );
		
		if ( !(new File( resultPath ).exists()) )
		{
			return "\"" + resultPath + "\" does'nt exist!";
		} 
			
		currentPath = resultPath;
		return "";
	}
	
	/**
	 * perform RENAME command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_RENAME( String cmd, StringTokenizer st ) throws TelnetException
	{			
		st.nextToken();
		
		int i = cmd.lastIndexOf( "/t" );
		
		String fromPath = Util.convertPath( currentPath + "\\" + cmd.substring( 7, i - 1 ) );
		String toPath = Util.convertPath( currentPath + "\\" + cmd.substring( i + 3 ) );
		File fromFile = new File( fromPath );
		if ( !fromFile.exists() )
		{
			return "File not found!";
		}
		
		File toFile = new File( toPath );
	
		if ( !fromFile.getParent().equals( toFile.getParent() ) )
		{
			return "Directories not equals!";
		}
		
		fromFile.renameTo( toFile );
		
		return "";
	}
	
	/**
	 * perform ERASE command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_ERASE( String cmd, StringTokenizer st ) throws TelnetException
	{	
		st.nextToken();
		String path = Util.convertPath( currentPath + "\\" + cmd.substring( 6 ) );
		File file = new File( path );
		
		if ( !file.exists() )
		{
			return "File not found!";
		}
	
		if ( !file.delete() )
		{
			return "Could not delete this file!";
		}
		
		return "";
	}
	
	/**
	 * perform RMDIR command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_RMDIR( String cmd, StringTokenizer st ) throws TelnetException
	{	
		st.nextToken();
		String path = Util.convertPath( currentPath + "\\" + cmd.substring( 6 ) );
		File file = new File( path );
		if ( !file.isDirectory() || ( file.listFiles().length > 0 ) )
		{
			return "Directory not found!";
		}
		
		if ( !file.delete() )
		{
			return "Could not delete this directory!";
		}
		
		return "";
	}
	
	/**
	 * perform MKDIR command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_MKDIR( String cmd, StringTokenizer st ) throws TelnetException
	{	
		st.nextToken();
		String path = Util.convertPath( currentPath + "\\" + cmd.substring( 6 ) );
		File file = new File( path );
		
		if ( file.exists() )
		{
			return "Such directory already exist!";
		}
		
		if ( !file.mkdir() )
		{
			return "Could not create directory!";
		}
		
		return "";
	}
	
	/**
	 * perform DIR command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_DIR( String cmd, StringTokenizer st ) throws TelnetException
	{	
		File file = new File( currentPath );
		if ( !file.exists() )
		{
			return "Directory does'nt exist!";
		}
		
		String content[] = file.list();
		StringBuilder buf = new StringBuilder();
		for ( int i = 0; i < content.length; i++ )
		{
			String fileName = content[ i ];
			buf.append( listFile( new File( currentPath + "\\" + fileName ) ) );
		}
		
		return buf.toString();
	}
	
	/**
	 * perform TYPE command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_TYPE( String cmd, StringTokenizer st ) throws TelnetException
	{	
		st.nextToken();
		
		StringBuffer buf = new StringBuffer();
		String path = Util.convertPath( currentPath + "\\" + cmd.substring( 5 ) );
		
		try 
		{
			BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( path ) ) );
			String line;
			while ( ( line = reader.readLine() ) != null )
			{
				buf.append( line );
				buf.append( "\r\n" );
			}
		} 
		catch ( Exception e) 
		{
			return "File not found!";
		}

		
		return buf.toString();
	}
	
	/**
	 * perform COPY command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_COPY( String cmd, StringTokenizer st ) throws TelnetException
	{	
		st.nextToken();
		
		int i = cmd.lastIndexOf( "/t" );
		String fromFileName = cmd.substring( 5, i - 1 );
		String fromPath = Util.convertPath( currentPath + "\\" + fromFileName );
		String toPath = Util.relativeToAbsolute( currentPath, cmd.substring( i + 3 ) );
		
		try 
		{
			InputStream in = new FileInputStream( fromPath );
			OutputStream out = new FileOutputStream( toPath + "\\" + fromFileName );
			byte buf[] = new byte[ BUF_SIZE ];
			
			int readNum = 0;
			while ( ( readNum = in.read( buf )) > 0 )
			{
				out.write( buf, 0, readNum );
			}
			
			in.close();
			out.close();
		} 
		catch ( Exception e) 
		{
			return "Error with file manipulation!";
		}
	
		return "";
	}
	
	/**
	 * perform HELP command
	 * @param cmd - full command string
	 * @param st - command string tokens
	 * @return
	 * @throws TelnetException
	 */
	public String perform_HELP( String cmd, StringTokenizer st ) throws TelnetException
	{	
		return ServerConstants.HELP_MSG;
	}
	
	/**
	 * gets file attributes in win format
	 * @param file - needed file
	 * @return
	 */
	private String listFile( File file )
	{
		Date date = new Date( file.lastModified() );
		SimpleDateFormat dateFormat = new SimpleDateFormat( "MMM dd yyyy", Locale.US );
		
		if ( Util.getDayRange( date, new Date() ) > 180 )
		{
			dateFormat = new SimpleDateFormat( "MMM dd hh:mm", Locale.US );
		}

		String sizeStr = Long.toString( file.length() );
		String sizeField = space( Math.max( 10 - sizeStr.length(), 0 ) ) + sizeStr;
		
		StringBuilder buf = new StringBuilder();
		buf.append( file.isDirectory() ? "<DIR>" : '-' );
		buf.append( sizeField + "b " + dateFormat.format( date ) + " " + file.getName() );
		buf.append( "\r\n" );
		
		return buf.toString();
	}
	
	/**
	 * Add needed spaces to the size field
	 * @param length - number of spaces
	 * @return
	 */
	private String space( int length )
	{
		StringBuffer buf = new StringBuffer();
		for ( int i = 0; i < length; i++ )
		{
			buf.append( ' ' );
		}
		
		return buf.toString();
	}
}
