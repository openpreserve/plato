package eu.scape_project.planning.criteria.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.component.SortOrder;
import org.slf4j.Logger;

import eu.scape_project.planning.criteria.bean.data.DiagramData;
import eu.scape_project.planning.criteria.bean.data.PotentialToRangeData;
import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.kbrowser.CriteriaTreeNode;

/**
 * Session Bean implementation class CriteriaSetsSummaryView
 */
@Stateful
@Named("criteriaSetsFull")
@SessionScoped
public class CriteriaSetsFullView implements Serializable {

	private static final long serialVersionUID = 2718623204104697111L;

	@Inject	private Logger log;

	@Inject
	private CriteriaHierarchyHelperBean criteriaHierarchyHelperBean;

	/**
	 * Sort order for summary table
	 */
	private SortOrder[] summaryTableSortOrder = { SortOrder.ascending,
			SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
			SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
			SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
			SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
			SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
			SortOrder.unsorted, SortOrder.unsorted };

	/**
	 * The currently selected criteria set.
	 */
	private CriteriaHierarchy selectedCriteriaSet;

	public CriteriaHierarchy getSelectedCriteriaSet() {
		return selectedCriteriaSet;
	}

	public void setSelectedCriteriaSet(CriteriaHierarchy selectedCriteriaSet) {
		if (selectedCriteriaSet == null) {
			log.debug("setSelectedCriteriaSet: null");
		} else {

			log.debug("setSelectedCriteriaSet: " + selectedCriteriaSet.getId()
					+ " - " + selectedCriteriaSet.getName());
		}
		this.selectedCriteriaSet = selectedCriteriaSet;
	}

	/**
	 * Returns a data object that holds data that shows the relation of
	 * potential to range for all structural nodes of the selected criteria set.
	 * 
	 * @return The data object
	 */
	public DiagramData getSelectedPotentialToRangeData() {

		// Check if criteria set is selected
		if (selectedCriteriaSet == null) {
			return null;
		}

		log.debug("Adding potential and range of node "
				+ selectedCriteriaSet.getName());

		// Create data object
		PotentialToRangeData data = new PotentialToRangeData();
		// Title
		data.setTitle("Potential to Range of " + selectedCriteriaSet.getName());
		data.setSourcesTitle("Structural nodes");

		data.setFormatString("%.3f");

		// Label of series
		ArrayList<String> seriesLabels = new ArrayList<String>();
		seriesLabels.add("SIF4: Potential");
		seriesLabels.add("SIF6: Range");

		// Data lists for structural nodes
		ArrayList<String> sourceLabels = new ArrayList<String>();
		ArrayList<Double> potentialSeries = new ArrayList<Double>();
		ArrayList<Double> rangeSeries = new ArrayList<Double>();

		// Get root of tree
		CriteriaNode treeRoot = selectedCriteriaSet.getCriteriaTreeRoot();

		// Add data of root
		sourceLabels.add(treeRoot.getName());
		potentialSeries.add(treeRoot.getImportanceFactorSIF4());
		rangeSeries.add(treeRoot.getImportanceFactorSIF6());

		// Add data of other structural nodes
		for (CriteriaTreeNode criteriaTreeNode : treeRoot
				.getAllSuccessiveTreeNodes()) {
			if (criteriaTreeNode instanceof CriteriaNode) {
				CriteriaNode criteriaNode = (CriteriaNode) criteriaTreeNode;
				sourceLabels.add(criteriaNode.getName());
				potentialSeries.add(criteriaNode.getImportanceFactorSIF4());
				rangeSeries.add(criteriaNode.getImportanceFactorSIF6());
			}
		}

		// Set data
		data.setSeriesLabels(seriesLabels);
		data.setSourceLabels(sourceLabels);
		data.setPotentialSeries(potentialSeries);
		data.setRangeSeries(rangeSeries);

		return data;
	}

	public List<CriteriaHierarchy> getAllCriteriaSetsForSummary() {
		return criteriaHierarchyHelperBean
				.getAllCriteriaHierarchiesForSummary();
	}

	public SortOrder[] getSummaryTableSortOrder() {
		return summaryTableSortOrder;
	}

	public void setSummaryTableSortOrder(SortOrder[] summaryTableSortOrder) {
		this.summaryTableSortOrder = summaryTableSortOrder;
	}

	/**
	 * Sets the sort order of the specified column in summary table.
	 * 
	 * @param column
	 *            Column index starting from 0.
	 */
	public void sortSummaryTableByColumn(int column) {
		log.debug("Sorting Criterion Impact Factors by IF"
				+ Integer.toString(column));
		SortOrder currentColumn = summaryTableSortOrder[column];
		clearSummaryTableSortOrders();
		if (currentColumn.equals(SortOrder.descending)) {
			summaryTableSortOrder[column] = SortOrder.ascending;
		} else {
			summaryTableSortOrder[column] = SortOrder.descending;
		}
	}

	/**
	 * Clears the sort orders for all columns of the summary table.
	 */
	private void clearSummaryTableSortOrders() {
		for (int i = 0; i < summaryTableSortOrder.length; i++) {
			summaryTableSortOrder[i] = SortOrder.unsorted;
		}
	}

	/**
	 * Export criteria sets summary to CSV.
	 */
	public void exportCriteriaSetsFullToCSV() {
		String csvString = "";
		// Header
		csvString += "Name;size;SIF1;SIF2;SIF3;SIF4;SIF5;SIF6;SIF7;SIF8;SIF9;SIF10;SIF11;SIF12;SIF13;SIF14;SIF15;SIF16\n";

		// Assemble CSV-data
		for (CriteriaHierarchy criteriaHierarchy : criteriaHierarchyHelperBean
				.getAllCriteriaHierarchiesForSummary()) {
			csvString += criteriaHierarchy.getName() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getAllSuccessiveLeaves().size()
					+ ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF1() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF2() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF3() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF4() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF5() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF6() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF7() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF8() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF9() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF10() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF11() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF12() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF13() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF14() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF15() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF16() + ";";
			csvString += "\n";
		}

		// Send CSV-file to browser
		byte[] csvByteStream = csvString.getBytes();
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		response.setHeader("Content-disposition",
				"attachment; filename= CriteriaHierarchiesSummary.csv");
		response.setContentLength(csvString.length());
		response.setContentType("application/vnd.ms-excel");
		try {
			response.getOutputStream().write(csvByteStream);
			response.getOutputStream().flush();
			response.getOutputStream().close();
			context.responseComplete();
			log.debug("Exported CriteriaHierarchiesSummary successfully to CSV-File.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
