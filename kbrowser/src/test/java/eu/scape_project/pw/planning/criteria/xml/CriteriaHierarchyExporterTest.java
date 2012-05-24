package eu.scape_project.pw.planning.criteria.xml;

import junit.framework.TestSuite;

import org.custommonkey.xmlunit.XMLTestCase;

import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.measurement.Criterion;

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
		Criterion imageWidthEqualsCrit = new Criterion();
		imageWidthEqualsCrit.setUri("outcome://object/image/width#equal");
		leaf1.setCriterion(imageWidthEqualsCrit);
		leaf1.setMapped(true);
		CriteriaLeaf leaf2 = new CriteriaLeaf();
		leaf2.setName("leaf2");
		CriteriaLeaf leaf3 = new CriteriaLeaf();
		leaf3.setName("leaf3");
		Criterion textQualityCrit = new Criterion();
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
