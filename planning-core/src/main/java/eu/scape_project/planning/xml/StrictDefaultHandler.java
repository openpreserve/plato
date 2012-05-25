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
