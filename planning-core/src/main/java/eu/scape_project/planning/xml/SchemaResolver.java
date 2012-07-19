/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.xml;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        private Logger log = LoggerFactory.getLogger(SchemaResolver.class);

        private Properties schemaMappings = new Properties();

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
		schemaMappings.put(systemId, schemaLocation);
		return this;
	}

	/**
	 * Resolves the entity with publicId and systemId to a InputSource.
	 * For this it uses previously added schema-locations.
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		String location = schemaMappings.getProperty(systemId);
		if (location != null) {
		        if (log.isDebugEnabled()) {
		            log.debug("resolved schema: " + systemId + " -> " + location);
		        }
			InputSource source = new InputSource(getClass().getClassLoader().getResourceAsStream(location));
			// important: set the sytemId to the created InputSource, the parser will use it to complete relative URI's in the schema
			source.setSystemId(systemId);
			return source;
		} 
		return null;
	}

}
