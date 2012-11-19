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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.ExecutablePlanDefinition;
import eu.scape_project.planning.model.Experiment;
import eu.scape_project.planning.model.Parameter;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanDefinition;
import eu.scape_project.planning.model.Policy;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.TargetValueObject;
import eu.scape_project.planning.model.Trigger;
import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.RestrictedScale;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.scales.ScaleType;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.TemplateTree;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.model.util.FloatFormatter;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.xml.plan.TimestampFormatter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

/**
 * Static methods providing means to export projects to XML using dom4j.
 * 
 * @author Michael Kraxner, Markus Hamm
 */
public class ProjectExporter implements Serializable {
    private static final long serialVersionUID = 7538933914251415135L;

    /**
     * Encoding used for writing data.
     */
    private static final String ENCODING = "UTF-8";

    private Logger log = LoggerFactory.getLogger(ProjectExporter.class);;

    private TimestampFormatter formatter = new TimestampFormatter();
    private FloatFormatter floatFormatter = new FloatFormatter();

    public static OutputFormat prettyFormat = new OutputFormat(" ", true, ENCODING); // OutputFormat.createPrettyPrint();
    public static OutputFormat compactFormat = new OutputFormat(null, false, ENCODING); // OutputFormat.createPrettyPrint();

    private static final Namespace XSI_NAMESPACE = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    private static final Namespace PLATO_NAMESPACE = new Namespace("", PlanXMLConstants.PLATO_NS);

    public ProjectExporter() {
    }

    /**
     * Returns the xml-representation of the given project as a String. NOTE: It
     * writes all data - including encoded binary data - directly to the DOM
     * tree this may result in performance problems for large amounts of data.
     * 
     * @param p
     *            the plan to export
     * @return the XML representation of the plan
     * @throws PlanningException
     *             if an error occured during export
     */
    public String exportToString(Plan p) throws PlanningException {
        return exportToXml(p).asXML();
    }

    /**
     * Takes the given project and turns it into a dom4j-xml-representation.
     * NOTE: It writes all data - including encoded binary data - directly to
     * the DOM tree this may result in performance problems for large amounts of
     * data.
     * 
     * @param p
     *            the plan to export
     * @return dom4j-document representing the given project
     * @throws PlanningException
     *             if an error occured during export
     */
    public Document exportToXml(Plan p) throws PlanningException {
        Document doc = createProjectDoc();
        addProject(p, doc, false);
        return doc;
    }

    /**
     * Creates a dom4j document template to add plans.
     * 
     * @return the dom4j document
     */
    public Document createProjectDoc() {
        Document doc = DocumentHelper.createDocument();

        Element root = doc.addElement("plans");

        root.add(XSI_NAMESPACE);
        root.add(PLATO_NAMESPACE);
        root.addAttribute(XSI_NAMESPACE.getPrefix() + ":schemaLocation", PlanXMLConstants.PLATO_NS + " "
            + PlanXMLConstants.PLATO_SCHEMA);
        root.add(new Namespace("fits", "http://hul.harvard.edu/ois/xml/ns/fits/fits_output"));

        // set version of corresponding schema
        root.addAttribute("version", "4.0.0");

        return doc;
    }

    /**
     * Creates a template document.
     * 
     * @return the dom4j-document
     */
    public Document createTemplateDoc() {
        Document doc = DocumentHelper.createDocument();

        Element root = doc.addElement("templates");

        root.add(XSI_NAMESPACE);

        return doc;
    }

    /**
     * This method creates a template xml from the objective tree contained in
     * Plan p. The template xml is of the form
     * 
     * <templates> <template name="Public Fragments"> <node name="Template 1"
     * weight="0.0" single="false" lock="false"> <node
     * name="Interactive multimedia presentations" weight="0.0" single="false"
     * lock="false"> ... </template> </templates>
     * 
     * For creating the tree we use
     * {@link ProjectExporter#addSubTree(TreeNode, Element)} and remove
     * afterwards the evaluation part which we don't want to have in the
     * template.
     * 
     * @param p
     *            preservation plan
     * @param templateLibrary
     *            name of the template library (e.g. 'Public Templates')
     * @param name
     *            name of the template
     * @param description
     *            description of the template
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
        Element treeRootNode = (Element) template.selectSingleNode("//templates/template/node");

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
        @SuppressWarnings("unchecked")
        List<Element> nodes = templateDoc.selectNodes("//leaf/evaluation");

        for (Element n : nodes) {
            n.getParent().remove(n);
        }

        return templateDoc.asXML();
    }

    /**
     * Takes the given projects and turns them into a dom4j-xml-representation.
     * NOTE: It writes all data - including encoded binary data - directly to
     * the DOM tree this may result in performance problems for large amounts of
     * data.
     * 
     * @return dom4j-document representing the given project public static
     *         Document exportToXml(List<Plan> projects) { Document doc =
     *         createProjectDoc();
     * 
     *         for (Plan project : projects) {
     *         ProjectExporter.addProject(project, doc, null, null); } return
     *         doc; }
     */

