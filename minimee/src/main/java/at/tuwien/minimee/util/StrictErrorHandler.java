/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package at.tuwien.minimee.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * A simple error handler for parsing XML-files: The first error stops the parsing process.
 *  
 * @author Michael Kraxner
 *
 */
public class StrictErrorHandler implements ErrorHandler {

    public void error(SAXParseException arg0) throws SAXException {
        throw arg0;
    }

    public void fatalError(SAXParseException arg0) throws SAXException {
        throw arg0;
    }

    public void warning(SAXParseException arg0) throws SAXException {
        throw arg0;
    }

}
