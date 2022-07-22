package servag.ftpserver.datarepresent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Base class for the represantation of data
 * @author SiVikt
 *
 */
public abstract class Representation 
{
	private static HashMap<String, Representation> repMap = new HashMap<String, Representation>();
	
	public static final Representation ASCII = new NAsciiRepresentation();
	
	public static final Representation IMAGE = new ImageRepresentation();
	
	private String name;
	
	private String code;
	
	protected Representation( String name, String code )
	{
		this.name = name;
		this.code = code;
		
		repMap.put( code, this );
	}
	
	public static Representation getRep( String code )
	{
		return repMap.get( code );
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
	 * Give output stream of the socket in need format
	 * @param sock - client socket
	 * @return - outputstream
	 * @throws IOException
	 */
	public abstract OutputStream getOutputStream( Socket sock ) throws IOException;
	
	/**
	 * Give input stream of the socket in need format
	 * @param sock - client socket
	 * @return - inputstream
	 * @throws IOException
	 */
	public abstract InputStream getInputStream( Socket sock ) throws IOException;

	/**
	 * Return size of file
	 * @param file
	 * @return number
	 * @throws IOException
	 */
	public abstract long sizeOf( File file ) throws IOException;

}
