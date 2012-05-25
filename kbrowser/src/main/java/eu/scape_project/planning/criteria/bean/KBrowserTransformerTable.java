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
package eu.scape_project.planning.criteria.bean;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import eu.scape_project.planning.model.transform.TransformationMode;

/**
 * Class used in KBrowserView to represent the Transformer-table and the
 * Transformer-chart.
 * 
 * @author Markus Hamm
 */
public class KBrowserTransformerTable {
	/**
	 * Target values displayed in the header row of the table.
	 */
	private List<Double> targetValues;

	/**
	 * Each row represents a transformer. The corresponding map is of the form
	 * TargetValue -> Transformer mapping (ordinal value, or threshold [as
	 * String]).
	 */
	private List<Map<Double, String>> transformerMappings;

	/**
	 * String which identifies each row in transformerMapping.
	 */
	private List<String> transformerMappingIdentification;

	/**
	 * Indicates if this table represents ordinal or numeric transformer.
	 */
	private Boolean ordinal;

	/**
	 * X-axis title of the chart. It must be a class variable because it is
	 * varying (depending on user criterion-selection [numeric/ordinal])
	 */
	// private String chartXAxisTitle;

	/**
	 * Number of X-axis ticks of the chart. It must be a class variable because
	 * it is varying (depending on user criterion-selection [numeric/ordinal])
	 */
	// private Double chartNumberOfXAxisTicks;

	public KBrowserTransformerTable() {
		targetValues = new ArrayList<Double>();
		transformerMappings = new ArrayList<Map<Double, String>>();
		transformerMappingIdentification = new ArrayList<String>();
		ordinal = true;
	}

	/**
	 * Adds a new possible targetValue.
	 * 
	 * @param value
	 *            value of TargetValue to add
	 */
	public void addTargetValue(Double value) {
		if (!targetValues.contains(value)) {
			targetValues.add(value);
			Collections.sort(targetValues);
		}
	}

	/**
	 * Adds a new transformer (row) to the table.
	 * 
	 * @param mapping
	 *            mapping of the transformer.
	 * @param identification
	 *            transformer identification string.
	 */
	public void addTransformerMapping(Map<Double, String> mapping,
			String identification) {
		transformerMappings.add(mapping);
		transformerMappingIdentification.add(identification);
	}

	/**
	 * Method responsible for returning the HTML formatted Table rows (except
	 * the header) as String representation for output in view. The output looks
	 * like this: <td>mapping1</td><td>Yes</td><td>No</td>... Remark: The
	 * inclusion of HTML code in the output is necessary because in this JSF
	 * version two nested foreach loops (rows + columns) do not work properly.
	 * The datatable/datamodel approach is not possible because of the flexible
	 * number of columns.
	 * 
	 * @return returning HTML formatted Table rows as String.
	 */
	// public List<String> getHtmlFormattedTableRows() {
	// List<String> rows = new ArrayList<String>();
	// int rowIndex = 0;
	//
	// for (Map<Double,String> transformerMapping : transformerMappings) {
	// String rowStr = "";
	// rowStr = rowStr + "<td>" + transformerMappingIdentification.get(rowIndex)
	// + "</td>";
	//
	// for (Double columnValue : targetValues) {
	// if (transformerMapping.containsKey(columnValue)) {
	// rowStr = rowStr + "<td>" + transformerMapping.get(columnValue) + "</td>";
	// }
	// else {
	// rowStr = rowStr + "<td></td>";
	// }
	// }
	//
	// rows.add(rowStr);
	// rowIndex++;
	// }
	//
	// return rows;
	// }

