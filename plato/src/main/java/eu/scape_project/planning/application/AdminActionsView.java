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
package eu.scape_project.planning.application;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.directory.InvalidAttributesException;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.event.FileUploadEvent;
import org.slf4j.Logger;

import at.tuwien.minimee.registry.ToolRegistry;
import eu.scape_project.planning.efficiency.PlanStatisticsGenerator;
import eu.scape_project.planning.efficiency.StateChangeLogGenerator;
import eu.scape_project.planning.model.Notification;
import eu.scape_project.planning.utils.FacesMessages;

/**
 * Class used as backing-bean for the admin-utils view.
 * 
 * @author Markus Hamm
 */
@Named("admin")
@SessionScoped
public class AdminActionsView implements Serializable {
    private static final long serialVersionUID = 7135700751688165420L;

    @Inject
    private Logger log;

    private Integer exportPlanRangeFromId;

    private Integer exportPlanRangeToId;

    private Integer planId;

    /**
     * Author of the news to add.
     */
    private String recipient;

    /**
     * Text of the news to add.
     */
    private String newsText;

    /**
     * Directory where to import xml-plans from.
     */
    private String xmlImportDirectory;

    /**
     * Plans xml-string representation to import.
     */
    private String xmlStringToImport;

    /**
     * Name of the file to import.
     */
    private String importFileName;

    /**
     * Data of the file to import.
     */
    private byte[] importFileData;

    @Inject
    private AdminActions adminActions;

    @Inject
    private Messages messages;

    @Inject
    private FacesMessages facesMessages;
    
    private List<Notification> notifications = new ArrayList<Notification>();
    
    @Inject
    EntityManager em;

