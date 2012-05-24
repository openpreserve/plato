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
package at.tuwien.minireef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A result set returned by {@link MiniREEF}
 * {@link MiniREEF#resolve(String)}
 * {@link MiniREEFEvaluator#evaluate(eu.scape_project.planning.model.Alternative, List, eu.scape_project.planning.evaluation.IStatusListener)}
 * @author cbu
 *
 */
public class ResultSet implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 2246813679115752182L;

    /**
     * list of column names
     */
    private List<String> columns = new ArrayList<String> ();
    
    /**
     * a list of values per column
     */
    private List<List<String>> rows = new ArrayList<List<String>>();
    public ResultSet() {
        
    }
    
    /**
     * defines the names of all columns
     * - removes existing values
     * 
     * @param cols
     */
    public void setColumnNames(List<String> cols) {
        columns = cols;

        rows.clear();
        for(int i = 0; i < cols.size(); i++) {
            rows.add(new ArrayList<String>());
        }
    }
    
    public List<String> getColumnNames(){
        return columns;
    }
    /**
     * adds a set of values - one per column - to the result set
     * 
     * @param values
     * @throws IllegalArgumentException
     */
    public void addRow(List<String> values) throws IllegalArgumentException{
        if (values.size() != columns.size()) {
            throw new IllegalArgumentException("Number of values does not match the number of predefined columns");
        }
        for(int i = 0; i < values.size(); i++) {
            rows.get(i).add(values.get(i));
        }
    }
    
    
    /**
     * returns all values of a given column
     * @param column
     * @return
     */
    public List<String> getColResults(String column){
        int i = columns.indexOf(column);
        if (i < 0) {
            return null;
        }
        return rows.get(i);
    }
    
    /**
     * returns a result ~tuple
     * @param i
     * @return
     */
    public List<String> getRow(int index) {
        if ((rows.size() == 0) || (rows.get(0).size() <= index)) {
            return null;
        }
        List<String> row = new ArrayList<String>();
        for (int i = 0; i < columns.size(); i++) {
            row.add(rows.get(i).get(index));
        }
        return row;
    }
    
    /**
     * returns the number of rows
     * @return
     */
    public int size() {
        if (rows.size() == 0) {
            return 0;
        }
        return rows.get(0).size();
    }
    
    
}
