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
import java.util.Map;

import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.values.INumericValue;

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
