package eu.scape_project.pw.planning.criteria.xml;

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
