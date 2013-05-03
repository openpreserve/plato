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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import eu.scape_project.planning.bean.CriterionSelector;
import eu.scape_project.planning.criteria.xml.CriteriaHierarchyExporter;
import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.kbrowser.CriteriaTreeNode;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.utils.Downloader;
import eu.scape_project.planning.utils.FacesMessages;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.richfaces.component.UITree;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

/**
 * Class responsible for supporting the view criteria_hierarchy.xhtml, at
 * executing CRUD-operations and manipulating the hierarchies.
 * 
 * @author Markus Hamm
 */
@Stateful
@Named("criteriaHierarchyHelper")
@SessionScoped
public class CriteriaHierarchyHelperBean implements Serializable {
    private static final long serialVersionUID = 4589982063027782730L;

    @Inject
    private Logger log;

    @PersistenceContext
    EntityManager em;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private PlanSelection planSelection;

    @Inject
    private CriteriaManager criteriaManager;

    @Inject
    private ManageCriteriaSets manageCriteriaSets;

    @Inject
    private CriteriaHierarchyExporter criteriaHierarchyExporter;

    @Inject
    private Downloader downloader;

    @Inject
    private CriterionSelector criterionSelector;

    /**
     * Currently selected criteria set
     */
    private CriteriaHierarchy selectedCriteriaHierarchy;

    /**
     * Name for a new criteria set
     */
    private String newHierarchyName;

    /**
     * Currently selected leaf in the tree
     */
    private CriteriaLeaf selectedLeaf;

    @PostConstruct
    public void initBean() {
        criterionSelector.init();
    }

    /**
     * Load the user selected hierarchy including all dependent data.
     * 
     * @return outcome string ("criteria_tree.jsf" on success).
     */
    public String loadHierarchy() {
        // Get the criteriaHierarchyId passed per request-parameter
        FacesContext context = FacesContext.getCurrentInstance();
        int clickedCriteriaHierarchyId = Integer.parseInt(context.getExternalContext().getRequestParameterMap()
            .get("criteriaHierarchyId"));

        selectedCriteriaHierarchy = manageCriteriaSets.getCriteriaHierarchy(clickedCriteriaHierarchyId);

        if (selectedCriteriaHierarchy == null) {
            log.warn("Could not find a criteria hierarchy with the id [{}].", clickedCriteriaHierarchyId);
            facesMessages.addError("Could not find the selected criteria hierarchy.");
            return "";
        }
        return "criteria_tree.jsf";
    }

    /**
     * Delete the user selected hierarchy.
     */
    public void deleteHierarchy() {
        // Get the criteriaHierarchyId passed per request-parameter
        FacesContext context = FacesContext.getCurrentInstance();
        int hierarchyId = Integer.parseInt(context.getExternalContext().getRequestParameterMap()
            .get("criteriaHierarchyId"));

        try {
            manageCriteriaSets.deleteCriteriaHierarchy(hierarchyId);
            facesMessages.addInfo("Criteria hierarchy deleted.");
        } catch (PlanningException e) {
            facesMessages.addError("Could not delete criteria hierarchy.");
        }
    }

    /**
     * Saves changes in the currently selected criteria set.
     * 
     * @return the navigation target
     */
    public String save() {
        manageCriteriaSets.saveCriteriaHierarchy(selectedCriteriaHierarchy);
        facesMessages.addInfo("Criteria hierarchy saved.");
        return "criteria_hierarchy.jsf";
    }

    /**
     * Discards changes in the currently selected criteria set.
     * 
     * @return the navigation target
     */
    public String discard() {
        return "criteria_hierarchy.jsf";
    }

    /**
     * Create a new hierarchy(including a new root tree node) in database based
     * on UI-input.
     * 
     * @return outcome string ("criteria_tree.jsf" on success).
     */
    public String createNewHierarchy() {
        selectedCriteriaHierarchy = manageCriteriaSets.createCriteriaHierarchy(newHierarchyName);
        newHierarchyName = "";

        return "criteria_tree.jsf";
    }

