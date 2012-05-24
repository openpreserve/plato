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
import eu.scape_project.planning.manager.StorageException;
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
    private PlanSettings planSettings;

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

    private String authPassword;

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
        if (planSettings.isUserAllowedToModifyPlanSettings(user, plan)) {
            int planId = plan.getId();
            planSettings.deletePlan(plan);
            log.info("Plan with id " + planId + " successfully deleted.");
            plan = null;

            return viewWorkflowManager.endWorkflow();
        } else {
            facesMessages.addError("You are not the owner of this plan and thus not allowed to delete it.");
            return null;
        }
    }

    /**
     * Method responsible for propagating the save operation.
     */
    public void save() {
        if (planSettings.isUserAllowedToModifyPlanSettings(user, plan)) {
            planSettings.save(plan, user);
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
            planSettings.uploadReport(digitalObject);
        } catch (StorageException e) {
            log.error("Exception at trying to upload report for plan with id " + plan.getId() + ": ", e);
            facesMessages.addError("Unable to upload report");
            return;
        }

        log.info("Uploaded report '" + digitalObject.getFullname() + "' for plan with id " + plan.getId());
        planSettings.save(plan, user);
    }

    /**
     * Method responsible for removing a report from a plan.
     */
    public void removeReport() {
        try {
            planSettings.removeReport();
        } catch (StorageException e) {
            log.error("Error at removing report from plan with id " + plan.getId(), e);
            facesMessages.addError("Unable to remove report");
            return;
        }

        planSettings.save(plan, user);
        log.info("removed report from plan with id " + plan.getId());
    }

    /**
     * Method responsible for starting the plan-report download in the users
     * web-browser.
     */
    public void downloadReport() {
        try {
            downloader.download(planSettings.fetchReport());
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
        if (planSettings.activateActionExecutionForPlan(plan, authPassword)) {
            facesMessages.addInfo("Activated action execution for plan");
            planSettings.save(plan, user);
        } else {
            facesMessages.addInfo("Wrong code");
        }
    }

    /**
     * Method indicating if the logged in user user is allowed to modify given
     * plan settings.
     */
    public boolean isUserAllowedToModify() {
        return planSettings.isUserAllowedToModifyPlanSettings(user, plan);
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
