package telnetag.exception;

/**
 * telnet exception
 * @author root
 *
 */
public class TelnetException extends Exception
{
	private static final long serialVersionUID = -5550547399732032709L;
	
	/*
	 * string with error description
	 */
	private String errMsg;
	
	public TelnetException( String msg )
	{
		this.errMsg = msg;
	}
	
	/**
	 * get error string
	 * @return error msg
	 */
	public String getMsg()
	{
		return this.errMsg;
	}

}
