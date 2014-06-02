/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Before;
import org.junit.Test;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.AlternativesDefinition;
import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Experiment;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.ProjectBasis;
import eu.scape_project.planning.model.ResourceDescription;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.Trigger;
import eu.scape_project.planning.model.TriggerDefinition;
import eu.scape_project.planning.model.TriggerType;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.tree.PolicyTree;

/**
 * Tests for ProjectExporter.
 */
public class ProjectExporterTest {

    protected static final Map<String, String> T2FLOW_NAMESPACE_MAP = new HashMap<String, String>();

    private ProjectExporter exporter;

    @Before
    public void setUp() {
        exporter = new ProjectExporter();
        T2FLOW_NAMESPACE_MAP.put("p", PlanXMLConstants.PLATO_NS);
        T2FLOW_NAMESPACE_MAP.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    }

    @Test
    public void createProjectDoc() {
        Document doc = exporter.createProjectDoc();
        assertThat(getNode(doc, "/plans"), notNullValue());
        assertThat(getContent(doc, "/plans/@version"), is(PlanXMLConstants.PLATO_SCHEMA_VERSION));
    }

    @Test
    public void createTemplateDoc() {
        Document doc = exporter.createTemplateDoc();
        assertThat(getNode(doc, "/templates"), notNullValue());
    }

    @Test
    public void exportToXml_planProperties() throws PlanningException {
        Plan p = generateBasicPlan();
        p.setPlanProperties(generatePlanProperties());

        Document doc = exporter.exportToXml(p);

        Element ppElement = (Element) getNode(doc, "/plans/p:plan/p:properties");

        assertThat(ppElement.attributeValue("author"), is("Author"));
        assertThat(ppElement.attributeValue("organization"), is("Organization"));
        assertThat(ppElement.attributeValue("name"), is("Name"));
        assertThat(ppElement.attributeValue("privateProject"), is("true"));
        assertThat(ppElement.attributeValue("reportPublic"), is("true"));
        assertThat(ppElement.attributeValue("repositoryIdentifier"), is("Repository ID"));

        Element reportElement = getElement(doc, "/plans/p:plan/p:properties/p:report");
        checkDigitalObject(reportElement, p.getPlanProperties().getReportUpload());

        assertThat(getElement(doc, "/plans/p:plan/p:properties/p:state").attributeValue("value"), is("0"));
        checkContentByName(ppElement, "p:description");
        checkContentByName(ppElement, "p:owner");
    }

    @Test
    public void exportToXml_basis() throws PlanningException {
        Plan p = generateBasicPlan();
        p.setProjectBasis(generateBasis());

        Document doc = exporter.exportToXml(p);

        Element basisElement = (Element) getNode(doc, "/plans/p:plan/p:basis");
        assertThat(basisElement.attributeValue("identificationCode"), is("Identification code"));

        checkContentByName(basisElement, "p:documentTypes");
        checkContentByName(basisElement, "p:applyingPolicies");
        checkContentByName(basisElement, "p:designatedCommunity");
        checkContentByName(basisElement, "p:mandate");
        checkContentByName(basisElement, "p:organisationalProcedures");
        checkContentByName(basisElement, "p:planningPurpose");
        checkContentByName(basisElement, "p:planRelations");
        checkContentByName(basisElement, "p:preservationRights");
        checkContentByName(basisElement, "p:referenceToAgreements");

        Element newTriggerElement = getElement(basisElement, "p:triggers/p:trigger[@type='NEW_COLLECTION']");
        assertThat(newTriggerElement.attributeValue("active"), is("true"));
        assertThat(newTriggerElement.attributeValue("description"), is("New description"));

        Element periodicTriggerElement = getElement(basisElement, "p:triggers/p:trigger[@type='PERIODIC_REVIEW']");
        assertThat(periodicTriggerElement.attributeValue("active"), is("false"));

        Element ceTriggerElement = getElement(basisElement, "p:triggers/p:trigger[@type='CHANGED_ENVIRONMENT']");
        assertThat(ceTriggerElement.attributeValue("active"), is("false"));
        assertThat(ceTriggerElement.attributeValue("description"), is("Changed environment description"));

        Element coTriggerElement = getElement(basisElement, "p:triggers/p:trigger[@type='CHANGED_OBJECTIVE']");
        assertThat(coTriggerElement.attributeValue("active"), is("false"));

        Element ccpTriggerElement = getElement(basisElement, "p:triggers/p:trigger[@type='CHANGED_COLLECTION_PROFILE']");
        assertThat(ccpTriggerElement.attributeValue("active"), is("false"));
    }

