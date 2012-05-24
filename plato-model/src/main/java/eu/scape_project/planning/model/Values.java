/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.constraints.Length;

import eu.scape_project.planning.model.values.Value;

/**
 * Comprises a list of {@link eu.scape_project.planning.model.values.Value} objects and
 * a {@link #comment}.
 *
 * We have values actually per
 * <ul>
 * <li> preservation strategy ({@link Alternative}),</li>
 * <li> leaf node (of course), AND </li>
 * <li> sample record.</li>
 * </ul>
 *
 * @author Hannes Kulovits
 */
@Entity(name="ValueList")
public class Values implements Serializable {

    private static final long serialVersionUID = -5716708734333958355L;

    @OneToMany(fetch = FetchType.EAGER)
    @IndexColumn(name="indexcol",base=1)
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<Value> list = new ArrayList<Value>();

    @Id
    @GeneratedValue
    private int id;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     *
     * Please note that when chaning something here don't forget to update the
     * length restriction in xhtml!
     */
    @Length(max = 2000000)
    @Column(length = 200000)
    private String comment;

    public int size() {
        return list.size();
    }

    public Value getValue(int record) {
        return list.get(record);
    }

    public void setValue(int record, Value v) {
        list.set(record, v);
    }

    public void add(Value v) {
        list.add(v);
    }

    public List<Value> getList() {
        return list;
    }

    public void setList(List<Value> list) {
        this.list = list;
    }

    /**
     * removes excess value objects that are over the threshold
     * @param size number of {@link Value} objects that SHOULD be in here
     * @return number of {@link Value} objects removed
     */
    public int removeLooseValues(int size) {
        int number = 0;
        while (size < list.size()) {
            list.remove(list.size()-1);
            number++;
        }
        return number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
