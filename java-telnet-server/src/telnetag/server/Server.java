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
 * 	Сервер Telnet предоставляет сеансы текстового терминала для клиентов Telnet.
 * 	Telnet входит в семейство протоколов TCP/IP и позволяет пользователю установить 
 * 	удаленный сеанс на сервере. Протокол поддерживает только алфавитно-цифровые 
 * 	терминалы: мыши и другие указывающие устройства, а также графические интерфейсы
 * 	пользователя не поддерживаются. Все команды должны вводиться в командную строку.
 * 	<br/>
 * 	Протокол Telnet не предоставляет практически никакой защиты. В сеансе Telnet, для
 * 	которого не используется проверка подлинности NTLM, все данные (включая пароли) 
 * 	передаются между клиентом и сервером в виде простого текста. Вследствие этого 
 * 	ограничения, а также в качестве общих рекомендаций против предоставления доступа 
 * 	к важным с точки зрения безопасности серверам недоверенным пользователям, сервер 
 * 	Telnet необходимо запускать только на компьютерах, на которых отсутствуют важные данные.
 * 	<br/>
 * 	Сервер Telnet выполняет роль шлюза для клиентов Telnet. Когда на компьютере выполняется 
 * 	сервер Telnet, пользователи могут использовать клиенты Telnet для подключения к этому 
 * 	компьютеру с удаленных компьютеров. При подключении клиента Telnet к компьютеру, на котором 
 * 	запущен сервер Telnet, удаленный пользователь получает запрос на ввод имени пользователя и 
 * 	пароля. По умолчанию для входа на сервер могут использоваться только сочетания имен и паролей, 
 * 	действующие на локальном сервере. После входа в систему пользователю предоставляется командная 
 *	строка, которую он может использовать так же, как окно командной строки, открытое на локальном 
 * 	компьютере. Однако по умолчанию пользователь не может использовать приложения, взаимодействующие
 *  с рабочим столом. Служба Telnet использует протокол Telnet, входящий в набор протоколов TCP/IP, 
 *  для подключения к удаленным компьютерам по сети.
 *  <br/>
 *  Войти на сервер Telnet могут члены группы «Администраторы». Доступ для других пользователей 
 *  контролируется членством группы TelnetClients. По умолчанию записи в этой группе отсутствуют. 
 *  Чтобы разрешить вход на сервер Telnet пользователям, не являющимся членами группы «Администраторы», 
 *  добавьте соответствующих пользователей и группы в группу TelnetClients.
 *  <br/>
 *  <br/>
 *  Принцип оговоренных опций охватывает тот факт, что многие 
 *  хосты скорее всего будут хотеть предоставить дополнительные 
 *  сервисы до или после их доступности в ВСТ, и многие пользователи 
 *  захотят иметь сложные терминалы с элементами изысканности, вместо 
 *  минимальных, для получения таких дополнительных сервисов. Независимые 
 *  от, но структурированные в TELNET протоколе различные «опции» 
 *  санкционированы и могут быть использованы с «DO, DON’T, WILL, WON’T» 
 *  структурой (обсуждается ниже) для того, чтобы позволить пользователю и 
 *  серверу сходиться в использовании более продуманного (или отличного) 
 *  набора соглашений для их TELNET соединения. Такие опции включают изменение 
 *  набора символов, режима эха, и т. д. Базовая стратегия для налаживания 
 *  использования опций — это на одной из сторон (или на обеих) инициировать 
 *  запрос: будет ли определённая опция иметь какой либо эффект. Другая сторона 
 *  может либо принять, либо отвергнуть запрос. Если запрос принимается, то опция 
 *  немедленно вступает в силу; если же опция отвергается, то связанный аспект 
 *  соединения остается как специфицировано для ВСТ. Очевидно, что сторона может 
 *  всегда отвергать запрос на включение, и никогда не должна отвергать запрос на 
 *  отключение некоторой опции начиная с момента когда стороны договорились о поддержке ВСТ. 
 *  Синтаксис оговоренной опции должен быть таким, чтобы если обе стороны запросят 
 *  одновременно опцию, то каждый будет рассматривать запрос с другой стороны как 
 *  положительное подтверждение этой опции.
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

