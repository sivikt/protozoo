package servag.ftpserver.handle;

import servag.ftpserver.datarepresent.Representation;
import servag.ftpserver.exception.CommandException;
import servag.ftpserver.transmode.TransmissionMode;

/**
 * Data Transfer Protocol. 
 * Using stream transmission mode as default.
 * In concordance with RFC-959 specification.
 * @author SiVikt
 */
abstract class ServerDTP implements Runnable
{
	protected TransmissionMode transmissionMode;
	
	protected Representation representation;
	
	protected int port;
	
	protected String host;
	
	protected ServerPI serverPI;
	
	public ServerDTP( ServerPI serverPI )
	{
		this.transmissionMode = TransmissionMode.STREAM;
		this.representation = Representation.ASCII;
		this.serverPI = serverPI;
	}
	
	public void setDataPort( String host, int port )
	{
		this.host = host;
		this.port = port;
	}
	
	public void run()
	{
		
	}
	
	public void setRepresentation( Representation representation )
	{
		this.representation = representation;
	}
	
	public void setTransmissionMode( TransmissionMode tMode )
	{
		this.transmissionMode = tMode;
	}
	
	public Representation getRepresentation()
	{
		return this.representation;
	}
	
	public TransmissionMode getTransmissionMode()
	{
		return this.transmissionMode;
	}
	
	public String getHost()
	{
		return this.host;
	}
	
	public int getPort()
	{
		return this.port;
	}
	 
	/**
	 * Send a copy of the file to the client. 
	 * This command does not affect the contents
	 * of the server's copy of the file.
	 * @param path - path to file
	 * @return
	 * @throws CommandException
	 */
	public abstract int retrieveFile( String path ) throws CommandException;
	
	/**
	 * The client provides the file name it wishes 
	 * to use for the upload. If the file already 
	 * exists on the server, it is replaced by the 
	 * uploaded file. If the file does not exist, it 
	 * is created. This command does not affect the 
	 * contents of the client's local copy of the file.
	 * @param path - path to save
	 * @return
	 * @throws CommandException
	 */
	public abstract int storeFile( String path ) throws CommandException;
	
	/**
	 * If the file does not exist on the server, it is created. 
	 * If the file already exists, it is not overwritten. Instead, 
	 * the server creates a unique file name and creates it for the 
	 * transferred file. The response by the server will contain the 
	 * created file name.
	 * @return
	 * @throws CommandException
	 */
	public abstract int stouFile() throws CommandException;
	
	/**
	 * The client provides the file name it wishes to use for 
	 * the upload. If the file already exists on the server, 
	 * the data is appended to the existing file. If the file 
	 * does not exist, it is created.
	 * @return
	 * @throws CommandException
	 */
	public abstract int appendFile() throws CommandException;
	
	/**
	 * If path refers to a file, sends information about that 
	 * file. If path refers to a directory, sends information 
	 * about each file in that directory. path defaults to the 
	 * current directory. 
	 * @param path
	 * @return
	 * @throws CommandException
	 */
	public abstract int list( String path ) throws CommandException;
	
	/**
	 * Returns a list of filenames in the given directory 
	 * (defaulting to the current directory), with no other information.
	 * @param path
	 * @return
	 * @throws CommandException
	 */
	public abstract int nameList( String path ) throws CommandException;
	
	/**
	 * Sets the point at which a file transfer should 
	 * start; useful for resuming interrupted transfers.
	 * @param pos
	 * @return
	 * @throws CommandException
	 */
	public abstract int setRest( int pos ) throws CommandException;
}
