package eu.scape_project.planning.criteria.bean;

import java.io.Serializable;

import eu.scape_project.planning.model.beans.ResultNode;

/**
 * Holds information on the plan
 * - plan id
 * - overall results for each alternative (so they need to be calculated only once)  
 * 
 * @author Michael Kraxner
 */
public class PlanInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private int id;
    /**
     * Holds the evaluation results for all alternatives
     * 
     */
    private ResultNode overallResults;
    
    public PlanInfo(){
        
    }
    public PlanInfo(final int id, final ResultNode results){
        this.id = id;
        this.overallResults = results;
    }
    
    public int getId() {
        return id;
    }
    public ResultNode getOverallResults() {
        return overallResults;
    }
    
    

}
