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
package eu.scape_project.planning.plato.wfview.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.Measure;

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
	
    private List<Attribute> allAttributes;
    private Collection<Attribute> filteredAttributes;
    
    private List<Measure> allMeasures;
    private Collection<Measure> filteredMeasures;
    
    private CriterionCategory selectedCategory;
    private Attribute selectedAttribute;
    private Measure selectedMeasure;
    
	/**
	 * Compares two Attribute instances regarding their name
	 * 
	 * @author kraxner
	 *
	 */
	class AttributeNameComparator implements Comparator<Attribute> {
		@Override
		public int compare(Attribute o1, Attribute o2) {
			if (null == o1) {
				return -1;
			} else if (null == o2) {
				return 1;
			}
			return o1.getName().compareTo(o2.getName());
		}
	}
	class MeasureNameComparator implements Comparator<Measure> {
		@Override
		public int compare(Measure o1, Measure o2) {
			if (null == o1) {
				return -1;
			} else if (null == o2) {
				return 1;
			}
			return o1.getName().compareTo(o2.getName());
		}
	}
	
    public CriterionSelector() {
    	categories = new ArrayList<CriterionCategory>(Arrays.asList(CriterionCategory.values()));
    	
    	clearSelection();
    }
    
    public void init() {
    	allAttributes = new ArrayList<Attribute>(criteriaManager.getAllAttributes());
    	Collections.sort(allAttributes, new AttributeNameComparator());

    	allMeasures = new ArrayList<Measure>(criteriaManager.getAllMeasures()); 
        Collections.sort(allMeasures, new MeasureNameComparator());
        
        
        clearSelection();
    }
    
    private void clearSelection(){
        filteredAttributes = new ArrayList<Attribute>();
        filteredMeasures = new ArrayList<Measure>();

    	selectedCategory = null;
        selectedAttribute = null;
        selectedMeasure = null;
    }
    
    
    
    public void setSelectedAttributeName(String name) {
        selectedAttribute = findAttributeByName(name);
    }
    
    private Attribute findAttributeByName(String name) {
    	if (name == null) {
    		return null;
    	}
    	for (Attribute a : allAttributes) {
    		if (name.equals(a.getName())) {
    			return a;
    		}
    	}
    	return null;
    }
    private Measure findMeasureByName(String name) {
    	if (name == null) {
    		return null;
    	}
    	for (Measure a : allMeasures) {
    		if (name.equals(a.getName())) {
    			return a;
    		}
    	}
    	return null;
    }
    
    public String getSelectedAttributeName() {
    	if (selectedAttribute == null) {
    		return null;
    	} else {
    		return selectedAttribute.getName();
    	}
    }
    
    public void setSelectedMeasureName(String name) {
    	selectedMeasure = findMeasureByName(name);
    }
    public String getSelectedMeasureName() {
    	if (selectedMeasure == null) {
    		return null;
    	} else {
    		return selectedMeasure.getName();
    	}
    }
    
    public void applyFilter() {
        // ATTENTION: Because of a Seam-Bug, this new creation of the filtered-measurableproperties-list is mandatory!
        // If you just clear the list and then refill it, the view (s:selectItems) does not mention a change and therefore does not update the associated selectBox.
        // Related bug: https://issues.jboss.org/browse/JBSEAM-4382
        Collection<Attribute> newFilteredMP = new ArrayList<Attribute>();
        
    	for (Attribute p : allAttributes) {
            if (selectedCategory == null || p.getCategory() == selectedCategory) {
                newFilteredMP.add(p);
            }
        }
        
        filteredAttributes.clear();
        filteredAttributes.addAll(newFilteredMP);
//        setFilteredMeasurablePropertiesCount(newFilteredMP.size());
                
        // check if selected Attribute is still available in the new filtered list.
        Boolean mpStillInFilteredList = false;
        if (selectedAttribute != null) {
//            for (Attribute mp : filteredAttributes) {
//                if (mp.getPropertyId().equals(selectedAttribute.getPropertyId())) {
//                    mpStillInFilteredList = true;
//                    log.debug("Selected Property still available in new filtered list");
//                }
//            }
            
            // if the previous selected MeasuableProperty is not available any more in the new filtered list
            // set the selection to null (which also affects the metrics select)
            if (!mpStillInFilteredList) {
            	// FIXME
//                setSelectedAttributeString(null);
//                log.debug("Reset Selected Property to null");
//                updateMetrics();
            }
        }
    }
    
    public Measure getSelectedCriterion() {
    	return selectedMeasure;
    }

    // --------------- general getter/setter ---------------
    
	public CriterionCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(CriterionCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
}
