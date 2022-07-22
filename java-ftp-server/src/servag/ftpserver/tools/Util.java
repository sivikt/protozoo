package servag.ftpserver.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
					retPath.append( '/' );
					
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
	 * Return current date and time in dd.MM.yyyy hh:mm
	 * @return
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
	 * @return
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
	 * @return 
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
	
}