    @Test
    public void exportToXml_alternative() throws PlanningException {
        Plan p = generateBasicPlan();

        AlternativesDefinition alternativesDefinition = new AlternativesDefinition();
        alternativesDefinition.setDescription("Description");
        Alternative alt1 = generateAlternative("Alternative 1");
        alternativesDefinition.addAlternative(alt1);
        Alternative alt2 = generateAlternative("Alternative 2");
        alternativesDefinition.addAlternative(alt2);
        p.setAlternativesDefinition(alternativesDefinition);

        Document doc = exporter.exportToXml(p);

        assertThat(getNodes(doc, "/plans/p:plan/p:alternatives/p:alternative").size(), is(2));

        // Alternative 1
        Element alt1Element = getElement(doc, "/plans/p:plan/p:alternatives/p:alternative[@name='Alternative 1']");
        assertThat(alt1Element, notNullValue());
        checkContentByName(alt1Element, "p:description");

        Element ex1Element = getElement(alt1Element, "p:experiment");
        checkContentByName(ex1Element, "p:description");
        checkContentByName(ex1Element, "p:settings");
        checkDigitalObject(getElement(ex1Element, "p:workflow"), alt1.getExperiment().getWorkflow());

        Element detailedInfo1Element = getElement(ex1Element, "p:detailedInfos/p:detailedInfo");
        assertThat(detailedInfo1Element.attributeValue("key"), is("Short name"));
        assertThat(detailedInfo1Element.attributeValue("successful"), is("true"));
        checkContentByName(detailedInfo1Element, "p:programOutput");
        checkContentByName(detailedInfo1Element, "p:cpr");

        Element measurement1Element = getElement(detailedInfo1Element, "p:measurements/p:measurement");
        assertThat(measurement1Element.attributeValue("measureId"), is("Measure id"));
        checkContentByName(measurement1Element, "p:freeStringValue/p:value");

        // Alternative 2
        Element alt2Element = getElement(doc, "/plans/p:plan/p:alternatives/p:alternative[@name='Alternative 2']");
        assertThat(alt2Element, notNullValue());
        checkContentByName(alt2Element, "p:description");

        Element ex2Element = getElement(alt1Element, "p:experiment");
        checkContentByName(ex2Element, "p:description");
        checkContentByName(ex2Element, "p:settings");
        checkDigitalObject(getElement(ex2Element, "p:workflow"), alt1.getExperiment().getWorkflow());

        assertThat(getContent(doc, "/plans/p:plan/p:alternatives/p:alternative[@name='Alternative 1']/p:experiment"),
            notNullValue());
    }

