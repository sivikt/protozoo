package servag.ftpserver.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;

public class FileLocker 
{
	private static HashMap<File, FileLock> lockMap = new HashMap<File, FileLock>();
	
	private FileLocker() {}
	
	public synchronized static void lock( File file ) throws IOException
	{
		lock( file, "rw" );
	}
	
	public synchronized static void lock( File file, String atr ) throws IOException
	{
		FileChannel fileChannel = new RandomAccessFile(file, atr).getChannel();
		FileLock fileLock = fileChannel.lock();
		lockMap.put(file, fileLock);
	}
	
	public synchronized static void release( File file ) throws IOException
	{
		FileLock fileLock = lockMap.get( file );
		if ( fileLock != null ) 
		{
			fileLock.release();
			lockMap.remove( file );
		}
	}
	
	public synchronized static void releaseAll()
	{
		for ( File file : lockMap.keySet() ) 
		{
			try 
			{
				lockMap.get( file ).release();
				lockMap.remove( file );
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
