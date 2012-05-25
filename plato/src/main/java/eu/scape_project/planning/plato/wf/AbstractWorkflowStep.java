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
package eu.scape_project.planning.plato.wf;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import eu.scape_project.planning.bean.PrepareChangesForPersist;
import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.manager.DigitalObjectManager;
import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.services.characterisation.fits.FitsIntegration;
import eu.scape_project.planning.utils.XmlXPathEvaluator;
import eu.scape_project.planning.validation.PlanValidator;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Base class for steps of the workflow  
 * 
 * @author Markus Hamm, Michael Kraxner
 *
 */
public abstract class AbstractWorkflowStep implements Serializable {

	private static final long serialVersionUID = -517256120799033395L;

	@Inject private Logger log;

	@Inject protected ByteStreamManager bytestreamManager;
	
	@Inject protected DigitalObjectManager digitalObjectManager;
	
	@Inject	protected EntityManager em;
	
	@Inject protected PlanManager planManager;
	
	@Inject protected PlanValidator planValidator;
	
	@Inject protected User user;
	
	@Inject protected PrepareChangesForPersist prepareChangesForPersist;
	
	@Inject	private FitsIntegration fits;
	
	@Inject private XmlXPathEvaluator xmlXPathEvaluator;
	
	protected Plan plan;
	
	/**
	 * Set containing a list of bytestreams(pids) stored since the last action-execution (save, discard)
	 */
	protected Set<String> addedBytestreams = new HashSet<String>();
	/**
	 * Set containing a list of bytestreams(pids) marked to delete since the last action-execution (save, discard)
	 */
	protected Set<String> bytestreamsToRemove = new HashSet<String>();	
	
	protected PlanState requiredPlanState;
	protected PlanState correspondingPlanState;

	public AbstractWorkflowStep() {
	}

	/**
	 * Initializes this workflow step.
	 * 
	 * @param p
	 */
	public void init(Plan p) {
		plan = p;
		prepareChangesForPersist.setUser(user.getUsername());
	}
	
	public void setPlan(Plan p) {
		this.plan = p;
	}
	
	public Plan getPlan() {
		return this.plan;
	}

	/**
	 * Checks if the plan has progressed far enough to continue to the next step.
	 * - if it is complete, the plans' state is set accordingly
	 * - if not, the plan state is set to this step, the plan is stored,
	 *           and explanations are added to errors
	 *           
	 * @return list of errors if check fails, otherwise an empty list
	 */
	public boolean proceed(List<ValidationError> errors) {	
		// save the plan - this way the state is reset to the requiredPlanState, and changes are not lost
		save();

		if (mayProceed(errors)) {
			plan.getPlanProperties().setState(correspondingPlanState);
			saveEntity(plan.getPlanProperties());
			return errors.isEmpty();
		}
		return false;
	}
	
	protected boolean mayProceed(List<ValidationError> errors) {
		return planValidator.isPlanStateSatisfied(plan, correspondingPlanState, errors);		
	}
	
	/**
	 * Stores all changes made in a step.
	 * - it resets the plan's state to requiredPlanState and persist it.
	 * - then only step-specific changes are saved.
	 * - clean-up added/deleted bytestreams
	 * Note: derived steps have to store their changes in {@link AbstractWorkflowStep#saveStepSpecific()} 
	 */
	public void save() {
		// set the plans' state according to this step before applying any other changes 
		// this way we ensure that the plan has to be validated, when changes have been made.
		plan.getPlanProperties().setState(requiredPlanState);
		plan.getPlanProperties().touch();
		saveEntity(plan.getPlanProperties());
		
		// -- debug code --
//        for (Leaf l: plan.getTree().getRoot().getAllLeaves()) {
//            log.debug(l.getName()+": "+l.getScale().getDisplayName());
//            for (String s: l.getValueMap().keySet()) {
//                log.debug("   value entry for "+s);
//            }
//            if (l.getTransformer() instanceof OrdinalTransformer) {
//                OrdinalTransformer t =(OrdinalTransformer) l.getTransformer();
//                for (String s: t.getMapping().keySet()) {
//                    log.debug("   transformer entry for "+s);
//                }
//            }
//
//        }
		
		// now the step specific changes can be saved
		saveStepSpecific();
		
		// added bytestreams are accepted
	    addedBytestreams.clear();
	    
	    // delete bytestreams marked to remove
	    for (String pid : bytestreamsToRemove) {
			try {
				bytestreamManager.delete(pid);
			} catch (StorageException e) {
				log.error("failed to delete bytestream: " + pid);
			}	    	
	    }
	    bytestreamsToRemove.clear();
	}

	/**
	 * Stores all changes made in a step WITHOUT modifying the plan-state
	 * This method is often required at re-using the business-logic of a workflow-step. 
	 * Steps executed:
	 * - save plan properties
	 * - save step-specific changes
	 * - clean-up added/deleted bytestreams
	 */
	public void saveWithoutModifyingPlanState() {
		prepareChangesForPersist.prepare(plan.getPlanProperties());		

		saveEntity(plan.getPlanProperties());
				
		// now the step specific changes can be saved
		saveStepSpecific();
		
		// added bytestreams are accepted
	    addedBytestreams.clear();
	    
	    // delete bytestreams marked to remove
	    for (String pid : bytestreamsToRemove) {
			try {
				bytestreamManager.delete(pid);
			} catch (StorageException e) {
				log.error("failed to delete bytestream: " + pid);
			}	    	
	    }
	    bytestreamsToRemove.clear();
	}
	
