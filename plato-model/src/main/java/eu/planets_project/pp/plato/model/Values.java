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

package eu.planets_project.pp.plato.model;

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

import eu.planets_project.pp.plato.model.values.Value;

/**
 * Comprises a list of {@link eu.planets_project.pp.plato.model.values.Value} objects and
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
