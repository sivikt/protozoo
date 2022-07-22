package telnetag.tools;

/**
 * Interface for a listeners a server messages
 * @author SiVikt
 */
public interface LogRecipient 
{
	/**
	 * Inform listener with message
	 * @param message
	 */
	public void inform( String message );
}
