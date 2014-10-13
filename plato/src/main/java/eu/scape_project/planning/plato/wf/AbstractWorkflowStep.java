/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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
 * Base class for steps of the workflow.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
public abstract class AbstractWorkflowStep implements Serializable {

    private static final long serialVersionUID = -517256120799033395L;

    @Inject
    private Logger log;

    @Inject
    protected ByteStreamManager bytestreamManager;

    @Inject
    protected DigitalObjectManager digitalObjectManager;

    @Inject
    protected EntityManager em;

    @Inject
    protected PlanManager planManager;

    @Inject
    protected PlanValidator planValidator;

    @Inject
    protected User user;

    @Inject
    protected PrepareChangesForPersist prepareChangesForPersist;

    @Inject
    private FitsIntegration fits;

    @Inject
    private XmlXPathEvaluator xmlXPathEvaluator;

    protected Plan plan;

    /**
     * Set containing a list of bytestreams(pids) stored since the last
     * action-execution (save, discard).
     */
    protected Set<String> addedBytestreams = new HashSet<String>();

    /**
     * Set containing a list of bytestreams(pids) marked to delete since the
     * last action-execution (save, discard).
     */
    protected Set<String> bytestreamsToRemove = new HashSet<String>();

    protected PlanState requiredPlanState;
    protected PlanState correspondingPlanState;

    /**
     * Empty constructor.
     */
    public AbstractWorkflowStep() {
    }

    /**
     * Initializes this workflow step.
     * 
     * @param p
     *            the plan
     */
    public void init(Plan p) {
        plan = p;
        prepareChangesForPersist.setUser(user.getUsername());
    }

    /**
     * Saves the plan. Updates the plan state if the current step is complete.
     * Otherwise the plan state is reset to the current state and errors are
     * added to the error list.
     * 
     * @param errors
     *            a list of errors
     * @return true if there were no errors, false otherwise
     */
    public boolean proceed(List<ValidationError> errors) {
        // save the plan - this way the state is reset to the requiredPlanState,
        // and changes are not lost
        save();

        if (mayProceed(errors)) {
            plan.getPlanProperties().setState(correspondingPlanState);
            saveEntity(plan.getPlanProperties());
            return errors.isEmpty();
        }
        return false;
    }

    /**
     * Checks if the plan may proceed to the next step.
     * 
     * @param errors
     *            a list of errors
     * @return true if the plan may proceed, false otherwise
     */
    protected boolean mayProceed(List<ValidationError> errors) {
        return planValidator.isPlanStateSatisfied(plan, correspondingPlanState, errors);
    }

    /**
     * Stores all changes made in a step.
     * 
     * Resets the plan's state to requiredPlanState and persist it. Saves
     * step-specific changes and cleans up added or deleted bytestreams.
     * 
     * Note: derived steps have to store their changes in
     * {@link AbstractWorkflowStep#saveStepSpecific()}
     */
    public void save() {
        plan.getPlanProperties().setState(requiredPlanState);
        plan.getPlanProperties().touch();

        saveWithoutModifyingPlanState();
    }

