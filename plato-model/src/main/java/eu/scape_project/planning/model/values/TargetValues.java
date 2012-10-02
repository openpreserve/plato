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
package eu.scape_project.planning.model.values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Contains a list of {@link eu.scape_project.planning.model.values.TargetValue} and
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
