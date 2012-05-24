package eu.scape_project.planning.plato.wfview.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.model.measurement.Criterion;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Metric;
import eu.scape_project.pw.planning.manager.CriteriaManager;

/**
 * Class responsible as supporting class for AJAX Criterion selection.
 * 
 * @author Markus Hamm
 */
public class CriterionSelector implements Serializable {
	private static final long serialVersionUID = -4868553688311289177L;

	@Inject	private Logger log;
	
    @Inject
	private CriteriaManager criteriaManager;
	
	private Collection<CriterionCategory> categories;
    private HashMap<String, CriterionCategory> categoriesMap;
    private CriterionCategory selectedCategory;
    
    private Collection<MeasurableProperty> allMeasurableProperties;
    private int allMeasurablePropertiesCount;
    
    private Collection<MeasurableProperty> filteredMeasurableProperties;
    private int filteredMeasurablePropertiesCount;
    private HashMap<String, MeasurableProperty> measurablePropertiesMap;
    private MeasurableProperty selectedMeasurableProperty;
    private String selectedMeasurablePropertyString;
    
    private List<Metric> metrics;
    private HashMap<String, Metric> metricsMap;
    private Metric selectedMetric;
    private String selectedMetricString;
    

    public CriterionSelector() {
    	categories = new ArrayList<CriterionCategory>();
    	for (CriterionCategory c: CriterionCategory.values()) {  
    		categories.add(c);
    	}
        constructCategoriesMap();
        selectedCategory = null;
        
        metrics = new ArrayList<Metric>();
        metricsMap = new HashMap<String, Metric>();
        selectedMetric = null;
        selectedMetricString = null;
    }
    
    public void init() {
        allMeasurableProperties = criteriaManager.getKnownProperties();
        ArrayList<MeasurableProperty> allMeasurablePropertiesSortable = new ArrayList<MeasurableProperty>(allMeasurableProperties);
        Collections.sort(allMeasurablePropertiesSortable);
        allMeasurableProperties = allMeasurablePropertiesSortable;
        allMeasurablePropertiesCount = allMeasurableProperties.size();
        filteredMeasurableProperties = new ArrayList<MeasurableProperty>(allMeasurableProperties);
        filteredMeasurablePropertiesCount = filteredMeasurableProperties.size();

        constructMeasurablePropertiesMap();
        selectedMeasurableProperty = null;
        selectedMeasurablePropertyString = null;
    }
    
    /* ----------------- Category Setup ----------------- */
    
    
    private void constructCategoriesMap() {
        categoriesMap = new HashMap<String, CriterionCategory>();
        for (CriterionCategory cat : CriterionCategory.values())
        {
            categoriesMap.put(cat.toString(), cat);
        }
    }
    
    private void constructMetricsWithMap(List<Metric> metrics) {
        // ATTENTION: Because of a Seam-Bug, this new creation of the metrics-list is mandatory!
        // If you just clear the list and fill it with new values, the view (s:selectItems) does not mention a change and therefore does not update the associated selectBox.
        // Related bug: https://issues.jboss.org/browse/JBSEAM-4382            
        setMetrics(metrics);
        
        metricsMap = new HashMap<String, Metric>();
        for (Metric m : this.metrics)
        {
            metricsMap.put(m.getMetricId(), m);
        }
    }
    
    /* ----------------- Property Setup ----------------- */
    
    private void constructMeasurablePropertiesMap() {
        measurablePropertiesMap = new HashMap<String, MeasurableProperty>();
        for (MeasurableProperty mp : allMeasurableProperties)
        {
            measurablePropertiesMap.put(mp.getName(), mp);
        }
    }
    
    /* ----------------- Category UI-Helper ----------------- */
    
    public void setSelectedMeasurableProperty(MeasurableProperty selectedMeasurableProperty) {
//        log.debug("setSelectedMeasurableProperty()");
        this.selectedMeasurableProperty = selectedMeasurableProperty;
    }

    public MeasurableProperty getSelectedMeasurableProperty() {
//        log.debug("getSelectedMeasurableProperty()=" + selectedMeasurableProperty);
        return selectedMeasurableProperty;
    }
    
    public Collection<CriterionCategory> getCategories() {
        return categories;
    }
    
    public void setCategories(Collection<CriterionCategory> categories) {
        this.categories = categories;
    }
    
    /* ----------------- Property UI-Helper ----------------- */

    public void setFilteredMeasurableProperties(Collection<MeasurableProperty> filteredMeasurableProperties) {
        this.filteredMeasurableProperties = filteredMeasurableProperties;
    }

    public Collection<MeasurableProperty> getFilteredMeasurableProperties() {
        return filteredMeasurableProperties;
    }          
    
    public void setSelectedMeasurablePropertyString(String selectedMeasurablePropertyString) {
        log.debug("setSelectedMeasurablePropertyString(" + selectedMeasurablePropertyString + ")");
        
        this.selectedMeasurablePropertyString = selectedMeasurablePropertyString;
        
        if (selectedMeasurablePropertyString == null)
        {
            selectedMeasurableProperty = null;
        }
        else
        {
            selectedMeasurableProperty = measurablePropertiesMap.get(selectedMeasurablePropertyString);
        }
    }
    
    public String getSelectedMeasurablePropertyString() {
//        log.debug("getSelectedMeasurablePropertyString()=" + selectedMeasurablePropertyString);
        return selectedMeasurablePropertyString;
    }
    
    public void setAllMeasurablePropertiesCount(int allMeasurablePropertiesCount) {
        this.allMeasurablePropertiesCount = allMeasurablePropertiesCount;
    }

