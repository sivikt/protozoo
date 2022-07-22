package servag.ftpserver.datarepresent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Binary represantation.
 * Operate with data as binary stream
 * @author SiVikt
 *
 */
public class ImageRepresentation extends Representation
{
	public ImageRepresentation()
	{
		super( "BINARY", "I" );
	}
	
	@Override
	public InputStream getInputStream(Socket sock) throws IOException 
	{
		return sock.getInputStream();
	}

	@Override
	public OutputStream getOutputStream(Socket sock) throws IOException 
	{
		return sock.getOutputStream();
	}

	@Override
	public long sizeOf(File file) throws IOException
	{
		return file.length();
	}

}