    @PostConstruct
    public void init() {
        refreshNotifications();
    }
    
    
    public void downloadStatistics() {
        // convert project-name to a filename, add date:
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");

        String filename = "statistics-" + formatter.format(new Date());

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
            .getResponse();
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachement; filename=\"" + filename + ".csv\"");
        // the length of the resulting XML file is unknown due to
        // formatting: response.setContentLength(xml.length());
        try {
            Writer writer = new OutputStreamWriter(response.getOutputStream());
            
            PlanStatisticsGenerator statstics = new PlanStatisticsGenerator(writer, em);
            
            statstics.writeCompleteStatistics();

        } catch (IOException e) {
            facesMessages.addError("An error occured while generating statistics.");
            log.error("An error occured while generating statistics.", e);
        }
        FacesContext.getCurrentInstance().responseComplete();
    }    

    public void downloadStageStatistics() {
        // convert project-name to a filename, add date:
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");

        String filename = "stage-statistics-" + formatter.format(new Date());

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
            .getResponse();
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachement; filename=\"" + filename + ".csv\"");
        // the length of the resulting XML file is unknown due to
        // formatting: response.setContentLength(xml.length());
        try {
            Writer writer = new OutputStreamWriter(response.getOutputStream());
            
            StateChangeLogGenerator statstics = new StateChangeLogGenerator(writer, em);
            
            statstics.writeCompleteStatistics();

        } catch (IOException e) {
            facesMessages.addError("An error occured while generating stage statistics.");
            log.error("An error occured while generating stage statistics.", e);
        }
        FacesContext.getCurrentInstance().responseComplete();
    }    

    /**
     * Method responsible for exporting all plans in zipped format.
     */
    public void exportAllPlansToZip() {
        boolean success = adminActions.exportAllPlansToZip();

        if (success) {
            facesMessages.addInfo("exportAllPlansToZip",
                "Exported successful to directory: " + adminActions.getLastProjectExportPath());
        } else {
            facesMessages.addError("expornotificationstAllPlansToZip", "Export failed");
        }
    }

    /**
     * Method responsible for exporting a range of plans in zipped format.
     */
    public void exportSomePlansToZip() {
        boolean success = adminActions.exportSomePlansToZip(exportPlanRangeFromId, exportPlanRangeToId);

        if (success) {
            facesMessages.addInfo("exportSomePlansToZip",
                "Exported successful to directory: " + adminActions.getLastProjectExportPath());
        } else {
            facesMessages.addError("exportSomePlansToZip", "Export failed");
        }
    }

    public void deleteAllPlans() {
        if (adminActions.deleteAllPlans()) {
            facesMessages.addInfo("deleteAllPlans", "All plans deleted!");
        } else {
            facesMessages.addInfo("deleteAllPlans", "Admin: Deletion of all plans failed, see log for more details.");
        }
    }

    public void cleanUpLoosePlanValues() {
        int nrOfValuesRemoved = adminActions.cleanUpLoosePlanValues();
        log.info("Admin: Cleaned up " + nrOfValuesRemoved + " loose plan values");
        facesMessages.addInfo("cleanUpLoosePlanValues", "Cleaned up " + nrOfValuesRemoved + " loose plan values");
    }

    public void unlockAllPlans() {
        adminActions.unlockAllPlans();
        facesMessages.addInfo("unlockAllPlans", "All plans are unlocked now.");
    }

    public void unlockPlan() {
        if (planId == 0) {
            facesMessages.addError("planId", "Please provide a valid value");
            return;
        }

        boolean success = adminActions.unlockPlan(planId);

        if (success) {
            facesMessages.addInfo("unlockPlan", "Unlock successful");
        } else {
            facesMessages.addError("unlockPlan", "Unlock failed");
        }
    }

    public void clonePlan() {
        if (planId == 0) {
            facesMessages.addError("planId", "Please provide a valid value");
            return;
        }

        boolean success = adminActions.clonePlan(planId, null);

        if (success) {
            facesMessages.addInfo("clonePlan", "Clone successful");
        } else {
            facesMessages.addError("clonePlan", "Clone failed");
        }
    }

    public void deletePlan() {
        if (planId == 0) {
            facesMessages.addError("planId", "Please provide a valid value");
            return;
        }

        if (adminActions.deletePlan(planId)) {
            facesMessages.addInfo("deletePlan", "Plan deleted");
        } else {
            facesMessages.addError("deletePlan", "Failed to delete plan. See log file for details.");
        }
    }
    
    public void fixAlternativeNames(){
        if (planId == 0) {
            facesMessages.addError("planId", "Please provide a valid value");
            return;
        }        notifications.clear();
        notifications.addAll(adminActions.getNotifications());


        if (adminActions.fixAlternativeNames(planId)) {
            facesMessages.addInfo("fixAlt", "Alternative names fixed");
        } else {
            facesMessages.addError("fixAlt", "Failed to fix alternative names. See log file for details.");
        }
        
    }

    /**
     * Method responsible for triggering a RuntimeException for tesing purposes.
     * 
     * @throws InvalidAttributesException
     */
    public void throwRuntimeException() {
        adminActions.throwRuntimeException();
    }

    /**
     * Method responsible for clearing the list containing exception messages
     * occured during Plato runtime.
     */
    public void clearErrors() {
        messages.clearErrors();
    }
    
    public void refreshNotifications(){
        notifications.clear();
        notifications.addAll(adminActions.getNotifications());
    }

    /**
     * Method responsible for adding a news entry.
     */
    public void addNotification() {
        adminActions.addNotification(recipient, newsText);
        refreshNotifications();
        newsText = "";
    }

    /**
     * Method responsible for clearing all news messages.
     */
    public void removeNotification(String uuid) {
        adminActions.removeNotification(uuid);
        refreshNotifications();        
    }

    /**
     * Method responsible for munching 500 MB of memory.
     */
    public void munch500MBofMemory() {
        adminActions.munchMem(500);
        facesMessages.addInfo("memtestAdd", "Munched 500 MB of memory");
    }

    /**
     * Method responsible for releasing memory.
     */
    public void releaseMemory() {
        adminActions.releaseMem();
        facesMessages.addInfo("memtestRelease", "Released memory");
    }

    /**
     * Method responsible for importing plans from a directory.
     */
    public void importPlansFromDirectory() {
        if (xmlImportDirectory == null || xmlImportDirectory.length() == 0) {
            facesMessages.addError("importDir", "You have to enter a valid import directory.");
            return;
        }

        int importedPlans = adminActions.importPlansFromDirectory(xmlImportDirectory);

        if (importedPlans == 0) {
            facesMessages.addError("directoryImport", "No Plans imported. Please check import directory.");
        } else {
            facesMessages.addInfo("directoryImport", "Imported " + importedPlans + " plans.");
        }
    }

    /**
     * Method responsible for importing plans via a given xml.
     */
    public void importFromXml() {
        if (xmlStringToImport == null || xmlStringToImport.length() == 0) {
            facesMessages.addError("importXml", "You have to enter a valid xml.");
            return;
        }

        int importedPlans = adminActions.importPlansFromXml(xmlStringToImport);

        if (importedPlans == 0) {
            facesMessages.addError("xmlImport", "No Plans imported. Please check XML input.");
        } else {
            facesMessages.addInfo("xmlImport", "Imported " + importedPlans + " plans.");
            xmlStringToImport = "";
        }
    }

    /**
     * Method responsible for importing Plans via a File.
     */
    public void importFromFile() {
        if (importFileData == null || importFileData.length == 0) {
            facesMessages.addError("file", "Please select a File");
            return;
        }

        int importedPlans = adminActions.importPlansFromFile(importFileData, false);

        if (importedPlans == 0) {
            facesMessages.addError("importFile", "No Plans imported. Please check your file selection.");
        } else {
            facesMessages.addInfo("importFile", "Imported " + importedPlans + " plans.");
            importFileData = null;
            importFileName = null;
        }
    }

    /**
     * Method responsible for importing Plans for the current logged in user,
     * via a File.
     */
    public void importFromFileForMyself() {
        if (importFileData == null || importFileData.length == 0) {
            facesMessages.addError("file", "Please select a File");
            return;
        }

        int importedPlans = adminActions.importPlansFromFile(importFileData, false);

        if (importedPlans == 0) {
            facesMessages.addError("importFileForMyself", "No Plans imported. Please check your file selection.");
        } else {
            facesMessages.addInfo("importFileForMyself", "Imported " + importedPlans + " plans.");
            importFileData = null;
            importFileName = null;
        }
    }

    /**
     * Method responsible for setting the file to upload.
     * 
     * @param event
     *            RichFaces FileUploadEvent containing file information.
     */
    public void selectImportFile(FileUploadEvent event) {
        importFileName = event.getUploadedFile().getName();
        importFileData = event.getUploadedFile().getData();
    }

    public void reloadMinimee(){
        ToolRegistry.getInstance().reload();
    }
    // --------------- getter/setter ---------------

    public Integer getExportPlanRangeFromId() {
        return exportPlanRangeFromId;
    }

    public void setExportPlanRangeFromId(Integer exportPlanRangeFromId) {
        this.exportPlanRangeFromId = exportPlanRangeFromId;
    }

    public Integer getExportPlanRangeToId() {
        return exportPlanRangeToId;
    }

    public void setExportPlanRangeToId(Integer exportPlanRangeToId) {
        this.exportPlanRangeToId = exportPlanRangeToId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public String getXmlImportDirectory() {
        return xmlImportDirectory;
    }

    public void setXmlImportDirectory(String xmlImportDirectory) {
        this.xmlImportDirectory = xmlImportDirectory;
    }

    public String getXmlStringToImport() {
        return xmlStringToImport;
    }

    public void setXmlStringToImport(String xmlStringToImport) {
        this.xmlStringToImport = xmlStringToImport;
    }

    public String getImportFileName() {
        return importFileName;
    }

    public void setImportFileName(String importFileName) {
        this.importFileName = importFileName;
    }

    public byte[] getImportFileData() {
        return importFileData;
    }

    public void setImportFileData(byte[] importFileData) {
        this.importFileData = importFileData;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
