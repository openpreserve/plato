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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.Measure;

/**
 * Backing bean for Measure selection.
 * 
 */
public class CriterionSelector implements Serializable {
    private static final long serialVersionUID = -4868553688311289177L;

    @Inject
    private CriteriaManager criteriaManager;

    private List<CriterionCategory> categories;

    private List<Attribute> allAttributes;
    private List<Attribute> filteredAttributes;

    private List<Measure> allMeasures;
    private List<Measure> filteredMeasures;

    private CriterionCategory selectedCategory;
    private Attribute selectedAttribute;
    private Measure selectedMeasure;
    
    private String searchTerm;

    /**
     * Compares two Attribute instances regarding their name
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

    class CategoryNameComparator implements Comparator<CriterionCategory> {
        @Override
        public int compare(CriterionCategory o1, CriterionCategory o2) {
            if (null == o1) {
                return -1;
            } else if (null == o2) {
                return 1;
            }
            return o1.getName().compareTo(o2.getName());
        }
    }

    public CriterionSelector() {

        clearSelection();
    }

    /**
     * Initializes the Measure selection.
     * Retrieves all available measures from the {@link #criteriaManager CriteriaManager}
     */
    public void init() {
        allMeasures = new ArrayList<Measure>(criteriaManager.getAllMeasures());

        allAttributes = new ArrayList<Attribute>();
        categories = new ArrayList<CriterionCategory>();
        // show only attributes and categories for which measures exist
        for (Measure m : allMeasures) {
            if (!allAttributes.contains(m.getAttribute())) {
                allAttributes.add(m.getAttribute());
            }
        }
        for (Attribute a : allAttributes) {
            if (!categories.contains(a.getCategory())) {
                categories.add(a.getCategory());
            }
        }
        Collections.sort(allMeasures, new MeasureNameComparator());
        Collections.sort(allAttributes, new AttributeNameComparator());
        Collections.sort(categories, new CategoryNameComparator());

        clearSelection();
    }

    /**
     * Removes all measures from the list of available measures where the uris are not in the given set of uris.
     *   
     * @param measureUris  The uris of the measures which shall be available for selection.
     */
    public void filterCriteria(Set<String> measureUris) {
        HashMap<String, CriterionCategory> filteredCategories = new HashMap<String, CriterionCategory>();
        HashMap<String, Attribute> filteredAttributes = new HashMap<String, Attribute>();
        HashMap<String, Measure> filteredMeasures = new HashMap<String, Measure>();

        for (Measure m : allMeasures) {
            if (measureUris.contains(m.getUri())) {
                filteredMeasures.put(m.getUri(), m);
                filteredAttributes.put(m.getAttribute().getUri(), m.getAttribute());
                filteredCategories.put(m.getAttribute().getCategory().getUri(), m.getAttribute().getCategory());
            }
        }

        categories.clear();
        categories.addAll(filteredCategories.values());
        allAttributes.clear();
        allAttributes.addAll(filteredAttributes.values());
        allMeasures.clear();
        allMeasures.addAll(filteredMeasures.values());
    }

    /**
     * Clears the currently selected attribute, measure and searchTerm
     */
    private void clearSelection() {
        filteredAttributes = new ArrayList<Attribute>();
        filteredMeasures = new ArrayList<Measure>();

        selectedCategory = null;
        selectedAttribute = null;
        selectedMeasure = null;
        
        searchTerm = "";
    }

    public String getSelectedCategoryName() {
        if (selectedCategory == null) {
            return null;
        } else {
            return selectedCategory.getName();
        }
    }

    public void setSelectedCategoryName(String name) {
        this.selectedCategory = findCategoryByName(name);
    }

    public void setSelectedAttributeName(String name) {
        selectedAttribute = findAttributeByName(name);
    }

