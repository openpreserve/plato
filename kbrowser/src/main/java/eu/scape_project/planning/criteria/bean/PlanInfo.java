package eu.scape_project.planning.criteria.bean;

import java.io.Serializable;

import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.beans.ResultNode;

/**
 * Holds information on the plan - plan id - overall results for each
 * alternative (so they need to be calculated only once).
 * 
 * @author Michael Kraxner
 */
public class PlanInfo implements Serializable {
    private static final long serialVersionUID = -1857618679835370437L;

    private int id;
    private String winningAlternative;
    private double winningResult;

    private PlanProperties planProperties;

    /**
     * Holds the evaluation results for all alternatives.
     * 
     */
    private ResultNode overallResults;

    /**
     * Empty constructor.
     */
    public PlanInfo() {
    }

    /**
     * Creates a new PlanInfo object with the provided parameters.
     * 
     * @param id
     *            the plan id
     * @param results
     *            results of the plan
     * @param planProperties
     *            plan properties
     */
    public PlanInfo(final int id, final ResultNode results, final PlanProperties planProperties) {
        this.id = id;
        this.overallResults = results;
        this.planProperties = planProperties;

        // determine the winning alternative
        double maxResult = Double.MIN_VALUE;
        for (String a : results.getResults().keySet()) {
            double result = results.getResults().get(a);
            if (result > maxResult) {
                maxResult = result;
                winningResult = maxResult;
                winningAlternative = a;
            }
        }

    }

    // ---------- getter/setter ----------
    public String getWinningAlternative() {
        return winningAlternative;
    }

    public double getWinningResult() {
        return winningResult;
    }

    public int getId() {
        return id;
    }

    public ResultNode getOverallResults() {
        return overallResults;
    }

    public PlanProperties getPlanProperties() {
        return planProperties;
    }

}
