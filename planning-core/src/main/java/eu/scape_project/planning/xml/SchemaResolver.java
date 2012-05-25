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
