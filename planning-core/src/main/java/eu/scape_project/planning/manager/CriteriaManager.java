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
package eu.scape_project.planning.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remove;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.Metric;

/**
 * For administration of metrics, measurable properties and criteria
 * This should be the interface to a Measurement Property Registry (MPR)
 * - the registry should be queried for all measurement entities
 * - this would prevent entities being overwritten by accident, and ease notification on changed entities
 * - changes to already known entities should trigger events for preservation watch 
 *  
 * @author kraxner
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Named("criteriaManager")
public class CriteriaManager implements Serializable {
    private static final long serialVersionUID = -2305838596050068452L;

    @Inject private Logger log;
    
    @PersistenceContext
    private EntityManager em;
    
	public CriteriaManager() {
    }
    
    /**
     * cache for lookup of all currently known criteria by their id (propertyId#metricId)
     */
    private Map<String, Measure> knownMeasures = new HashMap<String, Measure>();

    /**
     * cache for lookup of all currently known MeasurableProperties by their propertyId
     */
    private Map<String, Attribute> knownProperties = new HashMap<String, Attribute>();

    /**
     * Returns a list of all known criteria
     * IMPORTANT: this list MUST NOT be altered!  
     * @return
     */
    @Lock(LockType.READ)
    public Collection<Measure> getKnownCriteria() {
        return knownMeasures.values();
    }

    /**
     * returns a list of all known properties
     * IMPORTANT: this list MUST NOT be altered!
     * @return
     */
    @Lock(LockType.READ)
    public Collection<Attribute> getKnownProperties() {
        return knownProperties.values();
    }


    /**
     * Returns the criterion for the given criterionUri
     * @param uri
     * @return
     */
    @Lock(LockType.READ)
    public Measure getCriterion(String criterionUri) {
    	for (Measure measure : knownMeasures.values()) {
    		if (measure.getUri().equals(criterionUri)) {
    			return measure; 
    		}
    	}
    	return null;
    }

    /**
     * loads all existing properties, metrics, and criteria from the database
     */
    private void load() {
    }
    
    /**
     * FIXME: reload from RDF
     * 
     * Reads the XML file from {@link #DESCRIPTOR_FILE} and adds the contained criteria to the database.
     * For criteria that already exist in the database (as designated by URI), the information is updated. 
     * @see eu.scape_project.planning.application.ICriteriaManager#reload()
     * ATTENTION:
     * From all available CRUD operation only CReate and Update are covered. Delete operations are not executed.
     * Thus, if you have deleted Properties in your XML they are not deleted in database as well.
     */
    @Lock(LockType.WRITE)
    public void reload() {
    
    }
    
    // Method used for testing purposes (mocking the EntityManager)
    public void setEm(EntityManager em) {
        this.em = em;
    }
    
    @PostConstruct 
    public void init() {
        load();
        if (knownMeasures.isEmpty()) {
            reload();
        }
        
    }

    @Remove
    public void destroy() {
    }
}
