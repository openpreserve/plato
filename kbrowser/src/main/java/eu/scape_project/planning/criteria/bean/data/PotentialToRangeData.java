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
package eu.scape_project.planning.criteria.bean.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data structure to pass to the client to display the relation of potential to
 * range.
 * 
 * @author Markus Plangg
 */
public class PotentialToRangeData extends DiagramData implements Serializable {

	private static final long serialVersionUID = 4158027782317837386L;

	/**
	 * List of potential values.
	 */
	private List<Double> potentialSeries = new ArrayList<Double>(0);

	/**
	 * List of range values.
	 */
	private List<Double> rangeSeries = new ArrayList<Double>(0);

	public List<Double> getPotentialSeries() {
		return potentialSeries;
	}

	public void setPotentialSeries(List<Double> potentialSeries) {
		this.potentialSeries = potentialSeries;
	}

	public List<Double> getRangeSeries() {
		return rangeSeries;
	}

	public void setRangeSeries(List<Double> rangeSeries) {
		this.rangeSeries = rangeSeries;
	}
}