    private CriterionCategory findCategoryByName(String name) {
        if (name == null) {
            return null;
        }
        for (CriterionCategory c : categories) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        return null;
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
    
    public String getSearchTerm(){
        return searchTerm;
    }
    
    public void setSearchTerm(String value) {
        this.searchTerm = value;
    }
    
    /**
     * Searches for measures which comply to the currently set {@link #searchTerm}.
     * To be called when the search term has changed. 
     */
    public void updateSearch() {
        String[] terms = searchTerm.split("\\s");
        String pattern = "^";
        for (int i = 0; i < terms.length; i++) {
            // we use 
            pattern += "(?=.*" + terms[i] + ")";
        }
        pattern += ".*";
        Pattern searchPattern;
        try {
            searchPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        } catch (IllegalArgumentException e) {
            searchPattern = Pattern.compile(".*", Pattern.CASE_INSENSITIVE);
        }
        
        Set<Attribute> termFilteredAttributes = new HashSet<Attribute>();
        
        filteredMeasures.clear();
        for (Measure m : allMeasures) {
            // search in all descriptions at once, this is more what the user expects 
            String base = m.getName() + " " + m.getDescription() + " " + m.getAttribute().getName() + " " + m.getAttribute().getDescription() + " " + m.getAttribute().getCategory().getName();
            if (searchPattern.matcher(base).matches()) {
                filteredMeasures.add(m);
                termFilteredAttributes.add(m.getAttribute());
            }
        }
        filteredAttributes.clear();
        filteredAttributes.addAll(termFilteredAttributes);
        Collections.sort(filteredMeasures, new MeasureNameComparator());
        Collections.sort(filteredAttributes, new AttributeNameComparator());
    }
    
    public void categorySelected() {
        filteredAttributes.clear();
        if (selectedCategory == null) {
            filteredAttributes.addAll(allAttributes);
        } else {
            for (Attribute attr : allAttributes) {
                if (attr.getCategory().getUri().equals(selectedCategory.getUri())) {
                    filteredAttributes.add(attr);
                }
            }
            if (!filteredAttributes.contains(selectedAttribute)) {
                selectedAttribute = null;
            }
        }
        // if there is only one attribute, preselect it
        if (filteredAttributes.size() == 1) {
            selectedAttribute = filteredAttributes.iterator().next();
        }
        // propagate the new selection
        attributeSelected();
    }
    public void attributeSelected() {
        filteredMeasures.clear();
        if (selectedAttribute == null) {
            //filteredMeasures.addAll(allMeasures);
        } else {
            for (Measure m : allMeasures) {
                if (m.getAttribute().getUri().equals(selectedAttribute.getUri())) {
                    filteredMeasures.add(m);
                }
            }
            if (!filteredMeasures.contains(selectedMeasure)) {
                selectedMeasure = null;
            }
            // and also adjust the category, in case the textual filter was used
            if ((selectedCategory == null) ||
                (!selectedCategory.getUri().equals(selectedAttribute.getCategory().getUri()))) {
                selectedCategory = selectedAttribute.getCategory();
            }
        }
        // if there is only one measure, preselect it
        if (filteredMeasures.size() == 1) {
            selectedMeasure = filteredMeasures.iterator().next();
        }
    }

    public void measureSelected() {
        if (selectedMeasure != null) {
            if ((selectedAttribute == null) || (!selectedAttribute.getUri().equals(selectedMeasure.getAttribute().getUri()))) {
                selectedAttribute = findAttributeByName(selectedMeasure.getAttribute().getName());
                selectedCategory =  findCategoryByName(selectedAttribute.getCategory().getName());
            }
        }
    }

    public void selectMeasure(Measure measure) {
        if (measure != null) {
            selectedMeasure = measure;
            measureSelected();
            categorySelected();
            selectedMeasure = measure;
        } else {
            clearSelection();
        }

    }

    public Measure getSelectedMeasure() {
        return selectedMeasure;
    }

    public Collection<CriterionCategory> getCategories() {
        return categories;
    }

    public Collection<Measure> getFilteredMeasures() {
        return filteredMeasures;
    }

    public Collection<Attribute> getFilteredAttributes() {
        return filteredAttributes;
    }

    public Attribute getSelectedAttribute() {
        return selectedAttribute;
    }

    public CriterionCategory getSelectedCategory() {
        return selectedCategory;
    }
}
