package telnetag.server;

import telnetag.exception.TelnetException;

public interface TelnetCommandHandler 
{	
	/*
	 * call, when server ready for implements
	 */
	public void serverReady();
	
	/**
	 * perform any command
	 * @param cmd - command string
	 * @return cmd performed code
	 * @throws TelnetException
	 */
	public int doCommand( String cmd ) throws TelnetException;
	
	/**
	 * accept any option
	 * @param option - option string 
	 * @throws TelnetException
	 */
	public void doOption( String option ) throws TelnetException;
	
	/**
	 * dont accept any option
	 * @param option - option string
	 * @throws TelnetException
	 */
	public void dontOption( String option ) throws TelnetException;
	
	/**
	 * request for accept any option
	 * @param option - option string
	 * @throws TelnetException
	 */
	public void willOption( String option ) throws TelnetException;
	
	/**
	 * no I wont to accept any option
	 * @param option - option string
	 * @throws TelnetException
	 */
	public void wontOption( String option ) throws TelnetException;
	
	/**
	 * sends any message to client
	 * @param msg - message
	 * @param newLine - sends with CRLF?
	 */
	public void reply2Client( String msg, boolean newLine );
	
	/**
	 * send line with prompt to input command
	 */
	public void commandPrompt();
}
