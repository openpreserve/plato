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
package eu.scape_project.planning.criteria.xml;

import java.io.Serializable;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.kbrowser.CriteriaLeaf;
import eu.scape_project.planning.model.kbrowser.CriteriaNode;
import eu.scape_project.planning.model.kbrowser.CriteriaTreeNode;

/**
 * Method responsible for exporting CriteriaHierarchies.
 * 
 * @author Markus Hamm
 */
public class CriteriaHierarchyExporter implements Serializable {
	private static final long serialVersionUID = -2563221795308771643L;

	/**
	 * Method responsible for exporting a CriteriaHierarchy to freemind-xml format.
	 * 
	 * @param criteriaHierarchy CriteriaHierarchy to export.
	 * @return freemind-xml String.
	 */
	public String exportToFreemindXml(CriteriaHierarchy criteriaHierarchy) {
		return exportToFreemindXml(criteriaHierarchy.getCriteriaTreeRoot());
	}
	    
	/**
	 * Method responsible for exporting a CriteriaHierarchy-TreeNode to freemind-xml format.
	 * 
	 * @param criteriaTreeNode CriteriaHierarchy-Treenode to export
	 * @return freemind-xml String.
	 */
    private String exportToFreemindXml(CriteriaTreeNode criteriaTreeNode) {
        Document doc = DocumentHelper.createDocument();
        doc.setXMLEncoding("UTF-8");
        
        Element root = doc.addElement("map");
        Namespace xsi = new Namespace("xsi",  "http://www.w3.org/2001/XMLSchema-instance");

        root.add(xsi);
        root.addAttribute("version","0.8.1");

        root.addComment("To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net");
        addSubTreeFreemind(root, criteriaTreeNode);
        
        String xml =  doc.asXML();

        return xml;
    }

    /**
     * Method responsible for attaching the freemind-xml representation of the given CriteriaHierarchy-TreeNode to the given xml-element.
     * @param xmlElement Xml-element to attach freemind-xml to
     * @param criteriaTreeNode CriteriaHierarchy-TreeNode to convert and attach.
     */
    private void addSubTreeFreemind(Element xmlElement, CriteriaTreeNode criteriaTreeNode) {
        Element element = xmlElement.addElement("node");

        // LEAF
        if (criteriaTreeNode.getLeaf()) {
        	CriteriaLeaf leaf = (CriteriaLeaf) criteriaTreeNode;
        	String leafText = "";
        	
        	// mapped leaf
        	if (leaf.getMapped() && leaf.getCriterion() != null) {
        		leafText = leaf.getName() + "|" + leaf.getCriterion().getUri();
        	}
        	// unmapped leaf
        	else {
        		leafText = leaf.getName();
        	}
        	
        	element.addAttribute("TEXT", leafText);
        }
        // NODE
        else {
        	CriteriaNode node = (CriteriaNode) criteriaTreeNode;
        	element.addAttribute("TEXT", node.getName());
        	
        	// add children
            for (CriteriaTreeNode child : node.getChildren()) {
                addSubTreeFreemind(element, child);
            }
        }
    }
}