    /**
     * Attaches a new CriteriaLeaf to the given object (which is, hopefully, a
     * CriteriaNode)
     * 
     * @param parentNode
     *            Node to attach the new leaf to.
     */
    public void addLeafToNode(Object parentNode) {
        if (parentNode instanceof CriteriaNode) {
            CriteriaNode node = (CriteriaNode) parentNode;
            CriteriaLeaf newLeaf = new CriteriaLeaf(planSelection.getSelectedPlans().size());
            node.addChild(newLeaf);
            log.debug("Leaf added to Node");
        }
    }

    /**
     * Attaches a new CriteriaNode to the given object (which is, hopefully, a
     * CriteriaNode)
     * 
     * @param parentNode
     *            Node to attach the new node to.
     */
    public void addNodeToNode(Object parentNode) {
        if (parentNode instanceof CriteriaNode) {
            CriteriaNode node = (CriteriaNode) parentNode;
            CriteriaNode newNode = new CriteriaNode(planSelection.getSelectedPlans().size());
            node.addChild(newNode);
            log.debug("Node added to Node");
        }
    }

    /**
     * Removes a CriteriaTreeNode from the hierarchy-tree.
     * 
     * @param nodeToRemove
     *            The node to remove.
     */
    public void removeNode(Object nodeToRemove) {
        CriteriaTreeNode node = (CriteriaTreeNode) nodeToRemove;
        // only remove the node if it is not the root node
        if (node.getParent() != null) {
            CriteriaNode parentNode = (CriteriaNode) node.getParent();
            parentNode.removeChild(node);
        }
    }

    /**
     * Method responsible for indicating if a given part of the tree should be
     * displayed open or closed. In this case true is returned constantly,
     * because the tree should be displayed open all the time.
     * 
     * @param tree
     *            part of the tree
     * @return true if the node should be displayed open, false if the node
     *         should be displayed closed.
     */
    public Boolean adviseNodeOpened(UITree tree) {
        return true;
    }

    /**
     * Method responsible for setting the selected leaf.
     * 
     * @param leaf
     *            Selected leaf
     */
    public void selectLeaf(Object leaf) {
        if (!(leaf instanceof CriteriaLeaf)) {
            return;
        }
        selectedLeaf = (CriteriaLeaf) leaf;
        log.debug("Selected leaf with id=" + selectedLeaf.getId());
    }

    /**
     * Method responsible for attaching the user-selected criterion to the
     * selected leaf.
     */
    public void saveCriterionMapping() {
        manageCriteriaSets.assignMeasureToLeaf(criterionSelector.getSelectedMeasure(), selectedLeaf);
    }

    /**
     * Method responsible for importing the selected FreeMind file as
     * CriteriaHierarchy.
     * 
     * @param event
     *            the file upload event
     */
    public void uploadCriteriaHierarchy(FileUploadEvent event) {
        UploadedFile file = event.getUploadedFile();
        String filename = file.getName();

        // Do some input checks
        if (!filename.endsWith("mm")) {
            log.warn("The uploaded file [{}] is not a Freemind file.", filename);
            facesMessages.addError("The uploaded file is not a Freemind file.");
            return;
        }

        // Put file-data into a digital object
        DigitalObject importFile = new DigitalObject();
        importFile.setFullname(filename);
        importFile.getData().setData(file.getData());
        importFile.setContentType(file.getContentType());

        boolean importSuccessful = manageCriteriaSets.importCriteriaHierarchyFromFreemind(importFile);

        if (importSuccessful) {
            facesMessages.addInfo("Criteria set imported successfully");
            importFile = null;
            init();
        } else {
            facesMessages.addError("The uploaded file is not a valid Freemind mindmap.");
        }
    }

    /**
     * Initiates the download of the currently selected criteria hierarchy as
     * Freemind XML file.
     */
    public void exportCriteriaHierarchyAsFreeMindXML() {
        String freeMindXML = criteriaHierarchyExporter.exportToFreemindXml(selectedCriteriaHierarchy);
        downloader.downloadMM(freeMindXML, selectedCriteriaHierarchy.getName() + ".mm");
    }

