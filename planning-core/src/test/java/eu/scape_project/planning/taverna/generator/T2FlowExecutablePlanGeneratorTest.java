/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.taverna.generator;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import eu.scape_project.planning.services.myexperiment.domain.ComponentConstants;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.taverna.generator.T2FlowExecutablePlanGenerator.InputSource;

/**
 * Unit tests for T2FlowExecutablePlanGenerator.
 */
public class T2FlowExecutablePlanGeneratorTest {

    private static final String MIGRATION_DATAFLOW_ID = "00000000-0000-0000-0000-000000000000";
    private static final String QA_DATAFLOW_ID = "11111111-1111-1111-1111-111111111111";
    private static final String CC_DATAFLOW_ID = "22222222-2222-2222-2222-222222222222";
    protected static final Map<String, String> T2FLOW_NAMESPACE_MAP = new HashMap<String, String>();
    private static final String TOP_WF = "/t2f:workflow/t2f:dataflow[@role='top']";
    private static final String NESTED_WF = "/t2f:workflow/t2f:dataflow[@role='nested']";

    @Before
    public void setup() {
        T2FLOW_NAMESPACE_MAP.put("t2f", "http://taverna.sf.net/2008/xml/t2flow");
    }

    @Test
    public void createEmptyWorkflow() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");
        Document doc = getDocument(planGenerator);

        assertThat(getContent(doc, TOP_WF + "/t2f:name"), is("Name"));

        assertThat(
            getContent(
                doc,
                TOP_WF
                    + "/t2f:annotations/t2f:annotation_chain/net.sf.taverna.t2.annotation.AnnotationChainImpl/annotationAssertions/net.sf.taverna.t2.annotation.AnnotationAssertionImpl/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.Author']/text"),
            is("Author"));