	/**
	 * Method responsible for persisting a given Entity.
	 * 
	 * @param entity Entity to persist
	 * @return The Entity merged into current persistence context.
	 */
	protected Object saveEntity(Object entity) {
		prepareChangesForPersist.prepare(entity);
		
		Object merged = em.merge(entity);
		em.persist(merged);
		return merged;
	}
	
	/**
	 * Method responsible for removing a given Entity from database.
	 * 
	 * @param entity Entity to remove.
	 */
	protected void removeEntity(Object entity) {
		Object merged = em.merge(entity);
		em.remove(merged);
	}
	
	/**
	 * Discards all changes which have not been persisted so far.
	 */
	public void discard() throws PlanningException {
		// delete list of added bytestreams
		for (String pid : addedBytestreams) {
			try {
				bytestreamManager.delete(pid);
			} catch (StorageException e) {
				log.error("failed to delete discarded bytestream: " + pid);
			}
		}
	    addedBytestreams.clear();
	    
	    // changes are discarded - so nothing has to be discarded
	    bytestreamsToRemove.clear();
	    
	    plan =  planManager.reloadPlan(plan);
	}
	
	/**
	 *  Stores all changes made in this step.
	 *  Define here what you want to save in derived steps.  
	 */
	protected abstract void saveStepSpecific();
	
	/**
	 * Method responsible for retrieving a copy of a previously uploaded result file.
	 * 
	 * @param alternative Alternative the file was uploaded for.
	 * @param sampleObject Sample the file was uploaded for.
	 * @return A copy of the result file as DigitalObject.
	 * @throws StorageException is thrown if any error occurs at retrieving the result file.
	 */
	public DigitalObject fetchResultFile(Alternative alternative, SampleObject sampleObject) throws StorageException {
		DigitalObject digitalObject = alternative.getExperiment().getResults().get(sampleObject);
		
		return digitalObjectManager.getCopyOfDataFilledDigitalObject(digitalObject);
	}
	
	public DigitalObject fetchDigitalObject(DigitalObject object) throws StorageException {
		return digitalObjectManager.getCopyOfDataFilledDigitalObject(object);
	}
	
	/**
	 * Characterizes a digital object with FITS tool.
	 * 
	 * @param digitalObject Digital object to characterize.
	 * @return True if characterization was successful, false otherwise.
	 */
    public boolean characteriseFits(DigitalObject digitalObject) {
        if (fits == null) {
            log.debug("FITS is not available and needs to be reconfigured.");
            return false;
        }

        if (digitalObject != null && digitalObject.isDataExistent()) {
            try {
                String fitsXML = null;
                File sampleFile = bytestreamManager.getTempFile(digitalObject.getPid());
                fitsXML = fits.characterise(sampleFile);
                digitalObject.setFitsXMLString(fitsXML);
                log.debug("FITS xml stored in digital-object " + digitalObject.getFullname());
                
                return updateFormatInformationBasedOnFits(digitalObject);
            } catch (PlanningException e) {
                log.error("characterisation with FITS failed.",e);
                return false;
            }
        }
        
        return false;
    }
    
	/**
	 * Method responsible for updating SampleFile format-information based on its extracted Fits characteristics.
	 * 
	 * @param sampleObject SampleObject to update.
	 * @return True if format-information was updated, false otherwise
	 */
	private boolean updateFormatInformationBasedOnFits(DigitalObject digitalObject) {
		if (digitalObject == null || digitalObject.getFitsXMLString() == null || digitalObject.getFitsXMLString().isEmpty() ) {
			log.debug("Invalid sample object passed to process - stop update format-information.");
			return false;
		}

		try {
			xmlXPathEvaluator.setXmlToParse(digitalObject.getFitsXMLString());
			
			digitalObject.getFormatInfo().setPuid(xmlXPathEvaluator.extractValue("/fits/identification/identity/externalIdentifier[@type='puid']"));
			digitalObject.getFormatInfo().setName(xmlXPathEvaluator.extractValue("/fits/identification/identity/attribute::format"));
			digitalObject.getFormatInfo().setVersion(xmlXPathEvaluator.extractValue("/fits/identification/identity/version[@toolname='Jhove']"));
			digitalObject.getFormatInfo().setMimeType(xmlXPathEvaluator.extractValue("/fits/identification/identity/attribute::mimetype"));
			digitalObject.getFormatInfo().setDefaultExtension(extractFileExtension(digitalObject.getFullname()));
			
			digitalObject.getFormatInfo().touch();
			
			log.debug("Successfully updated fomat-information based on FITS for digital object " + digitalObject.getFullname());
			return true;
		}
		catch (Exception e) {
			log.error("Error at updating fomat-information based on FITS for digital object " + digitalObject.getFullname(), e);
			return false;
		}
	}
	
	/**
	 * Method responsible for extracting the file-extension out of a given filename.
	 * 
	 * @param fileName Filename to parse.
	 * @return File extension of the given filename, or an empty string if the filename has no extension.
	 */
	private String extractFileExtension(String fileName) {
		int stringLength = fileName.length();
		int separatorIndex = fileName.lastIndexOf('.');
		
		// if no extension is present in the filename
		if (separatorIndex == -1 || separatorIndex == (stringLength - 1)) {
			return "";
		}
		
		return fileName.substring(separatorIndex + 1);
	}
	
}
