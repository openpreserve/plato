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
package eu.scape_project.planning.model.measurement;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.values.FreeStringValue;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.Value;

@Entity
public class Measurement implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1189511961248081431L;

    @Id
    @GeneratedValue
    private int id;
    
    @ManyToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private MeasurableProperty property;
    
    @ManyToOne(cascade=CascadeType.ALL)
    private Value value;
    
    public Measurement() {
        
    }
    
    public Measurement(String propertyName, String value) {
        MeasurableProperty p = new MeasurableProperty();
        p.setName(propertyName);
        p.setScale(new FreeStringScale());
        FreeStringValue s = (FreeStringValue) p.getScale().createValue();
        s.setValue(value);
        this.setProperty(p);
        this.setValue(s);
    }
    
    public Measurement(String propertyName, double value) {
        MeasurableProperty p = new MeasurableProperty();
        p.setName(propertyName);
        p.setScale(new PositiveFloatScale());
        PositiveFloatValue v = (PositiveFloatValue) p.getScale().createValue();
        v.setValue(value);
        this.setProperty(p);
        this.setValue(v);
    }
    
    public MeasurableProperty getProperty() {
        return property;
    }
    public void setProperty(MeasurableProperty property) {
        this.property = property;
    }
    public Value getValue() {
        return value;
    }
    public void setValue(Value value) {
        this.value = value;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
