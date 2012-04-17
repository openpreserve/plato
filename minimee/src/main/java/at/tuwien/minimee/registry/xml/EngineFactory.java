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
package at.tuwien.minimee.registry.xml;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/**
 * Helper class for digesting a minimee configuration XML file
 */
public class EngineFactory extends AbstractObjectCreationFactory<Object> {
    @Override
    public Object createObject(Attributes arg0) throws Exception {
        String classname = (arg0.getValue("class"));
        return Class.forName(classname).newInstance();
    }
}
