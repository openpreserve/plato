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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlatoException;

/**
 * Roundtrip tests for parsing a plan from a file and exporting it again.
 */
public class PlanParserProjectExporterTest {

    private final Logger log = LoggerFactory.getLogger(PlanParserProjectExporterTest.class);

    @Test
    public void importProjectsExportToXmlMinimal() throws PlatoException, ParserConfigurationException, SAXException,
        DocumentException, PlanningException, IOException {

        PlanParser parser = new PlanParser();
        ProjectExporter exporter = new ProjectExporter();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-minimal.xml");

        List<Plan> plans = parser.importProjects(in);

        Assert.assertTrue(plans.size() == 1);
        Plan plan = plans.get(0);

        // Parse original
        InputStream importedStream = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-minimal.xml");
        Document original = parsePlan(importedStream);
        original.normalize();

        Document exported = exporter.exportToXml(plan);
        exported.normalize();

        // Compare
        XMLUnit.setIgnoreWhitespace(true);

        try {
            Diff diff = new Diff(original.asXML(), exported.asXML());
            Assert.assertTrue(diff.similar());
        } finally {
            XMLUnit.setIgnoreWhitespace(false);
        }
    }

    @Test
    public void importProjectsExportToXmlProfileResultsPAP() throws PlatoException, ParserConfigurationException,
        SAXException, DocumentException, PlanningException, IOException {

        PlanParser parser = new PlanParser();
        ProjectExporter exporter = new ProjectExporter();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-ProfileSamplesPAP.xml");

        List<Plan> plans = parser.importProjects(in);

        Assert.assertTrue(plans.size() == 1);
        Plan plan = plans.get(0);

        // Parse original
        InputStream importedStream = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("plans/PlanParserTest/PlanParserTest-PLAN_VALIDATED-ProfileSamplesPAP.xml");
        Document original = parsePlan(importedStream);
        original.normalize();

        Document exported = exporter.createProjectDoc();
        exporter.addProject(plan, exported, true);
        exported.normalize();

        // Compare
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalizeWhitespace(true);

        try {
            Diff diff = new Diff(original.asXML(), exported.asXML());
            diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());

            DetailedDiff detailedDiff = new DetailedDiff(diff);

            @SuppressWarnings("unchecked")
            List<Difference> differences = (List<Difference>) detailedDiff.getAllDifferences();
            for (Difference difference : differences) {
                log.warn(difference.toString());
            }

            Assert.assertTrue(diff.similar());

        } finally {
            XMLUnit.setIgnoreWhitespace(false);
            XMLUnit.setNormalizeWhitespace(false);
        }
    }

    /**
     * Parses the plan XML.
     * 
     * @param in
     *            the plan XML
     * @return the plan as XML document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws DocumentException
     */
    private Document parsePlan(InputStream in) throws ParserConfigurationException, SAXException, DocumentException {
        ValidatingParserFactory vpf = new ValidatingParserFactory();

        SAXParser parser = vpf.getValidatingParser();
        parser.setProperty(ValidatingParserFactory.JAXP_SCHEMA_SOURCE, PlanXMLConstants.PLAN_SCHEMAS);

        SAXReader reader = new SAXReader(parser.getXMLReader());
        reader.setValidation(true);
        reader.setStripWhitespaceText(true);
        reader.setIgnoreComments(true);

        SchemaResolver schemaResolver = new SchemaResolver();
        schemaResolver.addSchemaLocation(PlanXMLConstants.PLATO_SCHEMA_URI, PlanXMLConstants.PLATO_SCHEMA_LOCATION);
        schemaResolver.addSchemaLocation(PlanXMLConstants.PAP_SCHEMA_URI, PlanXMLConstants.PAP_SCHEMA_LOCATION);
        schemaResolver.addSchemaLocation(PlanXMLConstants.TAVERNA_SCHEMA_URI, PlanXMLConstants.TAVERNA_SCHEMA_LOCATION);
        reader.setEntityResolver(schemaResolver);

        return reader.read(in);
    }
}
