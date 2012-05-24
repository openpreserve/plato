package eu.scape_project.planning.xml;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simple DefaultHandler implementation for validating XML-files while parsing: 
 * - The first error stops the parsing process, by passing on the SAXParseException.
 * - Can delegate lookup of entities to an EntityResolver, if provided.   
 *  
 * @author Michael Kraxner
 *
 */
public class StrictDefaultHandler extends DefaultHandler {
	
	private EntityResolver entityResolver;
	
	/**
	 * Creates a simple default handler for strict validation.
	 */
	public StrictDefaultHandler(){
		
	}
	
	/**
	 * Creates a simple default handler for strict validation, 
	 * which uses the given EntityResolver
	 * 
	 * @param entityResolver
	 */
	public StrictDefaultHandler(EntityResolver entityResolver){
		this.entityResolver = entityResolver;
	}
	
	/**
	 * Resolves the entity to an InputSource.
	 * Delegates to the {@link #entityResolver}, if provided
	 * 
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		InputSource result = null;
		if (entityResolver != null) {
			result = entityResolver.resolveEntity(publicId, systemId);
		}
		if (result == null) {
			result = super.resolveEntity(publicId, systemId);
		}
		return result;
	}
	@Override
	public void warning(SAXParseException e) throws SAXException {
		throw e;
	}
	@Override
	public void error(SAXParseException e) throws SAXException {
		throw e;
	}
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}
	
	
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}
}
