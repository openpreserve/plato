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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * View Bean responsible for CriteriaSetsSummaryView
 */
@Named("criteriaSetsSummary")
@SessionScoped
public class CriteriaSetsSummaryView implements Serializable {

	private static final long serialVersionUID = 2718623204104697111L;

	@Inject private Logger log;

	@Inject
	private CriteriaHierarchyHelperBean criteriaHierarchyHelperBean;

	/**
	 * Sort order for summary table
	 */
	private SortOrder[] summaryTableSortOrder = { SortOrder.unsorted,
			SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted, SortOrder.unsorted,
			SortOrder.unsorted };
	
	private List<CriteriaHierarchy> allCriteriaSetsForSummary;

	/**
	 * The currently selected criteria set.
	 */
	private CriteriaHierarchy selectedCriteriaSet;

	/**
	 * Method responsible for data initialization before displaying the page.
	 */
	public void init() {
		this.allCriteriaSetsForSummary = new ArrayList<CriteriaHierarchy>(criteriaHierarchyHelperBean.getAllCriteriaHierarchiesForSummary());
	}
	
	/**
	 * Method responsible for selecting/setting the criteria-set of interest for further actions.
	 * 
	 * @param criteriaSet Criteria-set to select.
	 */
	public void selectCriteriaSet(CriteriaHierarchy criteriaSet) {
		if (criteriaSet == null) {
			log.debug("selectCriteriaSet: null");
		} else {
			log.debug("selectCriteriaSet: " + criteriaSet.getId() + " - " + criteriaSet.getName());
		}
		
		this.selectedCriteriaSet = criteriaSet;
	}
		
	/**
	 * Returns the selected criteria set.
	 * 
	 * @return The currently selected criteria set
	 */
	public CriteriaHierarchy getSelectedCriteriaSet() {
		return selectedCriteriaSet;
	}

	/**
	 * Sets the selected criteria set.
	 * 
	 * @param selectedCriteriaSet
	 *            The criteria set
	 */
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
		data.setTitle("Potential Impact Range to Actual Impact of " + selectedCriteriaSet.getName());
		data.setSourcesTitle("Structural nodes");

		data.setFormatString("%.3f");

		// Label of series
		ArrayList<String> seriesLabels = new ArrayList<String>();
		seriesLabels.add("Potential Impact Range");
		seriesLabels.add("Actual Impact");

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

	/**
	 * Returns a data object that holds summary data that shows the relation of
	 * potential to range for all criteria sets.
	 * 
	 * @return The data object
	 */
	public DiagramData getSummaryPotentialToRangeData() {

		// Get data
		List<CriteriaHierarchy> criteriaSets = criteriaHierarchyHelperBean
				.getAllCriteriaHierarchiesForSummary();

		if (criteriaSets == null) {
			return null;
		}

		// Sort by SIF4
		Collections.sort(criteriaSets,
				Collections.reverseOrder(new Comparator<CriteriaHierarchy>() {

					@Override
					public int compare(CriteriaHierarchy set1,
							CriteriaHierarchy set2) {

						CriteriaNode treeRoot1 = set1.getCriteriaTreeRoot();
						CriteriaNode treeRoot2 = set2.getCriteriaTreeRoot();

						Double comp1 = new Double(treeRoot1
								.getImportanceFactorSIF7());
						Double comp2 = new Double(treeRoot2
								.getImportanceFactorSIF7());
						return comp1.compareTo(comp2);
					}

				}));

		// Create data object
		PotentialToRangeData data = new PotentialToRangeData();
		// Title
		data.setTitle("Maximum Impact to Average Impact");
		data.setSourcesTitle("Criteria sets");

		data.setFormatString("%.3f");

		// Label of series
		ArrayList<String> seriesLabels = new ArrayList<String>();
		seriesLabels.add("Maximum Impact");
		seriesLabels.add("Average Impact");

		// Data lists for criteria sets
		ArrayList<String> sourceLabels = new ArrayList<String>();
		ArrayList<Double> potentialSeries = new ArrayList<Double>();
		ArrayList<Double> rangeSeries = new ArrayList<Double>();

		for (CriteriaHierarchy criteriaSet : criteriaSets) {

			log.debug("Adding potential and range of node "
					+ criteriaSet.getName());

			// Source label
			sourceLabels.add(criteriaSet.getName());

			// Get root of tree
			CriteriaNode treeRoot = criteriaSet.getCriteriaTreeRoot();

			// Add values
			potentialSeries.add(treeRoot.getImportanceFactorSIF7());
			rangeSeries.add(treeRoot.getImportanceFactorSIF6());
		}

		// Set data
		data.setSeriesLabels(seriesLabels);
		data.setSourceLabels(sourceLabels);
		data.setPotentialSeries(potentialSeries);
		data.setRangeSeries(rangeSeries);
		return data;
	}

	/**
	 * Returns all criteria sets.
	 * 
	 * @return The criteria sets
	 */
	public List<CriteriaHierarchy> getAllCriteriaSetsForSummary() {
		return allCriteriaSetsForSummary;
	}

	/**
	 * Returns the tree root of the selected criteria set.
	 * 
	 * @return The tree root
	 */
	public List<CriteriaNode> getSelectedCriteriaSetTreeRoots() {
		List<CriteriaNode> result = new ArrayList<CriteriaNode>();
		if (selectedCriteriaSet != null) {
			result.add(selectedCriteriaSet.getCriteriaTreeRoot());
		}
		return result;
	}

	/**
	 * Returns the sort order
	 * 
	 * @return The sort order
	 */
	public SortOrder[] getSummaryTableSortOrder() {
		return summaryTableSortOrder;
	}

	/**
	 * Sets the sort order.
	 * 
	 * @param summaryTableSortOrder
	 *            The sort order
	 */
	public void setSummaryTableSortOrder(SortOrder[] summaryTableSortOrder) {
		this.summaryTableSortOrder = summaryTableSortOrder;
	}

	/**
	 * Sets the sort order of the specified column in summary table.
	 * 
	 * @param column
	 *            Column index starting from 0.
	 */
	public void sortSummaryTableByColumn(long lcolumn) {
            int column = (int)lcolumn;
            log.debug("Sorting Criterion Impact Factors by IF" + column);
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
	public void exportCriteriaSetsSummaryToCSV() {
		String csvString = "";
		// Header
		csvString += "Name;Size;SIF2;SIF6;SIF16\n";

		// Assemble CSV-data
		for (CriteriaHierarchy criteriaHierarchy : criteriaHierarchyHelperBean
				.getAllCriteriaHierarchiesForSummary()) {
			csvString += criteriaHierarchy.getName() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getAllSuccessiveLeaves().size()
					+ ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF2() + ";";
			csvString += criteriaHierarchy.getCriteriaTreeRoot()
					.getStringFormattedImportanceFactorSIF6() + ";";
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

	public void setAllCriteriaSetsForSummary(
			List<CriteriaHierarchy> allCriteriaSetsForSummary) {
		this.allCriteriaSetsForSummary = allCriteriaSetsForSummary;
	}
}
