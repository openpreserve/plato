package eu.scape_project.pw.planning.criteria.bean.data;

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