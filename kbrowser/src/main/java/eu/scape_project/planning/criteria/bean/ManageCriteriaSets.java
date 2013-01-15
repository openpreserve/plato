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
package eu.scape_project.planning.criteria.bean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.kbrowser.CriteriaTreeNode;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.xml.TreeLoader;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.slf4j.Logger;

/**
 * Class to manipulate criteria sets.
 */
@Stateful
@SessionScoped
public class ManageCriteriaSets implements Serializable {

    private static final long serialVersionUID = 1569776681316820766L;

    @Inject
    private Logger log;

    @PersistenceContext
    EntityManager em;

    @Inject
    private CriteriaManager criteriaManager;

    @Inject
    private TreeLoader treeLoader;

    @Inject
    private PlanSelection planSelection;

    private List<CriteriaHierarchy> allCriteriaHierarchies;

    /**
     * Measures to delete on save.
     */
    private List<Measure> measuresToDelete = new ArrayList<Measure>();

    /**
     * Initialises the bean.
     */
    @PostConstruct
    public void init() {
        allCriteriaHierarchies = em.createQuery("SELECT h from CriteriaHierarchy h", CriteriaHierarchy.class)
            .getResultList();

        measuresToDelete.clear();
    }

    /**
     * Load the user selected hierarchy including all dependent data.
     * 
     * @param hierarchyId
     *            the ID of the criteria hierarchy
     */
    public CriteriaHierarchy getCriteriaHierarchy(int hierarchyId) {

        for (CriteriaHierarchy hierarchy : allCriteriaHierarchies) {
            if (hierarchy.getId() == hierarchyId) {
                log.debug("Loaded CriteriaHierarchy with id: " + hierarchy.getId());

                // assign relevant data to criteria root node
                hierarchy.getCriteriaTreeRoot().setNrOfRelevantPlans(planSelection.getSelectedPlans().size());

                for (CriteriaTreeNode criteriaTreeNode : hierarchy.getCriteriaTreeRoot().getAllSuccessiveTreeNodes()) {
                    // assign relevant data to criteria nodes
                    if (criteriaTreeNode instanceof CriteriaNode) {
                        CriteriaNode criteriaNode = (CriteriaNode) criteriaTreeNode;
                        criteriaNode.setNrOfRelevantPlans(planSelection.getSelectedPlans().size());
                    }
                    // assign relevant data to criteria leaves
                    if (criteriaTreeNode instanceof CriteriaLeaf) {
                        CriteriaLeaf criteriaLeaf = (CriteriaLeaf) criteriaTreeNode;
                        criteriaLeaf.setNrOfRelevantPlans(planSelection.getSelectedPlans().size());
                        if (criteriaLeaf.getMapped()) {
                            criteriaLeaf.setPlanLeaves(getPlanLeavesMatchingCriterion(criteriaLeaf.getMeasure()));
                        }
                    }
                }
                log.debug("Assigned relevant data to all Hierarchy tree nodes.");

                return hierarchy;
            }
        }

        return null;
    }

    /**
     * Delete the user selected hierarchy.
     */
    public void deleteCriteriaHierarchy(int hierarchyId) throws PlanningException {
        CriteriaHierarchy criteriaHierarchy = em.find(CriteriaHierarchy.class, hierarchyId);

        if (criteriaHierarchy == null) {
            log.error("Could not find criteria hiearachy [{}].", hierarchyId);
            throw new PlanningException("Could not find criteria hiearachy [" + hierarchyId + "]");
        }

        em.remove(criteriaHierarchy);
        log.info("Deleted CriteriaHierarchy with id [{}]", hierarchyId);

        init();
    }

    /**
     * Creates a new criteria hierarchy.
     * 
     * @param name
     *            the name of the new criteria hierarchy
     * @return the new criteria hierarchy
     */
    public CriteriaHierarchy createCriteriaHierarchy(String name) {
        // Create a new hierarchy with a new root tree node and persist it.
        CriteriaHierarchy hierarchy = new CriteriaHierarchy();
        hierarchy.setName(name);

        // Assign relevant data to criteria root node.
        CriteriaNode rootNode = new CriteriaNode(planSelection.getSelectedPlans().size());
        rootNode.setName(name);
        rootNode.setNrOfRelevantPlans(planSelection.getSelectedPlans().size());

        hierarchy.setCriteriaTreeRoot(rootNode);
        em.persist(hierarchy);

        log.debug("Created CriteriaHierarchy with name [{}].", name);

        init();

        return hierarchy;
    }

    /**
     * Method responsible for importing a criteria hierarchy from a a given
     * FreeMind file.
     * 
     * @param file
     *            FreeMind file to import the criteria hierarchy from.
     * @return true if the import was successful. False otherwise.
     */
    public boolean importCriteriaHierarchyFromFreemind(DigitalObject file) {
        CriteriaHierarchy criteriaHierarchy = null;

        try {
            InputStream is = new ByteArrayInputStream(file.getData().getData());
            criteriaHierarchy = treeLoader.loadFreeMindCriteriaHierarchy(is, criteriaManager);
        } catch (Exception e) {
            log.info("CriteriaHierarchy import from file " + file.getFullname() + " FAILED");
            log.error(e.getMessage(), e);
            return false;
        }

        if (criteriaHierarchy == null) {
            return false;
        }

        em.persist(em.merge(criteriaHierarchy));
        log.info("CriteriaHierarchy import from file " + file.getFullname() + " successful");

        init();

        return true;
    }

