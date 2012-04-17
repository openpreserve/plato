package eu.scape_project.pw.planning.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;

import sun.misc.BASE64Encoder;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.ChangeLog;
import eu.planets_project.pp.plato.model.DetailedExperimentInfo;
import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.ExecutablePlanDefinition;
import eu.planets_project.pp.plato.model.Experiment;
import eu.planets_project.pp.plato.model.Parameter;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PlanDefinition;
import eu.planets_project.pp.plato.model.Policy;
import eu.planets_project.pp.plato.model.PolicyNode;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.TargetValueObject;
import eu.planets_project.pp.plato.model.Trigger;
import eu.planets_project.pp.plato.model.measurement.Criterion;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.Measurement;
import eu.planets_project.pp.plato.model.measurement.Metric;
import eu.planets_project.pp.plato.model.scales.FreeStringScale;
import eu.planets_project.pp.plato.model.scales.RestrictedScale;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.scales.ScaleType;
import eu.planets_project.pp.plato.model.transform.NumericTransformer;
import eu.planets_project.pp.plato.model.transform.OrdinalTransformer;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.TemplateTree;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.model.util.FloatFormatter;
import eu.planets_project.pp.plato.model.values.Value;
import eu.scape_project.pw.planning.xml.plato.TimestampFormatter;

/**
 * Static methods providing means to export projects to XML using dom4j.
 *
 * @author Michael Kraxner, Markus Hamm
 */
public class ProjectExporter implements Serializable {
	private static final long serialVersionUID = 7538933914251415135L;
	
	@Inject	private Logger logger;
	
    private TimestampFormatter formatter = new TimestampFormatter();
    private FloatFormatter floatFormatter = new FloatFormatter();

    public static OutputFormat prettyFormat = new OutputFormat(" ", true,"UTF-8"); //OutputFormat.createPrettyPrint();
    public static OutputFormat compactFormat = new OutputFormat(null, false,"UTF-8"); //OutputFormat.createPrettyPrint();


    public static Namespace excutablePlanNS;
    public static Namespace xsi;
    public static Namespace platoNS;
    
    static {
        excutablePlanNS = new Namespace("wdt", "http://www.planets-project.eu/wdt");
        xsi = new Namespace("xsi",  "http://www.w3.org/2001/XMLSchema-instance");
        platoNS = new Namespace("", "http://www.planets-project.eu/plato");
    }
    
    public ProjectExporter() { }
        
    /**
     * Returns the xml-representation of the given project as a String.
     * NOTE: It writes all data - including encoded binary data - directly to the DOM tree
     *       this may result in performance problems for large amounts of data.  
     */
    public String exportToString(Plan p) {
        return exportToXml(p).asXML();
    }

   
    
    /**
     * Takes the given project and turns it into a dom4j-xml-representation.
     * NOTE: It writes all data - including encoded binary data - directly to the DOM tree
     *       this may result in performance problems for large amounts of data.  
     * @return dom4j-document representing the given project
     */
    public Document exportToXml(Plan p) {
        Document doc = createProjectDoc();
        addProject(p, doc, null, null);
        return doc;
    }
    
    public Document createProjectDoc() {
        Document doc = DocumentHelper.createDocument();
        
        Element root = doc.addElement("plans");
        
        root.add(xsi);
        root.add(platoNS);
        root.addAttribute(xsi.getPrefix()+":schemaLocation", "http://www.planets-project.eu/plato plato-3.0.xsd");

        root.add(excutablePlanNS);
        root.add(new Namespace("fits", "http://hul.harvard.edu/ois/xml/ns/fits/fits_output"));

        // set version of corresponding schema
        root
           .addAttribute("version", "3.0.1");
        
        return doc;
    }
    
    public Document createTemplateDoc() {
        Document doc = DocumentHelper.createDocument();
        
        Element root = doc.addElement("templates");
        
        root.add(xsi);
        
        return doc;
    }
    
    /**
     * This method creates a template xml from the objective tree contained in Plan p. The template xml is of the form
     * 
     * <templates>
     *   <template name="Public Fragments">
     *     <node name="Template 1" weight="0.0" single="false" lock="false">
     *         <node name="Interactive multimedia presentations" weight="0.0" single="false" lock="false">
     *      ...
     *   </template>
     * </templates>
     * 
     * For creating the tree we use {@link ProjectExporter#addSubTree(TreeNode, Element)} and remove afterwards
     * the evaluation part which we don't want to have in the template.
     *
     * @param p preservation plan
     * @param templateLibrary name of the template library (e.g. 'Public Templates')
     * @param name name of the template
     * @param description description of the template
     * @return xml as string
     */
    public String getObjectiveTreeAsTemplate(Plan p, String templateLibrary, String name, String description) {
        Document templateDoc = createTemplateDoc();
        
        Element template = templateDoc.getRootElement().addElement("template");
        template.addAttribute("name", templateLibrary);
        
        // Objectivetree (including weights, evaluation values and transformers)
        if (p.getTree().getRoot() != null) {
           addSubTree(p.getTree().getRoot(), template);
        }
        
        // get the root node of the tree
        Element treeRootNode = (Element)template.selectSingleNode("//templates/template/node");
        
        // change the name of the template
        if (name == null) {
            name = "";
        }
        treeRootNode.addAttribute("name", name);

        if (description == null) {
            description = "";
        }
        // set the description of the template
        Element descriptionElement = treeRootNode.addElement("description");
        descriptionElement.setText(description);
        

        // remove the evaluation from the template
        List<Element> nodes = templateDoc.selectNodes("//leaf/evaluation");
        
        for (Element n : nodes) {
            n.getParent().remove(n);
        }
        
        return templateDoc.asXML();
    }

