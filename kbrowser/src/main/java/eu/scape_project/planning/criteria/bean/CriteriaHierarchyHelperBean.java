package eu.scape_project.planning.criteria.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.richfaces.component.UITree;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.criteria.xml.CriteriaHierarchyExporter;
import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.kbrowser.CriteriaTreeNode;
import eu.scape_project.planning.model.kbrowser.VPlanLeaf;
import eu.scape_project.planning.model.measurement.Criterion;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.MeasurableProperty;
import eu.scape_project.planning.model.measurement.Metric;
import eu.scape_project.planning.utils.Downloader;
import eu.scape_project.planning.utils.FacesMessages;
import eu.scape_project.planning.xml.TreeLoader;

/**
 * Class responsible for supporting the view property_hierarchy.xhtml, 
 * at executing CRUD-operations and manipulating the hierarchies.
 * 
 * @author Markus Hamm
 */
@Stateful
@Named("criteriaHierarchyHelper")
@SessionScoped
public class CriteriaHierarchyHelperBean implements Serializable {
	private static final long serialVersionUID = 4589982063027782730L;

	private static final Logger log = LoggerFactory.getLogger(CriteriaHierarchyHelperBean.class);
	    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    private CriteriaManager criteriaManager;
    
    @Inject
    private TreeLoader treeLoader;
    
    @Inject
    private CriteriaHierarchyExporter criteriaHierarchyExporter;
    
    @Inject 
    private Downloader downloader;
    
    @Inject
    private FacesMessages facesMessages;
    
    @Inject
    private CriteriaSetsSummaryView criteriaSetsSummaryView;
    
    private DigitalObject importFile;
        
    @Inject
    private PlanSelection planSelection;
   
    private List<CriteriaHierarchy> allCriteriaHierarchies;
    private CriteriaHierarchy selectedCriteriaHierarchy;
    private List<CriteriaHierarchy> allCriteriaHierarchiesForSummary;
    private String newHierarchyName;
    private CriteriaLeaf selectedLeaf;
    

    
    // --- variables for property selection ----
    
    private Collection<CriterionCategory> categories;
    //private HashMap<String, CriterionCategory> categoriesMap;
    private CriterionCategory selectedCategory;
    //private String selectedCategoryString;
    
    private Collection<MeasurableProperty> allMeasurableProperties;
    private int allMeasurablePropertiesCount;
    private Collection<MeasurableProperty> filteredMeasurableProperties;
    private int filteredMeasurablePropertiesCount;
    private HashMap<String, MeasurableProperty> measurablePropertiesMap;
    private MeasurableProperty selectedMeasurableProperty;
    private String selectedMeasurablePropertyString;
    
    private List<Metric> metrics;
    private HashMap<String, Metric> metricsMap;
    private Metric selectedMetric;
    private String selectedMetricString;
    
    private Boolean isMeasurableCriterionSelected;
    
    public CriteriaHierarchyHelperBean() {
        allCriteriaHierarchiesForSummary = new ArrayList<CriteriaHierarchy>();
        
        collectCategories();
        //constructCategoriesMap();
        selectedCategory = null;
        //selectedCategoryString = null;
        
        
        metrics = new ArrayList<Metric>();
        metricsMap = new HashMap<String, Metric>();
        selectedMetric = null;
        selectedMetricString = null;
        
        isMeasurableCriterionSelected = false;
    }
    
    @PostConstruct 
    public void initBean(){
        allMeasurableProperties = criteriaManager.getKnownProperties();
        ArrayList<MeasurableProperty> allMeasurablePropertiesSortable = new ArrayList<MeasurableProperty>(allMeasurableProperties);
        Collections.sort(allMeasurablePropertiesSortable);
        allMeasurableProperties = allMeasurablePropertiesSortable;
        allMeasurablePropertiesCount = allMeasurableProperties.size();
        filteredMeasurableProperties = new ArrayList<MeasurableProperty>(allMeasurableProperties);
        filteredMeasurablePropertiesCount = filteredMeasurableProperties.size();
        
        constructMeasurablePropertiesMap();
        selectedMeasurableProperty = null;
        selectedMeasurablePropertyString = null;
        
    }
    /**
     * Load the user selected hierarchy including all dependent data.
     * 
     * @return outcome string ("criteria_tree.jsf" on success).
     */
    public String loadHierarchy() {
        // get the criteriaHierarchyId passed per request-parameter
        FacesContext context = FacesContext.getCurrentInstance();
        int clickedCriteriaHierarchyId = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("criteriaHierarchyId"));
        
