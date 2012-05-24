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
/**
 * 
 */
package eu.scape_project.pw.planning.xml.plan;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

import eu.scape_project.planning.model.ChangeLog;

/**
 * Helper class for {@link eu.scape_project.planning.model.ProjectImporter} to create a ChangeLog value of its XML representation. 
 * 
 * @author Michael Kraxner
 *
 */public class ChangeLogFactory extends AbstractObjectCreationFactory<ChangeLog> {

    @Override
    public ChangeLog createObject(Attributes arg0) throws Exception {
        ChangeLog c = new ChangeLog();
        TimestampFormatter formatter = new TimestampFormatter();
        c.setChangedBy(arg0.getValue("changedBy"));
        c.setCreatedBy(arg0.getValue("createdBy"));
        String changed = arg0.getValue("changed");
        String created = arg0.getValue("created");
        c.setChanged(formatter.parseTimestamp(changed));
        c.setCreated(formatter.parseTimestamp(created));
        return c;
    }

}