    /**
     * TODO: Debug code, remove.
     * 
     * @param doc
     *            the document to print
     */
    private void printDoc(Document doc) {

        try {
            PrintWriter out = new PrintWriter("/tmp/out.xml");
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(doc);
            writer.close();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Generates a basic plan.
     * 
     * @return the plan
     */
    private Plan generateBasicPlan() {
        Plan p = new Plan();

        PlanProperties planProperties = new PlanProperties();
        p.setPlanProperties(planProperties);

        return p;
    }

    /**
     * Generates an alternative.
     * 
     * @param name
     *            the alternative name
     * @return the alternative
     */
    private Alternative generateAlternative(String name) {
        Alternative a = new Alternative(name, "Description");
        a.setDiscarded(false);
        ResourceDescription resourceDescription = new ResourceDescription();
        a.setResourceDescription(resourceDescription);
        Experiment experiment = new Experiment();
        experiment.setDescription("Description");
        experiment.setSettings("Settings");
        experiment.setWorkflow(generateDigitalObject());

        Map<SampleObject, DetailedExperimentInfo> detailedInfo = experiment.getDetailedInfo();
        SampleObject sample1 = new SampleObject("Short name");
        DetailedExperimentInfo experimentInfo1 = new DetailedExperimentInfo();
        experimentInfo1.setSuccessful(true);
        experimentInfo1.setCpr("Cpr");
        experimentInfo1.setProgramOutput("Program output");
        experimentInfo1.getMeasurements().put("key", new Measurement("Measure id", "Value"));
        detailedInfo.put(sample1, experimentInfo1);

        a.setExperiment(experiment);
        return a;
    }

    /**
     * Generates plan properties.
     * 
     * @return the plan properties
     */
    private PlanProperties generatePlanProperties() {
        PlanProperties planProperties = new PlanProperties();
        planProperties.setAuthor("Author");
        planProperties.setName("Name");
        planProperties.setDescription("Description");
        planProperties.setOrganization("Organization");
        planProperties.setOwner("Owner");
        planProperties.setPrivateProject(true);
        planProperties.setReportPublic(true);
        planProperties.setRepositoryIdentifier("Repository ID");
        planProperties.setReportUpload(generateDigitalObject());
        planProperties.setState(PlanState.CREATED);

        return planProperties;
    }

    /**
     * Generates a project basis.
     * 
     * @return the project basis
     */
    private ProjectBasis generateBasis() {
        ProjectBasis basis = new ProjectBasis();
        basis.setIdentificationCode("Identification code");
        basis.setDocumentTypes("Document types");
        basis.setMandate("Mandate");
        basis.setPlanningPurpose("Planning purpose");
        basis.setDesignatedCommunity("Designated community");
        basis.setApplyingPolicies("Applying policies");
        basis.setOrganisationalProcedures("Organisational procedures");
        basis.setPreservationRights("Preservation rights");
        basis.setReferenceToAgreements("Reference to agreements");
        basis.setPlanRelations("Plan relations");

        TriggerDefinition triggers = new TriggerDefinition();
        Trigger newTrigger = new Trigger(TriggerType.NEW_COLLECTION);
        newTrigger.setActive(true);
        newTrigger.setDescription("New description");
        triggers.setTrigger(newTrigger);
        Trigger ceTrigger = new Trigger(TriggerType.CHANGED_ENVIRONMENT);
        ceTrigger.setActive(false);
        ceTrigger.setDescription("Changed environment description");
        triggers.setTrigger(ceTrigger);
        triggers.setTrigger(new Trigger(TriggerType.CHANGED_COLLECTION_PROFILE));
        basis.setTriggers(triggers);

        PolicyTree policyTree = new PolicyTree();
        PolicyNode root = new PolicyNode();
        root.setName("Root node");
        PolicyNode child1 = new PolicyNode();
        child1.setName("Child 1");
        child1.setParent(root);
        root.getChildren().add(child1);
        policyTree.setRoot(root);
        basis.setPolicyTree(policyTree);

        return basis;
    }

    /**
     * Generates a digital object.
     * 
     * @return the digital object
     */
    private DigitalObject generateDigitalObject() {
        DigitalObject d = new DigitalObject();
        d.setContentType("application/vnd.taverna.t2flow+xml");
        d.setFullname("workflow.t2flow");
        d.setPid("pid-experiment-workflow");

        ByteStream bs = new ByteStream();
        bs.setData("workflowdata".getBytes());
        d.setData(bs);

        return d;
    }

    private void checkDigitalObject(Element element, DigitalObject digitalObject) {
        assertThat(element.attributeValue("fullname"), is(digitalObject.getFullname()));
        assertThat(element.attributeValue("contentType"), is(digitalObject.getContentType()));

        Element dataElement = (Element) createXPath("p:data").selectSingleNode(element);

        assertThat(dataElement.attributeValue("hasData"), is("true"));
        assertThat(dataElement.attributeValue("encoding"), is("base64"));
    }

    /**
     * Checks if the content of the specified node against the nodes name. The
     * node name is coverter with: * Camel case separated into words * The first
     * letter capitalized * All other letters lowercased
     * 
     * @param context
     *            the search context
     * @param xPath
     *            the xPath
     */
    private void checkContentByName(Node context, String xPath) {
        String content = getContent(context, xPath);
        String xPathText = xPath.replaceAll(".+:", "").replaceAll("([A-Z])", " $1");
        xPathText = xPathText.substring(0, 1).toUpperCase() + xPathText.substring(1).toLowerCase();
        assertThat(content, is(xPathText));
    }

    /**
     * Creates an xPath element including namespaces.
     * 
     * @param xPath
     *            the xPath
     * @return the xPath object
     */
    private XPath createXPath(String xPath) {
        XPath x = DocumentHelper.createXPath("p:data");
        x.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
        return x;
    }

    /**
     * Returns nodes of the provided document identified by the xPath.
     * 
     * @param context
     *            the search context
     * @param xPath
     *            the xPath to search
     * @return a list of nodes
     */
    @SuppressWarnings("unchecked")
    private List<Node> getNodes(Node context, String xPath) {
        XPath x = DocumentHelper.createXPath(xPath);
        x.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
        return x.selectNodes(context);
    }

    /**
     * Returns a single node of the provided document identified by the xPath.
     * 
     * @param context
     *            the search context
     * @param xPath
     *            the xPath to search
     * @return a single node or null if none found
     */
    @SuppressWarnings("unchecked")
    private Node getNode(Node context, String xPath) {
        XPath x = DocumentHelper.createXPath(xPath);
        x.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
        List<Node> nodes = x.selectNodes(context);
        assertThat(nodes.size(), is(1));
        return nodes.get(0);
    }

    /**
     * Returns a single element of the provided document identified by the
     * xPath.
     * 
     * @param context
     *            the search context
     * @param xPath
     *            the xPath to search
     * @return a single node or null if none found
     */
    private Element getElement(Node context, String xPath) {
        return (Element) getNode(context, xPath);
    }

    /**
     * Returns the content of a single node.
     * 
     * @param context
     *            the search context
     * @param xPath
     *            the xPath to search
     * @return content of a single node or null if none found
     */
    private String getContent(Node context, String xPath) {
        Node n = getNode(context, xPath);
        if (n == null) {
            return null;
        } else {
            return n.getText();
        }
    }
}
