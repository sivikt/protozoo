package servag.ftpserver.handle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.StringTokenizer;

import servag.ftpserver.config.Configuration;
import servag.ftpserver.datarepresent.Representation;
import servag.ftpserver.exception.CommandException;
import servag.ftpserver.tools.FileLocker;
import servag.ftpserver.tools.GuiAnnunciator;
import servag.ftpserver.tools.Util;
import servag.ftpserver.transmode.TransmissionMode;

/**
 * The Protocol interpreter. 
 * In concordance with RFC-959 specification.
 * notice: Remake path converter!
 * 
 * all methods that perfom client messages are called as perfom_[msg_name]<br/>
 * Massages are implemented by this PI:<br/>
 *           USER <SP> <username> <CRLF>
 *           PASS <SP> <password> <CRLF>
 *           CWD  <SP> <pathname> <CRLF>
 *           CDUP <CRLF>
 *           QUIT <CRLF>
 *           REIN <CRLF>
 *           PORT <SP> <host-port> <CRLF>
 *           TYPE <SP> <type-code> <CRLF>
 *           MODE <SP> <mode-code> <CRLF>
 *           RETR <SP> <pathname> <CRLF>
 *           STOR <SP> <pathname> <CRLF>
 *           RNFR <SP> <pathname> <CRLF>
 *           RNTO <SP> <pathname> <CRLF>
 *           ABOR <CRLF>
 *           DELE <SP> <pathname> <CRLF>
 *           RMD  <SP> <pathname> <CRLF>
 *           MKD  <SP> <pathname> <CRLF>
 *           PWD  <CRLF>
 *           LIST [<SP> <pathname>] <CRLF>
 *           NLST [<SP> <pathname>] <CRLF>
 *           SYST <CRLF>
 *           HELP [<SP> <string>] <CRLF>
 *           NOOP <CRLF>
 * See RFC...
 * @author SiVikt
 */
class ServerPI implements Runnable 
{
	/**
	 * loggin for this class
	 */
	private final GuiAnnunciator log = new GuiAnnunciator( ServerPI.class );
	
	private final String rootDir = "/";
	
	@SuppressWarnings("rawtypes")
	private final Class perfomerArgTypes[] = { String.class, StringTokenizer.class };

	private Socket client;	
	
	private BufferedReader reader;	
	
	private PrintWriter writer;
	
	private ServerDTP serverDTP;
	
	private String commonHomeDir;
	
	private String homeDir;	
	
	private String currentDir;	
	
	private String login;	
	
	private String pass;	
	
	private String CLIENT_IP;	
	
	@SuppressWarnings("unused")
	private String CLIENT_HOST;
	
	private String lastCMD;
	
	private String anonymousName;
	
	private String anonymousHome;
	
	private String buf;
	
	public ServerPI( Socket socket )
	{
		try 
		{
			client = socket;
			CLIENT_IP = socket.getLocalAddress().getHostAddress();
			CLIENT_HOST = socket.getLocalAddress().getHostName();
			
			reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			writer = new PrintWriter( socket.getOutputStream() );
			
			login = null;
			pass = null;
			serverDTP = new PortServerDTP( this );
			
			commonHomeDir = Util.convertPath( Configuration.getStr( "props.home_dir", ".", "/home" ) );
			anonymousName = Configuration.getStr( "props.anonymous_name", ".", "anonymous" );
			anonymousHome = Configuration.getStr( "props.anonymous_home", ".", commonHomeDir );
			currentDir = rootDir;
			lastCMD = "";
		} 
		catch ( IOException e ) 
		{
			log.error( "Couldn't get client ["+ CLIENT_IP +"] intput and output streams.", e );
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		try 
		{
			performerLoop();
		} 
		catch ( IOException e ) 
		{
			log.info( "Client close connection or there is any problems with reader stream.", e );
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				client.close();
			} 
			catch ( IOException e ) 
			{
				log.error( "Client socket closing abortive attempt.", e );
				e.printStackTrace();
			}
		}
	}