    /**
     * Takes the given projects and turns them into a dom4j-xml-representation.
     * NOTE: It writes all data - including encoded binary data - directly to the DOM tree
     *   this may result in performance problems for large amounts of data.  
     * @return dom4j-document representing the given project
    public static Document exportToXml(List<Plan> projects) {
        Document doc = createProjectDoc();
        
        for (Plan project : projects) {
            ProjectExporter.addProject(project, doc, null, null);
        }
        return doc;
    }
    */
    
    /**
     * Writes the xml-representation of the given projects to the given target file.
     * NOTE: It writes all data - including encoded binary data - directly to the DOM tree
     *       this may result in performance problems for large amounts of data.  
     */
    public void exportToFile(Plan p, File target) throws IOException {
        XMLWriter writer = new XMLWriter(new FileWriter(target), ProjectExporter.prettyFormat);
        writer.write(exportToXml(p));
        writer.close();
    }

    /**
     * Writes the xml-representation of the given project into a temporary file and
     * returns the java-representation of this file.
     * NOTE: It writes all data - including encoded binary data - directly to the DOM tree
     *       this may result in performance problems for large amounts of data.  
     * 
     */
    public File exportToFile(Plan p) throws IOException {
        File temp = File.createTempFile("plato-plan-export-", ".xml");
        exportToFile(p, temp);
        return temp;
    }

/*    public static File exportTemplatesToFile(List<TemplateTree> trees) throws IOException {
        File temp = File.createTempFile("plato-templates-export-", ".xml");
        XMLWriter writer = new XMLWriter(new FileWriter(temp), ProjectExporter.prettyFormat);
        //writer.setMaximumAllowedCharacter(127);
        writer.write(ProjectExporter.exportTemplates(trees));
        writer.close();
        return temp;
    }*/

    public Document exportTemplates(List<TemplateTree> trees) {
        Document doc = DocumentHelper.createDocument();

        Element root = doc.addElement("templates");

        for (TemplateTree template : trees) {
            addTemplateTree(template, root);
        }
        return doc;
    }

    /**
     * Adds the XML representation of the complete template tree to the parent node <code>root</code>.
     *
     * @param tree
     * @param root
     */
    private void addTemplateTree(TemplateTree tree, Element root) {
        Element template = root.addElement("template");
        template.addAttribute("name", tree.getName());
        template.addAttribute("owner", tree.getOwner());
        for (TreeNode child : tree.getRoot().getChildren()) {
            addSubTree(child, template);
        }
    }

    private  void addSubPolicyTree(PolicyNode data, Element xmlRoot) {

        if (data == null) {
            return;
        }

        if (data.isPolicy()) {
            Element policy = xmlRoot.addElement("policy");

            Policy p = (Policy)data;

            policy.addAttribute("name", p.getName());
            policy.addAttribute("value", p.getValue());
        } else {
            Element policyNode = xmlRoot.addElement("policyNode");

            policyNode.addAttribute("name", data.getName());

            for (PolicyNode pn : data.getChildren()) {
                addSubPolicyTree(pn, policyNode);
            }
        }
    }
    
