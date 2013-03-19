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
/**
 * 
 */
package eu.scape_project.planning.plato.wf;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.aggregators.WeightedSum;
import eu.scape_project.planning.model.beans.ResultNode;
import eu.scape_project.planning.model.sensitivity.OrderChangeCountTest;
import eu.scape_project.planning.model.sensitivity.SimpleIterativeWeightModifier;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.plato.wfview.beans.ReportLeaf;

/**
 * Class containing business logic for workflow-step AnalyseResults.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@Stateful
@ConversationScoped
public class AnalyseResults extends AbstractWorkflowStep {

	private static final long serialVersionUID = -756737838773396705L;
	
	@Inject private Logger log;
		
	public AnalyseResults() {
		this.requiredPlanState = PlanState.WEIGHTS_SET;
		this.correspondingPlanState = PlanState.ANALYSED;
	}

	@Override
	protected void saveStepSpecific() {
		prepareChangesForPersist.prepare(plan);
		
		saveEntity(plan.getRecommendation());
	}
	
    /**
     * Method responsible for retrieving a copy of a previously uploaded sample object including its data.
     * 
     * @param sampleObject SampleObject(=extended DigitalObject) stored in file system for which the data should be retrieved.
     * @return Copy of the given SampleObject(as DigitalObject) including its data.
     * @throws StorageException is thrown if any error occurs at retrieving the result file.
     */
	public DigitalObject fetchSampleObject(SampleObject sampleObject) throws StorageException {
		return digitalObjectManager.getCopyOfDataFilledDigitalObject(sampleObject);
	}
	
	/**
	 * Method responsible for constructing report leaves for the plans tree.
	 * 
	 * @return List of plans report-leaves.
	 */
	public List<ReportLeaf> constructPlanReportLeaves() {
		List<ReportLeaf> leafBeans = new ArrayList<ReportLeaf>();
		
        for (Leaf l : plan.getTree().getRoot().getAllLeaves()) {
            leafBeans.add(new ReportLeaf(l, plan.getAlternativesDefinition().getConsideredAlternatives()));
            // TODO: Check if this statement can be removed
            l.initTransformer();
        }
        
        return leafBeans;
	}
	
	/**
	 * Method responsible for performing sensitivity analysis on the given result-tree.
	 * 
	 * @param rootNode Root ResultNode of the tree to analyze.
	 * @param alternatives Alternatives to include in the analysis.
	 */
	public void analyseSensitivity(ResultNode rootNode, List<Alternative> alternatives) {
        long start = System.currentTimeMillis();
        
        // FIXME HK reintroduce SENSITIVITY analysis for large trees - Plato 3.1
        if (plan.getTree().getRoot().getAllLeaves().size() < 40) {
            log.debug("Starting sensitivity analysis ... " );
            rootNode.analyseSensitivity(
                    new SimpleIterativeWeightModifier(),
                    new OrderChangeCountTest(plan.getTree().getRoot(),
                    new WeightedSum(), alternatives));
            log.debug("Sensitivity analysis took: " + (System.currentTimeMillis() - start) + "ms.");
        } else {
            log.debug("Sensitivity analysis NOT CONDUCTED: Too many leaves.");
        }
	}
	
	/**
	 * Method responsible for setting the recommended alternative.
	 * 
	 * @param recommendedAlternative Recommended alternative.
	 */
	public void recommendAlternative(Alternative recommendedAlternative) {
		plan.getRecommendation().setAlternative(recommendedAlternative);
		plan.getRecommendation().touch();
	}
}
