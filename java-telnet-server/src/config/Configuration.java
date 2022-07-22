package config;

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
	/*
	 * configuration only one...global
	 */
	private static Configuration instance = new Configuration();
	/*
	 * properties hash map
	 */
	private HashMap<String, Object> properties = new HashMap<String, Object>();
	/*
	 * parser for configuration
	 */
	private ConfigParser defaultConfigParser = new DefaultConfigParser();
	
	private Configuration() {}

	/**
	 * return instance of configuration
	 */
	public Configuration getInstance()
	{
		return instance;
	}
	
	/**
	 * loads configuration using default parser
	 * @param configFile - file with conf
	 */
	public static void loadConfig( File configFile )
	{
		loadConfig( configFile, instance.defaultConfigParser );
	}
	
	/**
	 * load config using special parser
	 * @param configFile
	 * @param parser
	 */
	public static void loadConfig( File configFile, ConfigParser parser )
	{
		instance.properties.clear();
		instance.properties = parser.load(configFile);
	}
	
	/**
	 * save configuration to file using default parser
	 * @param toFile
	 */
	public static void saveConfig( File toFile )
	{
		saveConfig( toFile, instance.defaultConfigParser );
	}
	
	/**
	 * save current configuration to file using special parser
	 * @param toFile
	 * @param parser
	 */
	public static void saveConfig( File toFile, ConfigParser parser )
	{
		parser.save( toFile, instance.properties );
	}
	
	/**
	 * get value by key as object
	 * @param key
	 * @param delim
	 * @return value
	 */
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
	
	/**
	 * get value by key. if it doesn't exist use default value
	 * @param key
	 * @param delim
	 * @param defValue
	 * @return value 
	 */  
	public static Object get( String key, String delim, Object defValue )
	{
		Object value = get( key, delim );
		if ( value != null )
		{
			return value;	
		}
		
		return defValue;
	}
	
	/**
	 * get value by key as integer
	 * @param key
	 * @param delim
	 * @param defValue - default value
	 * @return value
	 */
	public static int getInt( String key, String delim, int defValue )
	{
		Object value = get( key, delim );
		if ( value != null )
		{
			return new Integer( ( String )value ).intValue();
		}
		
		return defValue;
	}
	
	/**
	 * get key as string
	 * @param key - key path
	 * @param delim
	 * @param defValue - default value if key dont exist
	 * @return value
	 */
	public static String getStr( String key, String delim, String defValue )
	{
		Object value = get( key, delim );
		if ( value != null )
		{
			return ( String )value;
		}
		
		return defValue;
	}
	
	/**
	 * put new value to configuration
	 * @param key - value key path
	 * @param delim - delimeter for key path
	 * @param value - value
	 * @return - true if ok
	 */
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
	
	/**
	 * clear all configuration
	 */
	public static void clear()
	{
		instance.properties.clear();
	}
	
	/**
	 * set new config parser
	 * @param parser
	 */
	public static void setConfigParser( ConfigParser parser )
	{
		instance.defaultConfigParser = parser;
	}
	
}
