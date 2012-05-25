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
package eu.scape_project.planning.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Range;

/**
 * Represents the target value all measured values from experiments must be mapped to.
 * Target value must be between 0.0 and 5.0.
 *
 * JPA note: need this for JPA: OrdinalTransformer.mapping
 *
 * @author Christoph Becker
 *
 */
@Entity
public class TargetValueObject implements Serializable {

    private static final long serialVersionUID = 4355479396527254031L;

    @Id
    @GeneratedValue
    private int id;

    @Range(min=0,max=5)
    private double value = 0.0;

    public TargetValueObject() {
        
    }
    
    public TargetValueObject(double value) {
        setValue(value);
    }
    
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public TargetValueObject clone() {
        TargetValueObject t = new TargetValueObject();
        t.setId(0);
        t.setValue(value);
        return t;
    }
}
