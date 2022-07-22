package telnetag.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * some utils
 * @author root
 *
 */
public class Util 
{
	public static final long MLS_IN_DAY = 24*60*60*1000;
	
	/**
	 * Convert potential incorrect path to the <alpha>:/<string>[/<string>] 
	 * @param path - converting path
	 * @return fixed path
	 */
	public static String convertPath( String path )
	{
		StringBuffer retPath = new StringBuffer();
		
		if ( path == null )
		{
			return null;
		}
		
		for ( int i = 0; i < path.length(); i++ )
		{	
			if ( Character.isLetterOrDigit( path.charAt( i ) ) )
			{
				retPath.append( path.charAt( i ) );
			}
			else
			{
				if ( path.charAt( i ) == '/' || path.charAt( i ) == '\\' )
				{
					retPath.append( '\\' );
					
					while ( ++i < path.length() &&
							( path.charAt( i ) == '\\' || path.charAt( i ) == '/' ) ) ;
					i--;
				}
				else if ( path.charAt( i ) == ':' && i == 1 )
				{
					retPath.append( ':' );
				}
				else if ( ( path.charAt( i ) == ':' && i != 1 ) || 
						   path.charAt( i ) == '"' || path.charAt( i ) == '?' || 
						   path.charAt( i ) == '*' || path.charAt( i ) == '<' || 
						   path.charAt( i ) == '>' || path.charAt( i ) == '\\' || 
						   path.charAt( i ) == '/' )
				{
					while ( ( path.charAt( i ) == ':' && i != 1 ) || 
							path.charAt( i ) == '"' || path.charAt( i ) == '?' || 
							path.charAt( i ) == '*'	|| path.charAt( i ) == '<' || 
							path.charAt( i ) == '>' || path.charAt( i ) == '\\' || 
							path.charAt( i ) == '/' )
					{
					
						i++;
					}
					i--;
				}
				else
				{
					retPath.append( path.charAt( i ) );	
				}
			}
			
		}
		
		return retPath.toString();
	}
	
	/**
	 * Convert relative path to absolute
	 * @param relateTo - path 
	 * @param path - path to convert
	 * @return correct path
	 */
	public static String relativeToAbsolute( String  relateTo, String path )
	{
		String resultPath = "";
		if ( path.length() > 2 && path.substring(1, 3).equals( ":\\" ) )
		{
			resultPath = Util.convertPath( path );
		}
		else if ( path.equals("..") || path.equals( "." ) )  
		{
			int i = relateTo.lastIndexOf( "\\" );
			if ( relateTo.length() > 3 )
			{
				resultPath = Util.convertPath( relateTo.substring( 0, i + 1 ) );
			}
		}
		else 
		{
			resultPath = Util.convertPath( relateTo + "\\" + path );
		}
		
		return resultPath;
	}
	
	/**
	 * Return current date and time in dd.MM.yyyy hh:mm
	 * @return date and time as string 
	 */
	public static String getDateAndTime()
	{
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy hh:mm" );
		String dateStr = dateFormat.format( date );
		
		return dateStr;
	}
	
	/**
	 * Return current time in hh:mm format
	 * @return time as string
	 */
	public static String getTime()
	{
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat( "hh:mm" );
		String dateStr = dateFormat.format( date );
		
		return dateStr;
	}
	
	/**
	 * Compares two dates without time
	 * @param fDate
	 * @param sDate
	 * @return a value 0 if the fDate is equal to the sDate; 
	 *         the value less than 0 if the fDate is before the sDate;
	 *         the value greater than 0 if the fDate is after the sDate.
	 */
	public static int compareDates(Date fDate, Date sDate)
	{
	    Calendar fCal = Calendar.getInstance();
	    Calendar sCal = Calendar.getInstance();
	    
	    fCal.setTime(fDate);
	    sCal.setTime(sDate);
	    
	    fCal.set(Calendar.MILLISECOND, 0);
	    fCal.set(Calendar.SECOND, 0);
	    fCal.set(Calendar.MINUTE, 0);
	    fCal.set(Calendar.HOUR, 0);

	    sCal.set(Calendar.MILLISECOND, 0);
	    sCal.set(Calendar.SECOND, 0);
	    sCal.set(Calendar.MINUTE, 0);
	    sCal.set(Calendar.HOUR, 0);
	    
	    return fCal.compareTo(sCal);
	}
	
	/**
	 * Returns current date without time
	 * @return correct date
	 */
	public static Date getDate(Date date)
	{
	    Calendar cal = Calendar.getInstance();
	    
	    cal.setTime(date);
	    
	    cal.set(Calendar.MILLISECOND, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.HOUR, 0);
	    
	    return cal.getTime();
	}
	
	/**
	 * Returns number of days between two dates
	 * @param fDate
	 * @param sDate
	 * @return number
	 */
	public static long getDayRange( Date fDate, Date sDate ) 
	{   
		return Math.abs( fDate.getTime() - sDate.getTime() ) % MLS_IN_DAY;
	}
	
	/**
	 * delete all charecters, where '\b' in front of it
	 * @param str - string to convert
	 * @return correct string
	 */
	public static String applyBackspace( String str )
	{
		StringBuffer buf = new StringBuffer( str );
		int backspacePos;
		int deleteCount = 0;
		while ( ( backspacePos = buf.indexOf( "\b" ) )  != -1 )
		{
			deleteCount++;
			buf.delete( backspacePos - 1, backspacePos + 1 );
		}
			
		return buf.toString();
	}
	
}