    /**
     * Writes the xml-representation of the given projects to the given target
     * file. NOTE: It writes all data - including encoded binary data - directly
     * to the DOM tree this may result in performance problems for large amounts
     * of data.
     * 
     * @param p
     *            the plan to export
     * @param target
     *            the file to write the plan
     * @throws IOException
     *             if an error occured during write
     * @throws PlanningException
     *             if an error occured during export
     */
    public void exportToFile(Plan p, File target) throws IOException, PlanningException {
        XMLWriter writer = new XMLWriter(new FileWriter(target), ProjectExporter.prettyFormat);
        try {
            writer.write(exportToXml(p));
        } finally {
            writer.close();
        }
    }

    /**
     * Writes the xml-representation of the given project into a temporary file
     * and returns the java-representation of this file. NOTE: It writes all
     * data - including encoded binary data - directly to the DOM tree this may
     * result in performance problems for large amounts of data.
     * 
     * @param p
     *            the plan to export
     * @throws IOException
     *             if an error occured during export
     * @throws PlanningException
     *             if an error occured during export
     * 
     */
    public File exportToFile(Plan p) throws IOException, PlanningException {
        File temp = File.createTempFile("plato-plan-export-", ".xml");
        exportToFile(p, temp);
        return temp;
    }

    /*
     * public static File exportTemplatesToFile(List<TemplateTree> trees) throws
     * IOException { File temp = File.createTempFile("plato-templates-export-",
     * ".xml"); XMLWriter writer = new XMLWriter(new FileWriter(temp),
     * ProjectExporter.prettyFormat); //writer.setMaximumAllowedCharacter(127);
     * writer.write(ProjectExporter.exportTemplates(trees)); writer.close();
     * return temp; }
     */

    public Document exportTemplates(List<TemplateTree> trees) {
        Document doc = DocumentHelper.createDocument();

        Element root = doc.addElement("templates");

        for (TemplateTree template : trees) {
            addTemplateTree(template, root);
        }
        return doc;
    }