    /**
     * A function which exports all current criteria into a freemind xml string.
     * Used to ease creation of criteria-hierarchies (manual this is a hard
     * job).
     * 
     * @return the criteria as XML string
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

    /**
     * Download all criteria as Freemind XML file.
     */
    public void downloadAllCriteriaAsFreeMindXml() {
        downloader.downloadMM(exportAllCriteriaToFreeMindXml(), "allCriteria.mm");
    }

    public void exportCriteriaHierarchiesSummaryToCSV() {
        String csvString = "";
        // header
        csvString += "Name;size;SIF1;SIF2;SIF3;SIF4;SIF5;SIF6;SIF7;SIF8;SIF9;SIF10;SIF11;SIF12;SIF13;SIF14;SIF15;SIF16\n";

        manageCriteriaSets.loadCriteriaHierarchyDependentData();

        // assemble csv-data
        for (CriteriaHierarchy criteriaHierarchy : manageCriteriaSets.getAllCriteriaHierarchies()) {
            csvString += criteriaHierarchy.getName() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getAllSuccessiveLeaves().size() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF1() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF2() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF3() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF4() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF5() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF6() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF7() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF8() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF9() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF10() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF11() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF12() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF13() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF14() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF15() + ";";
            csvString += criteriaHierarchy.getCriteriaTreeRoot().getStringFormattedImportanceFactorSIF16() + ";";
            csvString += "\n";
        }

        // send csv-file to browser
        byte[] csvByteStream = csvString.getBytes();
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
            .getResponse();
        response.setHeader("Content-disposition", "attachment; filename= CriteriaHierarchiesSummary.csv");
        response.setContentLength(csvString.length());
        response.setContentType("application/vnd.ms-excel");
        try {
            response.getOutputStream().write(csvByteStream);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            context.responseComplete();
            log.debug("Exported CriteriaHierarchiesSummary successfully to CSV-File.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method responsible for returning the CriteriaTreeRoot of the selected
     * CriteriaHierarchy as a List (required for views
     * 'treeModelRecursiveAdaptor')
     * 
     * @return CriteriaTreeRoot of the selected CriteriaHierarchy as a List
     */
    public List<CriteriaNode> getSelectedCriteriaHierarchyCriteriaTreeRoots() {
        List<CriteriaNode> result = new ArrayList<CriteriaNode>();
        result.add(selectedCriteriaHierarchy.getCriteriaTreeRoot());
        return result;
    }

    /**
     * Returns all criteria hierarchies.
     * 
     * @return a list of criteria hierarchies
     */
    public List<CriteriaHierarchy> getAllCriteriaHierarchies() {
        return manageCriteriaSets.getAllCriteriaHierarchies();
    }

    // ---------------------- getter/setter ----------------------
    public void setNewHierarchyName(String newHierarchyName) {
        this.newHierarchyName = newHierarchyName;
    }

    public String getNewHierarchyName() {
        return newHierarchyName;
    }

    public void setSelectedLeaf(CriteriaLeaf selectedLeaf) {
        this.selectedLeaf = selectedLeaf;
    }

    public CriteriaLeaf getSelectedLeaf() {
        return selectedLeaf;
    }

    public void setSelectedCriteriaHierarchy(CriteriaHierarchy selectedCriteriaHierarchy) {
        this.selectedCriteriaHierarchy = selectedCriteriaHierarchy;
    }

    public CriteriaHierarchy getSelectedCriteriaHierarchy() {
        return selectedCriteriaHierarchy;
    }

    public CriterionSelector getCriterionSelector() {
        return criterionSelector;
    }

    public void setCriterionSelector(CriterionSelector criterionSelector) {
        this.criterionSelector = criterionSelector;
    }

    // ---------------------- init ----------------------
    public String init() {
        return "criteria_hierarchy.jsf";
    }
}
