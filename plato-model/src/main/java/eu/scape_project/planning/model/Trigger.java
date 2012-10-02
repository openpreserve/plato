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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Represents a certain trigger. Only triggers that are selected by the user are stored
 * in the preservation plan. The list of existing triggers is defined in {@link #getAllDefinedTriggers()}
 *
 * @author Hannes Kulovits
 */
@Entity(name="ttrigger")
public class Trigger implements Serializable{

    private static final long serialVersionUID = 979031314358185939L;

    public Trigger() {
    }
    
    public Trigger(TriggerType type)  {
        setType(type);
    }
    
    @Lob
    private String description;

    private boolean active;
    
    private TriggerType type;

    public TriggerType getType() {
        return type;
    }

    public void setType(TriggerType type) {
        this.type = type;
    }

    public Trigger(int id, TriggerType type) {
        this.type = type;
        this.id = id;
    }

    /**
     * ID of the trigger
     */
    @Id
    @GeneratedValue
    private int id;

    @Override
    public Trigger clone() {
        Trigger t = new Trigger(this.id, this.type);
        return t;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /*
    @Transient
    public static List<Trigger> getAllDefinedTriggers(){
        ArrayList<Trigger> triggers = new ArrayList<Trigger>();
        triggers.add(new Trigger(1, "New Collection"));
        triggers.add(new Trigger(2, "Periodic Review"));
        triggers.add(new Trigger(3, "Changed Environment"));
        triggers.add(new Trigger(4, "Changed Objective"));
        triggers.add(new Trigger(5, "Changed Collection Profile"));
        return triggers;
    }
    */
}
