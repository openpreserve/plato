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
package eu.scape_project.planning.plans;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.LoadedPlan;
import eu.scape_project.planning.bean.PrepareChangesForPersist;
import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.manager.DigitalObjectManager;
import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.plato.wfview.ViewWorkflowManager;
import eu.scape_project.planning.utils.Downloader;
import eu.scape_project.planning.utils.FacesMessages;
import eu.scape_project.planning.utils.FileUtils;
import eu.scape_project.planning.utils.OS;
import eu.scape_project.planning.xml.ProjectExportAction;

/**
 * Class used as backing-bean for the view plansettings.xhtml
 * 
 * @author Markus Hamm
 */
@Named("planSettings")
@ConversationScoped
public class PlanSettingsView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    @LoadedPlan
    private Plan plan;

    @Inject
    private User user;

    @Inject
    private Downloader downloader;

    @Inject
    private ViewWorkflowManager viewWorkflowManager;

    @Inject
    private ProjectExportAction projectExport;
    
    @Inject
    private PlanManager planManager;
    
    @Inject
    private DigitalObjectManager digitalObjectManager;

    @Inject
    private ByteStreamManager byteStreamManager;

    private String authPassword;
    
    private final static int pwCode = -3425080;

    /**
     * Method responsible for returning if the publish report feature should be
     * disabled or not.
     * 
     * @return True if feature should be disabled. False otherwise.
     */
    public boolean isPublishReportDisabled() {
        if (plan.getPlanProperties().isPrivateProject()) {
            return false;
        }

        return true;
    }

    /**
     * Method responsible for resetting the publishReport flag in the plan if
     * necessary. When the plan is not private the publishReport flag must not
     * be set. For this reason - if publishReport it is not set we also have to
     * disable the publishReport flag.
     */
    public void resetPublishReportIfNecessary() {
        if (!plan.getPlanProperties().isPrivateProject()) {
            plan.getPlanProperties().setReportPublic(false);
        }
    }

    /**
     * Method responsible for deleting the current loaded plan permanently.
     * 
     * @return Outcome of the delete action (for view redirect).
     */
    public String deletePlan() {
        if (isUserAllowedToModifyPlanSettings(user, plan)) {
            int planId = plan.getId();
            try {
                planManager.deletePlan(plan);
                log.info("Plan with id " + planId + " successfully deleted.");
                plan = null;
                return viewWorkflowManager.endWorkflow();
            } catch (PlanningException e) {
                facesMessages.addError("Failed to delete the plan.");
                log.error("Failed to delete the plan with id " + planId, e);
                return null;
            }
        } else {
            facesMessages.addError("You are not the owner of this plan and thus not allowed to delete it.");
            return null;
        }
    }

    /**
     * Method responsible for propagating the save operation.
     */
    public void save() {
        if (isUserAllowedToModifyPlanSettings(user, plan)) {
            save(plan, user);
        } else {
            facesMessages.addError("You are not the owner of this plan and thus not allowed to change it.");
        }
    }

    /**
     * Method responsible for adding a report to the plan.
     * 
     * @param event
     *            FileUploadEvent triggered form UI (containing all relevant
     *            data for report adding).
     */
    public void uploadReport(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();

        // Put file-data into a digital object
        DigitalObject digitalObject = new DigitalObject();
        digitalObject.setFullname(file.getName());
        digitalObject.getData().setData(file.getData());
        digitalObject.setContentType(file.getContentType());

        try {
            digitalObjectManager.moveDataToStorage(digitalObject);
            plan.getPlanProperties().setReportUpload(digitalObject);
        } catch (StorageException e) {
            log.error("Exception at trying to upload report for plan with id " + plan.getId() + ": ", e);
            facesMessages.addError("Unable to upload report");
            return;
        }

        log.info("Uploaded report '" + digitalObject.getFullname() + "' for plan with id " + plan.getId());
        save(plan, user);
    }

    /**
     * Method responsible for removing a report from a plan.
     */
    public void removeReport() {
        try {
            DigitalObject report = plan.getPlanProperties().getReportUpload();
            byteStreamManager.delete(report.getPid());

            plan.getPlanProperties().setReportUpload(new DigitalObject());
        } catch (StorageException e) {
            log.error("Error at removing report from plan with id " + plan.getId(), e);
            facesMessages.addError("Unable to remove report");
            return;
        }

        save(plan, user);
        log.info("removed report from plan with id " + plan.getId());
    }

    /**
     * Method responsible for starting the plan-report download in the users
     * web-selenium.
     */
    public void downloadReport() {
        try {
            DigitalObject digitalObject = plan.getPlanProperties().getReportUpload();
            downloader.download(digitalObjectManager.getCopyOfDataFilledDigitalObject(digitalObject));
        } catch (StorageException e) {
            log.error("Error at fetching report for plan with id " + plan.getId(), e);
            facesMessages.addError("Unable to fetch report");
        }
    }

    /**
     * Method responsible for activating action executions based on password
     * authentication.
     */
    public void authenticate() {
        if (authPassword.hashCode() == pwCode) {
            int count = 0;
            for (Alternative alt : plan.getAlternativesDefinition().getAlternatives()) {
                if (alt.getAction() != null && alt.getAction().getActionIdentifier().toLowerCase().contains("minimee")) {
                    alt.getAction().setExecute(true);
                    count++;
                }
            }
            log.debug("Activated execution of " + count + " actions in plan with id " + plan.getId());
            facesMessages.addInfo("Activated action execution for plan");
            save(plan, user);
        } else {
            facesMessages.addInfo("Wrong code");
        }
    }

    /**
     * Method indicating if the logged in user user is allowed to modify given
     * plan settings.
     */
    public boolean isUserAllowedToModify() {
        return isUserAllowedToModifyPlanSettings(user, plan);
    }

    /**
     * Starts the download of the currently loaded plan.
     */
    public void downloadPlan() {
        // convert project-name to a filename, add date:
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");

        String planName = plan.getPlanProperties().getName();

        if ((planName == null) || "".equals(planName)) {
            planName = "export";
        }
        String normalizedPlanName = FileUtils.makeFilename(planName);
        String filename = normalizedPlanName + "-" + formatter.format(new Date());

        String binarydataTempPath = OS.getTmpPath() + normalizedPlanName + System.currentTimeMillis() + "/";
        File binarydataTempDir = new File(binarydataTempPath);
        binarydataTempDir.mkdirs();
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                .getResponse();
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachement; filename=\"" + filename + ".xml\"");
            // the length of the resulting XML file is unknown due to
            // formatting: response.setContentLength(xml.length());
            try {
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());

                projectExport.exportComplete(plan.getPlanProperties().getId(), out, binarydataTempPath);

                out.flush();
                out.close();

            } catch (IOException e) {
                facesMessages.addError("An error occured while generating the export file.");
                log.error("An error occured while generating the export file.", e);
            }
            FacesContext.getCurrentInstance().responseComplete();
        } finally {
            OS.deleteDirectory(binarydataTempDir);
        }
    }
    /**
     * Method responsible for persisting the changes.
     */
    public void save(Plan plan, User user) {
        PrepareChangesForPersist prepChanges = new PrepareChangesForPersist(user.getUsername());
        prepChanges.prepare(plan);

        planManager.saveForPlanSettings(plan.getPlanProperties(), plan.getAlternativesDefinition());
    }
    
    /**
     * Method responsible to check if a user is allowed to modify the settings
     * of a given plan.
     * 
     * @param user
     *            User who wants to modify plan settings.
     * @param plan
     *            Plan to check for allowance.
     * @return true is user is allowed to modify plan settings of the given
     *         plan. False otherwise.
     */
    public boolean isUserAllowedToModifyPlanSettings(User user, Plan plan) {
        return (user != null) && (user.isAdmin() || user.getUsername().equals(plan.getPlanProperties().getOwner()));
    }
    

    // --------------- getter/setter ---------------

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }
}
