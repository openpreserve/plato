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

package eu.scape_project.planning.model;

/**
 * Differentiates two aggregation modes:
 * <ul>
 * <li>WORST which is the minimum value over the sample records' results</li>
 * <li>AVERAGE meaning the average over the sample records' results</li>
 * </ul>
 *
 * @author Hannes Kulovits
 */
public enum SampleAggregationMode {

    /**
     * good for boolean, probably also for ordinal values
     */
    WORST ("Worst result"),

    /**
     * good for numeric values
     */
    AVERAGE ("Arithmetic mean");

    public SampleAggregationMode[] getEnumValues() {
        return SampleAggregationMode.values();
    }

    private String niceName;

    private SampleAggregationMode(String niceName) {
        this.niceName = niceName;
    }

    public String toString() {
        return this.niceName;
    }

    /**
     * @return string value of WORST or AVERAGE.
     */
    public String getName() {
        return this.toString();
    }
}
