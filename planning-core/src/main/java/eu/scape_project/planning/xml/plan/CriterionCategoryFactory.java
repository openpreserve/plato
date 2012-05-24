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
package eu.scape_project.planning.xml.plan;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

import eu.scape_project.planning.model.measurement.CriterionCategory;

public class CriterionCategoryFactory extends AbstractObjectCreationFactory<CriterionCategory> {

    @Override
    public CriterionCategory createObject(Attributes arg0) throws Exception {
        String name = arg0.getValue("name");
        if (name!= null) {
            return CriterionCategory.valueOf(name);
        }
        return null;
    }

}