	/**
	 * Method responsible for getting all data-points used to visualize the
	 * transformers in a chart.
	 * 
	 * @return A list of chart data-points for each transformer.
	 */
	public List<List<Point2D>> getChartDataPoints() {
		List<List<Point2D>> listOfChartDataPoints = new ArrayList<List<Point2D>>();

		// Ordinal Transformer
		if (ordinal) {
			// Ordinal transformer cannot be visualized properly in a chart - no
			// data-points are created.
			return listOfChartDataPoints;

			/*
			 * Map<String, Double> allOrdinalsToNumberMap = new HashMap<String,
			 * Double>(); Double ordinalsToNumberIndex = 1d;
			 * 
			 * // collect all measured ordinal-values(String) and map them to
			 * numbers because only numbers can be used in the chart. for
			 * (Map<Double,String> transformerMapping : transformerMappings) {
			 * for (String ordinalValue : transformerMapping.values()) { if
			 * (!allOrdinalsToNumberMap.containsKey(ordinalValue)) {
			 * allOrdinalsToNumberMap.put(ordinalValue, ordinalsToNumberIndex);
			 * ordinalsToNumberIndex++; } } }
			 * 
			 * // now for each transformer add the relevant points for
			 * (Map<Double,String> transformerMapping : transformerMappings) {
			 * List<XYDataPoint> dataPoints = new ArrayList<XYDataPoint>();
			 * 
			 * // for each available ordinal value, check if a mapping is
			 * present - if so add a point for (String ordinalValue :
			 * allOrdinalsToNumberMap.keySet()) { for (Double transformedValue :
			 * transformerMapping.keySet()) { if
			 * (transformerMapping.get(transformedValue).equals(ordinalValue)) {
			 * XYDataPoint dataPoint = new XYDataPoint();
			 * dataPoint.setX(allOrdinalsToNumberMap.get(ordinalValue));
			 * dataPoint.setY(transformedValue); dataPoints.add(dataPoint); } }
			 * }
			 * 
			 * listOfChartDataPoints.add(dataPoints); }
			 * 
			 * // set chart parameters for GUI chartNumberOfXAxisTicks =
			 * ordinalsToNumberIndex - 1; chartXaxisTitle = ""; // this
			 * code-construct is necessary to support output of "x-axis legend"
			 * ordered by numeric value, which is mandatory for usability
			 * List<Double> sortedAllOrdinalsToNumberMapValues = new
			 * ArrayList<Double>(allOrdinalsToNumberMap.values());
			 * Collections.sort(sortedAllOrdinalsToNumberMapValues); for (Double
			 * numericValue : sortedAllOrdinalsToNumberMapValues) { for (String
			 * ordinalValue : allOrdinalsToNumberMap.keySet()) { if
			 * (allOrdinalsToNumberMap.get(ordinalValue) == numericValue) {
			 * chartXaxisTitle = chartXaxisTitle + numericValue + "=" +
			 * ordinalValue + "; "; } } }
			 */
		}
		// Numeric Transformer
		else {
			int index = 0;

			for (Map<Double, String> transformerMapping : transformerMappings) {
				List<Point2D> dataPoints = new ArrayList<Point2D>();

				Double previousTargetValue = 0d;
				List<Double> sortedTransformerMappingKeys = new ArrayList<Double>(
						transformerMapping.keySet());
				Collections.sort(sortedTransformerMappingKeys);
				for (Double targetValue : sortedTransformerMappingKeys) {
					// if the numeric transformer uses threshold-stepping,
					// additional data-points need to be added to construct
					// steps in the chart
					if (transformerMappingIdentification.get(index).contains(
							TransformationMode.THRESHOLD_STEPPING.getName().toLowerCase())) {
						Point2D dataPoint = new Point2D.Double(
								Double.parseDouble(transformerMapping
										.get(targetValue)), previousTargetValue);
						dataPoints.add(dataPoint);
					}

					Point2D dataPoint = new Point2D.Double(
							Double.parseDouble(transformerMapping
									.get(targetValue)), targetValue);
					previousTargetValue = targetValue;
					dataPoints.add(dataPoint);
				}

				listOfChartDataPoints.add(dataPoints);
				index++;
			}

			// set chart parameter for GUI
			// chartNumberOfXAxisTicks = null;
			// chartXAxisTitle = "Measured Values";
		}

		return listOfChartDataPoints;
	}

	public void setTargetValues(List<Double> targetValues) {
		this.targetValues = targetValues;
	}

	public List<Double> getTargetValues() {
		return targetValues;
	}

	public void setOrdinal(Boolean ordinal) {
		this.ordinal = ordinal;
	}

	public Boolean getOrdinal() {
		return ordinal;
	}

	public void setTransformerMappings(
			List<Map<Double, String>> transformerMappings) {
		this.transformerMappings = transformerMappings;
	}

	public List<Map<Double, String>> getTransformerMappings() {
		return transformerMappings;
	}

	public void setTransformerMappingIdentification(
			List<String> transformerMappingIdentification) {
		this.transformerMappingIdentification = transformerMappingIdentification;
	}

	public List<String> getTransformerMappingIdentification() {
		return transformerMappingIdentification;
	}

	// public void setChartXAxisTitle(String chartXaxisTitle) {
	// this.chartXAxisTitle = chartXaxisTitle;
	// }
	//
	// public String getChartXAxisTitle() {
	// return chartXAxisTitle;
	// }
	//
	// public void setChartNumberOfXAxisTicks(Double chartNumberOfXAxisTicks) {
	// this.chartNumberOfXAxisTicks = chartNumberOfXAxisTicks;
	// }
	//
	// public Double getChartNumberOfXAxisTicks() {
	// return chartNumberOfXAxisTicks;
	// }
}
