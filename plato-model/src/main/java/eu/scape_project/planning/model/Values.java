/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,  
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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import eu.scape_project.planning.model.values.Value;

/**
 * Comprises a list of {@link eu.scape_project.planning.model.values.Value}
 * objects and a {@link #comment}.
 * 
 * We have values actually per
 * <ul>
 * <li>preservation strategy ({@link Alternative}),</li>
 * <li>leaf node (of course), AND</li>
 * <li>sample record.</li>
 * </ul>
 * 
 * @author Hannes Kulovits
 */
@Entity(name = "ValueList")
public class Values implements Serializable {

    private static final long serialVersionUID = -5716708734333958355L;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Value> list = new ArrayList<Value>();

    @Id
    @GeneratedValue
    private int id;

    @Lob
    private String comment;

    /**
     * Returns the number of values.
     * 
     * @return the number of values
     */
    public int size() {
        return list.size();
    }

    /**
     * Gets a value at the provided record position.
     * 
     * @param record
     *            the record position
     * @return the value
     */
    public Value getValue(int record) {
        return list.get(record);
    }

    /**
     * Sets a value at the provided record position.
     * 
     * @param record
     *            the record position
     * @param v
     *            the value to set
     */
    public void setValue(int record, Value v) {
        list.set(record, v);
    }

    /**
     * Adds a value to the list of values.
     * 
     * @param v
     *            the value to add
     */
    public void add(Value v) {
        list.add(v);
    }

    /**
     * Removes excess value objects that are over the threshold.
     * 
     * @param size
     *            number of {@link Value} objects that SHOULD be in here
     * @return number of {@link Value} objects removed
     */
    public int removeLooseValues(int size) {
        int number = 0;
        while (size < list.size()) {
            list.remove(list.size() - 1);
            number++;
        }
        return number;
    }

    // ---------- getter/setter ----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Value> getList() {
        return list;
    }

    public void setList(List<Value> list) {
        this.list = list;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
