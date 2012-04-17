package eu.scape_project.pw.planning.plato.wf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.evaluation.evaluators.XmlExtractor;
import eu.planets_project.pp.plato.exception.PlanningException;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.ByteStream;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.Values;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.services.characterisation.fits.FitsIntegration;
import eu.planets_project.pp.plato.services.characterisation.fits.FitsNamespaceContext;
import eu.scape_project.pw.planning.manager.StorageException;

/**
 * Business logic for workflow step Define Sample Objects
 * 
 * @author Michael Kraxner, Markus Hamm
 *
 */
@Stateful
@ConversationScoped
public class DefineSampleObjects extends AbstractWorkflowStep {
	
	private static final long serialVersionUID = 5845302929371618848L;

	@Inject private Logger log;
			
    /**
     * Used to remove unused samples when saving. We need this list because we have
     * to remove dependent entries in the Uploads Hashmap in the
     * Experiment of every Alternative. 
     */
    List<SampleObject> samplesToRemove = new ArrayList<SampleObject>();
    
	public DefineSampleObjects(){
		this.requiredPlanState = PlanState.BASIS_DEFINED;
		this.correspondingPlanState = PlanState.RECORDS_CHOSEN;
	}
    
	/*
    public void init(Plan p){
    	super.init(p);
	 
    	try {
	        fits = new FitsIntegration();
	    } catch (Throwable e) {
	        fits = null;
	        log.error("Could not instantiate FITS, it is not configured properly.", e);
	    }
    }
    */
    
    public void saveStepSpecific(){
	    /*
	     * We need to persist the AlternativesDefinition here first, because
	     * every SampleObject is used as a key for the Uploads Hashmap in the
	     * Experiment of every Alternative. Therefore if one SampleObject is removed,
	     * but still used as a key for the HashMap Hibernate will throw error,
	     * because of foreign Key Relationship.
	     *
	     * So when SampleObject is removed from the System we remove it from the
	     * Hashmap too, then Save all Alternatives with the Experiments and DigitalObject
	     * Hashmaps -> the Sample Recordarg0 to remove is referenced nowhere in the project
	     * and the SampleRecordDefinition can be saved....?
	     * Or wait! One thing before that... all the Values objects have changed in #removeRecord()
	     * and we have to persist these as well before deleting the sampleobject.
	     * THEN the SampleRecordDefinition can be saved.
	     */
	    /** dont forget to prepare changed entities e.g. set current user */
    	prepareChangesForPersist.prepare(plan);
	
    	saveEntity(plan.getAlternativesDefinition());
    	    		   
	    for (SampleObject record : plan.getSampleRecordsDefinition()
	            .getRecords()) {
	        
	        //prep.prepare(record);
            if (!samplesToRemove.contains(record)) {
    	        if (record.getId() == 0) { // the record has not yet been persisted                                
    	            em.persist(record);
    	        } else {
                    em.persist(em.merge(record));
    	        }
            }
	        
	    }
	    
	    // If we removed samples, persist all the Values objects of all leaves in the tree
	    // - that leads to the orphan VALUE objects to be deleted from the database.
	    if (samplesToRemove.size() > 0) {
	        for (Leaf l: plan.getTree().getRoot().getAllLeaves()) {
	            for (Alternative a: plan.getAlternativesDefinition().getConsideredAlternatives()) {
	                Values v = l.getValues(a.getName());
	                if (v != null) {
	                    em.persist(em.merge(v));
	                } else {
	                    log.error("values is NULL: "+l.getName()+", "+a.getName());
	                }
	            }
	        }
	        //em.flush();
	    }
	    
	    
	    // and don't forget to remove bytestreams of samples too
	    for (SampleObject o : samplesToRemove) {
	    	try {
  	    		bytestreamManager.delete(o.getPid());
			} catch (StorageException e) {
				log.error("failed to delete sample: " + o.getPid(), e);
			}
	    }
	    
	    saveEntity(plan.getSampleRecordsDefinition());
	    
	    samplesToRemove.clear();    	
    }

    public SampleObject addSample(String filename, String contentType, byte[] bytestream) throws PlanningException {
    	SampleObject sample = new SampleObject();
		sample.setFullname(filename);
		sample.setShortName(filename);
		sample.setContentType(contentType);
		
		ByteStream bsData = new ByteStream();
		bsData.setData(bytestream);
		sample.setData(bsData);
		sample.getData().setSize(bytestream.length);
		

    	digitalObjectManager.moveDataToStorage(sample);
    	plan.getSampleRecordsDefinition().addRecord(sample);
    	addedBytestreams.add(sample.getPid());
		
		// identify format of newly uploaded samples
		if (shouldCharacterise(sample)) {
	//        identifyFormat(sample);
	//        describeInXcdl(sample);
		      characteriseFits(sample);
		}
		log.debug("Content-Type: " + sample.getContentType());
		log.debug("Size of samples Array: "
		        + plan.getSampleRecordsDefinition().getRecords()
		                .size());
		log.debug("FileName: " + sample.getFullname());
		log.debug("Length of File: " + sample.getData().getSize());
		log.debug("added SampleObject: " + sample.getFullname());
		log.debug("JHove initialized: " + (sample.getJhoveXMLString() != null));
		
		return sample;
	}
    
	/**
     * For some objects (such as raw camera files), calling characterisation tools is useless and
     * needs resources. This function tells us if we should attempt characterisation.
     * TODO Michael please explain!!
     * @param sample SampleObject to be checked
     * @return true if object should be characterised, false if it's better not to do that
     */
    private boolean shouldCharacterise(SampleObject sample) {
        String fullName = sample.getFullname();
        if (fullName.toUpperCase().endsWith(".CR2") ||
            fullName.toUpperCase().endsWith(".NEF") ||
            fullName.toUpperCase().endsWith(".CRW")) {
            return false;
        }
        return true;
    }

	public boolean hasDependetValues(SampleObject sample) {
        if (sample == null || plan.getSampleRecordsDefinition().getRecords().size() == 0) {
            return true;
        }

        int rec[] = { plan.getSampleRecordsDefinition().getRecords()
                .indexOf(sample) };
        
        // we need to construct the list of all altenative names because the tree doesnt know it
        Set<String> alternatives = new HashSet<String>();
        for (Alternative a: plan.getAlternativesDefinition().getConsideredAlternatives()) {
            alternatives.add(a.getName());
        }
        
        return plan.getTree().hasValues(rec, alternatives);  
	}



	public void removeSample(SampleObject sample) {
        samplesToRemove.add(sample);
        plan.removeSampleObject(sample);
	}
}
