package servag.ftpserver.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Parser for servAG configuration files.
 * @author SiVikt
 */
public class FTPConfigParser implements ConfigParser 
{
	private final Logger log = Logger.getLogger( FTPConfigParser.class );
	
	private final String SCHEMA_SOURCE = "src/servag/ftpserver/config/ftpconfig.xsd";
	
	private Element getRoot( File file )  
		throws ParserConfigurationException, IOException, SAXException, SAXParseException
	{
		SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
		Schema schemaXSD = schemaFactory.newSchema( new File ( SCHEMA_SOURCE ) );
		Validator validator = schemaXSD.newValidator();
			
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware( true);
		factory.setIgnoringComments( true );
		factory.setIgnoringElementContentWhitespace( true );

		DocumentBuilder builder = factory.newDocumentBuilder();
			
		Document doc = builder.parse( file );
				
		validator.validate( new DOMSource( doc ) );
			
		Element root = doc.getDocumentElement();
			
		return root; 		
	}
	
	@Override
	public HashMap<String, Object> load(File configFile)
	{
		HashMap<String, Object> store = new HashMap<String, Object>();
		try
		{
			Element rootElement = getRoot( configFile );
			
			getConfiguration( rootElement, store );
		}
		catch ( Exception e )
		{
			log.error( "Couldn't parse configuration file '" + configFile.getAbsolutePath() + "'.", e );
			e.printStackTrace();
		}
		
		return store;
	}
	
	private void getConfiguration( Element element, HashMap<String, Object> store ) 
	{
		NodeList children = element.getChildNodes();
		
		for ( int i = 0; i < children.getLength(); i++ ) 
		{
			Node child = children.item( i );
		
			if ( child instanceof Element ) 
			{
				Element childElement = ( Element )child;
				
				if( childElement.getTagName().equalsIgnoreCase( "props" ) )
				{
					HashMap<String, Object> propertiesStore = new HashMap<String, Object>();
					store.put( "props", propertiesStore );
					getProperties( childElement, propertiesStore );
				}	
				else if( childElement.getTagName().equalsIgnoreCase( "users" ) )
				{
					HashMap<String, Object> usersStore = new HashMap<String, Object>();
					store.put( "users", usersStore );
					getAllowedUsers( childElement, usersStore );
				}
			}
		}
	}

	private void getProperties( Element element, HashMap<String, Object> store )
	{
		NodeList children = element.getChildNodes();
		
		for ( int i = 0; i < children.getLength(); i++ ) 
		{
			Node child = children.item( i );
			
			if ( child instanceof Element ) 
			{
				Element childElement = ( Element )child;
				
				if ( childElement.getFirstChild() != null )
				{
					String key = childElement.getAttribute( "key" );
					String value = childElement.getFirstChild().getTextContent();
				
					if ( !key.equals( "" ) && !value.equals( "" ) )
					{
						store.put(key, value);
					}
				}
			}
		}
	}

	private void getAllowedUsers( Element element, HashMap<String, Object> store ) 
	{
		NodeList children = element.getChildNodes();
		
		for ( int i = 0; i < children.getLength(); i++ ) 
		{
			Node child = children.item( i );
			
			if ( child instanceof Element ) 
			{
				Element childElement = ( Element )child;
				
				HashMap<String, Object> userStore = new HashMap<String, Object>();
				store.put( childElement.getAttribute( "login" ), userStore );
				
				getUserInfo( childElement, userStore );
			}
		}
	}
	
	private void getUserInfo( Element element, HashMap<String, Object> store ) 
	{
		NodeList children = element.getChildNodes();
		
		for ( int i = 0; i < children.getLength(); i++ ) 
		{
			Node child = children.item( i );
		
			if ( child instanceof Element ) 
			{
				Element childElement = ( Element )child;
				
				if( childElement.getTagName().equalsIgnoreCase( "pass" ) )
				{
					if ( childElement.getFirstChild() != null )
					{
						store.put( "pass", childElement.getFirstChild().getTextContent() );
					}
					else
					{
						store.put( "pass", "" );
					}
				}
				if( childElement.getTagName().equalsIgnoreCase( "home" ) )
				{
					if ( childElement.getFirstChild() != null )
					{
						store.put( "home", childElement.getFirstChild().getTextContent() );
					}
				}
			}
		}
	}

	@Override
	public void save(File toFile, HashMap<String, Object> store) 
	{
	/*	
		  DOMImplementation domImpl = new DOMImplementationImpl();
		  Document doc = domImpl.createDocument(null, "app-settings", null);
		  Element root = doc.getDocumentElement();
		  Element propertiesElement = doc.createElement("properties");
		  root.appendChild(propertiesElement);
		  Set set = SINGLETON.fHashMap.keySet();
		  if (set != null) {
		    for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
		      String key = iterator.next().toString();
		      Element propertyElement = doc.createElement("property");
		      propertyElement.setAttribute("key", key);
		      Text nameText = doc.createTextNode(get(key).toString());
		      propertyElement.appendChild((Node) nameText);
		      propertiesElement.appendChild(propertyElement);
		    }
		  }

		  DOMSerializer serializer = new DOMSerializer();
		  serializer.serialize(doc, file);
		  return true; */
	}
	
}
