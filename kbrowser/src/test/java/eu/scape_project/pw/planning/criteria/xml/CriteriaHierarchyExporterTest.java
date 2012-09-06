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
package eu.scape_project.pw.planning.criteria.xml;

import junit.framework.TestSuite;

import org.custommonkey.xmlunit.XMLTestCase;

import eu.scape_project.planning.criteria.xml.CriteriaHierarchyExporter;
import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.measurement.Measure;

public class CriteriaHierarchyExporterTest extends XMLTestCase {	
	public CriteriaHierarchyExporterTest(String name) {
		super(name);
	}
	
	public static TestSuite suite() {
		return new TestSuite(CriteriaHierarchyExporterTest.class);
	}
		
	public void testExportToFreemindXml_noCriterionMapped() throws Exception {
		// create criteria-hierarchy tree
		CriteriaLeaf leaf1 = new CriteriaLeaf();
		leaf1.setName("leaf1");
		CriteriaLeaf leaf2 = new CriteriaLeaf();
		leaf2.setName("leaf2");
		CriteriaLeaf leaf3 = new CriteriaLeaf();
		leaf3.setName("leaf3");
		
		CriteriaNode subNode = new CriteriaNode();
		subNode.setName("subNode");
		subNode.addChild(leaf1);
		subNode.addChild(leaf2);
		
		CriteriaNode rootNode = new CriteriaNode();
		rootNode.setName("rootNode");
		rootNode.addChild(subNode);
		rootNode.addChild(leaf3);
		
		// create criteria-hierarchy
		CriteriaHierarchy cHierarchy = new CriteriaHierarchy();
		cHierarchy.setName("hierarchy");
		cHierarchy.setCriteriaTreeRoot(rootNode);
		
		CriteriaHierarchyExporter criteriaHierarchyExporter = new CriteriaHierarchyExporter();
		String exportedXml = criteriaHierarchyExporter.exportToFreemindXml(cHierarchy);
		
		String expectedXml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<map version=\"0.8.1\">" +
				"<!--To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net-->" + 
				"<node TEXT=\"rootNode\">" +
				"<node TEXT=\"subNode\">" + 
				"<node TEXT=\"leaf1\"/>" + 
				"<node TEXT=\"leaf2\"/>" + 
				"</node>" +
				"<node TEXT=\"leaf3\"/>" +
				"</node>" +
				"</map>";
		
		assertXMLEqual(expectedXml, exportedXml);
	}

	public void testExportToFreemindXml_severalMappedCriteria() throws Exception {
		// create criteria-hierarchy tree
		CriteriaLeaf leaf1 = new CriteriaLeaf();
		leaf1.setName("leaf1");
		Measure imageWidthEqualsCrit = new Measure();
		imageWidthEqualsCrit.setUri("outcome://object/image/width#equal");
		leaf1.setCriterion(imageWidthEqualsCrit);
		leaf1.setMapped(true);
		CriteriaLeaf leaf2 = new CriteriaLeaf();
		leaf2.setName("leaf2");
		CriteriaLeaf leaf3 = new CriteriaLeaf();
		leaf3.setName("leaf3");
		Measure textQualityCrit = new Measure();
		textQualityCrit.setUri("outcome://object/image/textQuality");
		leaf3.setCriterion(textQualityCrit);
		leaf3.setMapped(true);
		
		CriteriaNode subNode = new CriteriaNode();
		subNode.setName("subNode");
		subNode.addChild(leaf1);
		subNode.addChild(leaf2);
		
		CriteriaNode rootNode = new CriteriaNode();
		rootNode.setName("rootNode");
		rootNode.addChild(subNode);
		rootNode.addChild(leaf3);
		
		// create criteria-hierarchy
		CriteriaHierarchy cHierarchy = new CriteriaHierarchy();
		cHierarchy.setName("hierarchy");
		cHierarchy.setCriteriaTreeRoot(rootNode);
		
		CriteriaHierarchyExporter criteriaHierarchyExporter = new CriteriaHierarchyExporter();
		String exportedXml = criteriaHierarchyExporter.exportToFreemindXml(cHierarchy);
		
		String expectedXml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<map version=\"0.8.1\">" +
				"<!--To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net-->" + 
				"<node TEXT=\"rootNode\">" +
				"<node TEXT=\"subNode\">" + 
				"<node TEXT=\"leaf1|outcome://object/image/width#equal\"/>" + 
				"<node TEXT=\"leaf2\"/>" + 
				"</node>" +
				"<node TEXT=\"leaf3|outcome://object/image/textQuality\"/>" +
				"</node>" +
				"</map>";
		
		assertXMLEqual(expectedXml, exportedXml);
	}
	
	public void testExportToFreemindXml_setMappingWithNullCriterionIsIgnored() throws Exception {
		// create criteria-hierarchy tree
		CriteriaLeaf leaf1 = new CriteriaLeaf();
		leaf1.setName("leaf1");
		leaf1.setMapped(true);
		leaf1.setCriterion(null);
		
		CriteriaNode rootNode = new CriteriaNode();
		rootNode.setName("rootNode");
		rootNode.addChild(leaf1);

		// create criteria-hierarchy
		CriteriaHierarchy cHierarchy = new CriteriaHierarchy();
		cHierarchy.setName("hierarchy");
		cHierarchy.setCriteriaTreeRoot(rootNode);
		
		CriteriaHierarchyExporter criteriaHierarchyExporter = new CriteriaHierarchyExporter();
		String exportedXml = criteriaHierarchyExporter.exportToFreemindXml(cHierarchy);
		
		String expectedXml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<map version=\"0.8.1\">" +
				"<!--To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net-->" + 
				"<node TEXT=\"rootNode\">" +
				"<node TEXT=\"leaf1\"/>" + 
				"</node>" +
				"</map>";
		
		assertXMLEqual(expectedXml, exportedXml);
	}
}
