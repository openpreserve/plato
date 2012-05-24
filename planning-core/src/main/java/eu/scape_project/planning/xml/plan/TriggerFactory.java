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

import eu.scape_project.planning.model.Trigger;
import eu.scape_project.planning.model.TriggerType;

public class TriggerFactory extends AbstractObjectCreationFactory<Trigger> {

    @Override
    public Trigger createObject(Attributes arg0) throws Exception {
        Trigger trigger = new Trigger();
        
        
        // legacy import for old trigger definition pre plato-2.0:
        String id = arg0.getValue("id");
        String value = arg0.getValue("value");
        
        if (id != null) { // this means we have an old xml
            trigger.setActive(true);
            if ("1".equals(id)) {
                trigger.setType(TriggerType.NEW_COLLECTION);
            } else if ("2".equals(id)) {
                trigger.setType(TriggerType.PERIODIC_REVIEW);
            } else if ("3".equals(id)) {
                trigger.setType(TriggerType.CHANGED_ENVIRONMENT);
            } else if ("4".equals(id)) {
                trigger.setType(TriggerType.CHANGED_OBJECTIVE);
            } else if ("5".equals(id)) {
                trigger.setType(TriggerType.CHANGED_COLLECTION_PROFILE);
            } 
            trigger.setDescription(value);
        } else { // new xml >= plato 2.0
           trigger.setActive(Boolean.parseBoolean(arg0.getValue("active")));
           trigger.setDescription(arg0.getValue("description"));
           trigger.setType(TriggerType.valueOf(arg0.getValue("type")));
        }
        
        return trigger;
    }
}
