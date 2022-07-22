package servag.ftpserver.handle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import servag.ftpserver.exception.CommandException;
import servag.ftpserver.tools.FileLocker;
import servag.ftpserver.tools.GuiAnnunciator;
import servag.ftpserver.tools.Util;

/**
 * Server DTP that work in active mode
 * @author root
 *
 */
class PortServerDTP extends ServerDTP 
{
	/**
	 * loggin for this class
	 */
	private final GuiAnnunciator log = new GuiAnnunciator( PortServerDTP.class );
	
	public PortServerDTP( ServerPI serverPI ) 
	{
		super( serverPI );
	}

	@Override
	public int appendFile() 
	{
		return 0;
	}

	@Override
	public int list( String path ) throws CommandException
	{
		Socket socket = null;
		try
		{
			socket = new Socket( host, port );

			File file = new File( path );
			if ( !file.exists() )
			{
				return serverPI.reply2Client( 450, "Requested file action not taken." );
			}
			
			String content[] = file.list();
			PrintWriter pw = new PrintWriter( representation.getOutputStream( socket ) );
			serverPI.reply2Client( 150, "Opening " + representation.getName() + " mode data connection." );
		
			for ( int i = 0; i < content.length; i++ )
			{
				String fileName = content[ i ];
				listFile( new File( path + "\\" + fileName ), pw );
			}
			
			pw.flush();
		}
		catch ( IOException ioEx )
		{
			log.error( "Couldn't connect to client.", ioEx );
			ioEx.printStackTrace();
			throw new CommandException( 425, "Can't open data connection." );
		}
		finally
		{
			try 
			{
				socket.close();
			} 
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		return serverPI.reply2Client( 226, "Transfer complete." );
	}

	@Override
	public int nameList( String path ) throws CommandException
	{
		Socket socket = null;
		try
		{
			socket = new Socket( host, port );

			File file = new File( path );
			if ( !file.exists() )
			{
				return serverPI.reply2Client( 450, "Requested file action not taken." );
			}
			
			String content[] = file.list();
			PrintWriter pw = new PrintWriter( representation.getOutputStream( socket ) );
			serverPI.reply2Client( 150, "Opening " + representation.getName() + " mode data connection." );
		
			for ( int i = 0; i < content.length; i++ )
			{
				pw.print( content[ i ] );
				pw.println();
			}
			
			pw.flush();
		}
		catch ( IOException ioEx )
		{
			log.error( "Couldn't connect to client.", ioEx );
			ioEx.printStackTrace();
			throw new CommandException( 425, "Can't open data connection." );
		}
		finally
		{
			try 
			{
				socket.close();
			} 
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		return serverPI.reply2Client( 226, "Transfer complete." );
	}

	@Override
	public int retrieveFile(String path) throws CommandException
	{
		Socket socket = null;
		File file = null;
		try
		{
			socket = new Socket( host, port );
			
			file = new File( path );
			if ( !file.exists() || !file.isFile() )
			{
				throw new CommandException( 450, "Requested file action not taken." );
			}

			//FileLocker.lock( file );
			
			serverPI.reply2Client( 150, "Opening " + representation.getName() + " mode data connection." );
			transmissionMode.sendFile( new FileInputStream( file ), socket, representation );
			log.info( Util.getDateAndTime() + " RETR file <" + path + ">" );
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch ( CommandException ex )
		{
			throw ex;
		}
		finally
		{
			try 
			{
				FileLocker.release( file );
				socket.close();
			} 
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		return serverPI.reply2Client( 226, "Transfer complete." );
	}

	@Override
	public int storeFile( String path ) throws CommandException
	{
		Socket socket = null;
		File file = null;
		try
		{
			socket = new Socket( host, port );
			
			file = new File( path );
			if ( file.exists() )
			{
				throw new CommandException( 450, "Requested file action not taken." );
			}
			
			//FileLocker.lock( file );
			
			serverPI.reply2Client( 150, "Opening " + representation.getName() + " mode data connection." );
			transmissionMode.receiveFile( new FileOutputStream( file ), socket, representation );
			log.info( Util.getDateAndTime() + " STORE file <" + path + ">" );
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch ( CommandException ex )
		{
			throw ex;
		}
		finally
		{
			try 
			{
				FileLocker.release( file );
				socket.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return serverPI.reply2Client( 226, "Transfer complete." );
	}

	@Override
	public int stouFile() throws CommandException 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setRest( int pos ) throws CommandException
	{
		return 0;
	}

	/**
	 * Sends to the output stream format line 
	 * with attributes
	 * @param file - file for
	 * @param writer - to
	 */
	private void listFile( File file, PrintWriter writer )
	{
		Date date = new Date( file.lastModified() );
		SimpleDateFormat dateFormat = new SimpleDateFormat( "MMM dd yyyy", Locale.US );
		
		if ( Util.getDayRange( date, new Date() ) > 180 )
		{
			dateFormat = new SimpleDateFormat( "MMM dd hh:mm", Locale.US );
		}

		String sizeStr = Long.toString( file.length() );
		String sizeField = space( Math.max( 10 - sizeStr.length(), 0 ) ) + sizeStr;

		writer.print( file.isDirectory() ? 'd' : '-' );
		writer.print( "rwxrwxrwx    1 serv             serv" );
		writer.println( sizeField + " " + dateFormat.format( date ) + " " + file.getName() );
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
