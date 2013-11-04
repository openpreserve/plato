package eu.scape_project.planning.sla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.measurement.EvaluationScope;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.validation.ValidationError;
import eu.scape_project.watch.domain.DictionaryItem;
import eu.scape_project.watch.domain.Notification;
import eu.scape_project.watch.domain.Question;
import eu.scape_project.watch.domain.RequestTarget;
import eu.scape_project.watch.domain.Trigger;

/**
 * Generates trigger for preservation watch based on defined decision criteria. 
 * 
 * @author Michael Kraxner
 *
 */
public class TriggerGenerator {
    private List<Trigger> triggers;


    private long period;
    private String recipients;
    
    public TriggerGenerator() {
    	
    }
    
    public List<Trigger> generateTriggers(final String recipients, final long period, final Plan plan) {
    	triggers = new ArrayList<Trigger>();
    	
    	this.period = period;
    	this.recipients = recipients;
    	
        List<Leaf> leaves = plan.getTree().getRoot().getAllLeaves();
        List<ValidationError> errors = null;
        for (Leaf leaf : leaves) {
            if (leaf.isCompletelySpecified(errors) && leaf.isCompletelyTransformed(errors) && leaf.isMapped()) {
                if (EvaluationScope.ALTERNATIVE_ACTION == leaf.getMeasure().getAttribute().getCategory().getScope()) {
                    // generate trigger for preservation watch
                    addTrigger(leaf);
                }
            }
        }
    	return triggers;
    }
    
    private void addTrigger(Leaf leaf) {
    	Measure meas = leaf.getMeasure();
    	Question q = null;
        if (leaf.getTransformer() instanceof OrdinalTransformer) {
        	// TODO
        } else if (leaf.getTransformer() instanceof NumericTransformer){
            NumericTransformer numericT = (NumericTransformer)leaf.getTransformer();
            
            String name = meas.getName();
            String operator;
            if (numericT.hasIncreasingOrder()) {
            	// values must be greater or equal threshold 1, therefore we watch for smaller values
                operator = " < ";
            } else {
            	// values must be smaller or equal threshold 1, therefore we watch for higher values
                operator = " > ";
            }
            
	    	if ("http://purl.org/DP/quality/measures#11".equals(meas.getUri())) {
	    		// this value is accumulated, we check for min/max
	    		
	            if (numericT.hasIncreasingOrder()) {
	                name = "Minimum " + name;
	            } else {
	                name = "Maximum " + name;
	            }
	            String threshold = "" + numericT.getThreshold1();
	    		String sparql = String.format(
	    				"?p rdf:type watch:Property. " +
	    	    		"?p watch:name ?n . " +
	    	    		"?v watch:property ?p. " +
	    	    		"?v watch:floatValue ?fv. " +
	    	    		"FILTER (?n = \"%s\"  && ?fv %s %s ) " +
	    	    		" BIND(CONCAT(?n, \" is \", str(?fv) , \", should not be %s %s \") AS ?s )", 
	    	    		name, operator, threshold, operator, threshold);
	            
	    	    q = new Question(sparql, RequestTarget.PROPERTY_VALUE);
	    	}
        }
        if (q != null) {
    	    final Notification n = new Notification("email", Arrays.asList(new DictionaryItem("recepients",
  	    	      this.recipients)));
    	    final Trigger trigger = new Trigger(null, null, null, this.period, q, null,Arrays.asList(n));
    	    triggers.add(trigger);
        }
    }    

}