        String wfAnnotations = getSemanticAnnotation(doc, TOP_WF);
        assertThat(wfAnnotations, containsString("http://purl.org/DP/components#ExecutablePlan"));
        assertThat(wfAnnotations, containsString("image/tiff"));
        assertThat(wfAnnotations, containsString("image/jp2"));
    }

    @Test
    public void addSourcePort() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");
        planGenerator.addSourcePort();

        Document doc = getDocument(planGenerator);

        assertThat(getContent(doc, TOP_WF + "/t2f:inputPorts/t2f:port/t2f:name"), is("source"));
        assertThat(getContent(doc, TOP_WF + "/t2f:inputPorts/t2f:port/t2f:depth"), is("0"));
        assertThat(getSemanticAnnotation(doc, TOP_WF + "/t2f:inputPorts/t2f:port"),
            containsString(ComponentConstants.VALUE_SOURCE_OBJECT));
    }

    @Test
    public void addSourcePort_depth() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");
        planGenerator.addSourcePort(7);

        Document doc = getDocument(planGenerator);

        assertThat(getContent(doc, TOP_WF + "/t2f:inputPorts/t2f:port/t2f:name"), is("source"));
        assertThat(getContent(doc, TOP_WF + "/t2f:inputPorts/t2f:port/t2f:depth"), is("7"));
        assertThat(getSemanticAnnotation(doc, TOP_WF + "/t2f:inputPorts/t2f:port"),
            containsString(ComponentConstants.VALUE_SOURCE_OBJECT));
    }

    @Test
    public void addTargetPort() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");
        planGenerator.addTargetPort();

        Document doc = getDocument(planGenerator);

        assertThat(getContent(doc, TOP_WF + "/t2f:outputPorts/t2f:port/t2f:name"), is("target"));
        assertThat(getSemanticAnnotation(doc, TOP_WF + "/t2f:outputPorts/t2f:port"),
            containsString(ComponentConstants.VALUE_TARGET_OBJECT));
    }

    @Test
    public void addMeasurePort_purlDp() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");
        planGenerator.addMeasurePort("http://purl.org/DP/quality/measures#1");

        Document doc = getDocument(planGenerator);

        String portName = getContent(doc, TOP_WF + "/t2f:outputPorts/t2f:port/t2f:name");
        assertThat(portName, is("measures_1"));

        String portAnnotations = getSemanticAnnotation(doc, TOP_WF + "/t2f:outputPorts/t2f:port");
        assertThat(portAnnotations, containsString("http://purl.org/DP/quality/measures#1"));
    }
    
    @Test
    public void addMeasurePort_generic() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");
        planGenerator.addMeasurePort("http://example.com/DP/measures#1");

        Document doc = getDocument(planGenerator);

        String portName = getContent(doc, TOP_WF + "/t2f:outputPorts/t2f:port/t2f:name");
        assertThat(portName, is("example_com_DP_measures_1"));

        String portAnnotations = getSemanticAnnotation(doc, TOP_WF + "/t2f:outputPorts/t2f:port");
        assertThat(portAnnotations, containsString("http://example.com/DP/measures#1"));
    }

    @Test
    public void addMigrationComponent() throws IOException, ParserConfigurationException, SAXException,
        DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);

        Document doc = getDocument(planGenerator);

        assertThat(getContent(doc, TOP_WF + "/t2f:processors/t2f:processor/t2f:name"), is("Migration"));
        assertThat(getContent(doc, NESTED_WF + "/t2f:name"), is("Migration component"));

        assertThat(
            getContent(doc, TOP_WF
                + "/t2f:processors/t2f:processor/t2f:activities/t2f:activity/t2f:configBean/t2f:dataflow/@ref"),
            is(MIGRATION_DATAFLOW_ID));

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='source']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='target']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(2));
    }

    @Test
    public void addQaComponent_noMigration() throws IOException, ParserConfigurationException, SAXException,
        DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addQaMock(planGenerator, "QA component", "image/tiff", "image/jp2");

        Document doc = getDocument(planGenerator);

        assertThat(getContent(doc, TOP_WF + "/t2f:processors/t2f:processor/t2f:name"), is("QA_component"));
        assertThat(getContent(doc, NESTED_WF + "/t2f:name"), is("QA component"));

        assertThat(
            getContent(doc, TOP_WF
                + "/t2f:processors/t2f:processor/t2f:activities/t2f:activity/t2f:configBean/t2f:dataflow/@ref"),
            is(QA_DATAFLOW_ID));

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(2));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceLeft_targetRight() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        addQaMock(planGenerator, "QA component", "image/tiff", "image/jp2");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(5));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceRight_targetLeft() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        addQaMock(planGenerator, "QA component", "image/jp2", "image/tiff");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(5));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceBoth_targetBoth() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);

        List<WorkflowDescription.AcceptedMimetypes> acceptedMimetypes = new ArrayList<WorkflowDescription.AcceptedMimetypes>(
            1);
        acceptedMimetypes.add(new WorkflowDescription.AcceptedMimetypes("image/tiff", "image/jp2"));
        acceptedMimetypes.add(new WorkflowDescription.AcceptedMimetypes("image/jp2", "image/tiff"));
        addQaMock(planGenerator, "QA component", "image/tiff", "image/jp2");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(5));
    }

    @Test
    public void addQaComponent_mimetypePair_sourcePair_targetPair() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);

        List<WorkflowDescription.AcceptedMimetypes> acceptedMimetypes = new ArrayList<WorkflowDescription.AcceptedMimetypes>(
            1);
        acceptedMimetypes.add(new WorkflowDescription.AcceptedMimetypes("image/tiff", "image/tiff"));
        acceptedMimetypes.add(new WorkflowDescription.AcceptedMimetypes("image/jp2", "image/jp2"));
        addQaMock(planGenerator, "QA component", acceptedMimetypes);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceLeft_targetNone() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        addQaMock(planGenerator, "QA component", "image/tiff", "image/bmp");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceRight_targetNone() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        addQaMock(planGenerator, "QA component", "image/bmp", "image/tiff");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceNone_targetLeft() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        addQaMock(planGenerator, "QA component", "image/jp2", "image/bmp");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceNone_targetRight() throws IOException, ParserConfigurationException,
        SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        addQaMock(planGenerator, "QA component", "image/bmp", "image/jp2");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }

    @Test
    public void addQaComponent_mimetypePair_sourceWildcard_targetWildcard() throws IOException,
        ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        addQaMock(planGenerator, "QA component", "image/*", "image/*");

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(5));
    }

    @Test
    public void addQaComponent_mimetype_source_target() throws IOException, ParserConfigurationException, SAXException,
        DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        List<String> acceptedMimetypes = new ArrayList<String>(2);
        acceptedMimetypes.add("image/tiff");
        acceptedMimetypes.add("image/jp2");
        addQaMimetypeMock(planGenerator, "QA component", acceptedMimetypes);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(5));
    }

    @Test
    public void addQaComponent_mimetype_source() throws IOException, ParserConfigurationException, SAXException,
        DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        List<String> acceptedMimetypes = new ArrayList<String>(2);
        acceptedMimetypes.add("image/tiff");
        addQaMimetypeMock(planGenerator, "QA component", acceptedMimetypes);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }

    @Test
    public void addQaComponent_mimetype_target() throws IOException, ParserConfigurationException, SAXException,
        DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        List<String> acceptedMimetypes = new ArrayList<String>(2);
        acceptedMimetypes.add("image/jp2");
        addQaMimetypeMock(planGenerator, "QA component", acceptedMimetypes);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), nullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }

    @Test
    public void addQaComponent_mimetype_wildcard() throws IOException, ParserConfigurationException, SAXException,
        DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        List<String> acceptedMimetypes = new ArrayList<String>(1);
        acceptedMimetypes.add("image/*");
        addQaMimetypeMock(planGenerator, "QA component", acceptedMimetypes);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='dataflow' and t2f:port/text()='source']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='left']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='target']" + " and "
                + "t2f:sink[@type='processor' and t2f:port/text()='right']]"), notNullValue());
        assertThat(
            getSingleNode(doc, TOP_WF + "/t2f:datalinks/t2f:datalink"
                + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_1']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(5));
    }

    @Test
    public void addQaComponent_twoComponents() throws IOException, ParserConfigurationException, SAXException,
        DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        List<String> acceptedMimetypes = new ArrayList<String>(1);
        acceptedMimetypes.add("image/*");
        addQaMimetypeMock(planGenerator, "QA1", acceptedMimetypes);
        addQaMimetypeMock(planGenerator, "QA2", acceptedMimetypes);

        Document doc = getDocument(planGenerator);

        assertThat(
            getNodes(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='dataflow' and t2f:port/text()='source']"
                    + " and " + "t2f:sink[@type='processor' and t2f:port/text()='left']]").size(), is(2));
        assertThat(
            getNodes(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='processor' and t2f:port/text()='target']"
                    + " and " + "t2f:sink[@type='processor' and t2f:port/text()='right']]").size(), is(2));
        assertThat(
            getNodes(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink"
                    + "[t2f:source[@type='processor' and t2f:port/text()='qa_output']" + " and "
                    + "t2f:sink[@type='merge' and t2f:port/text()='measures_1']]").size(), is(2));
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(8));
    }

    @Test
    public void addCcComponent_source() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();

        List<String> acceptedMimetypes = new ArrayList<String>(1);
        acceptedMimetypes.add("image/*");
        addCcMock(planGenerator, "CC", acceptedMimetypes, InputSource.SOURCE_OBJECT);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='dataflow' and t2f:port/text()='source']"
                    + " and " + "t2f:sink[@type='processor' and t2f:port/text()='source']]"), notNullValue());
        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='processor' and t2f:port/text()='cc_output']"
                    + " and " + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_2']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(2));
    }
    
    @Test
    public void addCcComponent_target() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        List<String> acceptedMimetypes = new ArrayList<String>(1);
        acceptedMimetypes.add("image/*");
        addCcMock(planGenerator, "CC", acceptedMimetypes, InputSource.TARGET_OBJECT);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='processor' and t2f:port/text()='target']"
                    + " and " + "t2f:sink[@type='processor' and t2f:port/text()='source']]"), notNullValue());
        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='processor' and t2f:port/text()='cc_output']"
                    + " and " + "t2f:sink[@type='dataflow' and t2f:port/text()='measures_2']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(4));
    }
    
    @Test
    public void addCcComponent_twoComponents() throws IOException, ParserConfigurationException, SAXException, DocumentException {
        T2FlowExecutablePlanGenerator planGenerator = new T2FlowExecutablePlanGenerator("Name", "Author", "image/tiff",
            "image/jp2");

        planGenerator.addSourcePort();
        planGenerator.addTargetPort();

        addMigrationMock(planGenerator);
        List<String> acceptedMimetypes = new ArrayList<String>(1);
        acceptedMimetypes.add("image/*");
        addCcMock(planGenerator, "CC1", acceptedMimetypes, InputSource.SOURCE_OBJECT);
        addCcMock(planGenerator, "CC2", acceptedMimetypes, InputSource.TARGET_OBJECT);

        Document doc = getDocument(planGenerator);

        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='dataflow' and t2f:port/text()='source']"
                    + " and " + "t2f:sink[@type='processor' and t2f:port/text()='source' and t2f:processor/text()='CC1']]"), notNullValue());
        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='processor' and t2f:port/text()='cc_output' and t2f:processor/text()='CC1']"
                    + " and " + "t2f:sink[@type='merge' and t2f:port/text()='measures_2']]"), notNullValue());
        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='processor' and t2f:port/text()='target']"
                    + " and " + "t2f:sink[@type='processor' and t2f:port/text()='source' and t2f:processor/text()='CC2']]"), notNullValue());
        assertThat(
            getSingleNode(
                doc,
                TOP_WF + "/t2f:datalinks/t2f:datalink" + "[t2f:source[@type='processor' and t2f:port/text()='cc_output' and t2f:processor/text()='CC2']"
                    + " and " + "t2f:sink[@type='merge' and t2f:port/text()='measures_2']]"), notNullValue());
        assertThat(getNodes(doc, TOP_WF + "/t2f:datalinks/t2f:datalink").size(), is(6));
    }
    
    /**
     * Returns a document generated by the provided plan generator.
     * 
     * @param planGenerator
     *            the plan generator
     * @return a document
     * @throws IOException
     *             if an exception occurred
     * @throws ParserConfigurationException
     *             if an exception occurred
     * @throws SAXException
     *             if an exception occurred
     * @throws DocumentException
     *             if an exception occurred
     */
    private Document getDocument(T2FlowExecutablePlanGenerator planGenerator) throws IOException,
        ParserConfigurationException, SAXException, DocumentException {
        StringWriter w = new StringWriter();
        planGenerator.generate(w);

        StringReader r = new StringReader(w.toString());
        SAXReader reader = new SAXReader();
        return reader.read(r);
    }

    /**
     * Returns nodes of the provided document identified by the xPath.
     * 
     * @param document
     *            the document
     * @param xPath
     *            the xPath to search
     * @return a list of nodes
     */
    private List<Node> getNodes(Document document, String xPath) {
        XPath x = DocumentHelper.createXPath(xPath);
        x.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
        return x.selectNodes(document);
    }

    /**
     * Returns a single node of the provided document identified by the xPath.
     * 
     * @param document
     *            the document
     * @param xPath
     *            the xPath to search
     * @return a single node or null if none found
     */
    private Node getSingleNode(Document document, String xPath) {
        XPath x = DocumentHelper.createXPath(xPath);
        x.setNamespaceURIs(T2FLOW_NAMESPACE_MAP);
        return x.selectSingleNode(document);
    }

    /**
     * Returns the content of a single node.
     * 
     * @param document
     *            the document
     * @param xPath
     *            the xPath to search
     * @return content of a single node or null if none found
     */
    private String getContent(Document document, String xPath) {
        Node n = getSingleNode(document, xPath);
        if (n == null) {
            return null;
        } else {
            return n.getText();
        }
    }

    /**
     * Returns a semantic annotation of a node.
     * 
     * @param document
     *            the document
     * @param xPathPrefix
     *            the xPath of the node to search
     * @return the semantic annotation of the node or null if none found
     */
    private String getSemanticAnnotation(Document document, String xPathPrefix) {
        return getContent(
            document,
            xPathPrefix
                + "/t2f:annotations/t2f:annotation_chain_2_2/net.sf.taverna.t2.annotation.AnnotationChainImpl/annotationAssertions/net.sf.taverna.t2.annotation.AnnotationAssertionImpl/annotationBean[@class='net.sf.taverna.t2.annotation.annotationbeans.SemanticAnnotation']/content");
    }

    /**
     * Generates a very simple migration workflow.
     * 
     * @param uuid
     *            the UUID of the workflow
     * @param name
     *            the name of the workflow
     * @return the workflow as string
     */
    private String generateMigrationContent(String uuid, String name) {
        // @formatter:off
        return "<workflow xmlns=\"http://taverna.sf.net/2008/xml/t2flow\" version=\"1\" producedBy=\"taverna-2.4.0\">"
            + "<dataflow id=\"" + uuid + "\" role=\"top\">"
            + "<name>" + name + "</name>" 
            + "<inputPorts><port><name>source</name><depth>0</depth><granularDepth>0</granularDepth><annotations/></port></inputPorts>"
            + "<outputPorts><port><name>target</name><annotations/></port></outputPorts>"
            + "<processors/>" 
            + "<conditions/>" 
            + "<datalinks><datalink><sink type=\"dataflow\"><port>target</port></sink><source type=\"dataflow\"><port>source</port></source></datalink></datalinks>"
            + "<annotations><annotation_chain_2_2 encoding=\"xstream\"><null/></annotation_chain_2_2></annotations>" + "</dataflow>" + "</workflow>";
        // @formatter:on
    }

    /**
     * Generates a very simple QA workflow.
     * 
     * @param uuid
     *            the UUID of the workflow
     * @param name
     *            the name of the workflow
     * @return the workflow as string
     */
    private String generateQaContent(String uuid, String name) {
        // @formatter:off
        return "<workflow xmlns=\"http://taverna.sf.net/2008/xml/t2flow\" version=\"1\" producedBy=\"taverna-2.4.0\">"
            + "<dataflow id=\"" + uuid + "\" role=\"top\">"
            + "<name>" + name + "</name>" 
            + "<inputPorts><port><name>left</name><depth>0</depth><granularDepth>0</granularDepth><annotations/></port><port><name>right</name><depth>0</depth><granularDepth>0</granularDepth><annotations/></port></inputPorts>"
            + "<outputPorts><port><name>qa_output</name><annotations/></port></outputPorts>"
            + "<processors/>" 
            + "<conditions/>"
            + "<datalinks><datalink>"
            + "<sink type=\"merge\"><port>qa_output</port></sink><source type=\"dataflow\"><port>left</port></source>"
            + "</datalink><datalink>"
            + "<sink type=\"merge\"><port>qa_output</port></sink><source type=\"dataflow\"><port>right</port></source>"
            + "</datalink></datalinks>"
            + "<annotations><annotation_chain_2_2 encoding=\"xstream\"><null/></annotation_chain_2_2></annotations>" 
            + "</dataflow>" 
            + "</workflow>";
        // @formatter:on
    }
    
    /**
     * Generates a very simple CC workflow.
     * 
     * @param uuid
     *            the UUID of the workflow
     * @param name
     *            the name of the workflow
     * @return the workflow as string
     */
    private String generateCcContent(String uuid, String name) {
        // @formatter:off
        return "<workflow xmlns=\"http://taverna.sf.net/2008/xml/t2flow\" version=\"1\" producedBy=\"taverna-2.4.0\">"
            + "<dataflow id=\"" + uuid + "\" role=\"top\">"
            + "<name>" + name + "</name>" 
            + "<inputPorts><port><name>source</name><depth>0</depth><granularDepth>0</granularDepth><annotations/></port></inputPorts>"
            + "<outputPorts><port><name>cc_output</name><annotations/></port></outputPorts>"
            + "<processors/>" 
            + "<conditions/>"
            + "<datalinks><datalink>"
            + "<sink type=\"dataflow\"><port>cc_output</port></sink><source type=\"dataflow\"><port>source</port></source>"
            + "</datalink></datalinks>"
            + "<annotations><annotation_chain_2_2 encoding=\"xstream\"><null/></annotation_chain_2_2></annotations>" 
            + "</dataflow>" 
            + "</workflow>";
        // @formatter:on
    }

    /**
     * Adds a mock of a migration component to the plan generator.
     * 
     * @param planGenerator
     *            the plan generator to user
     */
    private void addMigrationMock(T2FlowExecutablePlanGenerator planGenerator) {
        WorkflowDescription wf = mock(WorkflowDescription.class);
        when(wf.getDataflowId()).thenReturn(MIGRATION_DATAFLOW_ID);

        List<WorkflowDescription.Port> inputPorts = new ArrayList<WorkflowDescription.Port>(1);
        inputPorts.add(new WorkflowDescription.Port("source", "Description", ComponentConstants.VALUE_SOURCE_OBJECT));
        when(wf.getInputPorts()).thenReturn(inputPorts);

        List<WorkflowDescription.Port> outputPorts = new ArrayList<WorkflowDescription.Port>(1);
        outputPorts.add(new WorkflowDescription.Port("target", "Description", ComponentConstants.VALUE_TARGET_OBJECT));
        when(wf.getOutputPorts()).thenReturn(outputPorts);

        List<WorkflowDescription.MigrationPath> migrationPaths = new ArrayList<WorkflowDescription.MigrationPath>(1);
        migrationPaths.add(new WorkflowDescription.MigrationPath("image/tiff", "image/jp2"));
        when(wf.getMigrationPaths()).thenReturn(migrationPaths);

        planGenerator.setMigrationComponent(wf, generateMigrationContent(MIGRATION_DATAFLOW_ID, "Migration component"),
            new HashMap<String, String>(0));
    }

    /**
     * Adds a mock of a QA component to the plan generator.
     * 
     * @param planGenerator
     *            the plan generator to use
     * @param name
     *            the component name
     * @param leftMimetype
     *            the left accepted mimetype
     * @param rightMimetype
     *            the right accepted mimetype
     */
    private void addQaMock(T2FlowExecutablePlanGenerator planGenerator, String name, String leftMimetype,
        String rightMimetype) {
        List<WorkflowDescription.AcceptedMimetypes> acceptedMimetypes = new ArrayList<WorkflowDescription.AcceptedMimetypes>(
            1);
        acceptedMimetypes.add(new WorkflowDescription.AcceptedMimetypes(leftMimetype, rightMimetype));
        addQaMock(planGenerator, name, acceptedMimetypes);
    }

    /**
     * Adds a mock of a QA component to the plan generator.
     * 
     * @param planGenerator
     *            the plan generator to use
     * @param name
     *            the component name
     * @param acceptedMimetypes
     *            a list of accepted mimetypes
     */
    private void addQaMock(T2FlowExecutablePlanGenerator planGenerator, String name,
        List<WorkflowDescription.AcceptedMimetypes> acceptedMimetypes) {
        WorkflowDescription wf = mock(WorkflowDescription.class);
        when(wf.getName()).thenReturn(name);
        when(wf.getDataflowId()).thenReturn(QA_DATAFLOW_ID);

        List<WorkflowDescription.Port> inputPorts = new ArrayList<WorkflowDescription.Port>(2);
        inputPorts.add(new WorkflowDescription.Port("left", "Description", ComponentConstants.VALUE_LEFT_OBJECT));
        inputPorts.add(new WorkflowDescription.Port("right", "Description", ComponentConstants.VALUE_RIGHT_OBJECT));
        when(wf.getInputPorts()).thenReturn(inputPorts);

        List<WorkflowDescription.Port> outputPorts = new ArrayList<WorkflowDescription.Port>(1);
        outputPorts.add(new WorkflowDescription.Port("qa_output", "Description", "http://purl.org/DP/quality/measures#1"));
        when(wf.getOutputPorts()).thenReturn(outputPorts);

        when(wf.getAcceptedMimetypes()).thenReturn(acceptedMimetypes);

        List<String> measures = new ArrayList<String>(1);
        measures.add("http://purl.org/DP/quality/measures#1");

        planGenerator.addQaComponent(wf, generateQaContent(QA_DATAFLOW_ID, name), new HashMap<String, String>(0),
            measures);
    }

    /**
     * Adds a mock of a QA component to the plan generator.
     * 
     * @param planGenerator
     *            the plan generator to use
     * @param name
     *            the component name
     * @param mimetypes
     *            a list of mimetypes
     */
    private void addQaMimetypeMock(T2FlowExecutablePlanGenerator planGenerator, String name, List<String> mimetypes) {
        WorkflowDescription wf = mock(WorkflowDescription.class);
        when(wf.getName()).thenReturn(name);
        when(wf.getDataflowId()).thenReturn(QA_DATAFLOW_ID);

        List<WorkflowDescription.Port> inputPorts = new ArrayList<WorkflowDescription.Port>(2);
        inputPorts.add(new WorkflowDescription.Port("left", "Description", ComponentConstants.VALUE_LEFT_OBJECT));
        inputPorts.add(new WorkflowDescription.Port("right", "Description", ComponentConstants.VALUE_RIGHT_OBJECT));
        when(wf.getInputPorts()).thenReturn(inputPorts);

        List<WorkflowDescription.Port> outputPorts = new ArrayList<WorkflowDescription.Port>(1);
        outputPorts.add(new WorkflowDescription.Port("qa_output", "Description", "http://purl.org/DP/quality/measures#1"));
        when(wf.getOutputPorts()).thenReturn(outputPorts);

        when(wf.getAcceptedMimetype()).thenReturn(mimetypes);

        List<String> measures = new ArrayList<String>(1);
        measures.add("http://purl.org/DP/quality/measures#1");

        planGenerator.addQaComponent(wf, generateQaContent(QA_DATAFLOW_ID, name), new HashMap<String, String>(0),
            measures);
    }

    /**
     * Adds a mock of a CC component to the plan generator.
     * 
     * @param planGenerator
     *            the plan generator to use
     * @param name
     *            the component name
     * @param acceptedMimetype
     *            a list of accepted mimetype
     * @param inputSource
     *            the input source of the CC component
     */
    private void addCcMock(T2FlowExecutablePlanGenerator planGenerator, String name, List<String> acceptedMimetype,
        InputSource inputSource) {
        WorkflowDescription wf = mock(WorkflowDescription.class);
        when(wf.getName()).thenReturn(name);
        when(wf.getDataflowId()).thenReturn(CC_DATAFLOW_ID);

        List<WorkflowDescription.Port> inputPorts = new ArrayList<WorkflowDescription.Port>(1);
        inputPorts.add(new WorkflowDescription.Port("source", "Description", ComponentConstants.VALUE_SOURCE_OBJECT));
        when(wf.getInputPorts()).thenReturn(inputPorts);

        List<WorkflowDescription.Port> outputPorts = new ArrayList<WorkflowDescription.Port>(1);
        outputPorts.add(new WorkflowDescription.Port("cc_output", "Description", "http://purl.org/DP/quality/measures#2"));
        when(wf.getOutputPorts()).thenReturn(outputPorts);

        when(wf.getAcceptedMimetype()).thenReturn(acceptedMimetype);

        List<String> measures = new ArrayList<String>(1);
        measures.add("http://purl.org/DP/quality/measures#2");

        planGenerator.addCcComponent(wf, generateCcContent(CC_DATAFLOW_ID, name), new HashMap<String, String>(0),
            measures, inputSource);
    }
}
