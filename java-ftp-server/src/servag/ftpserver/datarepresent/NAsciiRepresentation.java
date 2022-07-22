package servag.ftpserver.datarepresent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * FTP ASCII data representation
 * converts "\r\n" to "\n" before returning from read().
 * @author SiVikt
 */
public class NAsciiRepresentation extends Representation
{
	public NAsciiRepresentation()
	{
		super( "ASCII Non-print", "A" );
	}
	
	@Override
	public InputStream getInputStream( Socket sock ) throws IOException 
	{
		return new NAsciiInputStream( sock.getInputStream() );
	}

	@Override
	public OutputStream getOutputStream( Socket sock ) throws IOException 
	{
		return new NAsciiOutputStream( sock.getOutputStream() );
	}

	@Override
	public long sizeOf( File file ) throws IOException
	{	
		InputStream in = new FileInputStream( file );
		long count = 0;
		
		try
		{
			int c;
			while ( ( c = in.read() ) != -1 )
			{
				if ( c == '\r' )
				{
					continue;
				}
				count++;
			}
		} 
		finally
		{
			in.close();
		}
		
		return count;
	}

}

/**
 * converts "\r\n" to "\n" before returning from read().
 */
class NAsciiInputStream extends FilterInputStream
{
	public NAsciiInputStream( InputStream in )
	{
		super( in );
	}

	public int read() throws IOException
	{
		int c;
		if ( ( c = in.read() ) == -1 )
		{
			return c;
		}
		if ( c == '\r' )
		{
			if ( ( c = in.read() ) == -1 )
			{
				return c;
			}
		}
		
		return c;
	}

	public int read( byte data[], int off, int len ) throws IOException
	{
		if ( len <= 0 )
		{
			return 0;
		}

		int c;
		
		if ( ( c = read() ) == -1 )
		{
			return -1;
		}
		else
		{
			data[ off ] = ( byte )c;
		}

		int i = 1;
		try
		{
			for ( ; i < len; i++ )
			{
				if ( ( c = read() ) == -1 )
				{
					break;
				}
				if ( c == '\r' )
				{
					if ( ( c = in.read() ) == -1 )
					{
						break;
					}
					data[ off + i ] = ( byte )c;
				}
			}
		}
		catch ( IOException e ) { }

		return i;
	}
}

/**
 * converts "\n" to "\r\n" before writing the data.
 */
class NAsciiOutputStream extends FilterOutputStream
{
	public NAsciiOutputStream( OutputStream out )
	{
		super( out );
	}

	public void write( int b ) throws IOException
	{
		if ( b == '\r' )
		{
			return;
		}
		if ( b == '\n' )
		{
			out.write( '\r' );
		}
		
		out.write( b );
	}

	public void write( byte data[], int off, int len ) throws IOException
	{
		for ( int i = 0; i < len; i++ )
		{
			byte b = data[ off + i ];
			if ( b == '\n' )
			{
				out.write( '\r' );
			}
			out.write( b );
		}
	}
}
