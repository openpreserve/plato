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

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.manager.PlanManager;
import eu.scape_project.planning.manager.PlanManager.WhichProjects;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.utils.FacesMessages;
import eu.scape_project.planning.utils.FileUtils;
import eu.scape_project.planning.xml.ProjectImporter;

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
    private PlanManager planManager;

    @Inject
    private ProjectImporter projectImporter;

    @Inject
    private FacesMessages facesMessages;
    
    private List<String> transformations;

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
        resetTransformations();
        projectSelection = WhichProjects.ALLPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listFTEProjects() {
        resetTransformations();
        projectSelection = WhichProjects.FTEPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listAllProjects() {
        resetTransformations();
        projectSelection = WhichProjects.ALLPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listMyProjects() {
        resetTransformations();
        projectSelection = WhichProjects.MYPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listPublicProjects() {
        resetTransformations();
        projectSelection = WhichProjects.PUBLICPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String listPublicFTEResults() {
        resetTransformations();
        projectSelection = WhichProjects.PUBLICFTEPROJECTS;
        list = planManager.list(projectSelection);
        log.debug("listing " + list.size() + " plans");
        return "/plans.jsf";
    }

    public String unlock(final int pid) {
        resetTransformations();
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
            resetTransformations();
            projectImporter.importPlans(tmp);
            tmp.delete();

            List<String> appliedTransformations = projectImporter.getAppliedTransformations();
            transformations = appliedTransformations;
            if (!appliedTransformations.isEmpty()) {
                facesMessages.addInfo(null, 
                    "Your XML file was outdated, therefore it had to be migrated to the current Plato Schema.");
            }

            list = planManager.list(projectSelection);
        } catch (PlatoException e) {
            log.error("Failed to upload plan: " + item.getName(), e);

            facesMessages.addError("Failed to upload plan: " + item.getName());
        }
    }
    
    private void resetTransformations(){
        transformations = null;
    }
    
    public List<String> getTransformations() {
        return transformations;
    }

    // --------------- getter/setter ---------------

    public WhichProjects getProjectSelection() {
        return projectSelection;
    }
}
