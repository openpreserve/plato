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
package eu.scape_project.planning.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.xml.sax.SAXException;

import eu.scape_project.planning.manager.IByteStreamStorage;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.tree.TemplateTree;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.utils.OS;

@Stateful
@SessionScoped
@Named
public class ProjectImporter extends PlanXMLConstants implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    /**
     * Used to store digital objects after de-serializing a plan.
     */
    @Inject
    private IByteStreamStorage storage;

    @Inject
    private EntityManager em;
    
    private List<String> appliedTransformations = new ArrayList<String>();
    
    private PlanParser planParser = new PlanParser();
    

    /**
     * Deserializes the plans stored in the given file.
     * 
     * @param file
     * @return
     * @throws PlatoException
     */
    public List<Plan> importPlans(final InputStream plans) throws PlatoException {
        appliedTransformations.clear();

        String tempPath = OS.getTmpPath() + "import_xml" + System.currentTimeMillis() + File.separator;
        File tempDir = new File(tempPath);
        tempDir.deleteOnExit();
        tempDir.mkdirs();
        try {
            PlanMigrator planMigrator = new PlanMigrator();
            String currentVersionFile = planMigrator.getCurrentVersionData(plans, tempPath, appliedTransformations);

            if (currentVersionFile == null) {
                log.error("Failed to migrate plans.");
                return new ArrayList<Plan>();
            } else {
                return planParser.importProjects(new FileInputStream(currentVersionFile));
            }
        } catch (FileNotFoundException e) {
            throw new PlatoException("IMPORT FAILED: could not find migrated file", e);
        } finally {
            OS.deleteDirectory(tempDir);
        }
    }
    
    public List<Plan> importPlans(final String file) throws PlatoException {
        try {
            return importPlans(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new PlatoException("IMPORT FAILED: could not find file " + file, e);
        }
    }
    
    
    /**
     * Imports all plans from the given file and stores them in the database
     * 
     * @param file
     * @throws PlatoException
     */
    public void importPlans(final File file) throws PlatoException {
        log.debug("importing file: " + file.getName());
        for (Plan p : importPlans(file.getAbsolutePath())) {
            storeDigitalObjects(p);
            em.persist(p);
        }
    }    

    /**
     * Imports all plans in the given directory and stores them in the database
     * 
     * @param dir
     * @return
     * @throws PlatoException
     */
    public int importAllProjectsFromDir(final String dir) throws PlatoException {
        int count = 0;
        File f = new File(dir);
        if (!f.exists()) {
            throw new PlatoException("Directory not found: " + dir);
        }
        String files[] = f.list();
        if (files == null) {
            throw new PlatoException("Directory is empty: " + dir);
        }
        for (String s : files) {
            log.debug("importing file: " + s);
            String file = f.getAbsolutePath() + File.separator + s;
            log.info("Importing file: " + file);
            for (Plan p : importPlans(file)) {
                storeDigitalObjects(p);
                em.persist(p);
                count++;
            }
            // FIXME: if persisting to the database fails, the
            // DigitalObjects stored
            // to the filesystem have to be removed!
        }
        return count;
    }

    /**
     * stores byte streams of digital objects
     * 
     * @param p
     * @throws PlatoException
     */
    public void storeDigitalObjects(final Plan p) throws PlatoException {
        try {
            List<DigitalObject> digitalObjects = p.getDigitalObjects();
            for (DigitalObject o : digitalObjects) {
                if (o.getData().getSize() > 0) {
                    String pid = storage.store(null, o.getData().getRealByteStream().getData());
                    o.setPid(pid);
                }
            }
        } catch (Exception e) {
            throw new PlatoException(e);
        }
    }

    
    @Remove
    public void destroy() {

    }

    /**
     * This method takes a template xml and stores the templates in the template
     * library. The xml is of the form:
     * 
     * <templates> <template name="Public Fragments"> <node name="Template 1"
     * weight="0.0" single="false" lock="false"> <node
     * name="Interactive multimedia presentations" weight="0.0" single="false"
     * lock="false"> ... </template> </templates>
     * 
     * We go through the templates //templates/template/node and store them in
     * the respective template library, in this case 'Public Fragments'
     * 
     * @param xml
     * @throws SAXException
     * @throws IOException
     */
    public void storeTemplatesInLibrary(final InputStream xml) throws PlatoException {

        List<TemplateTree> templates = planParser.importTemplates(xml);
        /*
         * store all templates
         */
        for (TemplateTree template : templates) {

            // we get the template tree ("Public Templates") from the database
            TemplateTree tdb;
            try {
                tdb = (TemplateTree) em.createQuery("select n from TemplateTree n where name = :name")
                    .setParameter("name", template.getName()).getSingleResult();
            } catch (NoResultException e) {
                tdb = new TemplateTree(template.getName(), null);
            }

            if (tdb != null) {

                // we get the templates and add them to the tree
                // and store them
                for (TreeNode n : template.getRoot().getChildren()) {
                    tdb.getRoot().addChild(n);
                    em.persist(n);
                }

                em.persist(em.merge(tdb));
                em.flush();

            }
        }
    }

    public List<String> getAppliedTransformations() {
        return appliedTransformations;
    }
}
