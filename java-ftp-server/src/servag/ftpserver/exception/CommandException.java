package servag.ftpserver.exception;

/**
 * Standart exception of the handle of the server
 * @author SiVikt
 */
public class CommandException extends Exception 
{
	private static final long serialVersionUID = -660709092208722459L;
	
	private static final int DEF_CODE = 501;
	
	private static final String DEF_REPLY = "Syntax error in parameters or arguments.";
	
	private int code;
	
	private String reply;
	
	public CommandException()
	{
		this( DEF_CODE, DEF_REPLY );
	}
	
	public CommandException( int code, String reply )
	{
		this.code = code;
		this.reply = reply;
	}
	
	public String getReply()
	{
		return this.reply;
	}
	
	public int getCode()
	{
		return this.code;
	}

}
