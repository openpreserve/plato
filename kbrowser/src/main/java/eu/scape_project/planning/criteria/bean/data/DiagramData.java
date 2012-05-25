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

public class DiagramData {

	/**
	 * Title of the data set.
	 */
	private String title = "";
	/**
	 * Title of the data sources.
	 */
	private String sourcesTitle = "";
	/**
	 * Maximum value to display.
	 */
	private Double maxValue = null;
	/**
	 * Format string for output (e.g. %.3f).
	 */
	private String formatString = "";
	/**
	 * List of labels, one for each data source.
	 */
	private List<String> sourceLabels = new ArrayList<String>(0);
	/**
	 * Labels for each series. The first label is used for the potential series,
	 * the second for the range series.
	 */
	private List<String> seriesLabels = new ArrayList<String>(0);

	public DiagramData() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSourcesTitle() {
		return sourcesTitle;
	}

	public void setSourcesTitle(String sourcesTitle) {
		this.sourcesTitle = sourcesTitle;
	}

	public List<String> getSourceLabels() {
		return sourceLabels;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public String getFormatString() {
		return formatString;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	public void setSourceLabels(List<String> sourceLabels) {
		this.sourceLabels = sourceLabels;
	}

	public List<String> getSeriesLabels() {
		return seriesLabels;
	}

	public void setSeriesLabels(List<String> seriesLabels) {
		this.seriesLabels = seriesLabels;
	}

}
