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
package eu.planets_project.pp.plato.model.values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Contains a list of {@link eu.planets_project.pp.plato.model.values.TargetValue} and
 * aggregation methods {@link #average()} and {@link #worst()}.
 *
 * @author Hannes Kulovits
 */
public class TargetValues implements Serializable{

    private static final long serialVersionUID = 301277576213442803L;

    private List<TargetValue> list = new ArrayList<TargetValue>();

    public int size() {
        return list.size();
    }

    public Value getValue(int record) {
        return list.get(record);
    }

    public void setValue(int record, TargetValue v) {
        list.set(record, v);
    }

    public void add(TargetValue v) {
        list.add(v);
    }

    public List<TargetValue> getList() {
        return list;
    }

    public List<TargetValue> list() {
        return list;
    }

    /**
     * Returns the min value over the sample records' results
     * - we only support this operation after transformation.
     */
    public double worst() {
        if (list.isEmpty()) {
            return -1;
        }
        double min = Double.MAX_VALUE;
        for (TargetValue v : list) {
            if (v.getValue() < min) {
                min = v.getValue();
            }
        }
        return min;
    }


    /**
     * returns the average over the sample records' results
     * - we only support this operation after transformation.
     */
    public double average() {
        if (list.isEmpty()) {
            return -1;
        }

        double sum = 0.0;
        for (TargetValue v : list) {
            sum += v.getValue();
        }

        return sum / list.size();
    }
}