    public int getAllMeasurablePropertiesCount() {
        return allMeasurablePropertiesCount;
    }
    
    public void setFilteredMeasurablePropertiesCount(
            int filteredMeasurablePropertiesCount) {
        this.filteredMeasurablePropertiesCount = filteredMeasurablePropertiesCount;
    }

    public int getFilteredMeasurablePropertiesCount() {
        return filteredMeasurablePropertiesCount;
    }

    /* ----------------- Metric UI-Helper ----------------- */

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }          
    
    public void setSelectedMetricString(String selectedMetricString) {
        log.debug("setSelectedMetricString(): " + selectedMetricString);
        
        this.selectedMetricString = selectedMetricString;
        
        if (selectedMetricString == null)
        {
            selectedMetric = null;
        }
        else
        {
            selectedMetric = metricsMap.get(selectedMetricString);
        }
    }
       
    public String getSelectedMetricString() {
//        log.debug("getSelectedMetricString(): " + selectedMetricString);
        return selectedMetricString;
    }
    
    /* ----------------- Category ValueChangeListener ----------------- */
    
    /**
     * Method responsible for handling the onchange-Events from Category-Selectbox in GUI.
     * All model-values are updated appropriate (including dependent Selectboxes). 
     */
    public void selectCategory() {
        // debug output
        if (selectedCategory == null)
        {
            log.info("Category: Nothing selected");
        }
        else
        {
            log.info("Category selected: " + selectedCategory.toString());
        }        
        
        filterMeasurableProperties();
    }
           
    /* ----------------- Property ValueChangeListener ----------------- */
    
    /**
     * Method responsible for handling the onchange-Events from Property-Selectbox in GUI.
     * All model-values are updated appropriate (including dependent Selectboxes). 
     */
    public void selectProperty() {
        log.debug("CALL selectProperty()");
        
        // debug output
        if (selectedMeasurableProperty == null)
        {
            log.info("Property: Nothing selected");
        }
        else
        {
            log.info("Property selected: " + selectedMeasurableProperty.getName());
        }
        
        updateMetrics();
    }

    public void filterMeasurableProperties() {
        // ATTENTION: Because of a Seam-Bug, this new creation of the filtered-measurableproperties-list is mandatory!
        // If you just clear the list and then refill it, the view (s:selectItems) does not mention a change and therefore does not update the associated selectBox.
        // Related bug: https://issues.jboss.org/browse/JBSEAM-4382
        Collection<MeasurableProperty> newFilteredMP = new ArrayList<MeasurableProperty>();
        
    	for (MeasurableProperty p : allMeasurableProperties) {
            if (selectedCategory == null || p.getCategory() == selectedCategory) {
                newFilteredMP.add(p);
            }
        }
        
        filteredMeasurableProperties.clear();
        filteredMeasurableProperties.addAll(newFilteredMP);
        setFilteredMeasurablePropertiesCount(newFilteredMP.size());
                
        // check if selected MeasurableProperty is still available in the new filtered list.
        Boolean mpStillInFilteredList = false;
        if (selectedMeasurableProperty != null) {
            for (MeasurableProperty mp : filteredMeasurableProperties) {
                if (mp.getPropertyId().equals(selectedMeasurableProperty.getPropertyId())) {
                    mpStillInFilteredList = true;
                    log.debug("Selected Property still available in new filtered list");
                }
            }
            
            // if the previous selected MeasuableProperty is not available any more in the new filtered list
            // set the selection to null (which also affects the metrics select)
            if (!mpStillInFilteredList) {
                setSelectedMeasurablePropertyString(null);
                log.debug("Reset Selected Property to null");
                updateMetrics();
            }
        }
    }
    
    /* ----------------- Metric ValueChangeListener ----------------- */
    
    /**
     * Method responsible for handling the onchange-Events from Metric-Selectbox in GUI.
     * All model-values are updated appropriate. 
     */
    public void selectMetric() {
        log.debug("CALL selectMetric()");
        
        // debug output
        if (selectedMetric == null)
        {
            log.debug("Metric: Nothing selected");
        }
        else
        {
            log.debug("Metric selected: " + selectedMetric.getMetricId());
        }        
    }
    
    public void updateMetrics() {
        setSelectedMetricString(null);
        
        if (selectedMeasurableProperty == null)
        {
            // ATTENTION: Because of a Seam-Bug, this new creation of the metrics-list is mandatory!
            // If you just clear the list, the view (s:selectItems) does not mention a change and therefore does not update the associated selectBox.
            // Related bug: https://issues.jboss.org/browse/JBSEAM-4382            
            setMetrics(new ArrayList<Metric>());
            metricsMap.clear();
        }
        else
        {
            constructMetricsWithMap(selectedMeasurableProperty.getPossibleMetrics());
        }
        
        log.debug("Reset Metric to null");
    }
    
    public boolean isMeasurableCriterionSelected() {
        if ((selectedMeasurableProperty != null && selectedMetric != null) || 
            (selectedMeasurableProperty != null && selectedMeasurableProperty.getScale() != null)) {
                return true;
        }
        
        return false;
    }
    
    public Criterion getSelectedCriterion() {
    	return criteriaManager.getCriterion(selectedMeasurableProperty, selectedMetric);
    }

    // --------------- general getter/setter ---------------
    
	public Metric getSelectedMetric() {
		return selectedMetric;
	}

	public void setSelectedMetric(Metric selectedMetric) {
		this.selectedMetric = selectedMetric;
	}

	public CriterionCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(CriterionCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
}
