package eu.scape_project.pw.planning.manager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

import eu.planets_project.pp.plato.model.measurement.Criterion;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.Metric;
import eu.scape_project.pw.planning.xml.MeasurementsDescriptorParser;

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
    private static final String DESCRIPTOR_FILE = "data/evaluation/measurementsConsolidated.xml";
    
    @PersistenceContext
    private EntityManager em;
    
	public CriteriaManager() {
    }
    
    /**
     * cache for lookup of all currently known criteria by their id (propertyId#metricId)
     */
    private Map<String, Criterion> knownCriteria = new HashMap<String, Criterion>();

    /**
     * Returns a list of all known criteria
     * IMPORTANT: this list MUST NOT be altered!  
     * @return
     */
    @Lock(LockType.READ)
    public Collection<Criterion> getKnownCriteria() {
        return knownCriteria.values();
    }

    /**
     * returns a list of all known properties
     * IMPORTANT: this list MUST NOT be altered!
     * @return
     */
    @Lock(LockType.READ)
    public Collection<MeasurableProperty> getKnownProperties() {
        return knownProperties.values();
    }

    /**
     * cache for lookup of all currently known metrics by their metricId
     */
    private Map<String, Metric> knownMetrics = new HashMap<String, Metric>();
    
    /**
     * cache for lookup of all currently known MeasurableProperties by their propertyId
     */
    private Map<String, MeasurableProperty> knownProperties = new HashMap<String, MeasurableProperty>();

    /**
     * returns the criterion for the given propertyId and metricId (provided in the corresponding bean instances)
     * IMPORTANT: this criterion MUST NOT be altered!
     */
    @Lock(LockType.READ)
    public Criterion getCriterion(MeasurableProperty property, Metric metric) {
        String propertyId = (property != null) ? property.getPropertyId() : null;
        String metricId = (metric != null)? metric.getMetricId() : null;
        
    	if (propertyId == null) {
    		return null;
    	}
    	for (Criterion criterion : knownCriteria.values()) {
    		if (criterion.getProperty().getPropertyId().equals(propertyId)) {
    			if ((criterion.getMetric() == null) ||(criterion.getMetric().getMetricId() == null)) {
    				if (metricId == null) {
    					return criterion;
    				}
    			} else if (criterion.getMetric().getMetricId().equals(metricId)){
    				return criterion;
    			}
    		}
    	}
    	return null;
    }

    /**
     * Returns the criterion for the given criterionUri
     * @param uri
     * @return
     */
    @Lock(LockType.READ)
    public Criterion getCriterion(String criterionUri) {
    	for (Criterion criterion : knownCriteria.values()) {
    		if (criterion.getUri().equals(criterionUri)) {
    			return criterion; 
    		}
    	}
    	return null;
    }

    /**
     * loads all existing properties, metrics, and criteria from the database
     */
    private void load() {
        List<Criterion> criteria = em.createQuery("select c from Criterion c").getResultList();
        for (Criterion criterion : criteria) {
        	if (criterion.getUri() == null) {
        		criterion.setUri(criterion.buildUri());
        	}
            knownCriteria.put(criterion.getUri(), criterion);
            MeasurableProperty property = criterion.getProperty();
            if (property != null) {
            	knownProperties.put(property.getPropertyId(), property);
            }
            Metric metric = criterion.getMetric();
            if (metric != null) {
            	knownMetrics.put(metric.getMetricId(), metric);
            }
        }
    }
    
    /**
     * Reads the XML file from {@link #DESCRIPTOR_FILE} and adds the contained criteria to the database.
     * For criteria that already exist in the database (as designated by URI), the information is updated. 
     * @see eu.planets_project.pp.plato.application.ICriteriaManager#reload()
     * ATTENTION:
     * From all available CRUD operation only CReate and Update are covered. Delete operations are not executed.
     * Thus, if you have deleted Properties in your XML they are not deleted in database as well.
     */
    @Lock(LockType.WRITE)
    public void reload() {
        try {
//            File descr = FileUtils.getResourceFile(DESCRIPTOR_FILE);
//            if (descr != null) {
            Reader descriptorReader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(DESCRIPTOR_FILE));
            	
//                String descriptorStr = new String(FileUtils.getBytesFromFile(descr), "UTF-8");
                MeasurementsDescriptorParser parser = new MeasurementsDescriptorParser();
                
                Map<String, MeasurableProperty> digestedProperties = new HashMap<String, MeasurableProperty>();
                Map<String, Metric> digestedMetrics = new HashMap<String, Metric>();
                
                parser.load(descriptorReader, digestedProperties, digestedMetrics);
                
                // refresh all metrics
                for (String key : digestedMetrics.keySet()) {
                    Metric digestedMetric = digestedMetrics.get(key);
                    Metric knownMetric = knownMetrics.get(key);
                    if (knownMetric == null) {
                        // a new metric, store it
                        em.persist(digestedMetric);
                        knownMetrics.put(key, digestedMetric);
                        log.info("added metric " + ", " + digestedMetric.getMetricId());
                    } else {
                        // metric already exits - overwrite it
                        // TODO: raise event in case there are differences
                        knownMetric = em.merge(knownMetric);
                        knownMetric.setDescription(digestedMetric.getDescription());
                        knownMetric.setName(digestedMetric.getName());
                        knownMetric.setScale(digestedMetric.getScale());
                        em.persist(knownMetric);
                        knownMetrics.put(key, knownMetric);
                        log.info("updated metric " + digestedMetric.getMetricId());
                    }
                }
                
                // refresh all properties
                for (String key : digestedProperties.keySet()) {
                    MeasurableProperty digestedProperty = digestedProperties.get(key);
                    MeasurableProperty knownProperty = knownProperties.get(key);
                    if (knownProperty == null) {
                        // a new property, store it
                        knownProperty = digestedProperty;
                        em.persist(digestedProperty);
                        knownProperty = em.merge(digestedProperty);
                        knownProperties.put(key, knownProperty);
                        log.info("added property " + digestedProperty.getPropertyId());
                    } else {
                        // property already exits - overwrite it
                        knownProperty = em.merge(knownProperty);
                        knownProperty.setName(digestedProperty.getName());
                        knownProperty.setDescription(digestedProperty.getDescription());
                        knownProperty.setScale(digestedProperty.getScale());
                        em.persist(knownProperty);
                        knownProperty = em.merge(knownProperty);
                        knownProperties.put(key, knownProperty);
                        log.info("updated property " + digestedProperty.getPropertyId());
                    }
                    
                    // and refresh corresponding criteria too
                    if (knownProperty.getScale() != null) {
                        // this is also a non-derived measurable property
                        Criterion criterion = getCriterion(knownProperty, null);
                        if (criterion == null) {
                            // it's a new property, add a criterion for it
                            criterion = new Criterion();
                            criterion.setProperty(knownProperty);
                        	if (criterion.getUri() == null) {
                        		criterion.setUri(criterion.buildUri());
                        	}
                            em.persist(criterion);
                            criterion = em.merge(criterion);
                            knownCriteria.put(criterion.getUri(), criterion);
                        }
                    }
                    for (Metric metric : digestedProperty.getPossibleMetrics()) {
                        Criterion criterion = getCriterion(knownProperty, metric);
                        if (criterion == null) {
                            // it's a new property, add a criterion for it
                            criterion = new Criterion();
                            criterion.setProperty(knownProperty);
                            // metrics have been merged before, so we can retrieve it from the cache 
                            criterion.setMetric(knownMetrics.get(metric.getMetricId()));
                        	if (criterion.getUri() == null) {
                        		criterion.setUri(criterion.buildUri());
                        	}
                            em.persist(criterion);
                            criterion = em.merge(criterion);
                            knownCriteria.put(criterion.getUri(), criterion);
                        } else {
                            // a criterion has only references to property and metric, no update is necessary 
                        }
                }
            }
                
            // have to attach possible Metrics to MeasurableProperties at reload too!
            attachPossibleMetricsToMeasurableProperties();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }        
    }

    /**
     * Method responsible for attaching the appropriate possibleMetrics to all MeasurableProperties.
     * This method is necessary because the field possibleMetrics is a transient field of MeasurableProperties and therefore is not persisted in database.
     * Because of this, this information has to be collected every time the CriteriaManager is initialized.  
     */
    private void attachPossibleMetricsToMeasurableProperties() {
        for (Criterion criterion : knownCriteria.values()) {
            if (criterion.getMetric() != null) {
                MeasurableProperty property = criterion.getProperty();
                Metric metric = criterion.getMetric();
                
                if (!property.getPossibleMetrics().contains(metric)) {
                    property.addPossibleMetric(metric);
                }
            }
        }
    }
    
    // Method used for testing purposes (mocking the EntityManager)
    public void setEm(EntityManager em) {
        this.em = em;
    }
    
    @PostConstruct 
    public void init() {
        load();
        if (knownCriteria.isEmpty()) {
            reload();
        }
        
        attachPossibleMetricsToMeasurableProperties();
    }

    @Remove
    public void destroy() {
    }
}
