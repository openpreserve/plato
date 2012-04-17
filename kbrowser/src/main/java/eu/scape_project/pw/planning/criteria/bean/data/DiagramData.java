package eu.scape_project.pw.planning.criteria.bean.data;

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