    /**
     * Adds the XML representation of the complete template tree to the parent
     * node <code>root</code>.
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

    private void addSubPolicyTree(PolicyNode data, Element xmlRoot) {

        if (data == null) {
            return;
        }

        if (data.isPolicy()) {
            Element policy = xmlRoot.addElement("policy");

            Policy p = (Policy) data;

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
     * Adds the XML-representation of the treenode <code>data</code> to the
     * parent node <code>xmlRoot</code>. (also called recursivly for its
     * children)
     * 
     * @param data
     * @param xmlRoot
     */
    private void addSubTree(TreeNode data, Element xmlRoot) {
        if (data.isLeaf()) {
            Element leaf = xmlRoot.addElement("leaf");
            addNodeAttributes(data, leaf);
            Leaf l = (Leaf) data;
            leaf.addElement("aggregationMode").addAttribute("value", l.getAggregationMode().name());

            String typename = null;
            /*
             * Scale: A special element is created, depending on the type of the
             * scale
             */
            Scale s = l.getScale();

            if (s != null) {
                typename = deriveElementname(s.getClass());

                addScale(s, leaf);

                // Transformer
                if (l.getTransformer() != null) {
                    Element transformer = leaf.addElement(deriveElementname(l.getTransformer().getClass()));

                    if (l.getTransformer() instanceof OrdinalTransformer) {

                        Map<String, TargetValueObject> mapping = ((OrdinalTransformer) l.getTransformer()).getMapping();
                        Element mappings = transformer.addElement("mappings");
                        for (String ordinal : mapping.keySet()) {
                            mappings
                                .addElement("mapping")
                                .addAttribute("ordinal", ordinal)
                                .addAttribute("target",
                                    floatFormatter.formatFloatPrecisly(mapping.get(ordinal).getValue()));
                        }
                    }
                    if (l.getTransformer() instanceof NumericTransformer) {
                        NumericTransformer nt = (NumericTransformer) l.getTransformer();
                        transformer.addElement("mode").addAttribute("value", nt.getMode().name());
                        Element thresholds = transformer.addElement("thresholds");
                        thresholds.addElement("threshold1").setText(
                            floatFormatter.formatFloatPrecisly(nt.getThreshold1()));
                        thresholds.addElement("threshold2").setText(
                            floatFormatter.formatFloatPrecisly(nt.getThreshold2()));
                        thresholds.addElement("threshold3").setText(
                            floatFormatter.formatFloatPrecisly(nt.getThreshold3()));
                        thresholds.addElement("threshold4").setText(
                            floatFormatter.formatFloatPrecisly(nt.getThreshold4()));
                        thresholds.addElement("threshold5").setText(
                            floatFormatter.formatFloatPrecisly(nt.getThreshold5()));

                    }
                    addChangeLog(l.getTransformer().getChangeLog(), transformer);
                }

                if (l.isMapped()) {
                    addMeasure(l.getMeasure(), leaf);
                }

                Element eval = leaf.addElement("evaluation");
                typename = typename.substring(0, typename.lastIndexOf("Scale"));
                /*
                 * keep in mind: there are only values of the considered
                 * alternatives in the map
                 */
                for (String a : l.getValueMap().keySet()) {
                    Element alt = eval.addElement("alternative");
                    alt.addAttribute("key", a);
                    addStringElement(alt, "comment", l.getValueMap().get(a).getComment());
                    for (Value v : l.getValueMap().get(a).getList()) {
                        /*
                         * A special element is created, depending on the type
                         * of the scale
                         */
                        Element valElement = alt.addElement(typename + "Result");
                        String valueStr = v.toString();
                        if (!"".equals(valueStr)) {
                            addStringElement(valElement, "value", v.toString());
                        }
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
     * creates a new element with all information of the given measure and adds
     * it to the parent node.
     * 
     * @param info
     * @param parent
     */
    private void addMeasure(Measure measure, Element parent) {
        Element measureEl = parent.addElement("measure");

        measureEl.addAttribute("ID", measure.getUri());
        addStringElement(measureEl, "name", measure.getName());
        addStringElement(measureEl, "description", measure.getDescription());

        Attribute attribute = (Attribute) measure.getAttribute();
        if (attribute != null) {
            Element attributeEl = measureEl.addElement("attribute");
            attributeEl.addAttribute("ID", attribute.getUri());
            addStringElement(attributeEl, "name", attribute.getName());
            addStringElement(attributeEl, "description", attribute.getDescription());

            CriterionCategory category = attribute.getCategory();
            Element categoryEl = attributeEl.addElement("category");
            categoryEl.addAttribute("ID", category.getUri());
            categoryEl.addAttribute("scope", category.getScope().toString());
            addStringElement(categoryEl, "name", category.getName());
        }
        addScale(measure.getScale(), measureEl);

        // addChangeLog(measure.getChangeLog(), measureEl);
    }

    /**
     * Adds the given node's properties to the xmlNode.
     */
    private void addNodeAttributes(TreeNode data, Element xmlNode) {
        xmlNode.addAttribute("name", data.getName()).addAttribute("weight",
            floatFormatter.formatFloatPrecisly(data.getWeight()));
        if (data instanceof Leaf) {
            xmlNode.addAttribute("single", Boolean.toString(data.isSingle()));
        }
        xmlNode.addAttribute("lock", Boolean.toString(data.isLock()));

        addStringElement(xmlNode, "description", data.getDescription());

        addChangeLog(data.getChangeLog(), xmlNode);
    }

    /**
     * Adds a changelog element to the provided parent if changelog is defined.
     * 
     * @param log
     *            the changelog to add
     * @param parent
     *            the parent element
     * @return the newly created element or null
     */
    private Element addChangeLog(ChangeLog log, Element parent) {
        Element xmlNode = null;
        if (log != null) {
            xmlNode = parent.addElement("changelog")
                .addAttribute("created", formatter.formatTimestamp(log.getCreated()))
                .addAttribute("createdBy", log.getCreatedBy())
                .addAttribute("changed", formatter.formatTimestamp(log.getChanged()))
                .addAttribute("changedBy", log.getChangedBy());
        }
        return xmlNode;
    }

    /**
     * Adds the digital object to the parentNode. If the object is null or has
     * no data, it is not added.
     * 
     * @param upload
     *            the DigitalObject to add
     * @param parent
     *            the parent element of the element to create
     * @param elementName
     *            the name of the element to create
     * @param encoder
     *            encoder to use for writing data
     * @param addDigitalObjectData
     *            true if the data should be written, false otherwise
     * @return the newly created element or null if none was created
     * @throws PlanningException
     *             if an error occured during export
     */
    private Element addUpload(DigitalObject upload, Element parent, String elementName, BASE64Encoder encoder,
        boolean addDigitalObjectData) throws PlanningException {
        Element xmlNode = null;
        if (upload != null && upload.isDataExistent()) {
            xmlNode = addEncodedDigitalObject(upload, parent, elementName, encoder, addDigitalObjectData);
        }
        return xmlNode;
    }

    /**
     * Adds the digital object to the parentNode. If the object is null it is
     * not added.
     * 
     * @param upload
     *            the DigitalObject to add
     * @param parent
     *            the parent element of the element to create
     * @param elementName
     *            the name of the element to create
     * @param encoder
     *            encoder to use for writing data
     * @param addDigitalObjectData
     *            true if the data should be written, false otherwise
     * @return the newly created element or null if none was created
     * @throws PlanningException
     *             if an error occured during export
     */
    private Element addEncodedDigitalObject(DigitalObject upload, Element parent, String elementName,
        BASE64Encoder encoder, boolean addDigitalObjectData) throws PlanningException {

        Element xmlNode = null;
        if (upload != null) {
            xmlNode = parent.addElement(elementName);
            xmlNode.addAttribute("fullname", upload.getFullname()).addAttribute("contentType", upload.getContentType());

            Element data = xmlNode.addElement("data");
            if (upload.isDataExistent()) {
                data.addAttribute("hasData", "true");
                data.addAttribute("encoding", "base64");
                if (!addDigitalObjectData) {
                    // Add only DigitalObject ID, it can be replaced later
                    data.setText(String.valueOf(upload.getId()));
                } else {
                    // Add encoded data
                    data.setText(encoder.encode(upload.getData().getData()));
                }
            } else {
                data.addAttribute("hasData", "false");
            }

            addUpload(upload.getXcdlDescription(), xmlNode, "xcdlDescription", encoder, addDigitalObjectData);
            addJhoveInfo(upload, encoder, xmlNode);
            addFitsInfo(upload, encoder, xmlNode);
            Element formatInfo = xmlNode.addElement("formatInfo")
                .addAttribute("puid", upload.getFormatInfo().getPuid())
                .addAttribute("name", upload.getFormatInfo().getName())
                .addAttribute("version", upload.getFormatInfo().getVersion())
                .addAttribute("mimeType", upload.getFormatInfo().getMimeType())
                .addAttribute("defaultExtension", upload.getFormatInfo().getDefaultExtension());
            addChangeLog(upload.getFormatInfo().getChangeLog(), formatInfo);
            addChangeLog(upload.getChangeLog(), xmlNode);
        }
        return xmlNode;
    }

    /**
     * Adds the Jhove information of the digital object to the provided element
     * if it has one.
     * 
     * @param digitalObject
     *            the digital object
     * @param encoder
     *            encoder to use for writing data
     * @param parent
     *            the parent element of the element to create
     * @return the newly created element or null if none was created
     * @throws PlanningException
     *             if an error occured during export
     */
    private Element addJhoveInfo(DigitalObject digitalObject, BASE64Encoder encoder, Element parent)
        throws PlanningException {
        Element jhoveElement = null;
        String jhoveXML = digitalObject.getJhoveXMLString();
        if ((jhoveXML != null) && (!"".equals(jhoveXML))) {
            jhoveElement = parent.addElement("jhoveXML");
            jhoveElement.addAttribute("encoding", "base64");
            try {
                jhoveElement.setText(encoder.encode(jhoveXML.getBytes(ENCODING)));
            } catch (UnsupportedEncodingException e) {
                log.error("Error writing JHOVE info {}.", e.getMessage());
                throw new PlanningException("Error writing JHOVE info.", e);
            }
        }
        return jhoveElement;
    }

    /**
     * Adds the fits information of the digital object to the provided element
     * if it has one.
     * 
     * @param digitalObject
     *            the digital object
     * @param encoder
     *            encoder to use for writing data
     * @param parent
     *            the parent element of the element to create
     * @return the newly created element or null if none was created
     * @throws PlanningException
     *             if an error occured during export
     */
    private Element addFitsInfo(DigitalObject digitalObject, BASE64Encoder encoder, Element parent)
        throws PlanningException {
        Element fitsElement = null;
        String fitsInfo = digitalObject.getFitsXMLString();
        if ((fitsInfo != null) && (!"".equals(fitsInfo))) {
            fitsElement = parent.addElement("fitsXML");
            fitsElement.addAttribute("encoding", "base64");
            try {
                fitsElement.setText(encoder.encode(fitsInfo.getBytes(ENCODING)));
            } catch (UnsupportedEncodingException e) {
                log.error("Error writing fits info {}.", e.getMessage());
                throw new PlanningException("Error writing fits info.", e);
            }
        }
        return fitsElement;
    }

    /**
     * Creates an element name from the provided class.
     * 
     * @param c
     *            the class to use
     * @return an element name
     */
    private String deriveElementname(Class<?> c) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return name;
    }

    /**
     * Adds the XML-representation of the given project to the parent
     * <code>projectNode</code>.
     * 
     * @param p
     *            the plan to add
     * @param projectsDoc
     *            the document where the plan should be added
     * @param addDigitalObjectData
     *            whether the digital object data should be added to the XML
     * @throws PlanningException
     *             if an error occured during export
     */
    public void addProject(Plan p, Document projectsDoc, boolean addDigitalObjectData) throws PlanningException {

        // Base64 encoder for binary data
        BASE64Encoder encoder = new BASE64Encoder();

        Element projectNode = projectsDoc.getRootElement().addElement(new QName("plan", PLATO_NAMESPACE));

        addChangeLog(p.getChangeLog(), projectNode);

        Element properties = projectNode.addElement("properties");
        addUpload(p.getPlanProperties().getReportUpload(), properties, "report", encoder, addDigitalObjectData);

        // Plan state
        properties.addElement("state").addAttribute("value",
            Integer.toString(p.getPlanProperties().getState().getValue()));

        // Plan properties
        properties.addAttribute("author", p.getPlanProperties().getAuthor())
            .addAttribute("organization", p.getPlanProperties().getOrganization())
            .addAttribute("name", p.getPlanProperties().getName())
            .addAttribute("privateProject", Boolean.toString(p.getPlanProperties().isPrivateProject()))
            .addAttribute("reportPublic", Boolean.toString(p.getPlanProperties().isReportPublic()))
            .addAttribute("planType", p.getPlanProperties().getPlanType().toString());
        addStringElement(properties, "description", p.getPlanProperties().getDescription());
        addStringElement(properties, "owner", p.getPlanProperties().getOwner());
        addChangeLog(p.getPlanProperties().getChangeLog(), properties);

        // Plan Basis
        Element basis = projectNode.addElement("basis");
        basis.addAttribute("identificationCode", p.getProjectBasis().getIdentificationCode());

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
        if (p.getProjectBasis().getTriggers() != null) {
            addTrigger(p.getProjectBasis().getTriggers().getNewCollection(), triggers);
            addTrigger(p.getProjectBasis().getTriggers().getPeriodicReview(), triggers);
            addTrigger(p.getProjectBasis().getTriggers().getChangedEnvironment(), triggers);
            addTrigger(p.getProjectBasis().getTriggers().getChangedObjective(), triggers);
            addTrigger(p.getProjectBasis().getTriggers().getChangedCollectionProfile(), triggers);
        }

        Element policyTree = basis.addElement("policyTree");

        addSubPolicyTree(p.getProjectBasis().getPolicyTree().getRoot(), policyTree);

        addChangeLog(p.getProjectBasis().getChangeLog(), basis);

        // Sample Records
        Element samplerecords = projectNode.addElement("sampleRecords");
        addStringElement(samplerecords, "samplesDescription", p.getSampleRecordsDefinition().getSamplesDescription());

        Element collectionProfile = samplerecords.addElement("collectionProfile");
        if (p.getSampleRecordsDefinition().getCollectionProfile() != null) {
            addStringElement(collectionProfile, "collectionID", p.getSampleRecordsDefinition().getCollectionProfile()
                .getCollectionID());
            addStringElement(collectionProfile, "description", p.getSampleRecordsDefinition().getCollectionProfile()
                .getDescription());
            addStringElement(collectionProfile, "expectedGrowthRate", p.getSampleRecordsDefinition()
                .getCollectionProfile().getExpectedGrowthRate());
            addStringElement(collectionProfile, "numberOfObjects", p.getSampleRecordsDefinition()
                .getCollectionProfile().getNumberOfObjects());
            addStringElement(collectionProfile, "typeOfObjects", p.getSampleRecordsDefinition().getCollectionProfile()
                .getTypeOfObjects());
            addStringElement(collectionProfile, "retentionPeriod", p.getSampleRecordsDefinition()
                .getCollectionProfile().getRetentionPeriod());
            addUpload(p.getSampleRecordsDefinition().getCollectionProfile().getProfile(), collectionProfile, "profile",
                encoder, addDigitalObjectData);
        }

        for (SampleObject rec : p.getSampleRecordsDefinition().getRecords()) {
            Element sampleRecord = addEncodedDigitalObject(rec, samplerecords, "record", encoder, addDigitalObjectData);

            if (sampleRecord != null) {
                sampleRecord.addAttribute("shortName", rec.getShortName());
                addStringElement(sampleRecord, "description", rec.getDescription());
                addStringElement(sampleRecord, "originalTechnicalEnvironment", rec.getOriginalTechnicalEnvironment());
            }
        }
        addChangeLog(p.getSampleRecordsDefinition().getChangeLog(), samplerecords);

        // Requirementsdefinition
        Element rdef = projectNode.addElement("requirementsDefinition");
        addStringElement(rdef, "description", p.getRequirementsDefinition().getDescription());
        Element uploads = rdef.addElement("uploads");
        for (DigitalObject upload : p.getRequirementsDefinition().getUploads()) {
            addUpload(upload, uploads, "upload", encoder, addDigitalObjectData);
        }
        addChangeLog(p.getRequirementsDefinition().getChangeLog(), rdef);

        // Alternatives
        Element alternatives = projectNode.addElement("alternatives");
        addStringElement(alternatives, "description", p.getAlternativesDefinition().getDescription());

        for (Alternative a : p.getAlternativesDefinition().getAlternatives()) {
            /*
             * Export all alternatives (also discarded ones) Indices of the
             * result-set reference only the considered alternatives!
             */
            Element alt = alternatives.addElement("alternative")
                .addAttribute("discarded", Boolean.toString(a.isDiscarded())).addAttribute("name", a.getName());
            addStringElement(alt, "description", a.getDescription());
            if (a.getAction() != null) {
                Element action = alt.addElement("action");
                action.addAttribute("shortname", a.getAction().getShortname())
                    .addAttribute("url", a.getAction().getUrl())
                    .addAttribute("actionIdentifier", a.getAction().getActionIdentifier())
                    .addAttribute("info", a.getAction().getInfo())
                    .addAttribute("targetFormat", a.getAction().getTargetFormat())
                    .addAttribute("executable", String.valueOf(a.getAction().isExecutable()));
                addStringElement(action, "descriptor", a.getAction().getDescriptor());
                addStringElement(action, "parameterInfo", a.getAction().getParameterInfo());

                Element params = action.addElement("params");
                if (a.getAction().getParams() != null) {
                    for (Parameter param : a.getAction().getParams()) {
                        params.addElement("param").addAttribute("name", param.getName())
                            .addAttribute("value", param.getValue());
                    }
                }
                addChangeLog(a.getAction().getChangeLog(), action);
            }

            Element resourceDescr = alt.addElement("resourceDescription");
            addStringElement(resourceDescr, "necessaryResources", a.getResourceDescription().getNecessaryResources());
            addStringElement(resourceDescr, "configSettings", a.getResourceDescription().getConfigSettings());
            addStringElement(resourceDescr, "reasonForConsidering", a.getResourceDescription()
                .getReasonForConsidering());
            addChangeLog(a.getResourceDescription().getChangeLog(), resourceDescr);

            Element experiment = alt.addElement("experiment");
            Experiment exp = a.getExperiment();
            addStringElement(experiment, "description", exp.getDescription());
            // addStringElement(experiment, "runDescription",
            // exp.getRunDescription());
            addStringElement(experiment, "settings", exp.getSettings());
            Element results = experiment.addElement("results");
            for (Entry<SampleObject, DigitalObject> entry : exp.getResults().entrySet()) {
                Element result = addUpload(entry.getValue(), results, "result", encoder, addDigitalObjectData);
                if (result != null) {
                    result.addAttribute("key", entry.getKey().getShortName());
                }
            }

            // // */experiment/xcdlDescriptions/xcdlDescription
            // Element xcdls = experiment.addElement("xcdlDescriptions");
            // for (SampleObject record : exp.getResults().keySet()) {
            // DigitalObject result = exp.getResults().get(record);
            // if (result != null) {
            // XcdlDescription x = result.getXcdlDescription();
            // if (x != null) {
            // // only existing xcdls are exported
            // Element upload = addUpload(x, xcdls, "xcdlDescription",
            // encoder, uploadIDs);
            // if (upload != null) {
            // upload.addAttribute("key", record.getShortName());
            // }
            // }
            // }
            // }

            // export detailed experiment info's
            Element detailedInfos = experiment.addElement("detailedInfos");
            for (SampleObject record : exp.getDetailedInfo().keySet()) {
                DetailedExperimentInfo dinfo = exp.getDetailedInfo().get(record);
                Element detailedInfo = detailedInfos.addElement("detailedInfo")
                    .addAttribute("key", record.getShortName()).addAttribute("successful", "" + dinfo.getSuccessful());
                addStringElement(detailedInfo, "programOutput", dinfo.getProgramOutput());
                addStringElement(detailedInfo, "cpr", dinfo.getCpr());

                Element measurements = detailedInfo.addElement("measurements");
                for (Measurement m : dinfo.getMeasurements().values()) {
                    Element measurement = measurements.addElement("measurement");
                    measurement.addAttribute("measureId", m.getMeasureId());

                    // measurement.value:
                    String typename = deriveElementname(m.getValue().getClass());

                    Element valueElem = measurement.addElement(typename);
                    // .addAttribute("value", m.getValue().toString());
                    addStringElement(valueElem, "value", m.getValue().toString());
                    addChangeLog(m.getValue().getChangeLog(), valueElem);
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
            recommendation.addAttribute("alternativeName", p.getRecommendation().getAlternative().getName());
        }
        addStringElement(recommendation, "reasoning", p.getRecommendation().getReasoning());
        addStringElement(recommendation, "effects", p.getRecommendation().getEffects());
        addChangeLog(p.getRecommendation().getChangeLog(), recommendation);

        // transformation
        Element trafo = projectNode.addElement("transformation");
        addStringElement(trafo, "comment", p.getTransformation().getComment());
        addChangeLog(p.getTransformation().getChangeLog(), trafo);

        // Objectivetree (including weights, evaluation values and
        // transformers)
        Element tree = projectNode.addElement("tree");
        tree.addAttribute("weightsInitialized", "" + p.getTree().isWeightsInitialized());
        if (p.getTree().getRoot() != null) {
            addSubTree(p.getTree().getRoot(), tree);
        }

        // add ExecutablePlanDefinition
        Element executablePlanDef = projectNode.addElement("executablePlan");
        ExecutablePlanDefinition plan = p.getExecutablePlanDefinition();
        addStringElement(executablePlanDef, "objectPath", plan.getObjectPath());
        addStringElement(executablePlanDef, "toolParameters", plan.getToolParameters());
        addStringElement(executablePlanDef, "triggersConditions", plan.getTriggersConditions());
        addStringElement(executablePlanDef, "validateQA", plan.getValidateQA());
        addUpload(plan.getT2flowExecutablePlan(), executablePlanDef, "workflow", encoder, addDigitalObjectData);
        addChangeLog(plan.getChangeLog(), executablePlanDef);

        // Export generated preservation action plan
        exportPreservationActionPlan(plan.getPreservationActionPlan(), projectNode, addDigitalObjectData);

        // Plan definition
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
        if (pdef.getTriggers() != null) {
            addTrigger(pdef.getTriggers().getNewCollection(), triggers);
            addTrigger(pdef.getTriggers().getPeriodicReview(), triggers);
            addTrigger(pdef.getTriggers().getChangedEnvironment(), triggers);
            addTrigger(pdef.getTriggers().getChangedObjective(), triggers);
            addTrigger(pdef.getTriggers().getChangedCollectionProfile(), triggers);
        }

        addChangeLog(pdef.getChangeLog(), planDef);
    }

    /**
     * Adds the provided trigger to the parent element.
     * 
     * @param t
     *            the trigger to add
     * @param parent
     *            the parent of the element to create
     * @return the newly created element or null if none was created
     */
    private Element addTrigger(Trigger t, Element parent) {
        Element trigger = null;
        if (t != null) {
            trigger = parent.addElement("trigger");
            trigger.addAttribute("type", t.getType().name());
            trigger.addAttribute("active", Boolean.toString(t.isActive()));
            trigger.addAttribute("description", t.getDescription());
        }
        return trigger;
    }

    /**
     * Long strings are stored as XML-elements, not as attributes. It is not
     * possible to add an element with value <code>null</code>, therefore this
     * has to be handled here: A new element is only added if there is a value
     * at all.
     * 
     * @param parent
     *            the parent element of the element to create
     * @param name
     *            the name of the element to create
     * @param value
     *            the value of the element to create
     * @return the newly created element
     */
    private Element addStringElement(Element parent, String name, String value) {
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

    public String exportTreeToFreemind(Plan plan) {
        return exportTreeToFreemind(plan.getTree().getRoot());
    }

    public String exportTreeToFreemind(TreeNode treeRoot) {
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement("map");
        Namespace xsi = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        root.add(xsi);
        root.addAttribute("version", "0.8.1");

        root.addComment("To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net");
        addSubTreeFreemind(root, treeRoot);

        String xml = doc.asXML();
        // PlatoLogger.getLogger(ProjectExporter.class).debug(arg0)
        return xml;
    }

    private void addSubTreeFreemind(Element xmlElement, TreeNode node) {
        Element element = xmlElement.addElement("node");
        addFreemindAttributes(node, element);

        if (node.isLeaf()) {
            // add scale
            Leaf leaf = (Leaf) node;
            Scale scale = leaf.getScale();
            if (scale != null) {
                Element scaleElement = element.addElement("node");
                String restriction = "?";

                // restrictions: restrictedscale, ordinals -freestring

                if (scale instanceof FreeStringScale) {
                    restriction = "free text";
                } else if ((scale.getType() == ScaleType.ordinal || scale.getType() == ScaleType.restricted)
                    && !"".equals(((RestrictedScale) scale).getRestriction())) {
                    restriction = ((RestrictedScale) scale).getRestriction();
                } else {
                    restriction = scale.getUnit();
                }
                scaleElement.addAttribute("TEXT", restriction);
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
        // TODO export weights? units? single? >> future.

        String mInfoUri = null;

        if (node.isLeaf()) {
            Leaf leaf = (Leaf) node;
            if (leaf.isMapped()) {
                mInfoUri = leaf.getMeasure().getUri();
            }
        }
        // add DESCRIPTION if existent
        if (((mInfoUri != null) && (!"".equals(mInfoUri)))
            || (node.getDescription() != null && !"".equals(node.getDescription()))) {
            Element hook = element.addElement("hook");
            hook.addAttribute("NAME", "accessories/plugins/NodeNote.properties");
            Element description = hook.addElement("text");

            String descr = "";
            // and measurement info

            if ((mInfoUri != null) && (!"".equals(mInfoUri))) {
                descr = "measureId=" + mInfoUri + "\n";
            }
            if (node.getDescription() != null) {
                descr = descr + node.getDescription();
            }
            description.setText(descr);
        }
    }

    /**
     * Adds the provided scale to the parent element.
     * 
     * @param s
     *            the scale to add
     * @param parent
     *            the parent element of the element to add
     * @return the newly created element
     */
    private Element addScale(Scale s, Element parent) {
        Element scale = null;
        if (s != null) {
            String typename = deriveElementname(s.getClass());

            scale = parent.addElement(typename);
            // && (!"".equals(s.getUnit()
            if (s.getUnit() != null) {
                scale.addAttribute("unit", s.getUnit());
            }
            // scale.addAttribute("displayName", s.getDisplayName());

            if (s instanceof RestrictedScale) {
                scale.addAttribute("restriction", ((RestrictedScale) s).getRestriction());
            }
            addChangeLog(s.getChangeLog(), scale);
        }
        return scale;
    }

    private Element exportPreservationActionPlan(DigitalObject preservationActionPlan, Element parent,
        boolean addDigitalObjectData) throws PlanningException {

        Element preservationActionPlanElement = null;

        if (preservationActionPlan != null && preservationActionPlan.isDataExistent()) {
            if (!addDigitalObjectData) {
                preservationActionPlanElement = parent.addElement("preservationActionPlan");
                preservationActionPlanElement.setText(String.valueOf(preservationActionPlan.getId()));
            } else {
                Document doc;
                try {
                    doc = DocumentHelper.parseText(new String(preservationActionPlan.getData().getData(), ENCODING));

                    if (doc.getRootElement().hasContent()) {
                        preservationActionPlanElement = doc.getRootElement();
                    }
                } catch (UnsupportedEncodingException e) {
                    log.error("Error parsing preservation action plan {}.", e.getMessage());
                    throw new PlanningException("Error parsing preservation action plan.", e);
                } catch (DocumentException e) {
                    log.error("Error parsing preservation action plan {}.", e.getMessage());
                    throw new PlanningException("Error parsing preservation action plan.", e);
                }
            }

        }

        return preservationActionPlanElement;
    }
}