	/**
	 * main loop where gets client messages and perfom it
	 * @throws IOException
	 */
	// reply codes : 120, 220, 421
	private void performerLoop() throws IOException
	{
		reply2Client( 220, Configuration.getStr( "props.ftp_banner", ".", "server ready..." ) );
				
		String cmdLine;
		while ( ( cmdLine = reader.readLine() ) != null )
		{
			StringTokenizer strTokenizer = new StringTokenizer( cmdLine, " " );
			String command = strTokenizer.nextToken().toUpperCase();
			Object args[] = { cmdLine, strTokenizer };

			try 
			{
				Method commandPerformer = getClass().getMethod( "perform_" + command, perfomerArgTypes );
				int code = ( Integer )commandPerformer.invoke( this, args );
				
				log.info( Util.getTime() + "	" + command + " from " + login + " [" + CLIENT_IP + "]" );
				if ( code == 221 )
				{
					return;
				}
			} 
			catch ( InvocationTargetException e )
			{
				try
				{
					throw ( Exception )e.getTargetException();
				}
				catch ( CommandException cmdEx )
				{
					reply2Client( cmdEx.getCode(), cmdEx.getReply() );
					log.error( Util.getTime() + " Couldn't to invoke the method. From " + CLIENT_IP, cmdEx );
				} 
				catch ( Exception otherEx ) 
				{
					reply2Client( 500, "Syntax error, command unrecognized." );
					log.error( "Couldn't to invoke the method (" + cmdLine + ").", otherEx );
					e.printStackTrace();
				}
			}
			catch ( Exception e ) 
			{
				reply2Client( 500, "Syntax error, command unrecognized." );
				log.error( "Couldn't to invoke the method (" + cmdLine + ").", e );
				e.printStackTrace();
			}
			finally 
			{
				lastCMD = command;
			}
		}
	}
	
	// reply codes : 230, 530, 500, 501, 421, 331, 332
	public int perform_USER( String cmd, StringTokenizer st ) throws CommandException
	{
		if ( !st.hasMoreTokens() )
		{
			throw new CommandException( 530, "Not logged in." );
		}
			
		login = cmd.substring( 5 );
		pass = null;
		currentDir = rootDir;
		
		return reply2Client( 331, "User name okay, need password." );
	}
	
	// reply codes : 230, 202, 332, 421, 500, 501, 503, 530
	public int perform_PASS( String cmd, StringTokenizer st ) throws CommandException
	{
		if ( !lastCMD.equals( "USER" ) )
		{
			throw new CommandException( 503, "Bad sequence of commands." );
		}

		pass = cmd.substring( 4 ).trim();
		if ( !login.equals( anonymousName ) &&
			 (Configuration.get( "users\n" + login, "\n" ) == null || 
			  !Configuration.get( "users\n" + login + "\npass", "\n", "" ).equals( pass ) ) )
		{
			pass = null;
			throw new CommandException( 530, "Not logged in." );
		}
		
		if ( login.equals( anonymousName ) )
		{
			homeDir = anonymousHome;
		}
		else 
		{
			homeDir = servag.ftpserver.tools.Util.convertPath( Configuration.getStr( "users\n" + login + "\nhome", "\n", commonHomeDir ) );
		}
		
		return reply2Client( 230, "User logged in, proceed." );
	}
	
