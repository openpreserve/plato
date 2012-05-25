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
package eu.scape_project.planning.plato.wfview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.fte.*;
import eu.scape_project.planning.plato.wfview.full.*;

/**
 * Factory class responsible for creating ViewWorkflow-steps.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
public class ViewWorkflowFactory implements Serializable {
	private static final long serialVersionUID = 1031838310289177775L;

	/**
	 *  Note: We only retrieve handles to the WF step beans, to create them later 
	 */
	@Inject	private Instance<DefineBasisView> defineBasisSource;
	@Inject private Instance<DefineSampleRecordsView> defineSampleRecordsSource;
	@Inject private Instance<IdentifyRequirementsView> identifyRequirementsSource;
	@Inject private Instance<DefineAlternativesView> defineAlternativesSource;
	@Inject private Instance<TakeGoDecisionView> takeGoDecisionSource;
	@Inject private Instance<DevelopExperimentsView> developExperimentsSource;
	@Inject private Instance<RunExperimentsView> runExperimentsSource;
	@Inject private Instance<EvaluateExperimentsView> evaluateExperimentsSource;
	@Inject private Instance<TransformMeasuredValuesView> transformMeasuredValuesSource;
	@Inject private Instance<SetImportanceFactorsView> setImportanceFactorsSource;
	@Inject private Instance<AnalyseResultsView> analyseResultsSource;
	@Inject private Instance<CreateExecutablePlanView> createExecutablePlanSource;
	@Inject private Instance<DefinePreservationPlanView> definePreservationPlanSource;
	@Inject private Instance<ValidatePlanView> validatePlanSource;
	
	@Inject private Instance<FTDefineRequirementsView> ftDefineRequirementsSource;
	@Inject private Instance<FTEvaluateAlternativesView> ftEvaluateAlternativesSource;
	@Inject private Instance<FTAnalyseResultsView> ftAnalyseResultsSource;
	
	/**
	 * Method responsible for constructing the appropriate viewWorkflow-steps for a given plan.
	 * 
	 * @param plan Plan to construct the viewWorkflow-steps for
	 * @return List of viewWorkflow-steps.
	 */
	public List<AbstractView> constructWorkflowSteps(Plan plan) {
		List<AbstractView> result = new ArrayList<AbstractView>();
		
		if (plan.isFastTrackEvaluationPlan()) {
			result.add(ftDefineRequirementsSource.get());
			result.add(ftEvaluateAlternativesSource.get());
			result.add(ftAnalyseResultsSource.get());
		} else {
			result.add(defineBasisSource.get());
			result.add(defineSampleRecordsSource.get());
			result.add(identifyRequirementsSource.get());
			result.add(defineAlternativesSource.get());
			result.add(takeGoDecisionSource.get());
			result.add(developExperimentsSource.get());
			result.add(runExperimentsSource.get());
			result.add(evaluateExperimentsSource.get());
			result.add(transformMeasuredValuesSource.get());
			result.add(setImportanceFactorsSource.get());
			result.add(analyseResultsSource.get());
			result.add(createExecutablePlanSource.get());
			result.add(definePreservationPlanSource.get());
			result.add(validatePlanSource.get());			
		}
		
		return result;
	}
	
	// --------------- getter/setter ---------------
	
	public Instance<DefineBasisView> getDefineBasisSource() {
		return defineBasisSource;
	}

	public void setDefineBasisSource(Instance<DefineBasisView> defineBasisSource) {
		this.defineBasisSource = defineBasisSource;
	}

	public Instance<DefineSampleRecordsView> getDefineSampleRecordsSource() {
		return defineSampleRecordsSource;
	}

	public void setDefineSampleRecordsSource(
			Instance<DefineSampleRecordsView> defineSampleRecordsSource) {
		this.defineSampleRecordsSource = defineSampleRecordsSource;
	}

	public Instance<IdentifyRequirementsView> getIdentifyRequirementsSource() {
		return identifyRequirementsSource;
	}

	public void setIdentifyRequirementsSource(
			Instance<IdentifyRequirementsView> identifyRequirementsSource) {
		this.identifyRequirementsSource = identifyRequirementsSource;
	}

	public Instance<DefineAlternativesView> getDefineAlternativesSource() {
		return defineAlternativesSource;
	}

	public void setDefineAlternativesSource(
			Instance<DefineAlternativesView> defineAlternativesSource) {
		this.defineAlternativesSource = defineAlternativesSource;
	}

	public Instance<TakeGoDecisionView> getTakeGoDecisionSource() {
		return takeGoDecisionSource;
	}

	public void setTakeGoDecisionSource(
			Instance<TakeGoDecisionView> takeGoDecisionSource) {
		this.takeGoDecisionSource = takeGoDecisionSource;
	}

	public Instance<DevelopExperimentsView> getDevelopExperimentsSource() {
		return developExperimentsSource;
	}

	public void setDevelopExperimentsSource(
			Instance<DevelopExperimentsView> developExperimentsSource) {
		this.developExperimentsSource = developExperimentsSource;
	}

	public Instance<RunExperimentsView> getRunExperimentsSource() {
		return runExperimentsSource;
	}

	public void setRunExperimentsSource(
			Instance<RunExperimentsView> runExperimentsSource) {
		this.runExperimentsSource = runExperimentsSource;
	}

	public Instance<EvaluateExperimentsView> getEvaluateExperimentsSource() {
		return evaluateExperimentsSource;
	}

	public void setEvaluateExperimentsSource(
			Instance<EvaluateExperimentsView> evaluateExperimentsSource) {
		this.evaluateExperimentsSource = evaluateExperimentsSource;
	}

	public Instance<TransformMeasuredValuesView> getTransformMeasuredValuesSource() {
		return transformMeasuredValuesSource;
	}

	public void setTransformMeasuredValuesSource(
			Instance<TransformMeasuredValuesView> transformMeasuredValuesSource) {
		this.transformMeasuredValuesSource = transformMeasuredValuesSource;
	}

	public Instance<SetImportanceFactorsView> getSetImportanceFactorsSource() {
		return setImportanceFactorsSource;
	}

	public void setSetImportanceFactorsSource(
			Instance<SetImportanceFactorsView> setImportanceFactorsSource) {
		this.setImportanceFactorsSource = setImportanceFactorsSource;
	}

	public Instance<AnalyseResultsView> getAnalyseResultsSource() {
		return analyseResultsSource;
	}

	public void setAnalyseResultsSource(
			Instance<AnalyseResultsView> analyseResultsSource) {
		this.analyseResultsSource = analyseResultsSource;
	}

	public Instance<CreateExecutablePlanView> getCreateExecutablePlanSource() {
		return createExecutablePlanSource;
	}

	public void setCreateExecutablePlanSource(
			Instance<CreateExecutablePlanView> createExecutablePlanSource) {
		this.createExecutablePlanSource = createExecutablePlanSource;
	}

	public Instance<DefinePreservationPlanView> getDefinePreservationPlanSource() {
		return definePreservationPlanSource;
	}

	public void setDefinePreservationPlanSource(
			Instance<DefinePreservationPlanView> definePreservationPlanSource) {
		this.definePreservationPlanSource = definePreservationPlanSource;
	}
	
	public Instance<ValidatePlanView> getValidatePlanSource() {
		return validatePlanSource;
	}

	public void setValidatePlanSource(Instance<ValidatePlanView> validatePlanSource) {
		this.validatePlanSource = validatePlanSource;
	}

	public Instance<FTDefineRequirementsView> getFtDefineRequirementsSource() {
		return ftDefineRequirementsSource;
	}

	public void setFtDefineRequirementsSource(
			Instance<FTDefineRequirementsView> ftDefineRequirementsSource) {
		this.ftDefineRequirementsSource = ftDefineRequirementsSource;
	}

	public Instance<FTEvaluateAlternativesView> getFtEvaluateAlternativesSource() {
		return ftEvaluateAlternativesSource;
	}

	public void setFtEvaluateAlternativesSource(
			Instance<FTEvaluateAlternativesView> ftEvaluateAlternativesSource) {
		this.ftEvaluateAlternativesSource = ftEvaluateAlternativesSource;
	}

	public Instance<FTAnalyseResultsView> getFtAnalyseResultsSource() {
		return ftAnalyseResultsSource;
	}

	public void setFtAnalyseResultsSource(
			Instance<FTAnalyseResultsView> ftAnalyseResultsSource) {
		this.ftAnalyseResultsSource = ftAnalyseResultsSource;
	}	
}
