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

    private String measureId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Value value;

    public Measurement() {

    }

    public Measurement(String measureId, String value) {
        this.measureId = measureId;
        this.value = new FreeStringValue();
        ((FreeStringValue) this.value).setValue(value);
    }

    public Measurement(String measureId, double value) {
        this.measureId = measureId;
        this.value = new PositiveFloatValue();
        ((PositiveFloatValue) this.value).setValue(value);
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

    public String getMeasureId() {
        return measureId;
    }

    public void setMeasureId(String measureId) {
        this.measureId = measureId;
    }
}