        for (CriteriaHierarchy hierarchy : allCriteriaHierarchies) {
            if (hierarchy.getId() == clickedCriteriaHierarchyId) {
                selectedCriteriaHierarchy = hierarchy;
                log.debug("Loaded CriteriaHierarchy with id: " + hierarchy.getId());
                
                // assign relevant data to criteria root node
                selectedCriteriaHierarchy.getCriteriaTreeRoot().setNrOfRelevantPlans(planSelection.getSelectedPlans().size());
                
                for (CriteriaTreeNode criteriaTreeNode : selectedCriteriaHierarchy.getCriteriaTreeRoot().getAllSuccessiveTreeNodes()) {
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
                            criteriaLeaf.setPlanLeaves(getPlanLeavesMatchingCriterion(criteriaLeaf.getCriterion()));
                        }
                    }
                }
                log.debug("assigned relevant data to all Hierarchy tree nodes");
                
                return "criteria_tree.jsf";
            }
        }

        return "";
    }

    /**
     * Delete the user selected hierarchy.
     */
    public void deleteHierarchy() {
        // get the criteriaHierarchyId passed per request-parameter
        FacesContext context = FacesContext.getCurrentInstance();
        int clickedCriteriaHierarchyId = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("criteriaHierarchyId"));

        CriteriaHierarchy clickedCriteriaHierarchy = em.find(CriteriaHierarchy.class, clickedCriteriaHierarchyId);
        if (clickedCriteriaHierarchy != null) {
        	em.remove(clickedCriteriaHierarchy);
        	log.info("deleted CriteriaHierarchy with id=" + clickedCriteriaHierarchyId);
        }
    }
    
    /**
     * Method responsible for loading dependent data of all hierarchies.
     * 
     * @return outcome string ("success" on success).
     */
    public String loadAllHierarchiesDataForSummary() {
        allCriteriaHierarchiesForSummary = new ArrayList<CriteriaHierarchy>();
        
        // FIXME: to use getAllCriteriaHierarchies here is a bad hack to don't get a NullPointerException here when calling from index-page.
        // 		  Making a db-select in a getter (getAllCriteriaHierarchies) is bad practice - and has to be changed!!
        for (CriteriaHierarchy hierarchy : getAllCriteriaHierarchies()) {
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
                        criteriaLeaf.setPlanLeaves(getPlanLeavesMatchingCriterion(criteriaLeaf.getCriterion()));
                    }
                }
            }
            
            allCriteriaHierarchiesForSummary.add(hierarchy);
        }
        
        log.debug("assigned relevant data to tree nodes of all hierarchies");
        log.debug("Stored all hierarchies in hierarchy summary array");
        
        criteriaSetsSummaryView.init();
        
        return "criteria_sets_summary.jsf";
    }
        
    /**
     * Method responsible for returning all plan leaves matching the given criterion
     * 
     * @param property property criterion
     * @param metric metric criterion
     * @return a list of all plan leaves matching the given criterion.
     */
    private List<VPlanLeaf> getPlanLeavesMatchingCriterion(Criterion criterion) {
        List<VPlanLeaf> matchingLeaves = new ArrayList<VPlanLeaf>();
        
        // test which leaves match
        for (VPlanLeaf leaf : planSelection.getSelectionPlanLeaves()) {
            if (leaf.getCriterion() != null) {
                if (leaf.getCriterion().getUri().equals(criterion.getUri())) {
                    matchingLeaves.add(leaf);
                }
            }
        }
        
        return matchingLeaves;
    }
    
    /**
     * Method responsible for persisting the currently selected property hierarchy.
     */
    public void saveSelectedCriteriaHierarchy() {
        /*
         * ATTENTION: The EntityManger is injected by seam, which injects a new instance at every method call.
         * For this reason also the persistence context changes every time a method is called.
         * For this reason you cannot use em.persist() here and have to use em.merge().
         * (The object is not associated with the passed em-persistence-context but still exists in database).
         */
        selectedCriteriaHierarchy = em.merge(selectedCriteriaHierarchy);
        log.debug("saved critierahierarchy with id=" + selectedCriteriaHierarchy.getId());
    }
    
    /**
     * Create a new hierarchy(including a new root tree node) in database based on UI-input.
     * 
     * @return outcome string ("criteria_tree.jsf" on success).
     */
    public String createNewHierarchy() {
        // create a new hierarchy with a new root tree node and persist it.
        CriteriaHierarchy hierarchy = new CriteriaHierarchy();
        hierarchy.setName(newHierarchyName);

        // assign relevant data to criteria root node
        CriteriaNode rootNode = new CriteriaNode(planSelection.getSelectedPlans().size());
        rootNode.setName(newHierarchyName);
        rootNode.setNrOfRelevantPlans(planSelection.getSelectedPlans().size());
        
        hierarchy.setCriteriaTreeRoot(rootNode);
        em.persist(hierarchy);
        
        log.debug("Created CriteriaHierarchy with name: " + newHierarchyName);
        
        // set selected hierarchy and reset input field
        selectedCriteriaHierarchy = hierarchy;
        newHierarchyName = "";
        
        return "criteria_tree.jsf";
    }
    
    /**
     * Attaches a new CriteriaLeaf to the given object (which is, hopefully, a CriteriaNode)
     * 
     * @param parentNode Node to attach the new leaf to.
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
     * Attaches a new CriteriaNode to the given object (which is, hopefully, a CriteriaNode)
     * 
     * @param parentNode Node to attach the new node to.
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
     * @param nodeToRemove The node to remove.
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
     * Method responsible for indicating if a given part of the tree should be displayed open or closed.
     * In this case true is returned constantly, because the tree should be displayed open all the time.
     * 
     * @param interesting part of the tree 
     * @return true if the node should be displayed open, false if the node should be displayed closed.
     */
    public Boolean adviseNodeOpened(UITree tree) {
        return true;
    }
    
    /**
     * Method responsible for setting the selected leaf.
     * 
     * @param leaf Selected leaf 
     */
    public void selectLeaf(Object leaf) {
        if (!(leaf instanceof CriteriaLeaf))
            return;
        selectedLeaf = (CriteriaLeaf)leaf;       
        log.debug("Selected leaf with id=" + selectedLeaf.getId());
    }
    
    /**
     * Method responsible for attaching the user selected criterion to the selected leaf.
     */
    public void saveCriterionMapping() {
        selectedLeaf.setCriterion(
                criteriaManager.getCriterion(selectedMeasurableProperty, selectedMetric));
        
        // align name of the selected leaf with the selected property name
        selectedLeaf.setName(selectedMeasurableProperty.getName());
        
        selectedLeaf.setMapped(true);
        
        // add association plan leaves to the property leaf
        List<VPlanLeaf> associatedPlanLeaves = getPlanLeavesMatchingCriterion(selectedLeaf.getCriterion());
        selectedLeaf.setPlanLeaves(associatedPlanLeaves);
        
        log.debug("Saved criterion mapping.");
    }
    
    /**
     * Method responsible for selecting/setting a file for a later import.
     * (View related code)
     * 
     * @param event Richfaces FileUploadEvent data.
     */
    public void selectImportFile(FileUploadEvent event) {
    	UploadedFile file = event.getUploadedFile();
    	
    	// Do some input checks
    	if (!file.getName().endsWith("mm")) {
    		facesMessages.addError("importPanel", "Please select a FreeMind file.");
    		importFile = null;
    		return;
    	}
    	
		// Put file-data into a digital object
		importFile = new DigitalObject();
		importFile.setFullname(file.getName());
		importFile.getData().setData(file.getData());
		importFile.setContentType(file.getContentType());
    }  
    
    /**
     * Method responsible for importing the selected FreeMind File as CriteriaHierarchy.
     * (View related code)
     */
    public void importCriteriaHierarchy() {
		boolean importSuccessful = importCriteriaHierarchyFromFreemind(importFile);
		
		if (importSuccessful) {
			facesMessages.addInfo("importPanel", "Policy tree imported successfully");
			importFile = null;
			init();
		}
		else {
			facesMessages.addError("importPanel", "The uploaded file is not a valid Freemind mindmap. Maybe it is corrupted?");
		}
    }
    
	/**
	 * Method responsible for importing a criteria hierarchy from a a given FreeMind file.
	 * 
	 * @param file FreeMind file to import the criteria hierarchy from.
	 * @return True if the import was successful. False otherwise.
	 */
	public boolean importCriteriaHierarchyFromFreemind(DigitalObject file) {
        CriteriaHierarchy criteriaHierarchy = null;
        
        try {
            InputStream istream = new ByteArrayInputStream(file.getData().getData());
            criteriaHierarchy = treeLoader.loadFreeMindCriteriaHierarchy(istream, criteriaManager);
        } catch (Exception e) {
        	log.info("CriteriaHierarchy import from file " + file.getFullname() + " FAILED");
            log.error(e.getMessage(),e);
            return false;
        }
        
        if (criteriaHierarchy == null) {
        	return false;
        }
        
        em.persist(criteriaHierarchy);
        log.info("CriteriaHierarchy import from file " + file.getFullname() + " successful");
		
		return true;
	}

	public void exportCriteriaHierarchyAsFreeMindXML() {
		String freeMindXML = criteriaHierarchyExporter.exportToFreemindXml(selectedCriteriaHierarchy);				
		downloader.downloadMM(freeMindXML, selectedCriteriaHierarchy.getName() + ".mm");
	}
	

	
    /**
     * A function which exports all current criteria into a freemind-xml.
     * Used to ease creation of criteria-hierarchies (manual this is a hard job).
     */
    private String exportAllCriteriaToFreeMindXml() {
        Document doc = DocumentHelper.createDocument();
        doc.setXMLEncoding("UTF-8");
        
        Element root = doc.addElement("map");
        Namespace xsi = new Namespace("xsi",  "http://www.w3.org/2001/XMLSchema-instance");

        root.add(xsi);
        root.addAttribute("version","0.8.1");

        root.addComment("To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net");
        
        Element baseNode = root.addElement("node");
        baseNode.addAttribute("TEXT", "allCriteria");

        Collection<Criterion> allCriteria = criteriaManager.getKnownCriteria();
        ArrayList<Criterion> allCriteriaSortable = new ArrayList<Criterion>(allCriteria);
        Collections.sort(allCriteriaSortable);
        allCriteria = allCriteriaSortable;
        
//		allMeasurableProperties = criteriaManager.getKnownProperties();
//		ArrayList<MeasurableProperty> allMeasurablePropertiesSortable = new ArrayList<MeasurableProperty>(
//				allMeasurableProperties);
//		Collections.sort(allMeasurablePropertiesSortable);
//		allMeasurableProperties = allMeasurablePropertiesSortable;
        
        // each criterion should be added as simple node
        for (Criterion criterion : allCriteria) {
        	// construct node text
        	String nodeText = criterion.getProperty().getName();
        	if (criterion.getMetric() != null) {
        		nodeText = nodeText + "#" + criterion.getMetric().getName();
        	}
        	nodeText = nodeText + "|" + criterion.getUri();
        	
        	// add node
        	Element node = baseNode.addElement("node");
        	node.addAttribute("TEXT", nodeText);
        }
        
        String xml =  doc.asXML();

        return xml;
    }
    
    /**
     * Download all criteria as freemind xml.
     */
    public void downloadAllCriteriaAsFreeMindXml() {
    	downloader.downloadMM(exportAllCriteriaToFreeMindXml(), "allCriteria.mm");
    }
   
    //// ---------------------- methods for property selection ----------------------
    
    private void collectCategories() {
        categories = new ArrayList<CriterionCategory>();
        categories.add(CriterionCategory.OUTCOME_OBJECT);
        categories.add(CriterionCategory.OUTCOME_FORMAT);
        categories.add(CriterionCategory.OUTCOME_EFFECT);
        categories.add(CriterionCategory.ACTION);
    }
    
    /*
    private void constructCategoriesMap() {
        categoriesMap = new HashMap<String, CriterionCategory>();
        for (CriterionCategory cat : categories)
        {
            categoriesMap.put(cat.toString(), cat);
        }
    }
    */

    private void constructMeasurablePropertiesMap() {
        measurablePropertiesMap = new HashMap<String, MeasurableProperty>();
        for (MeasurableProperty mp : allMeasurableProperties)
        {
            measurablePropertiesMap.put(mp.getName(), mp);
        }
    }
    
    /**
     * Method responsible for handling the onchange-Events from Category-Selectbox in GUI.
     * All model-values are updated appropriate (including dependent Selectboxes). 
     */
    public void selectCategory() {
        log.debug("CALL selectCategory()");
               
        // debug output
        if (selectedCategory == null)
        {
            log.debug("Category: Nothing selected");
        }
        else
        {
            log.debug("Category selected: " + selectedCategory.toString());
        }        
        
        filterMeasurableProperties();
        updateIsMeasurableCriterionSelected();
    }
    
    public void filterMeasurableProperties() {
        // ATTENTION: Because of a Seam-Bug, this new creation of the filtered-measurableproperties-list is mandatory!
        // If you just clear the list and then refill it, the view (s:selectItems) does not mention a change and therefore does not update the associated selectBox.
        // Related bug: https://issues.jboss.org/browse/JBSEAM-4382
        Collection<MeasurableProperty> newFilteredMP = new ArrayList<MeasurableProperty>();
        
        Collection<MeasurableProperty> measurablePropertiesToFilter = allMeasurableProperties;
        for (MeasurableProperty p : measurablePropertiesToFilter) {
            if (selectedCategory == null || p.getCategory() == selectedCategory) {
                newFilteredMP.add(p);
            }
        }
        
        setFilteredMeasurableProperties(newFilteredMP);
        setFilteredMeasurablePropertiesCount(newFilteredMP.size());
                
        // check if selected MeasurableProperty is still available in the new filtered list.
        Boolean mpStillInFilteredList = false;
        if (selectedMeasurableProperty != null) {
            for (MeasurableProperty mp : filteredMeasurableProperties) {
                if (mp.getPropertyId().equals(selectedMeasurableProperty.getPropertyId())) {
                    mpStillInFilteredList = true;
                    log.debug("Selected Property still available in new filtered list");
                }
            }
            
            // if the previous selected MeasuableProperty is not available any more in the new filtered list
            // set the selection to null (which also affects the metrics select)
            if (!mpStillInFilteredList) {
                setSelectedMeasurablePropertyString(null);
                log.debug("Reset Selected Property to null");
                updateMetrics();
            }
        }
    }
    
    /**
     * Method responsible for handling the onchange-Events from Property-Selectbox in GUI.
     * All model-values are updated appropriate (including dependent Selectboxes). 
     */
    public void selectProperty() {
        log.debug("CALL selectProperty()");
        
        // debug output
        if (selectedMeasurableProperty == null)
        {
            log.debug("Property: Nothing selected");
        }
        else
        {
            log.debug("Property selected: " + selectedMeasurableProperty.getName());
        }
        
        updateMetrics();
        updateIsMeasurableCriterionSelected();
    }

    /**
     * Method responsible for handling the onchange-Events from Metric-Selectbox in GUI.
     * All model-values are updated appropriate. 
     */
    public void selectMetric() {
        log.debug("CALL selectMetric()");

        // debug output
        if (selectedMetric == null)
        {
            log.debug("Metric: Nothing selected");
        }
        else
        {
            log.debug("Metric selected: " + selectedMetric.getMetricId());
        }
        
        for (Metric m : metrics) {
            log.debug("Metric: " + m.getMetricId());
        }
        
        updateIsMeasurableCriterionSelected();
    }
    
    public void updateMetrics() {
    	setSelectedMetricString(null);
        
        if (selectedMeasurableProperty == null)
        {
            // ATTENTION: Because of a Seam-Bug, this new creation of the metrics-list is mandatory!
            // FIXME probably fixed in a new Seam?
            // If you just clear the list, the view (s:selectItems) does not mention a change and therefore does not update the associated selectBox.
            // Related bug: https://issues.jboss.org/browse/JBSEAM-4382            
            setMetrics(new ArrayList<Metric>());
            metricsMap.clear();
        }
        else
        {
            constructMetricsWithMap(selectedMeasurableProperty.getPossibleMetrics());
        }
        
        log.debug("Reset Metric to null");
    }
    
    private void constructMetricsWithMap(List<Metric> metrics) {
        // ATTENTION: Because of a Seam-Bug, this new creation of the metrics-list is mandatory!
        // If you just clear the list and fill it with new values, the view (s:selectItems) does not mention a change and therefore does not update the associated selectBox.
        // Related bug: https://issues.jboss.org/browse/JBSEAM-4382            
        setMetrics(metrics);
        
        metricsMap = new HashMap<String, Metric>();
        for (Metric m : this.metrics)
        {
            metricsMap.put(m.getMetricId(), m);
        }
    }
    
    /**
     * Method responsible for updating the measurablecriterion flag , dependent on the current user selection.
     */
    public void updateIsMeasurableCriterionSelected() {
        if ((selectedMeasurableProperty != null && selectedMetric != null) || 
            (selectedMeasurableProperty != null && selectedMeasurableProperty.getScale() != null)) {
            isMeasurableCriterionSelected = true;
            log.debug("MEASURABLE");
        }
        else {
            isMeasurableCriterionSelected = false;
            log.debug("NOT MEASURABLE");
        }
    }
    
    public Collection<CriterionCategory> getCategories() {
        return categories;
    }
    
    public void setCategories(Collection<CriterionCategory> categories) {
        this.categories = categories;
    }

    /*
    public String getSelectedCategoryString() {
        return selectedCategoryString;
    }
    
    public void setSelectedCategoryString(String selectedCategoryString) {
        this.selectedCategoryString = selectedCategoryString;
        
        if (selectedCategoryString == null)
        {
            selectedCategory = null;
        }
        else
        {
            selectedCategory = categoriesMap.get(selectedCategoryString);
        }        
    }
    */

    public void setSelectedMeasurableProperty(MeasurableProperty selectedMeasurableProperty) {
        log.debug("setSelectedMeasurableProperty()");
        this.selectedMeasurableProperty = selectedMeasurableProperty;
    }

    public MeasurableProperty getSelectedMeasurableProperty() {
        log.debug("getSelectedMeasurableProperty()=" + selectedMeasurableProperty);
        return selectedMeasurableProperty;
    }

    public void setFilteredMeasurableProperties(Collection<MeasurableProperty> filteredMeasurableProperties) {
        log.debug("setFilteredMeasurableProperties(): " + filteredMeasurableProperties.size());
        this.filteredMeasurableProperties = filteredMeasurableProperties;
    }

    public Collection<MeasurableProperty> getFilteredMeasurableProperties() {
        log.debug("getFilteredMeasurableProperties(): " + filteredMeasurableProperties.size());
        return filteredMeasurableProperties;
    }          
    
    public void setSelectedMeasurablePropertyString(String selectedMeasurablePropertyString) {
        log.debug("setSelectedMeasurablePropertyString(" + selectedMeasurablePropertyString + ")");
        
        this.selectedMeasurablePropertyString = selectedMeasurablePropertyString;
        
        if (selectedMeasurablePropertyString == null)
        {
            selectedMeasurableProperty = null;
        }
        else
        {
            selectedMeasurableProperty = measurablePropertiesMap.get(selectedMeasurablePropertyString);
        }
    }
    
    public String getSelectedMeasurablePropertyString() {
        log.debug("getSelectedMeasurablePropertyString()=" + selectedMeasurablePropertyString);
        return selectedMeasurablePropertyString;
    }
    
    public void setAllMeasurablePropertiesCount(int allMeasurablePropertiesCount) {
        this.allMeasurablePropertiesCount = allMeasurablePropertiesCount;
    }

    public int getAllMeasurablePropertiesCount() {
        return allMeasurablePropertiesCount;
    }
    
    public void setFilteredMeasurablePropertiesCount(
            int filteredMeasurablePropertiesCount) {
        this.filteredMeasurablePropertiesCount = filteredMeasurablePropertiesCount;
    }

    public int getFilteredMeasurablePropertiesCount() {
        return filteredMeasurablePropertiesCount;
    }
    
    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }          
    
    public void setSelectedMetricString(String selectedMetricString) {
        log.debug("setSelectedMetricString(): " + selectedMetricString);
        
        this.selectedMetricString = selectedMetricString;
        
        if (selectedMetricString == null)
        {
            selectedMetric = null;
        }
        else
        {
            selectedMetric = metricsMap.get(selectedMetricString);
        }
    }
       
    public String getSelectedMetricString() {
        log.debug("getSelectedMetricString(): " + selectedMetricString);
        return selectedMetricString;
    }
    
    public void setSelectedMetric(Metric selectedMetric) {
        this.selectedMetric = selectedMetric;
    }

    public Metric getSelectedMetric() {
        return selectedMetric;
    }
    
    public void setIsMeasurableCriterionSelected(Boolean isMeasurableCriterionSelected) {
        this.isMeasurableCriterionSelected = isMeasurableCriterionSelected;
    }

    public Boolean getIsMeasurableCriterionSelected() {
        return isMeasurableCriterionSelected;
    }
    
    public void exportCriteriaHierarchiesSummaryToCSV() {        
        String csvString = "";
        // header
        csvString += "Name;size;SIF1;SIF2;SIF3;SIF4;SIF5;SIF6;SIF7;SIF8;SIF9;SIF10;SIF11;SIF12;SIF13;SIF14;SIF15;SIF16\n";

        // assemble csv-data
        for (CriteriaHierarchy criteriaHierarchy : allCriteriaHierarchiesForSummary) {
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
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
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
     * Method responsible for returning the CriteriaTreeRoot of the selected CriteriaHierarchy as a List (required for views 'treeModelRecursiveAdaptor')
     * @return CriteriaTreeRoot of the selected CriteriaHierarchy as a List
     */
    public List<CriteriaNode> getSelectedCriteriaHierarchyCriteriaTreeRoots() {
    	List<CriteriaNode> result = new ArrayList<CriteriaNode>();
    	result.add(selectedCriteriaHierarchy.getCriteriaTreeRoot());
    	
    	return result;
    }
    
    //// ---------------------- general getter/setter ----------------------

    public void setAllCritereaHierarchies(List<CriteriaHierarchy> allCriteriaHierarchies) {
        this.allCriteriaHierarchies = allCriteriaHierarchies;
    }

    public List<CriteriaHierarchy> getAllCriteriaHierarchies() {
        allCriteriaHierarchies = (List<CriteriaHierarchy>) em.createQuery("SELECT h from CriteriaHierarchy h").getResultList();
        return allCriteriaHierarchies;
    }

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

    public void setAllCriteriaHierarchiesForSummary(
            List<CriteriaHierarchy> allCriteriaHierarchiesForSummary) {
        this.allCriteriaHierarchiesForSummary = allCriteriaHierarchiesForSummary;
    }

    public List<CriteriaHierarchy> getAllCriteriaHierarchiesForSummary() {
        return allCriteriaHierarchiesForSummary;
    }
    
	public CriterionCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(CriterionCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	
	public DigitalObject getImportFile() {
		return importFile;
	}

	public void setImportFile(DigitalObject importFile) {
		this.importFile = importFile;
	}    


	////---------------------- init ----------------------
    
    public String init() {
        log.debug("init finally called");
        return "criteria_hierarchy.jsf";
    }
}