    /**
     * Saves the provided criteria hierarchy.
     * 
     * @param hierarchy
     *            the criteria hierarchy to save
     */
    public void saveCriteriaHierarchy(CriteriaHierarchy hierarchy) {
        em.merge(hierarchy);
        log.debug("Saved critierahierarchy with id [{}]", hierarchy.getId());

        deleteOrphanedEntities();

        init();
    }

    /**
     * Deletes orphaned entities which are not removed by persistence provider
     * automatically, namely: measures.
     */
    private void deleteOrphanedEntities() {
        for (Measure m : measuresToDelete) {
            if (m.getId() != 0) {
                Object merged = em.merge(m);
                em.remove(merged);
            }
        }
        measuresToDelete.clear();
    }

    /**
     * Assigns a measure to a leaf.
     * 
     * @param measure
     *            the measure to assign
     * @param criteriaLeaf
     *            the leaf to assign the measure to
     */
    public void assignMeasureToLeaf(final Measure measure, CriteriaLeaf criteriaLeaf) {
        Measure oldMeasure = criteriaLeaf.getMeasure();
        if (oldMeasure != null) {
            measuresToDelete.add(oldMeasure);
        }

        criteriaLeaf.setMeasure(measure);
        criteriaLeaf.setName(measure.getName());
        criteriaLeaf.setMapped(true);

        // Add association plan leaves to the property leaf
        List<VPlanLeaf> associatedPlanLeaves = getPlanLeavesMatchingCriterion(measure);
        criteriaLeaf.setPlanLeaves(associatedPlanLeaves);

        log.debug("Assigned measure [{}] to leaf.", measure.getName());
    }

    /**
     * Returns all plan leaves matching the given measure.
     * 
     * @param measure
     *            the measure
     * @return a list of all plan leaves matching the given measure
     */
    private List<VPlanLeaf> getPlanLeavesMatchingCriterion(Measure measure) {
        List<VPlanLeaf> matchingLeaves = new ArrayList<VPlanLeaf>();

        // Test which leaves match
        for (VPlanLeaf leaf : planSelection.getSelectionPlanLeaves()) {
            if (leaf.getMeasure() != null) {
                if (leaf.getMeasure().getUri().equals(measure.getUri())) {
                    matchingLeaves.add(leaf);
                }
            }
        }

        return matchingLeaves;
    }

    /**
     * Method responsible for loading dependent data of all hierarchies.
     * 
     * @return outcome string ("success" on success).
     */
    public void loadCriteriaHierarchyDependentData() {
        for (CriteriaHierarchy hierarchy : allCriteriaHierarchies) {
            // assign relevant data to criteria root node
            hierarchy.getCriteriaTreeRoot().setNrOfRelevantPlans(planSelection.getSelectedPlans().size());

            for (CriteriaTreeNode criteriaTreeNode : hierarchy.getCriteriaTreeRoot().getAllSuccessiveTreeNodes()) {
                // assign relevant data to criteria nodes
                if (criteriaTreeNode instanceof CriteriaNode) {
                    CriteriaNode criteriaNode = (CriteriaNode) criteriaTreeNode;
                    criteriaNode.setNrOfRelevantPlans(planSelection.getSelectedPlans().size());
                }
                // assign relevant data to criteria leaves
                if (criteriaTreeNode instanceof CriteriaLeaf) {
                    CriteriaLeaf criteriaLeaf = (CriteriaLeaf) criteriaTreeNode;
                    criteriaLeaf.setNrOfRelevantPlans(planSelection.getSelectedPlans().size());
                    if (criteriaLeaf.getMapped()) {
                        criteriaLeaf.setPlanLeaves(getPlanLeavesMatchingCriterion(criteriaLeaf.getMeasure()));
                    }
                }
            }
        }
    }

    /**
     * A function which exports all current criteria into a freemind-xml. Used
     * to ease creation of criteria-hierarchies (manual this is a hard job).
     */
    private String exportAllCriteriaToFreeMindXml() {
        Document doc = DocumentHelper.createDocument();
        doc.setXMLEncoding("UTF-8");

        Element root = doc.addElement("map");
        Namespace xsi = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        root.add(xsi);
        root.addAttribute("version", "0.8.1");

        root.addComment("To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net");

        Element baseNode = root.addElement("node");
        baseNode.addAttribute("TEXT", "allCriteria");

        Collection<Measure> allCriteria = criteriaManager.getAllMeasures();
        ArrayList<Measure> allCriteriaSortable = new ArrayList<Measure>(allCriteria);
        Collections.sort(allCriteriaSortable);
        allCriteria = allCriteriaSortable;

        // each criterion should be added as simple node
        for (Measure measure : allCriteria) {
            // construct node text
            String nodeText = measure.getAttribute().getName() + "#" + measure.getName() + "|" + measure.getUri();

            // add node
            Element node = baseNode.addElement("node");
            node.addAttribute("TEXT", nodeText);
        }

        String xml = doc.asXML();
        return xml;
    }

    // ---------------------- getter/setter ----------------------
    public void setAllCritereaHierarchies(List<CriteriaHierarchy> allCriteriaHierarchies) {
        this.allCriteriaHierarchies = allCriteriaHierarchies;
    }

    public List<CriteriaHierarchy> getAllCriteriaHierarchies() {
        return allCriteriaHierarchies;
    }
}
