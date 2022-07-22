package servag.ftpserver.transmode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import servag.ftpserver.datarepresent.Representation;

/**
 * Base class for differnt transmission modes of the FTP server
 * @author SiVikt
 *
 */
public abstract class TransmissionMode
{
	private static HashMap<String, TransmissionMode> tMap = new HashMap<String, TransmissionMode>();
	
	public static final TransmissionMode STREAM = new StreamTransmissionMode();
	
	private String name;
	
	private String code;
	
	public TransmissionMode( String name, String code )
	{
		this.name = name;
		this.code = code;
		
		tMap.put( code, this );
	}
	
	public static TransmissionMode getTMode( Character code )
	{
		return tMap.get( code );
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getCode() 
	{
		return this.code;
	}
	
	/**
	 * Send file to the remote host
	 * @param in - file input stream
	 * @param sock - client socket
	 * @param representation - representation of sending data
	 * @throws IOException
	 */
	public abstract void sendFile( InputStream in, Socket sock, Representation representation )
		throws IOException;
	
	/**
	 * Receive file from the remote host
	 * @param out - file output stream
	 * @param sock - client socket
	 * @param representation - representation of sending data
	 * @throws IOException
	 */
	public abstract void receiveFile( OutputStream out, Socket sock, Representation representation )
		throws IOException;
}
