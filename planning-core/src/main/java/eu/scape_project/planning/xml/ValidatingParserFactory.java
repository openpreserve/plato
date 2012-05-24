package eu.scape_project.planning.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A parser factory for validating parsers.
 *  
 * @author Michael Kraxner
 */
public class ValidatingParserFactory {
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	

	/**
	 * Creates a new SAXParser with enabled validation.
	 * 
	 * Important: If you want to get notified of parsing errors, don't use {@link DefaultHandler}  directly,
	 *   but an own implementation instead. (e.g. you could use {@link StrictDefaultHandler} )
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public SAXParser getValidatingParser() throws ParserConfigurationException, SAXException{
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setValidating(true);
		
		SAXParser parser = saxParserFactory.newSAXParser();
		parser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		
		return parser;
	}
}
