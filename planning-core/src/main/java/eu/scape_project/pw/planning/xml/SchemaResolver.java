package eu.scape_project.pw.planning.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An EntityResolver for Schemas. 
 * 
 * @author Michael Kraxner
 *
 */
public class SchemaResolver implements EntityResolver {

	private Map<String, String> entityLocations = new HashMap<String, String>();

	/**
	 * Creates a schema resolver, use {@link #addSchemaLocation(String, String)} to configure it.  
	 */
	public SchemaResolver() {
	}
	
	/**
	 * Adds a systemId which shall be resolved to the given schemaLocation.
	 * - schemaLocations are evaluated relative to the classpath.
	 *  
	 * @param systemIds
	 * @param schemaLocations
	 * @return <code>this</code>, SchemaResolver object, for subsequent calls. 
	 * 
	 */
	public SchemaResolver addSchemaLocation(String systemId, String schemaLocation) {
		if ((systemId == null) || (schemaLocation == null) ) {
			throw new IllegalArgumentException("systemId and schemaLocation must not be null");
		}
		entityLocations.put(systemId, schemaLocation);
		return this;
	}

	/**
	 * Resolves the entity with publicId and systemId to a InputSource.
	 * For this it uses previously registered schema-locations.
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		String location = entityLocations.get(systemId);
		if (location != null) {
			System.out.println("resolved entity: " + systemId + " -> " + location);
			return new InputSource(getClass().getClassLoader().getResourceAsStream(location));
		}
		return null;
	}

}