	// reply codes : 230, 202, 530, 500, 501, 503, 421
	public int perform_ACCT( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	// reply codes : 250, 421, 500, 501, 502, 530, 550
	public int perform_CWD( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		
		if ( !st.hasMoreTokens() )
		{
			currentDir = rootDir;
		}
		else
		{	
			String absDir;
			String relDir = cmd.substring( 4 ) + "/";
			if ( relDir.startsWith( "/" ) || relDir.startsWith( "\\" ) )
			{
				absDir =  Util.convertPath( homeDir + relDir );
			}
			else 
			{
				absDir =  Util.convertPath( homeDir + currentDir + "/" + relDir );
			}
		
			String oldDir = currentDir;
			currentDir = absDir.substring( homeDir.length() - 1, absDir.lastIndexOf( '/' ) );
			File destFile = new File( absDir );
			if ( !destFile.exists() || !destFile.isDirectory() )
			{
				CommandException ex = new CommandException( 550, currentDir + ": No such file or directory." );
				currentDir = oldDir;
				throw ex;
			}
		}
		
		return reply2Client( 250, "Directory changed to " + currentDir );
	}
	
	// reply codes : 257, 500, 501, 502, 421, 550
	public int perform_PWD( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		
		File destDir = new File( homeDir + currentDir );
		if ( !destDir.exists() || !destDir.isDirectory() )
		{
			throw new CommandException( 550, "No such file or directory." );
		}
		
		return reply2Client( 257, "/" + currentDir + "/ is current directory." );
	}
	
	public int perform_XCWD( String cmd, StringTokenizer st ) throws CommandException
	{
		cmd = "CWD" + cmd.substring( 3 );
		return perform_CWD( cmd, st );
	}
	
	public int perform_XPWD( String cmd, StringTokenizer st ) throws CommandException
	{
		cmd = "PWD" + cmd.substring( 3 );
		return perform_PWD( cmd, st );
	}
	
	// reply codes : 250, 500, 501, 502, 421, 530, 550
	public int perform_CDUP( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		
		currentDir = currentDir.substring( 0, currentDir.lastIndexOf( "/" ) );
		if ( currentDir.length() == 0 )
		{
			currentDir = "/";
		}
		
		return reply2Client( 250, "Directory changed to " + currentDir );
	}
	
	public int perform_SMNT( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}

	// reply codes : 215, 421, 500, 501, 502
	public int perform_SYST( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		
		return reply2Client( 215, System.getProperty("os.name").toUpperCase() );
	}
	
	// reply codes : 120, 220, 421, 500, 502
	public int perform_REIN( String cmd, StringTokenizer st ) throws CommandException
	{
		// transfering must be complete before
		checkLoggin();
		
		this.login = null;
		this.pass = null;
		this.currentDir = rootDir;
		serverDTP = new PortServerDTP( this );
		
		reply2Client( 120, "Service ready in nnn minutes." );
		
		return reply2Client( 220, "Service ready for new user." );
	}
	
	// reply codes : 200, 500, 501, 530, 421
	public int perform_PORT( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		
		StringTokenizer sTokenizer = new StringTokenizer( st.nextToken(), "," );
		String h1 = sTokenizer.nextToken();
		String h2 = sTokenizer.nextToken();
		String h3 = sTokenizer.nextToken();
		String h4 = sTokenizer.nextToken();
		int p1 = Integer.valueOf( sTokenizer.nextToken() );
		int p2 = Integer.valueOf( sTokenizer.nextToken() );
		
		String host = h1 + "." + h2 + "." + h3 + "." + h4;
		int port = ( p1 * 256 ) + p2;
		if ( port < 1024 || port > 65535 )
		{
			throw new CommandException( 530, "Port number has to be between 1024 and 65535." );
		}
		
		//serverDTP = new PortServerDTP( this );
		serverDTP.setDataPort( host, port );
		
		return reply2Client( 200, "PORT command successful.");
	}
	
	// reply codes : 200, 500, 501, 504, 421, 530
	public int perform_TYPE( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		
		String args = st.nextToken();
		
		Representation representation = Representation.getRep( args );
		if ( representation == null )
		{
			throw new CommandException( 504, "Command not implemented for that parameter." );
		}
		
		serverDTP.setRepresentation( representation );
		
		return reply2Client( 200, "TYPE command successful.");
	}

	// reply codes : 200, 500, 501, 504, 421, 530
	public int perform_MODE( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		
		String arg = st.nextToken();
		
		TransmissionMode tMode = TransmissionMode.getTMode( arg.charAt( 0 ) );
		if ( tMode == null )
		{
			throw new CommandException( 504, "Command not implemented for that parameter." );
		}
		
		serverDTP.setTransmissionMode( tMode );

		return reply2Client( 200, "MODE command successful." );
	}
	
	// reply codes : 125, 150, 110, 226, 250, 425, 426, 451, 450, 550, 500, 501, 421, 530, 534, 535
	public int perform_RETR( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		
		st.nextToken();
		String path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) ); 
		
