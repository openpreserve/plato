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

package eu.scape_project.pw.planning.xml.plan;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

import eu.scape_project.planning.model.TargetValueObject;

/**
 * Helper class for {@link eu.scape_project.planning.xml.ProjectImporter} to create a OrdinalTransformer - TargetValueObject 
 * with the data of its XML representation. 
 * 
 * @author Michael Kraxner
 *
 */

public class OrdinalTransformerMappingFactory extends AbstractObjectCreationFactory<TargetValueObject> {

    @Override
    public TargetValueObject createObject(Attributes arg0) throws Exception {
        TargetValueObject o = new TargetValueObject();
        o.setValue(Double.parseDouble(arg0.getValue("target")));
        return  o;
    }

}
