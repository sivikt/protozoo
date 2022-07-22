package servag.ftpserver.transmode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import servag.ftpserver.datarepresent.Representation;

/**
 * Stream transmission mode based on file
 * @author SiVikt
 *
 */
public class StreamTransmissionMode extends TransmissionMode 
{
	private final int BUF_SIZE = 1024;
	
	public StreamTransmissionMode() 
	{
		super( "stream", "S" );
	}

	@Override
	public void receiveFile(OutputStream out, Socket sock, Representation representation)
		throws IOException
	{
		InputStream in = representation.getInputStream( sock );
		byte buf[] = new byte[ BUF_SIZE ];
		
		int readNum = 0;
		while ( ( readNum = in.read( buf )) > 0 )
		{
			out.write( buf, 0, readNum );
		}
		
		in.close();
		out.close();
	}

	@Override
	public void sendFile(InputStream in, Socket sock, Representation representation)
		throws IOException
	{
		OutputStream out = representation.getOutputStream( sock );
		byte buf[] = new byte[ BUF_SIZE ];
		
		int readNum = 0;
		while ( ( readNum = in.read( buf )) > 0 )
		{
			out.write( buf, 0, readNum );
		}
		
		out.close();
		in.close();
	}
}
