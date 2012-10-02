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
package eu.scape_project.planning.plato.fte;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.AnalyseResults;
import eu.scape_project.planning.plato.wfview.beans.ReportLeaf;

/**
 * @author Markus Hamm
 */
@Stateful
@ConversationScoped
public class FTAnalyseResults extends AbstractWorkflowStep {
    private static final long serialVersionUID = 6356595757974848875L;

    // BL of full wf-steps
    @Inject
    AnalyseResults analyseResults;

    public FTAnalyseResults() {
        this.requiredPlanState = PlanState.RESULTS_CAPTURED;
        this.correspondingPlanState = PlanState.ANALYSED;
    }

    @Override
    public void init(Plan p) {
        super.init(p);

        // map unmapped text-transformer values to neutral 2.5
        initTextTransformer(new Double(2.5));

        // Init correspoding wf-step BL beans (this is mandatory to be able to
        // call them later)
        analyseResults.init(p);
    }

    /**
     * Method delegating the BL execution to AnalyseResults.
     * 
     * @see AnalyseResults#constructPlanReportLeaves()
     */
    public List<ReportLeaf> constructPlanReportLeaves() {
        return analyseResults.constructPlanReportLeaves();
    }

    /**
     * Method delegating the BL execution to AnalyseResults.
     * 
     * @see AnalyseResults#getAggregatedMultiplicationResultNode()
     */
    public ResultNode getAggregatedMultiplicationResultNode() {
        return analyseResults.getAggregatedMultiplicationResultNode();
    }

    /**
     * Method delegating the BL execution to AnalyseResults.
     * 
     * @see AnalyseResults#getAggregatedSumResultNode()
     */
    public ResultNode getAggregatedSumResultNode() {
        return analyseResults.getAggregatedSumResultNode();
    }

    /**
     * Method delegating the BL execution to AnalyseResults.
     * 
     * @see AnalyseResults#getAcceptableAlternatives()
     */
    public List<Alternative> getAcceptableAlternatives() {
        return analyseResults.getAcceptableAlternatives();
    }

    /**
     * Method responsible for creating a standard preservation plan out of this
     * fast track preservation plan.
     */
    public void transformToStandardPreservationPlan() {
        plan.createPPFromFastTrack();
        plan.getPlanProperties().setState(PlanState.INITIALISED);
    }

    @Override
    protected void saveStepSpecific() {
        analyseResults.saveWithoutModifyingPlanState();

        // set plan state to the completed one if an alternative is given by the
        // user
        // because this last fasttrack step it supports no proceed. Thus if an
        // alternative is set - it is done.
        if (plan.getRecommendation().getAlternative() != null) {
            plan.getPlanProperties().setState(this.correspondingPlanState);
        }
        saveEntity(plan.getPlanProperties());
    }

    /**
     * Method responsible for initializing free text transformer values AND to
     * map unmapped values to the given unmappedResultValue. (This is necessary
     * because in fast track evaluation the user is not able to edit the
     * transformer mappings by himself.
     * 
     * @param unmappedResultValue
     */
    private void initTextTransformer(Double unmappedResultValue) {
        for (Leaf l : plan.getTree().getRoot().getAllLeaves()) {
            l.initTransformer(unmappedResultValue);
        }
    }
}
