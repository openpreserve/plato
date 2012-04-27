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

import eu.planets_project.pp.plato.model.PlanProperties;
import eu.planets_project.pp.plato.model.PlatoException;
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
     * public void setDirectory(String directory) { this.directory = directory;
     * }
     */

    private List<PlanProperties> list;

    public List<PlanProperties> getList() {
        return this.list;
    }

    public String listAll() {
        this.projectSelection = WhichProjects.ALLPROJECTS;
        this.list = this.planManager.list(this.projectSelection);
        this.log.debug("listing " + this.list.size() + " plans");
        return "/plans.jsf";
    }

    public String listFTEProjects() {
        this.projectSelection = WhichProjects.FTEPROJECTS;
        this.list = this.planManager.list(this.projectSelection);
        this.log.debug("listing " + this.list.size() + " plans");
        return "/plans.jsf";
    }

    public String listAllProjects() {
        this.projectSelection = WhichProjects.ALLPROJECTS;
        this.list = this.planManager.list(this.projectSelection);
        this.log.debug("listing " + this.list.size() + " plans");
        return "/plans.jsf";
    }

    public String listMyProjects() {
        this.projectSelection = WhichProjects.MYPROJECTS;
        this.list = this.planManager.list(this.projectSelection);
        this.log.debug("listing " + this.list.size() + " plans");
        return "/plans.jsf";
    }

    public String listPublicProjects() {
        this.projectSelection = WhichProjects.PUBLICPROJECTS;
        this.list = this.planManager.list(this.projectSelection);
        this.log.debug("listing " + this.list.size() + " plans");
        return "/plans.jsf";
    }

    public String listPublicFTEResults() {
        this.projectSelection = WhichProjects.PUBLICFTEPROJECTS;
        this.list = this.planManager.list(this.projectSelection);
        this.log.debug("listing " + this.list.size() + " plans");
        return "/plans.jsf";
    }

    public String unlock(final int pid) {
        this.planManager.unlockPlan(pid);
        this.list = this.planManager.list(this.projectSelection);
        return null;
    }

    public void listener(FileUploadEvent event) throws Exception {
        final UploadedFile item = event.getUploadedFile();

        final File tmp = File.createTempFile(item.getName(), "xml");
        tmp.deleteOnExit();
        FileUtils.writeToFile(item.getInputStream(), new FileOutputStream(tmp));
        try {
            this.projectImporter.importPlans(tmp);
            tmp.delete();

            this.list = this.planManager.list(this.projectSelection);
        } catch (final PlatoException e) {
            this.log.error("Failed to upload plan: " + item.getName(), e);

            this.facesMessages.addError("Failed to upload plan: " + item.getName());
        }
    }

    // --------------- getter/setter ---------------

    public WhichProjects getProjectSelection() {
        return this.projectSelection;
    }
}
