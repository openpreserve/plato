package eu.scape_project.planning.services.pa.taverna;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;

public class SparqlResultComponentsParser {
	
	
	public void addComponentsFromSparqlResult(List<PreservationActionDefinition> components, Reader sparqlResult) throws PlatoException {
		
		
		SAXReader reader = new SAXReader();
		try {
			Document sparqlDoc = reader.read(sparqlResult);
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put( "sparql", "http://www.w3.org/2005/sparql-results#");
			  
			XPath xpath = DocumentHelper.createXPath("sparql:results/*");
			XPath selectDescriptorXpath = DocumentHelper.createXPath("sparql:binding[@name='w']/sparql:uri");
			XPath selectTitleXpath = DocumentHelper.createXPath("sparql:binding[@name='wt']/sparql:literal");
			XPath selectDescriptionXpath = DocumentHelper.createXPath("sparql:binding[@name='wdesc']/sparql:literal");
			XPath selectUrlXpath = DocumentHelper.createXPath("sparql:binding[@name='wurl']/sparql:uri");

			xpath.setNamespaceURIs(map);
			selectDescriptorXpath.setNamespaceURIs(map);
			selectTitleXpath.setNamespaceURIs(map);
			selectDescriptionXpath.setNamespaceURIs(map);
			selectUrlXpath.setNamespaceURIs(map);
			
			@SuppressWarnings("rawtypes")
			List componentsNodes = xpath.selectNodes(sparqlDoc.getRootElement());
			
			for (int i = 0; i < componentsNodes.size(); i++) {
				Element component = (Element)componentsNodes.get(i);
				PreservationActionDefinition def = new PreservationActionDefinition();

				def.setShortname(selectTitleXpath.selectSingleNode(component).getText());
				def.setUrl(selectUrlXpath.selectSingleNode(component).getText());
				def.setInfo(selectDescriptionXpath.selectSingleNode(component).getText());
				def.setDescriptor(selectDescriptorXpath.selectSingleNode(component).getText());
				
				components.add(def);
			}
		} catch (DocumentException e) {
			throw new PlatoException("An error occured while reading the sparql result: {}", e);
		}
	}
}
