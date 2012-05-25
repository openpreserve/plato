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
package eu.scape_project.planning.model.values;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


/**
 * This is the target value to which everything gets transformed
 * @author Christoph Becker
 */
@Entity
@DiscriminatorValue("T")
public class TargetValue extends Value implements Comparable<TargetValue>{

    @Transient
    protected String displayName = "Target value";
    
    private static final long serialVersionUID = 5014527595089555687L;

    public TargetValue() {
    }

    @Column(name = "target_value")
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    public double value() {
        return value;
    }
    public int compareTo(TargetValue v) {
        return Double.valueOf(this.getValue()).compareTo(v.getValue());
    }
    
    @Override
    public void parse(String text) {
        setValue(Double.parseDouble(text));
    }
    
}
