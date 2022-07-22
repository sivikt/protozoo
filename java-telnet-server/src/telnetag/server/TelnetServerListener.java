package telnetag.server;

/**
 * interface, wich implements all server listeners
 * (for example differs command handlers)
 * @author root
 *
 */
public interface TelnetServerListener
{
	/*
	 * fires when server stoped
	 */
	public void serverStoped();
}
