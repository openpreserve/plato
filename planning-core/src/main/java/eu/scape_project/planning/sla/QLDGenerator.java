package eu.scape_project.planning.sla;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.measurement.EvaluationScope;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.validation.ValidationError;

public class QLDGenerator {

    private final static String CONTEXT_MEASURE = "measure[@type='${MEASURE}' and (@subject != 'inputFile')]";
    private final static Namespace SCHEMATRON_NS = new Namespace("", "http://purl.oclc.org/dsdl/schematron");

    private Document doc;
    private Element root;
    

    public QLDGenerator() {
    }
    
    public Document getQldNode() {
        return doc;
    }
    public String getQLDs(){
        OutputFormat prettyFormat = new OutputFormat(" ", true, "UTF-8");
        StringWriter qldWriter = new StringWriter();
        XMLWriter writer = new XMLWriter(qldWriter , prettyFormat);
        try {
            writer.write(doc);
        } catch (Exception e) {
            
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
        return qldWriter.toString();
        
    }

    /**
     * Generates triggers and QLDs based on decision criteria of the plan.
     *  
     * @param plan
     */
    public void generateQLD(Plan plan) {
    	
        doc = DocumentHelper.createDocument();
        root = doc.addElement("schema");
        root.clearContent();
        root.add(SCHEMATRON_NS);
        
        Element pattern = root.addElement(new QName("pattern", SCHEMATRON_NS));
        
        String ident = (null == plan.getPlanProperties().getRepositoryIdentifier()?"" : "("+plan.getPlanProperties().getRepositoryIdentifier()+")");
        pattern.addElement("title")
        .setText("QLDs based on plan " + plan.getPlanProperties().getName() + ident);
        List<Leaf> leaves = plan.getTree().getRoot().getAllLeaves();
        List<ValidationError> errors = null;
        for (Leaf leaf : leaves) {
            if (leaf.isCompletelySpecified(errors) && leaf.isCompletelyTransformed(errors) && leaf.isMapped()) {
                if (EvaluationScope.OBJECT == leaf.getMeasure().getAttribute().getCategory().getScope()) {
                    // generate QLD for Preservation Action Plan
                    addQLD(pattern, leaf);
                }
            }
        }

    }


    

    /**
     * Adds a QLD derived from the decision criteria of the given leaf to the given parent node.
     * 
     *  <schema xmlns="http://purl.oclc.org/dsdl/schematron">
     *      <pattern>
     *      <title>measures rules</title>
     *      <rule context="measure[@type='similarity']">
     *      	<assert test=". &gt; 0.87">Similarity score must be greater than 0.87</assert>
     *      </rule>
     *      </pattern>
     *  </schema>
     *  
     * @param parent
     * @param leaf
     */
    private void addQLD(Element parent, Leaf leaf) {
        // is this a drop out criteria?
        if (leaf.getTransformer() instanceof OrdinalTransformer) {
            //Element search for the unacceptable values (there can be more than one!)
            String explanation = leaf.getMeasure().getName() + " must have (one) of the the following values: [";
            String test = "";
            OrdinalTransformer ordinalT = (OrdinalTransformer)leaf.getTransformer();
            for (String key : ordinalT.getMapping().keySet()) {
                double value = ordinalT.getMapping().get(key).getValue();
                if (Double.compare(value, 0.0) <= 0) {
                    test = test + "(. != '" + key + "') and ";
                } else {
                    explanation = explanation + key + ", ";
                }
            }
            if (test.length() > 0) {
                test = test.substring(0, test.length() - 5);
                explanation = explanation.substring(0, explanation.length() - 2) + "]";
                    
                Element rule = parent.addElement("rule");
                rule.addAttribute("context", CONTEXT_MEASURE.replace("${MEASURE}", leaf.getMeasure().getUri()));
                Element assertEl = rule.addElement("assert");
                assertEl.addAttribute("test", test);
                assertEl.setText(explanation);
            }
           
        } else if (leaf.getTransformer() instanceof NumericTransformer){
            NumericTransformer numericT = (NumericTransformer)leaf.getTransformer();
            String explanation = leaf.getMeasure().getName() + " must be ";
            String operator;
            if (numericT.hasIncreasingOrder()) {
                explanation = explanation + " greater than or equal to " + numericT.getThreshold1(); 
                operator = " >= ";
            } else {
                explanation = explanation + " less than or equal to " + numericT.getThreshold1(); 
                operator = " <= ";                
            }
            String test = " . " + operator + " " + numericT.getThreshold1();

            Element rule = parent.addElement("rule");
            rule.addAttribute("context", CONTEXT_MEASURE.replace("${MEASURE}", leaf.getMeasure().getUri()));
            Element assertEl = rule.addElement("assert");
            assertEl.addAttribute("test", test);
            assertEl.setText(explanation);
        }
    }

}
