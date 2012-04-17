/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.model.measurement;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import eu.planets_project.pp.plato.model.ChangeLog;
import eu.planets_project.pp.plato.model.IChangesHandler;
import eu.planets_project.pp.plato.model.ITouchable;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.util.CriterionUri;

@Entity
public class Criterion implements Comparable<Criterion>, Serializable, ITouchable {
    private static final long serialVersionUID = -3942656115528678720L;
    
    @Id
    @GeneratedValue
    private long id;
    
    private String uri;
    
    @ManyToOne(cascade=CascadeType.MERGE)
    private MeasurableProperty property;
    
    @ManyToOne(cascade=CascadeType.MERGE)
    private Metric metric;
    
    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();
    
    public CriterionUri toCriterionUri(){
        CriterionUri uri = new CriterionUri(getUri());
        return uri;
    }
    public Scale getScale() {
        Scale s = null;
        if ((metric != null)&& (metric.getScale() != null)) {
            s = metric.getScale();
        } else if ((property != null) &&(property.getScale() != null)) {
            s = property.getScale();
        }
        return s;
    }
    public String getUnit() {
        Scale s = getScale();
        if (s != null) {
            return s.getUnit();
        } else {
            return null;
        }
    }

    /**
     * configures a criterion (its property and metric) according to the given uri
     * - The URI is interpreted as follows:
     *    <criterion category>://<criterion sub-category>/<propertyId>[#<metricId>]
     * 
     * - if null or an empty string is passed to this method, all measurement information is reset.
     * 
     * This instance is then NOT linked 
     * 
     * @throws IllegalArgumentException  if uri is invalid, or does not correspond to a criterion category 
     * @param uri
     */
//    public void mockFromUri(String uri) throws IllegalArgumentException {
//        CriterionUri info = new CriterionUri();
//
//        // this may throw an IllegalArgumentException, we do not catch it, but pass it on directly
//        info.setAsURI(uri);
//        String scheme = info.getScheme();
//        String path = info.getPath();
//        String fragment = info.getFragment();
//        
//        // if the URI is empty, reset measurement info
//        if ((scheme == null)||(path == null)) {
//            property = null;
//            metric = null;
//            return;
//        }
//        
//        // check, if scheme and path correspond to a valid CriterionCategory:
//        // 1. extract criterion sub-category from path  
//        int subCategoryEndIdx = path.indexOf("/");
//        if (subCategoryEndIdx == -1) {
//            throw new IllegalArgumentException("invalid measurment info uri - scheme and path do not correspond to a criterion category: " + uri);
//        } else if ((subCategoryEndIdx + 1) >= path.length()) {
//            throw new IllegalArgumentException("invalid measurment info uri - no property defined: " + uri);
//        }
//        // 2. extract propertyId and criterion sub-category
//        String propertyId = path.substring(subCategoryEndIdx+1); 
//        String subCategory = path.substring(0, subCategoryEndIdx);
//
//        // 3. try to get the corresponding category
//        CriterionCategory cat = CriterionCategory.getType(scheme, subCategory);
//        if (cat == null) {
//            throw new IllegalArgumentException("invalid measurement info uri - scheme and path don't correspond to a criterion category: " +uri);
//        }
//        // reuse existing property, when possible
//        MeasurableProperty prop = getProperty();
//        if (prop == null) {
//            prop = new MeasurableProperty();
//            setProperty(prop);
//        } else {
//            // there is a property, reset the old values
//            prop.setName(null);
//            prop.setDescription(null);
//            prop.getPossibleMetrics().clear();
//            prop.setScale(null);
//        }
//        // populate it with known values from URI
//        getProperty().setCategory(cat);
//        getProperty().setPropertyId(propertyId);
//        
//        // if fragment is null metric should also be null
//        if (fragment == null) {
//            metric = null;
//        }
//        // otherwise fill it with known values from URI
//        else {
//            // reuse existing metric, when possible
//            Metric metr = getMetric();
//            if (metr == null) {
//                metr = new Metric();
//                setMetric(metr);
//            } else {
//                metr.setDescription(null);
//                metr.setName(null);
//                metr.setScale(null);
//                metr.setType(null);
//            }
//            // populate it with known values from URI
//            metr.setMetricId(fragment);
//        }
//    }
    
    

    /**
     * returns the string representation of this measurement info
     * <criterion category>://<criterion subcategory>/<propertyId>[#<metricId>]
     * @return
     */
    @Deprecated()
    public String buildUri() {
        String scheme = null;
        String path = null;
        
        if ((property == null)||(property.getCategory() == null)) {
            return null;
        }
        CriterionCategory cat = property.getCategory();
        
        scheme = cat.getCategory();
        path =  "".equals(cat.getSubCategory()) ? "" : cat.getSubCategory() + "/";

        String fragment;
        if ((metric != null) && (metric.getMetricId() != null)) {
            fragment = "#" + metric.getMetricId();
        } else {
            fragment = "";
        }
        return scheme + "://" + path + property.getPropertyId() + fragment;
    }
    

    public void setId(long id) {
        this.id = id;
    }

    public MeasurableProperty getProperty() {
        return property;
    }

    public void setProperty(MeasurableProperty property) {
        this.property = property;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
        // call handleChanges of all properties
        if (property != null) {
            property.handleChanges(h);
        }
        if (metric != null) {
            metric.handleChanges(h);
        }
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
        
    }

    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public long getId() {
		return id;
	}
	
	@Override
	public int compareTo(Criterion c) {
		return uri.toLowerCase().compareTo(c.getUri().toLowerCase());
		//return property.getName().toLowerCase().compareTo(c.getProperty().getName().toLowerCase());
	}
}