    /**
     * Adds the XML-representation of the treenode <code>data</code> to the parent node <code>xmlRoot</code>.
     * (also called recursivly for its children)
     *
     * @param data
     * @param xmlRoot
     */
    private void addSubTree(TreeNode data, Element xmlRoot) {
        if (data.isLeaf()) {
            Element leaf = xmlRoot.addElement("leaf");
            addNodeAttributes(data, leaf);
            Leaf l = (Leaf) data;
            leaf.addElement("aggregationMode")
            .addAttribute("value", l.getAggregationMode().name());

            String typename= null;
            /*
             * Scale:
             * A special element is created, depending on the type of the scale
             */
            Scale s = l.getScale();

            if (s != null) {
                typename = deriveElementname(s.getClass());


                addScale(s, leaf);

                // Transformer
                if (l.getTransformer() != null) {
                    Element transformer = leaf.addElement(deriveElementname(l.getTransformer().getClass()));

                    if (l.getTransformer() instanceof OrdinalTransformer) {

                        Map<String,TargetValueObject> mapping = ((OrdinalTransformer) l.getTransformer()).getMapping();
                        Element mappings = transformer.addElement("mappings");
                        for (String ordinal : mapping.keySet()) {
                            mappings.addElement("mapping")
                            .addAttribute("ordinal", ordinal)
                            .addAttribute("target", floatFormatter.formatFloatPrecisly(mapping.get(ordinal).getValue()));
                        }
                    }
                    if (l.getTransformer() instanceof NumericTransformer) {
                        NumericTransformer nt = (NumericTransformer) l.getTransformer();
                        transformer.addElement("mode")
                                   .addAttribute("value", nt.getMode().name());
                        Element thresholds = transformer.addElement("thresholds");
                        thresholds.addElement("threshold1").setText(floatFormatter.formatFloatPrecisly(nt.getThreshold1()));
                        thresholds.addElement("threshold2").setText(floatFormatter.formatFloatPrecisly(nt.getThreshold2()));
                        thresholds.addElement("threshold3").setText(floatFormatter.formatFloatPrecisly(nt.getThreshold3()));
                        thresholds.addElement("threshold4").setText(floatFormatter.formatFloatPrecisly(nt.getThreshold4()));
                        thresholds.addElement("threshold5").setText(floatFormatter.formatFloatPrecisly(nt.getThreshold5()));
                        
                    }
                    addChangeLog(l.getTransformer().getChangeLog(), transformer);
                }
                
                if (l.isMapped()) {
                    addCriterionInfo(l.getCriterion(), leaf);
                }
                
                Element eval = leaf.addElement("evaluation");
                typename = typename.substring(0, typename.lastIndexOf("Scale"));
                /*
                 * keep in mind: there are only values of the considered alternatives in the map
                 */
                for (String a : l.getValueMap().keySet()) {
                    Element alt = eval.addElement("alternative");
                    alt.addAttribute("key", a);
                    addStringElement(alt, "comment", l.getValueMap().get(a).getComment());
                    for (Value v : l.getValueMap().get(a).getList()) {
                        /*
                         * A special element is created, depending on the type of the scale
                         */
                        Element valElement = alt.addElement(typename+"Result");
                        addStringElement(valElement, "value", v.toString());
                        addStringElement(valElement, "comment", v.getComment());
                        addChangeLog(v.getChangeLog(), valElement);
                        
                    }
                }
            }

        } else { // not a leaf
            Element node = xmlRoot.addElement("node");
            addNodeAttributes(data, node);

            for (TreeNode child : data.getChildren()) {
                addSubTree(child, node);
            }
        }

    }

    /**
     * creates a new element with all information of the given criterion mapping, and adds it to the parent node.
     * This includes the property and the chosen metric, but NOT the list of possible metrics. 
     *  
     * @param info
     * @param parent
     */
    private void addCriterionInfo(Criterion criterion, Element parent) {
        Element infoEl = parent.addElement("criterion");

        MeasurableProperty prop = (MeasurableProperty) criterion.getProperty();
        if (prop != null) {
            Element propertyEl = infoEl.addElement("property");
            addStringElement(propertyEl, "category", prop.getCategory().toString());
            addStringElement(propertyEl, "propertyId", prop.getPropertyId());
            addStringElement(propertyEl, "name", prop.getName());
            addStringElement(propertyEl, "description", prop.getDescription());
            addStringElement(propertyEl, "subject", prop.getSubject().toString());

            addScale(prop.getScale(), propertyEl);
            // note: we only keep the selected property and metric
            addChangeLog(prop.getChangeLog(), propertyEl);
            
        }
        addMetric(criterion.getMetric(), infoEl);

        addChangeLog(criterion.getChangeLog(), infoEl);
    }
    
    /**
     * creates a new element with all information of the given metric, 
     * and adds it to the parent node.
     * 
     * @param m
     * @param parent
     */
    private void addMetric(Metric m, Element parent) {
        if (m != null) {
            Element metricEl = parent.addElement("metric");
            addStringElement(metricEl, "metricId", m.getMetricId());
            addStringElement(metricEl, "name", m.getName());
            addStringElement(metricEl, "description", m.getDescription());
            addScale(m.getScale(), metricEl);
            addChangeLog(m.getChangeLog(), metricEl);
        }
    }

    /**
     * Adds the given node's properties to the xmlNode.
     */
    private  void addNodeAttributes(TreeNode data, Element xmlNode) {
        xmlNode
        .addAttribute("name", data.getName())
        .addAttribute("weight", floatFormatter.formatFloatPrecisly(data.getWeight()));
        if (data instanceof Leaf) {
            xmlNode.addAttribute("single", Boolean.toString(data.isSingle()));
        }
        xmlNode
        .addAttribute("lock", Boolean.toString(data.isLock()));
        
        addStringElement(xmlNode, "description", data.getDescription());
        
        addChangeLog(data.getChangeLog(), xmlNode);
    }

