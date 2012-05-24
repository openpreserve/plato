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
public class PotentialToRangeMaxData extends DiagramData implements
		Serializable {

	private static final long serialVersionUID = 4158027782317837386L;

	/**
	 * List of potential values.
	 */
	private List<Double> seriesData = new ArrayList<Double>(0);

	public List<Double> getSeriesData() {
		return seriesData;
	}

	public void setSeriesData(List<Double> seriesData) {
		this.seriesData = seriesData;
	}
}