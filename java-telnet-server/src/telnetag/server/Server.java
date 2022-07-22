package telnetag.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import config.Configuration;
import config.TelnetConfigParser;

import telnetag.tools.GuiAnnunciator;
import telnetag.tools.LogAnnunciator;
import telnetag.tools.Util;

/**
 * <div >
 * 	������ Telnet ������������� ������ ���������� ��������� ��� �������� Telnet.
 * 	Telnet ������ � ��������� ���������� TCP/IP � ��������� ������������ ���������� 
 * 	��������� ����� �� �������. �������� ������������ ������ ���������-�������� 
 * 	���������: ���� � ������ ����������� ����������, � ����� ����������� ����������
 * 	������������ �� ��������������. ��� ������� ������ ��������� � ��������� ������.
 * 	<br/>
 * 	�������� Telnet �� ������������� ����������� ������� ������. � ������ Telnet, ���
 * 	�������� �� ������������ �������� ����������� NTLM, ��� ������ (������� ������) 
 * 	���������� ����� �������� � �������� � ���� �������� ������. ���������� ����� 
 * 	�����������, � ����� � �������� ����� ������������ ������ �������������� ������� 
 * 	� ������ � ����� ������ ������������ �������� ������������ �������������, ������ 
 * 	Telnet ���������� ��������� ������ �� �����������, �� ������� ����������� ������ ������.
 * 	<br/>
 * 	������ Telnet ��������� ���� ����� ��� �������� Telnet. ����� �� ���������� ����������� 
 * 	������ Telnet, ������������ ����� ������������ ������� Telnet ��� ����������� � ����� 
 * 	���������� � ��������� �����������. ��� ����������� ������� Telnet � ����������, �� ������� 
 * 	������� ������ Telnet, ��������� ������������ �������� ������ �� ���� ����� ������������ � 
 * 	������. �� ��������� ��� ����� �� ������ ����� �������������� ������ ��������� ���� � �������, 
 * 	����������� �� ��������� �������. ����� ����� � ������� ������������ ��������������� ��������� 
 *	������, ������� �� ����� ������������ ��� ��, ��� ���� ��������� ������, �������� �� ��������� 
 * 	����������. ������ �� ��������� ������������ �� ����� ������������ ����������, �����������������
 *  � ������� ������. ������ Telnet ���������� �������� Telnet, �������� � ����� ���������� TCP/IP, 
 *  ��� ����������� � ��������� ����������� �� ����.
 *  <br/>
 *  ����� �� ������ Telnet ����� ����� ������ ����������������. ������ ��� ������ ������������� 
 *  �������������� ��������� ������ TelnetClients. �� ��������� ������ � ���� ������ �����������. 
 *  ����� ��������� ���� �� ������ Telnet �������������, �� ���������� ������� ������ ����������������, 
 *  �������� ��������������� ������������� � ������ � ������ TelnetClients.
 *  <br/>
 *  <br/>
 *  ������� ����������� ����� ���������� ��� ����, ��� ������ 
 *  ����� ������ ����� ����� ������ ������������ �������������� 
 *  ������� �� ��� ����� �� ����������� � ���, � ������ ������������ 
 *  ������� ����� ������� ��������� � ���������� ������������, ������ 
 *  �����������, ��� ��������� ����� �������������� ��������. ����������� 
 *  ��, �� ����������������� � TELNET ��������� ��������� ������ 
 *  ��������������� � ����� ���� ������������ � �DO, DON�T, WILL, WON�T� 
 *  ���������� (����������� ����) ��� ����, ����� ��������� ������������ � 
 *  ������� ��������� � ������������� ����� ������������ (��� ���������) 
 *  ������ ���������� ��� �� TELNET ����������. ����� ����� �������� ��������� 
 *  ������ ��������, ������ ���, � �. �. ������� ��������� ��� ����������� 
 *  ������������� ����� � ��� �� ����� �� ������ (��� �� �����) ������������ 
 *  ������: ����� �� ����������� ����� ����� ����� ���� ������. ������ ������� 
 *  ����� ���� �������, ���� ���������� ������. ���� ������ �����������, �� ����� 
 *  ���������� �������� � ����; ���� �� ����� �����������, �� ��������� ������ 
 *  ���������� �������� ��� ��������������� ��� ���. ��������, ��� ������� ����� 
 *  ������ ��������� ������ �� ���������, � ������� �� ������ ��������� ������ �� 
 *  ���������� ��������� ����� ������� � ������� ����� ������� ������������ � ��������� ���. 
 *  ��������� ����������� ����� ������ ���� �����, ����� ���� ��� ������� �������� 
 *  ������������ �����, �� ������ ����� ������������� ������ � ������ ������� ��� 
 *  ������������� ������������� ���� �����.
 * </div>
 * 
 * @author SiVikt
 */
public class Server implements Runnable
{
	/**
	 * loggin for this class
	 */
	private final GuiAnnunciator log = new GuiAnnunciator( Server.class );
	/*
	 * server version
	 */
	public static final String VERSION = "Version 0.1";
	/*
	 * server name
	 */
	public static final String SERVER_NAME = "Telnet server 'telnetAG'";
	/*
	 * server TCP port
	 */
	public static final int SERVER_PORT = 23;
	/**
	 * file with server properties
	 */
	private final String SERVER_CONFIG_FILE = "src\\telnetag_props.xml";
	/*
	 * server socket
	 */
	private ServerSocket serverHost;
	/*
	 * server running flag. false when stop
	 */
	private boolean runFlag;
	/*
	 * server listeners list
	 */
	private List<TelnetServerListener> listeners = new ArrayList<TelnetServerListener>();
	
	/**
	 * starts the server
	 */
	private void startServer()
	{
		try 
		{
			serverHost = new ServerSocket( SERVER_PORT );
			runFlag = true;
			
			Configuration.setConfigParser( new TelnetConfigParser() );
			Configuration.loadConfig( new File( SERVER_CONFIG_FILE ) );
			
			log.info( "Server startup in " + Util.getDateAndTime() );
			while ( true )
			{
				Socket userHost = serverHost.accept();
				ServerNVT serverNVT = new ServerNVT( userHost, new MSCommandHandler( userHost ) );
				listeners.add(serverNVT);
				
				new Thread( serverNVT ).start();
				
				log.info( Util.getTime() + " New client connection [" + userHost.getInetAddress().getHostAddress() + "]." );
			}
		}
		catch ( IOException e )
		{
			if (runFlag) 
			{
				log.error( "Server startup faild or socket has been closed.", e );
			}
		}
	}
	
	/**
	 * stops the server
	 */
	public void stop()
	{
		if ( serverHost != null )
		{
			try
			{
				runFlag = false;
				serverHost.close();
				fireServerStopEvent();
				
				log.info( "Server stopped at " + Util.getDateAndTime() );
			}
			catch ( IOException e )
			{
				log.error( "Couldn't stop the server.", e );
			}
		}
	}

	/**
	 * run server in the thread
	 */
	@Override
	public void run()
	{
		startServer();
	} 
	
	/*
	 * return object, that inform listeners about server messages
	 */
	public LogAnnunciator getAnnunciator()
	{
		return this.log;
	}
	
	/*
	 * inform server listeners about server stop event
	 */
	private void fireServerStopEvent()
	{
		for ( int i = 0; i < listeners.size(); i++ )
		{
			listeners.get( i ).serverStoped();
		}
	}
}

