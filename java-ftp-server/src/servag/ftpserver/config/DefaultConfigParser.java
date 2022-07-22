package servag.ftpserver.config;

import java.io.File;
import java.util.HashMap;

public class DefaultConfigParser implements ConfigParser 
{
	@Override
	public HashMap<String, Object> load(File configFile) 
	{
		return null;
	}

	@Override
	public void save(File toFile, HashMap<String, Object> store) 
	{
		
	}

}
