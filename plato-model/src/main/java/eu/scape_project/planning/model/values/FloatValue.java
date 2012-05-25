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

import eu.scape_project.planning.model.util.FloatFormatter;

@Entity
@DiscriminatorValue("F")
public class FloatValue extends Value implements INumericValue {
    private static final long serialVersionUID = 5692740794103970288L;
    
    @Transient
    private FloatFormatter formatter;
    
    @Column(name = "float_value")
    private double value;

    public double value() {
        return value;
    }
    
    @Override
    public String toString() {
        if (formatter == null) {
            formatter = new FloatFormatter();
        }
        return formatter.formatFloatPrecisly(value);
    }

    @Override
    public String getFormattedValue() {
        if (formatter == null) {
            formatter = new FloatFormatter();
        }
        return formatter.formatFloat(value);
    }
    
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    @Override
    public void parse(String text) {
        setValue(Double.parseDouble(text));        
    }
    
 }