		return serverDTP.retrieveFile( path );
	}
	
	// reply codes : 125, 150, 110, 226, 250, 425, 426, 451, 450, 550, 500, 501, 421, 530, 534, 535
	public int perform_STOR( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		checkDTPAccess();
		
		st.nextToken();
		String path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) ); 
		
		return serverDTP.storeFile( path );
	}
	
	// reply codes : 500, 501, 502, 421, 530, 350
	public int perform_REST( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	// reply codes : 450, 550, 500, 501, 502, 421, 530, 350
	public int perform_RNFR( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		checkDTPAccess();
		
		st.nextElement();
		buf = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) );
		File file = new File( buf );
		if ( !file.exists() )
		{
			throw new CommandException( 450, "Requested file action not taken." );
		}
		
		return reply2Client( 350, "File or directory exists, ready for destination name.");
	}
	
	// reply codes : 250, 532, 553, 500, 501, 502, 503, 421, 530
	public int perform_RNTO( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		checkDTPAccess();
		
		if ( !lastCMD.equals( "RNFR" ) )
		{
			throw new CommandException( 503, "Bad sequence of commands." );
		}
		
		st.nextElement();
		String toName = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) );
		File file = new File( buf );
		
		try
		{
			//FileLocker.lock(file);
			File toFile = new File( toName );
		
			if ( !file.getParent().equals( toFile.getParent() ) )
			{
				throw new CommandException( 553, "Requested action not taken; File name not allowed." );
			}
		
			file.renameTo( toFile );
		}
		finally
		{
			try
			{
				FileLocker.release( file );
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		return reply2Client( 250, "RNTO command successful." );
	}
	
	// reply codes : 250, 450, 550, 500, 501, 502, 421, 530
	public int perform_DELE( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		checkDTPAccess();
		
		st.nextToken();
		String path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) );
		File file = new File( path );
		
		try 
		{
			//FileLocker.lock( file );
			if ( !file.exists() )
			{
				throw new CommandException( 450, "Requested file action not taken." );
			}
		
			if ( !file.delete() )
			{
				throw new CommandException( 550, "Requested action not taken; can't delete file." );
			}
		}
		finally
		{
			try 
			{
				FileLocker.release( file );
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return reply2Client( 250, "DELE command successful." );
	}
	
	// reply codes : 250, 500, 501, 502, 421, 530, 550
	public int perform_RMD( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		checkDTPAccess();
		
		st.nextToken();
		String path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 4 ) );
		File file = new File( path );
		if ( !file.isDirectory() || ( file.listFiles().length > 0 ) )
		{
			throw new CommandException( 550, "Requested action not taken; file unavailable." );
		}
		
		if ( !file.delete() )
		{
			throw new CommandException( 550, "Requested action not taken; can't delere dir." );
		}
		
		return reply2Client( 250, "RMD command successful." );
	}
	
	// reply codes : 250, 500, 501, 502, 421, 530, 550
	public int perform_MKD( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		checkDTPAccess();
		
		st.nextToken();
		String path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 4 ) );
		File file = new File( path );
		
		if ( file.exists() )
		{
			throw new CommandException( 550, "Requested action not taken; file unavailable." );
		}
		
		if ( !file.mkdir() )
		{
			throw new CommandException( 550, "Requested action not taken; file unavailable." );
		}
		
		return reply2Client( 250, "directory created." );
	}
	
	// reply codes : 250, 500, 501, 502, 421, 530, 550
	public int perform_SIZE( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		checkDTPAccess();
		
		st.nextToken();
		String path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) );
		File file = new File( path );
		
		if ( !file.exists() )
		{
			throw new CommandException( 550, "Requested action not taken; file unavailable." );
		}
		
		long fSize;
		try 
		{
			fSize = serverDTP.getRepresentation().sizeOf(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw new CommandException( 550, "Requested action not taken; file unavailable." );
		}
		
		return reply2Client( 213, "" + fSize );
	}
	
	public int perform_XMKD( String cmd, StringTokenizer st ) throws CommandException
	{
		cmd = "MKD" + cmd.substring( 3 );
		return perform_MKD( cmd, st );
	}
	
	public int perform_XRMD( String cmd, StringTokenizer st ) throws CommandException
	{
		cmd = "RMD" + cmd.substring( 3 );
		return perform_RMD( cmd, st );
	}
	
	public int perform_XDEL( String cmd, StringTokenizer st ) throws CommandException
	{
		cmd = "DELE" + cmd.substring( 4 );
		return perform_DELE( cmd, st );
	}
	
	// reply codes : 125, 150, 226, 250, 425, 426, 451, 450, 500, 501, 502, 421, 530, 534, 535
	public int perform_LIST( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		
		String path;
		if ( st.hasMoreElements() )
		{
			path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) );
		}
		else
		{
			path = Util.convertPath( homeDir + currentDir );
		}
		
		return serverDTP.list( path );
	}
	
	// reply codes : 125, 150, 226, 250, 421, 425, 426, 450, 451, 500, 501, 502, 504, 530, 534, 535, 550
	public int perform_NLST( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		
		String path;
		if ( st.hasMoreElements() )
		{
			path = Util.convertPath( homeDir + currentDir + "/" + cmd.substring( 5 ) );
		}
		else
		{
			path = Util.convertPath( homeDir + currentDir );
		}
		
		return serverDTP.list( path );
	}
	
	public int perform_STOU( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	public int perform_STRU( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	public int perform_ALLO( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	public int perform_STAT( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	public int perform_SITE( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	// reply codes : 227, 500, 501, 502, 421, 530
	public int perform_PASV( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		throw new CommandException( 202, "Command not implemented, superfluous at this site." );
	}
	
	// reply codes : 211, 214, 500, 501, 502, 421
	public int perform_HELP( String cmd, StringTokenizer st ) throws CommandException
	{	
		checkLoggin();
		
		return reply2Client( 200, "HELP command successful.");
	}
	
	// reply codes : 200, 500, 421
	public int perform_NOOP( String cmd, StringTokenizer st ) throws CommandException
	{
		checkLoggin();
		
		return reply2Client( 200, "NOOP command successful.");
	}
	
	// reply codes : 221, 500
	public int perform_QUIT( String cmd, StringTokenizer st ) throws CommandException
	{	
		// transfering must be complete before
		return reply2Client( 221, "goodBye-bye-bye-bye..." + this.login );
	}
	
	private int checkLoggin() throws CommandException
	{
		if ( this.pass == null ) 
		{
			throw new CommandException( 530, "Not logged in." );
		}
		
		return 200;
	}
	
	private int checkDTPAccess() throws CommandException
	{
		return 200;
	}
	
	public int reply2Client( int code, String reply )
	{
		writer.println( code + " " + reply );
		writer.flush();
		
		log.info( "Reply to [" + CLIENT_IP + "] : " + code + " " + reply );
		
		return code;
	}
		
}
