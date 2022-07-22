package telnetag.tools;

/**
 * Interface for object that could annonce any listeners about event
 * @author SiVikt
 *
 */
public interface LogAnnunciator 
{
	/**
	 * Imform all listaners about event
	 */
	public void informAboutEvent();
	
	/**
	 * Adds listener
	 * @param recipient
	 */
	public void addRecipient( LogRecipient recipient );
	
	/**
	 * Remove listener
	 * @param recipient
	 */
	public void removeRecipient( LogRecipient recipient );
}
