package config;

import java.io.File;
import java.util.HashMap;

/**
 * An interface for the configuration parsers
 * @author SiVikt
 *
 */
public interface ConfigParser 
{
	/**
	 * Load configuration from file
	 * @param configFile - file from
	 * @return HashMap with pares key and value
	 */
	public HashMap< String, Object > load( File configFile );
	
	/**
	 * Save current configuration into the file
	 * @param toFile - file save to
	 * @param store - configuration
	 */
	public void save( File toFile, HashMap< String, Object > store );
}
