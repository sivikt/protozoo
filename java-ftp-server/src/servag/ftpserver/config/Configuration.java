package servag.ftpserver.config;

import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Class wich provides simple configuration actions
 * @author SiVikt
 *
 */
public class Configuration 
{
	private static Configuration instance = new Configuration();
	
	private HashMap<String, Object> properties = new HashMap<String, Object>();
	
	private ConfigParser defaultConfigParser = new DefaultConfigParser();
	
	private Configuration() {}

	public Configuration getInstance()
	{
		return instance;
	}
	
	public static void loadConfig( File configFile )
	{
		loadConfig( configFile, instance.defaultConfigParser );
	}
	
	public static void loadConfig( File configFile, ConfigParser parser )
	{
		instance.properties.clear();
		instance.properties = parser.load(configFile);
	}
	
	public static void saveConfig( File toFile )
	{
		saveConfig( toFile, instance.defaultConfigParser );
	}
	
	public static void saveConfig( File toFile, ConfigParser parser )
	{
		parser.save( toFile, instance.properties );
	}
	
	@SuppressWarnings("unchecked")
	public static Object get( String key, String delim )
	{
		if ( key == null || key.equals( "" ) )
		{
			return null;
		}
		
		StringTokenizer st = new StringTokenizer( key, delim );
		HashMap<String, Object> subKey = instance.properties;
		while ( st.countTokens() > 1 )
		{
			subKey = ( HashMap<String, Object> )subKey.get( st.nextToken() );
		}
		
		return subKey.get( st.nextToken() );
	}
	
	public static Object get( String key, String delim, Object defValue )
	{
		Object value = get( key, delim );
		if ( value != null )
		{
			return value;	
		}
		
		return defValue;
	}
	
	public static int getInt( String key, String delim, int defValue )
	{
		Object value = get( key, delim );
		if ( value != null )
		{
			return new Integer( ( String )value ).intValue();
		}
		
		return defValue;
	}
	
	public static String getStr( String key, String delim, String defValue )
	{
		Object value = get( key, delim );
		if ( value != null )
		{
			return ( String )value;
		}
		
		return defValue;
	}
	
	public static boolean put( String key, String delim, Object value )
	{
		if ( key != null && !key.equals( "" ) && !key.equals( delim ) && value != null )
		{
			StringTokenizer st = new StringTokenizer( key, delim );
			HashMap<String, Object> valueHashMap = instance.properties;
			while ( st.countTokens() > 1 )
			{
				HashMap<String, Object> subHashMap = new HashMap<String, Object>();
				valueHashMap.put( st.nextToken(), subHashMap );
				valueHashMap = subHashMap;
			}
			
			valueHashMap.put( st.nextToken(), value );
			
			return true;
		}
		
		return false;
	}
	
	public static void clear()
	{
		instance.properties.clear();
	}
	
	public static void setConfigParser( ConfigParser parser )
	{
		instance.defaultConfigParser = parser;
	}
	
}