    /**
     * Stores all changes made in a step WITHOUT modifying the plan-state. This
     * method is often required at re-using the business-logic of a
     * workflow-step.
     * 
     * Saves plan properties, step-specific changes and cleans-up added/deleted
     * bytestreams.
     */
    public void saveWithoutModifyingPlanState() {
        saveEntity(plan.getPlanProperties());

        saveStepSpecific();

        // Added bytestreams are accepted
        addedBytestreams.clear();

        // Delete bytestreams marked to remove
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
     * Persists the provided entity.
     * 
     * @param entity
     *            Entity to persist
     * @return The entity merged into current persistence context.
     */
    protected Object saveEntity(Object entity) {
        prepareChangesForPersist.prepare(entity);

        Object merged = em.merge(entity);
        em.persist(merged);
        return merged;
    }

    /**
     * Removes the provided entity from the database.
     * 
     * @param entity
     *            Entity to remove
     */
    protected void removeEntity(Object entity) {
        Object merged = em.merge(entity);
        em.remove(merged);
    }

    /**
     * Discards all changes that have not been persisted so far.
     * 
     * @throws PlanningException
     *             if the reload failed
     */
    public void discard() throws PlanningException {
        // Delete added bytestreams
        for (String pid : addedBytestreams) {
            try {
                bytestreamManager.delete(pid);
            } catch (StorageException e) {
                log.error("Failed to delete discarded bytestream: " + pid);
            }
        }
        addedBytestreams.clear();

        // Clear discarded bytestreams
        bytestreamsToRemove.clear();

        plan = planManager.reloadPlan(plan);
    }

    /**
     * Stores all changes made in this step. Define here what you want to save
     * in derived steps.
     */
    protected abstract void saveStepSpecific();

    /**
     * Method responsible for retrieving a copy of a previously uploaded result
     * file.
     * 
     * @param alternative
     *            Alternative the file was uploaded for
     * @param sampleObject
     *            Sample the file was uploaded for
     * @return A copy of the result file as DigitalObject
     * @throws StorageException
     *             if any error occurred retrieving the result file
     */
    public DigitalObject fetchResultFile(Alternative alternative, SampleObject sampleObject) throws StorageException {
        DigitalObject digitalObject = alternative.getExperiment().getResults().get(sampleObject);

        return digitalObjectManager.getCopyOfDataFilledDigitalObject(digitalObject);
    }

    /**
     * Fetches a copy of the provided digital object filled with data.
     * 
     * @param object
     *            the object to fetch
     * @return a data filled digital object
     * @throws StorageException
     *             if any error occurred fetching data
     */
    public DigitalObject fetchDigitalObject(DigitalObject object) throws StorageException {
        return digitalObjectManager.getCopyOfDataFilledDigitalObject(object);
    }

    /**
     * Characterizes a digital object with FITS tool.
     * 
     * @param digitalObject
     *            digital object to characterize
     * @param updateFormatInfo
     *            true to update the format info, false otherwise
     * @return true if characterization was successful, false otherwise
     */
    public boolean characteriseFits(DigitalObject digitalObject, boolean updateFormatInfo) {
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

                if (updateFormatInfo) {
                    return updateFormatInformationBasedOnFits(digitalObject);
                }
                return true;
            } catch (PlanningException e) {
                log.error("characterisation with FITS failed.", e);
                return false;
            }
        }

        return false;
    }

    /**
     * Characterizes a digital object with FITS tool and updates the format
     * info.
     * 
     * @param digitalObject
     *            digital object to characterize
     * @return true if characterization was successful, false otherwise
     */
    public boolean characteriseFits(DigitalObject digitalObject) {
        return characteriseFits(digitalObject, true);
    }

    /**
     * Updates the format info of the digital object based on the FITS string.
     * 
     * @param digitalObject
     *            digital object to update
     * @return true if the format information was updated, false otherwise
     */
    private boolean updateFormatInformationBasedOnFits(DigitalObject digitalObject) {
        if (digitalObject == null || digitalObject.getFitsXMLString() == null
            || digitalObject.getFitsXMLString().isEmpty()) {
            log.debug("Invalid sample object passed to process - stop update format-information.");
            return false;
        }

        try {
            xmlXPathEvaluator.setXmlToParse(digitalObject.getFitsXMLString());

            digitalObject.getFormatInfo().setPuid(
                xmlXPathEvaluator.extractValue("/fits/identification/identity/externalIdentifier[@type='puid']"));
            digitalObject.getFormatInfo().setName(
                xmlXPathEvaluator.extractValue("/fits/identification/identity/attribute::format"));
            digitalObject.getFormatInfo().setVersion(
                xmlXPathEvaluator.extractValue("/fits/identification/identity/version[@toolname='Jhove']"));
            digitalObject.getFormatInfo().setMimeType(
                xmlXPathEvaluator.extractValue("/fits/identification/identity/attribute::mimetype"));
            digitalObject.getFormatInfo().setDefaultExtension(extractFileExtension(digitalObject.getFullname()));

            digitalObject.getFormatInfo().touch();

            log.debug("Successfully updated fomat-information based on FITS for digital object "
                + digitalObject.getFullname());
            return true;
        } catch (Exception e) {
            log.error(
                "Error at updating fomat-information based on FITS for digital object " + digitalObject.getFullname(),
                e);
            return false;
        }
    }

    /**
     * Extracts the file extension of a given filename.
     * 
     * @param fileName
     *            filename to parse
     * @return file extension or an empty string if the filename has no
     *         extension
     */
    private String extractFileExtension(String fileName) {
        int separatorIndex = fileName.lastIndexOf('.');

        // if no extension is present in the filename
        if (separatorIndex == -1 || separatorIndex == (fileName.length() - 1)) {
            return "";
        }

        return fileName.substring(separatorIndex + 1);
    }

    // ********** getter/setter **********
    public void setPlan(Plan p) {
        this.plan = p;
    }

    public Plan getPlan() {
        return this.plan;
    }
}