    private  void addChangeLog(ChangeLog log, Element xmlNode) {
        if (log != null) {
            xmlNode.addElement("changelog")
            .addAttribute("created", formatter.formatTimestamp(log.getCreated()))
            .addAttribute("createdBy", log.getCreatedBy())
            .addAttribute("changed", formatter.formatTimestamp(log.getChanged()))
            .addAttribute("changedBy", log.getChangedBy());
        }
    }

    private  Element addUpload(DigitalObject upload, Element parentNode, 
            String elementName, BASE64Encoder encoder, List<Integer> uploadIDs) {
        Element xmlNode = null;
        if ((upload != null)&&(!"".equals(upload.getFullname())) &&
            upload.isDataExistent()) {
            xmlNode = parentNode.addElement(elementName);
            xmlNode
            .addAttribute("fullname", upload.getFullname())
            .addAttribute("contentType", upload.getContentType());

            Element data =xmlNode.addElement("data");
            data.addAttribute("hasData", "true");
            data.addAttribute("encoding", "base64");
            if (uploadIDs != null) {
                // write only DigitalObject.id, it will be replaced later
                data.setText(""+upload.getId());
                // remember ID of upload
                uploadIDs.add(upload.getId());
            } else {
                // directly write encoded data
                data.setText(encoder.encode(upload.getData().getData()));
            }
            if (upload.getXcdlDescription() != null) {
                addUpload(upload.getXcdlDescription(), xmlNode, "xcdlDescription", encoder, uploadIDs);
            }
            addJhoveString(upload, encoder, xmlNode);
            addFitsInfo(upload, encoder, xmlNode);
            addChangeLog(upload.getChangeLog(), xmlNode);
         }
        return xmlNode;
    }
    private void addJhoveString(DigitalObject digitalObject,
            BASE64Encoder encoder, Element xmlNode) {
        String jhoveXML = digitalObject.getJhoveXMLString();
        if ((jhoveXML != null) && (!"".equals(jhoveXML))) {
            Element jhove = xmlNode.addElement("jhoveXML");
            jhove.addAttribute("encoding", "base64");
            try {
                jhove.setText(encoder.encode(jhoveXML.getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                jhove.setText("");
                logger.error(e.getMessage(),e);
            }
        }
    }
    private void addFitsInfo(DigitalObject digitalObject,
            BASE64Encoder encoder, Element xmlNode) {
        String fitsInfo = digitalObject.getFitsXMLString();
        if ((fitsInfo != null) && (!"".equals(fitsInfo))) {
            Element fitsElement = xmlNode.addElement("fitsXML");
            fitsElement.addAttribute("encoding", "base64");
            try {
                fitsElement.setText(encoder.encode(fitsInfo.getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                fitsElement.setText("");
                logger.error(e.getMessage(),e);
            }
        }
    }
    
    private  String deriveElementname(Class c) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".")+1);
        name = name.substring(0,1).toLowerCase() + name.substring(1);
        return name;
    }

    /**
     * Adds the XML-representation of the given project to the parent <code>projectNode</code>
     *
     * @param p
     * @param projectNode
     */
    public  void addProject(Plan p, Document projectsDoc, List<Integer> uploadIDs, List<Integer> recordIDs) {
        
        Element projectNode = projectsDoc.getRootElement().addElement(new QName("plan", platoNS));
        
        // Base64 encoder for binary data
        BASE64Encoder encoder = new BASE64Encoder();

        addChangeLog(p.getChangeLog(), projectNode);

        Element properties = projectNode.addElement("properties");
        addUpload(p.getPlanProperties().getReportUpload(), properties, "report", encoder, uploadIDs);
        
        // Plan state
        properties.addElement("state")
        .addAttribute("value", Integer.toString(p.getPlanProperties().getState().getValue()));
        

        // Plan properties
        properties
        .addAttribute("author", p.getPlanProperties().getAuthor())
        .addAttribute("organization", p.getPlanProperties().getOrganization())
        .addAttribute("name", p.getPlanProperties().getName())
        .addAttribute("privateProject", Boolean.toString(p.getPlanProperties().isPrivateProject()))
        .addAttribute("reportPublic", Boolean.toString(p.getPlanProperties().isReportPublic()));
        addStringElement(properties,"description", p.getPlanProperties().getDescription());
        addStringElement(properties,"owner", p.getPlanProperties().getOwner());
        addChangeLog(p.getPlanProperties().getChangeLog(), properties);
        
        // Plan Basis
        Element basis = projectNode.addElement("basis");
        basis
           .addAttribute("identificationCode", p.getProjectBasis().getIdentificationCode());

        addStringElement(basis, "documentTypes", p.getProjectBasis().getDocumentTypes());
        addStringElement(basis, "applyingPolicies", p.getProjectBasis().getApplyingPolicies());
        addStringElement(basis, "designatedCommunity", p.getProjectBasis().getDesignatedCommunity());
        addStringElement(basis, "mandate", p.getProjectBasis().getMandate());
        addStringElement(basis, "organisationalProcedures", p.getProjectBasis().getOrganisationalProcedures());
        addStringElement(basis, "planningPurpose", p.getProjectBasis().getPlanningPurpose());
        addStringElement(basis, "planRelations", p.getProjectBasis().getPlanRelations());
        addStringElement(basis, "preservationRights", p.getProjectBasis().getPreservationRights());
        addStringElement(basis, "referenceToAgreements", p.getProjectBasis().getReferenceToAgreements());
        
        Element triggers = basis.addElement("triggers");
        if (p.getProjectBasis().getTriggers()!= null) {
            addTrigger(triggers,p.getProjectBasis().getTriggers().getNewCollection());
            addTrigger(triggers,p.getProjectBasis().getTriggers().getPeriodicReview());
            addTrigger(triggers,p.getProjectBasis().getTriggers().getChangedEnvironment());
            addTrigger(triggers,p.getProjectBasis().getTriggers().getChangedObjective());
            addTrigger(triggers,p.getProjectBasis().getTriggers().getChangedCollectionProfile());
        }    

        Element policyTree = basis.addElement("policyTree");

        addSubPolicyTree(p.getProjectBasis().getPolicyTree().getRoot(), policyTree);

        addChangeLog(p.getProjectBasis().getChangeLog(), basis);

        // Sample Records
        Element samplerecords = projectNode.addElement("sampleRecords");
        addStringElement(samplerecords, "samplesDescription", p.getSampleRecordsDefinition().getSamplesDescription());

        Element collectionProfile = samplerecords.addElement("collectionProfile");
        if (p.getSampleRecordsDefinition().getCollectionProfile() != null) {
            addStringElement(collectionProfile,"collectionID", p.getSampleRecordsDefinition().getCollectionProfile().getCollectionID());
            addStringElement(collectionProfile,"description", p.getSampleRecordsDefinition().getCollectionProfile().getDescription());
            addStringElement(collectionProfile,"expectedGrowthRate", p.getSampleRecordsDefinition().getCollectionProfile().getExpectedGrowthRate());
            addStringElement(collectionProfile,"numberOfObjects", p.getSampleRecordsDefinition().getCollectionProfile().getNumberOfObjects());
            addStringElement(collectionProfile,"typeOfObjects", p.getSampleRecordsDefinition().getCollectionProfile().getTypeOfObjects());
            addStringElement(collectionProfile,"retentionPeriod", p.getSampleRecordsDefinition().getCollectionProfile().getRetentionPeriod());

        for (SampleObject rec : p.getSampleRecordsDefinition().getRecords()) {
            Element sampleRecord = samplerecords.addElement("record")
            .addAttribute("shortName", rec.getShortName())
            .addAttribute("fullname", rec.getFullname())
            .addAttribute("contentType", rec.getContentType());

            Element data =sampleRecord.addElement("data");
            if (rec.isDataExistent()) {
                data.addAttribute("hasData", "true");
                data.addAttribute("encoding", "base64");
                if (recordIDs != null) {
                    data.setText(""+rec.getId());
                    recordIDs.add(rec.getId());
                } else {
                    data.setText(encoder.encode(rec.getData().getData()));
                }
                addUpload(rec.getXcdlDescription(), sampleRecord, "xcdlDescription", encoder, uploadIDs);
                addJhoveString(rec, encoder, sampleRecord);
                addFitsInfo(rec, encoder, sampleRecord);
            } else {
                data.addAttribute("hasData","false");
            }
            
            Element formatInfo = sampleRecord.addElement("formatInfo")
                .addAttribute("puid", rec.getFormatInfo().getPuid())
                .addAttribute("name", rec.getFormatInfo().getName())
                .addAttribute("version", rec.getFormatInfo().getVersion())
                .addAttribute("mimeType", rec.getFormatInfo().getMimeType())
                .addAttribute("defaultExtension", rec.getFormatInfo().getDefaultExtension());
            addChangeLog(rec.getFormatInfo().getChangeLog(), formatInfo);
            
            addChangeLog(rec.getChangeLog(), sampleRecord);

            addStringElement(sampleRecord,"description", rec.getDescription());
            addStringElement(sampleRecord,"originalTechnicalEnvironment",  rec.getOriginalTechnicalEnvironment());
            
        }
        addChangeLog(p.getSampleRecordsDefinition().getChangeLog(), samplerecords);

        // Requirementsdefinition
        Element rdef = projectNode.addElement("requirementsDefinition");
        addStringElement(rdef, "description", p.getRequirementsDefinition().getDescription());
        Element uploads = rdef.addElement("uploads");
        for (DigitalObject upload : p.getRequirementsDefinition().getUploads()) {
            addUpload(upload, uploads, "upload", encoder, uploadIDs);
        }
        addChangeLog(p.getRequirementsDefinition().getChangeLog(), rdef);

        // Alternatives
        Element alternatives = projectNode.addElement("alternatives");
        addStringElement(alternatives,"description", p.getAlternativesDefinition().getDescription());
        
        for (Alternative a : p.getAlternativesDefinition().getAlternatives()) {
            /*
             * Export all alternatives (also discarded ones)
             * Indices of the result-set reference only the considered alternatives!
             */
            Element alt = alternatives.addElement("alternative")
            .addAttribute("discarded", Boolean.toString(a.isDiscarded()))
            .addAttribute("name", a.getName());
            addStringElement(alt, "description", a.getDescription());
            if (a.getAction() != null) {
                Element action = alt.addElement("action");
                action
                .addAttribute("shortname", a.getAction().getShortname())
                .addAttribute("url", a.getAction().getUrl())
                .addAttribute("actionIdentifier", a.getAction().getActionIdentifier() )
                .addAttribute("info", a.getAction().getInfo())
                .addAttribute("targetFormat", a.getAction().getTargetFormat())
                .addAttribute("executable", String.valueOf(a.getAction().isExecutable()));
                addStringElement(action, "descriptor", a.getAction().getDescriptor());
                addStringElement(action, "parameterInfo", a.getAction().getParameterInfo());

                Element params = action.addElement("params");
                if (a.getAction().getParams() != null) {
                    for (Parameter param : a.getAction().getParams()) {
                        params.addElement("param")
                        .addAttribute("name", param.getName())
                        .addAttribute("value", param.getValue());
                    }
                }
                addChangeLog(a.getAction().getChangeLog(), action);
            }

            Element resourceDescr = alt.addElement("resourceDescription");
            addStringElement(resourceDescr, "necessaryResources", a.getResourceDescription().getNecessaryResources());
            addStringElement(resourceDescr, "configSettings", a.getResourceDescription().getConfigSettings());
            addStringElement(resourceDescr, "reasonForConsidering", a.getResourceDescription().getReasonForConsidering());
            addChangeLog(a.getResourceDescription().getChangeLog(), resourceDescr);

            Element experiment = alt.addElement("experiment");
            Experiment exp = a.getExperiment();
            addStringElement(experiment, "description", exp.getDescription());
            //addStringElement(experiment, "runDescription", exp.getRunDescription());
            addStringElement(experiment, "settings", exp.getSettings());
            uploads = experiment.addElement("results");
            for (SampleObject record : exp.getResults().keySet()) {
                DigitalObject up = exp.getResults().get(record);
                if (up != null) {
                    // only existing uploads are exported
                    Element upload = addUpload(up, uploads, "result", encoder, uploadIDs);
                    if (upload != null) {
                       upload.addAttribute("key", record.getShortName());
                    }
                }
            }
//            // */experiment/xcdlDescriptions/xcdlDescription
//            Element xcdls = experiment.addElement("xcdlDescriptions");
//            for (SampleObject record : exp.getResults().keySet()) {
//                DigitalObject result = exp.getResults().get(record);
//                if (result != null) {
//                    XcdlDescription x = result.getXcdlDescription();
//                    if (x != null) {
//                        // only existing xcdls are exported
//                        Element upload = addUpload(x, xcdls, "xcdlDescription", encoder, uploadIDs);
//                        if (upload != null) {
//                           upload.addAttribute("key", record.getShortName());
//                        }
//                    }
//                }
//            }
            // export detailed experiment info's
            Element detailedInfos = experiment.addElement("detailedInfos");
            for (SampleObject record : exp.getDetailedInfo().keySet()) {
                DetailedExperimentInfo dinfo = exp.getDetailedInfo().get(record);
                Element detailedInfo = detailedInfos.addElement("detailedInfo")
                .addAttribute("key", record.getShortName())
                .addAttribute("successful", ""+dinfo.getSuccessful());
                addStringElement(detailedInfo, "programOutput", dinfo.getProgramOutput());
                addStringElement(detailedInfo, "cpr", dinfo.getCpr());

                Element measurements = detailedInfo.addElement("measurements");
                for (Measurement m : dinfo.getMeasurements().values()) {
                    Element measurement = measurements.addElement("measurement");
                    // measurement.value:
                    String typename = deriveElementname(m.getValue().getClass());
                    
                    Element valueElem = measurement.addElement(typename);
                    //.addAttribute("value", m.getValue().toString());
                    addStringElement(valueElem, "value", m.getValue().toString());
                    addChangeLog(m.getValue().getChangeLog(),valueElem);
                    
                    // measurement.property:
                    Element property = measurement.addElement("property")
                    .addAttribute("name", m.getProperty().getName());
                    addScale(m.getProperty().getScale(), property);
                 }
            }
            addChangeLog(a.getExperiment().getChangeLog(), experiment);


            addChangeLog(a.getChangeLog(), alt);
        }
        addChangeLog(p.getAlternativesDefinition().getChangeLog(), alternatives);

        // go-nogo - is created in the go-nogo step and need not exist
        if (p.getDecision() != null) {
            Element decision = projectNode.addElement("decision");
            addStringElement(decision, "reason", p.getDecision().getReason());
            addStringElement(decision, "actionNeeded", p.getDecision().getActionNeeded());
            decision.addElement("goDecision").addAttribute("value", p.getDecision().getDecision().name());
            addChangeLog(p.getDecision().getChangeLog(), decision);
        }
        // Evaluation
        Element evaluation = projectNode.addElement("evaluation");
        addStringElement(evaluation, "comment", p.getEvaluation().getComment());
        addChangeLog(p.getEvaluation().getChangeLog(), evaluation);



        // importance weighting
        Element importanceWeighting = projectNode.addElement("importanceWeighting");
        addStringElement(importanceWeighting, "comment", p.getImportanceWeighting().getComment());
        addChangeLog(p.getImportanceWeighting().getChangeLog(), importanceWeighting);

        // Recommendation
        Element recommendation = projectNode.addElement("recommendation");
        if (p.getRecommendation().getAlternative() != null) {
            recommendation
               .addAttribute("alternativeName", p.getRecommendation().getAlternative().getName());
        }
        addStringElement(recommendation, "reasoning", p.getRecommendation().getReasoning());
        addStringElement(recommendation, "effects", p.getRecommendation().getEffects());
        addChangeLog(p.getRecommendation().getChangeLog(), recommendation);

        // transformation
        Element trafo = projectNode.addElement("transformation");
        addStringElement(trafo, "comment", p.getTransformation().getComment());
        addChangeLog(p.getTransformation().getChangeLog(),
                trafo);

        // Objectivetree (including weights, evaluation values and transformers)
        Element tree = projectNode.addElement("tree");
        tree.addAttribute("weightsInitialized",""+p.getTree().isWeightsInitialized());
        if (p.getTree().getRoot() != null)
           addSubTree(p.getTree().getRoot(), tree);
        }
        
        Element executablePlan = projectNode.addElement("executablePlan");

        try {
            if (p.getExecutablePlanDefinition().getExecutablePlan() != null) {
                Document execPlan = DocumentHelper.parseText(p.getExecutablePlanDefinition().getExecutablePlan());
                Element execPlanRoot = execPlan.getRootElement();
                if (execPlanRoot.hasContent()){
                    Element planWorkflow = executablePlan.addElement("planWorkflow");
                    planWorkflow.add(execPlanRoot);                    
                }
            }
            
            if (p.getExecutablePlanDefinition().getEprintsExecutablePlan() != null) {
                Document execPlan = DocumentHelper.parseText(p.getExecutablePlanDefinition().getEprintsExecutablePlan());
                Element execPlanRoot = execPlan.getRootElement();
                if (execPlanRoot.hasContent()) {
                    //Element planWorkflow = executablePlan.addElement("eprintsPlan");
                    executablePlan.add(execPlanRoot);                    
                }
            }
            
        } catch (DocumentException e) {
            // if the stored exec. plan is invalid for some reason, we leave the plan out.
            // TODO: HK this should no happen as we write the xml ourselves, but still, 
            // we need a mechanism here to prevent the export if the xml is invalid.
            logger.error(e.getMessage(),e);
        }
        
        
        // TODO HK how does this here relate to the upper try-catch block and the exception??
        // Smells like a hack!
        ExecutablePlanDefinition plan = p.getExecutablePlanDefinition(); 
        addStringElement(executablePlan, "objectPath", plan.getObjectPath());
        addStringElement(executablePlan, "toolParameters", plan.getToolParameters());
        addStringElement(executablePlan, "triggersConditions", plan.getTriggersConditions());
        addStringElement(executablePlan, "validateQA", plan.getValidateQA());
        addChangeLog(plan.getChangeLog(), executablePlan);
        
        Element planDef = projectNode.addElement("planDefinition");
        PlanDefinition pdef = p.getPlanDefinition();
        planDef.addAttribute("currency", pdef.getCurrency());
        addStringElement(planDef, "costsIG", pdef.getCostsIG());
        addStringElement(planDef, "costsPA", pdef.getCostsPA());
        addStringElement(planDef, "costsPE", pdef.getCostsPE());
        addStringElement(planDef, "costsQA", pdef.getCostsQA());
        addStringElement(planDef, "costsREI", pdef.getCostsREI());
        addStringElement(planDef, "costsRemarks", pdef.getCostsRemarks());
        addStringElement(planDef, "costsRM", pdef.getCostsRM());
        addStringElement(planDef, "costsTCO", pdef.getCostsTCO());
        
        addStringElement(planDef, "responsibleExecution", pdef.getResponsibleExecution());
        addStringElement(planDef, "responsibleMonitoring", pdef.getResponsibleMonitoring());

        triggers = planDef.addElement("triggers");
        if (pdef.getTriggers()!= null) {
            addTrigger(triggers,pdef.getTriggers().getNewCollection());
            addTrigger(triggers,pdef.getTriggers().getPeriodicReview());
            addTrigger(triggers,pdef.getTriggers().getChangedEnvironment());
            addTrigger(triggers,pdef.getTriggers().getChangedObjective());
            addTrigger(triggers,pdef.getTriggers().getChangedCollectionProfile());
        }    

        addChangeLog(pdef.getChangeLog(), planDef);
        
    }
    
    private  void addTrigger(Element triggers, Trigger t) {
        if (t == null) {
            return;
        }
        Element trigger = triggers.addElement("trigger");
        trigger.addAttribute("type",t.getType().name());
        trigger.addAttribute("active", Boolean.toString(t.isActive()));
        trigger.addAttribute("description", t.getDescription());
    }

    /**
     * Long strings are stored as XML-elements, not as attributes.
     * It is not possible to add an element with value <code>null</code>,
     * therefore this has to be handled here:
     * A new element is only added if there is a value at all.
     *
     * @param parent
     * @param name
     * @param value
     */
    private  Element addStringElement(Element parent, String name, String value){
        Element e = null;
        // &&(!"".equals(value)
        if (value != null) {
           e = parent.addElement(name);
           if (!"".equals(value)) {
               e.addText(value);
           }
        }
        return e;
    }
	
	public  String exportTreeToFreemind(Plan plan) {
        return exportTreeToFreemind(plan.getTree().getRoot());
    }
    
    public  String exportTreeToFreemind(TreeNode treeRoot) {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("map");
        Namespace xsi = new Namespace("xsi",  "http://www.w3.org/2001/XMLSchema-instance");

        root.add(xsi);
        root.addAttribute("version","0.8.1");

        root.addComment("To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net");
        addSubTreeFreemind(root, treeRoot);
        
        String xml =  doc.asXML();
        //PlatoLogger.getLogger(ProjectExporter.class).debug(arg0)
        return xml;
    }

    private void addSubTreeFreemind(Element xmlElement, TreeNode node) {
        Element element = xmlElement.addElement("node");
        addFreemindAttributes(node, element);

        if (node.isLeaf()) {
            //add scale
            Leaf leaf = (Leaf) node;
            Scale scale = leaf.getScale();
            if (scale != null) {
                Element scaleElement = element.addElement("node");
                String restriction = "?";
                
                //restrictions: restrictedscale, ordinals -freestring
                
                if (scale instanceof FreeStringScale) {
                    restriction = "free text";
                } else if ((scale.getType() == ScaleType.ordinal || scale.getType() == ScaleType.restricted) 
                        && !"".equals(((RestrictedScale)scale).getRestriction())) 
                {
                    restriction = ((RestrictedScale)scale).getRestriction();
                } else {
                    restriction = scale.getUnit();
                }
                scaleElement.addAttribute("TEXT",restriction);
            }
            
        } else {
            // add children
            for (TreeNode child : node.getChildren()) {
                addSubTreeFreemind(element, child);
            }
        }
    }

    private void addFreemindAttributes(TreeNode node, Element element) {
        element.addAttribute("TEXT", node.getName());
      //TODO export weights? units? single? >> future.

        String mInfoUri = null;
        
        if (node.isLeaf()) {
        	Leaf leaf = (Leaf) node;
        	if (leaf.isMapped()) {
        		mInfoUri = leaf.getCriterion().getUri();
        	}       	
//        	((Leaf)node).getCriterion().getUri()
//            mInfoUri = ((Leaf)node).getMeasurementInfo().getUri();
        }
        // add DESCRIPTION if existent
        if (((mInfoUri != null) && (!"".equals(mInfoUri))) ||
            (node.getDescription() != null && !"".equals(node.getDescription()))) {
            Element hook = element.addElement("hook");
            hook.addAttribute("NAME", "accessories/plugins/NodeNote.properties");
            Element description = hook.addElement("text");
            
            String descr = "";
            // and measurement info
             
            if ((mInfoUri != null) && (! "".equals(mInfoUri))){
                descr = "measuredProperty=" + mInfoUri + "\n";
            }
            if (node.getDescription() != null) {
                descr = descr + node.getDescription();
            }
            description.setText(descr);
        }
    }
    
    private void addScale(Scale s, Element parent) {
        if (s != null) {
            String typename = deriveElementname(s.getClass());
            
            Element scale = parent.addElement(typename);
            //  && (!"".equals(s.getUnit()
            if (s.getUnit() != null) {
                scale.addAttribute("unit", s.getUnit());
            }
            //scale.addAttribute("displayName", s.getDisplayName());
                
            if (s instanceof RestrictedScale) {
                scale.addAttribute("restriction", ((RestrictedScale)s).getRestriction());
            }
            addChangeLog(s.getChangeLog(), scale);
        }
    }
}