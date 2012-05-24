package eu.scape_project.pw.planning.plans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.pw.planning.manager.PlanManager;
import eu.scape_project.pw.planning.manager.PlanManager.WhichProjects;
import eu.scape_project.pw.planning.utils.FacesMessages;
import eu.scape_project.pw.planning.utils.FileUtils;
import eu.scape_project.pw.planning.xml.ProjectImporter;

/**
 * controller for listing plans
 * 
 * @author cb
 * @see {@link PlanManager}
 */
@SessionScoped
@Named("planLister")
public class PlanListerView implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private PlanManager planManager;

    @Inject
    private ProjectImporter projectImporter;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private Conversation conversation;

    private PlanProperties selectedProp;

    /**
     * Variable determining the plan selection which should be shown to the
     * user.
     */
    private WhichProjects projectSelection = WhichProjects.ALLPROJECTS;

    /*
     * private String directory = "";
     * 
     * public String importFromDir() { try {
     * projectImporter.importFromDir(directory); } catch (PlatoException e) {
     * log.debug(e); } return listAll();
     * 
     * }
     * 
     * public String getDirectory() { return directory; }
     * 
     * public void setDirectory(String directory) { directory = directory; }
     */

    private List<PlanProperties> list;

    public List<PlanProperties> getList() {
        return list;
    }

    public String listAll() {
        projectSelection = WhichProjects.ALLPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listFTEProjects() {
        projectSelection = WhichProjects.FTEPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listAllProjects() {
        projectSelection = WhichProjects.ALLPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listMyProjects() {
        projectSelection = WhichProjects.MYPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listPublicProjects() {
        projectSelection = WhichProjects.PUBLICPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listPublicFTEResults() {
        projectSelection = WhichProjects.PUBLICFTEPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String unlock(final int pid) {
        planManager.unlockPlan(pid);
        list = planManager.list(projectSelection);
        return null;
    }

    public void listener(final FileUploadEvent event) throws Exception {
        UploadedFile item = event.getUploadedFile();

        File tmp = File.createTempFile(item.getName(), "xml");
        tmp.deleteOnExit();
        FileUtils.writeToFile(item.getInputStream(), new FileOutputStream(tmp));
        try {
            projectImporter.importPlans(tmp);
            tmp.delete();

            List<String> appliedTransformations = projectImporter.getAppliedTransformations();

            if (!appliedTransformations.isEmpty()) {
                StringBuffer msg = new StringBuffer();
                msg.append("The following transformations have been applied:<br/><br/>");
                msg.append("<ul>");
                for (String xsl : appliedTransformations) {

                    msg.append("<li>").append("<a href='/data/xslt/" + xsl + "' target='_blank'>" + xsl + "</a>")
                        .append("</li>");
                }
                msg.append("</ul>");
                facesMessages.addInfo(null, 
                    "Your XML file was outdated, therefore it had to be migrated to the current Plato XML format.",
                    msg.toString());
            }

            list = planManager.list(projectSelection);
        } catch (PlatoException e) {
            log.error("Failed to upload plan: " + item.getName(), e);

            facesMessages.addError("Failed to upload plan: " + item.getName());
        }
    }

    // --------------- getter/setter ---------------

    public WhichProjects getProjectSelection() {
        return projectSelection;
    }
}
