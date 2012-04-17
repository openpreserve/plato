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
package eu.planets_project.pp.plato.model.measurement;

import java.io.Serializable;
import java.util.Map;

import eu.planets_project.pp.plato.model.DetailedExperimentInfo;
import eu.planets_project.pp.plato.model.values.INumericValue;

public class MeasurementStatistics implements Serializable {
    private static final long serialVersionUID = 1588887112971264736L;

    public static ToolExperience generateToolExperience(Map<String,DetailedExperimentInfo> detailedInfos){
        ToolExperience toolExp = new ToolExperience();
        for (DetailedExperimentInfo info : detailedInfos.values()) {
            /* put measurements of sample files to toolExp */
            for (Measurement m : info.getMeasurements().values()) {
               /* for calculating the average only numeric measurements are of interest 
                  but we need the list of all available properties to write statistics to files */
               toolExp.addMeasurement(m);
            }
        }
        return toolExp;
    }
    
    public static DetailedExperimentInfo getAverage(ToolExperience toolExp){
        DetailedExperimentInfo averages = new DetailedExperimentInfo();
        for(String key:toolExp.getMeasurements().keySet()) {
            /* average can only calculated of numeric values */
            if (toolExp.getMeasurements().get(key). getList().get(0).getValue() instanceof INumericValue) {
               Measurement m = toolExp.getAverage(key);
               if (!Double.isInfinite(((INumericValue)m.getValue()).value()))
                  averages.getMeasurements().put(key, m);
            }
        }
        return averages;
    }
    
}
