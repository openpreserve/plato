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
