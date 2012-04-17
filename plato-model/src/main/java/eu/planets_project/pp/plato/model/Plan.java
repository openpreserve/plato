/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import eu.planets_project.pp.plato.exception.PlanningException;
import eu.planets_project.pp.plato.model.aggregators.WeightedMultiplication;
import eu.planets_project.pp.plato.model.beans.ResultNode;
import eu.planets_project.pp.plato.model.measurement.MeasurableProperty;
import eu.planets_project.pp.plato.model.measurement.Measurement;
import eu.planets_project.pp.plato.model.scales.BooleanScale;
import eu.planets_project.pp.plato.model.transform.NumericTransformer;
import eu.planets_project.pp.plato.model.transform.OrdinalTransformer;
import eu.planets_project.pp.plato.model.transform.TransformationMode;
import eu.planets_project.pp.plato.model.transform.Transformer;
import eu.planets_project.pp.plato.model.tree.Leaf;
import eu.planets_project.pp.plato.model.tree.Node;
import eu.planets_project.pp.plato.model.tree.ObjectiveTree;
import eu.planets_project.pp.plato.model.tree.TreeNode;
import eu.planets_project.pp.plato.model.values.INumericValue;
import eu.planets_project.pp.plato.model.values.Value;

/**
 * This is a preservation planning project, the root class of all domain model
 * data. Please refer to the terminology in deliverable PP4-D1 for an
 * explanation of terms.
 * 
 * @author Christoph Becker
 * 
 */
@Entity
public class Plan implements Serializable, ITouchable {

	private static final long serialVersionUID = 7855716962826361459L;

	public static final String fastTrackEvaluationPrefix = "FAST-TRACK-";

	/**
	 * the Go/No-Go-decision
	 */
	@OneToOne(cascade = CascadeType.ALL)
	private Decision decision = new Decision();

	@Id
	@GeneratedValue
	private int id;
	
	@OneToOne(cascade = CascadeType.ALL)
	private PlanProperties planProperties = new PlanProperties();

	@OneToOne(cascade = CascadeType.ALL)
	private SampleRecordsDefinition sampleRecordsDefinition = new SampleRecordsDefinition();

	@OneToOne(cascade = CascadeType.ALL)
	private ProjectBasis projectBasis = new ProjectBasis();

	@OneToOne(cascade = CascadeType.ALL)
	private RequirementsDefinition requirementsDefinition = new RequirementsDefinition();

	@Valid
	@OneToOne(cascade = CascadeType.ALL)
	private ObjectiveTree tree = new ObjectiveTree();

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "alternativesdefinition_id")
	private AlternativesDefinition alternativesDefinition = new AlternativesDefinition();

	@OneToOne(cascade = CascadeType.ALL)
	private Evaluation evaluation = new Evaluation();

	@OneToOne(cascade = CascadeType.ALL)
	private Transformation transformation = new Transformation();

	@OneToOne(cascade = CascadeType.ALL)
	private ImportanceWeighting importanceWeighting = new ImportanceWeighting();

	@OneToOne(cascade = CascadeType.ALL)
	private Recommendation recommendation = new Recommendation();

	@OneToOne(cascade = CascadeType.ALL)
	private ExecutablePlanDefinition executablePlanDefinition = new ExecutablePlanDefinition();

	@OneToOne(cascade = CascadeType.ALL)
	private PlanDefinition planDefinition = new PlanDefinition();

	@OneToOne(cascade = CascadeType.ALL)
	private ChangeLog changeLog = new ChangeLog();
	
	public Plan(){
        TreeNode root = new Node();
        root.setName("Root");
        getTree().setRoot(root);		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public String getStateName() {
		return this.getPlanProperties().getState().getName();
	}

	public ObjectiveTree getTree() {
		return tree;
	}

	public void setTree(ObjectiveTree tree) {
		this.tree = tree;
	}

	public Decision getDecision() {
		return decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	public SampleRecordsDefinition getSampleRecordsDefinition() {
		return sampleRecordsDefinition;
	}

	public void setSampleRecordsDefinition(SampleRecordsDefinition sampleRecords) {
		this.sampleRecordsDefinition = sampleRecords;
	}

	public PlanProperties getPlanProperties() {
		return planProperties;
	}

	public void setPlanProperties(PlanProperties planProperties) {
		this.planProperties = planProperties;
	}

	public ProjectBasis getProjectBasis() {
		return projectBasis;
	}

	public void setProjectBasis(ProjectBasis projectBasis) {
		this.projectBasis = projectBasis;
	}

	public ExecutablePlanDefinition getExecutablePlanDefinition() {
		return executablePlanDefinition;
	}

	public void setPlanDefinition(PlanDefinition planDefinition) {
		this.planDefinition = planDefinition;
	}

	public PlanDefinition getPlanDefinition() {
		return planDefinition;
	}

	public void setExecutablePlanDefinition(
			ExecutablePlanDefinition executablePlanDefinition) {
		this.executablePlanDefinition = executablePlanDefinition;
	}

	public AlternativesDefinition getAlternativesDefinition() {
		return alternativesDefinition;
	}

	public void setAlternativesDefinition(
			AlternativesDefinition alternativesDefinition) {
		this.alternativesDefinition = alternativesDefinition;
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public Transformation getTransformation() {
		return transformation;
	}

	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	public ImportanceWeighting getImportanceWeighting() {
		return importanceWeighting;
	}

	public void setImportanceWeighting(ImportanceWeighting importanceWeighting) {
		this.importanceWeighting = importanceWeighting;
	}

	public ChangeLog getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(ChangeLog value) {
		changeLog = value;
	}

	public boolean isChanged() {
		return changeLog.isAltered();
	}

	public void touch() {
		changeLog.touch();
	}

	/**
	 * @see ITouchable#handleChanges(IChangesHandler)
	 */
	public void handleChanges(IChangesHandler h) {
		h.visit(this);
		// call handleChanges of all child elements
		alternativesDefinition.handleChanges(h);
		if (decision != null)
			decision.handleChanges(h);
		evaluation.handleChanges(h);
		importanceWeighting.handleChanges(h);
		projectBasis.handleChanges(h);
		planProperties.handleChanges(h);
		recommendation.handleChanges(h);
		requirementsDefinition.handleChanges(h);
		sampleRecordsDefinition.handleChanges(h);
		transformation.handleChanges(h);
		executablePlanDefinition.handleChanges(h);
		planDefinition.handleChanges(h);
		tree.getRoot().handleChanges(h);
	}

	public RequirementsDefinition getRequirementsDefinition() {
		return requirementsDefinition;
	}

	public void setRequirementsDefinition(
			RequirementsDefinition requirementsDefinition) {
		this.requirementsDefinition = requirementsDefinition;
	}

	public Recommendation getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(Recommendation recommendation) {
		this.recommendation = recommendation;
	}

	/**
	 * removes a sample object and its associated result files and values
	 * 
	 * @param record
	 *            SampleObject
	 */
	public void removeSampleObject(SampleObject record) {
		int index = getSampleRecordsDefinition().getRecords().indexOf(record);
		getSampleRecordsDefinition().removeRecord(record);

		getTree().removeValues(getAlternativesDefinition().getAlternatives(),
				index);
		// this SampleRecordsDefinition has been changed
		getSampleRecordsDefinition().touch();

		for (Alternative alt : getAlternativesDefinition().getAlternatives()) {
			alt.getExperiment().getResults().remove(record);
			alt.getExperiment().getDetailedInfo().remove(record);
		}

		getAlternativesDefinition().touch();
	}

    /**
     * Adds an alternative.
     * 
     * @param alternative Alternative to add.
     * @throws PlanningException if an error at adding occurs (e.g. want to add an Alternative with an already existing name).
     */
    public void addAlternative(Alternative alternative) throws PlanningException {
    	alternativesDefinition.addAlternative(alternative);
    	alternativesDefinition.touch();
    }
	
    /**
     * Removes an alternative AND also removes all associated evaluation values contained in the tree, if there are any.
     * 
     * @param alternative alternative to remove
     */
    public void removeAlternative(Alternative alternative) {
        if (recommendation.getAlternative() == alternative) {
        	recommendation.setAlternative(null);
        	recommendation.setReasoning("");
        	recommendation.setEffects("");
        }
        
        alternativesDefinition.removeAlternative(alternative);
        alternativesDefinition.touch();
        
        tree.removeValues(alternative);
    }
    
    /**
     * Method responsible for renaming an alternative (including all necessary follow-up actions).
     * 
     * @param alternative Alternative to rename
     * @param newName New name of the alternative
     * @throws PlanningException This exception is thrown at any kind of problems at renaming.
     */
    public void renameAlternative(Alternative alternative, String newName) throws PlanningException {
    	// renaming only makes sense if the name really changed
    	if (alternative.getName().equals(newName)) {
    		return;
    	}
    	
    	String oldName = alternative.getName();
    	
    	// rename the alternative
    	alternativesDefinition.renameAlternative(alternative, newName);
    	
    	// also update the alternative names in the tree    		
    	tree.updateAlternativeName(oldName, newName);
    }
            
	public List<MeasurableProperty> getMeasurableProperties() {
		List<MeasurableProperty> props = new ArrayList<MeasurableProperty>();
		for (Alternative alternative : alternativesDefinition
				.getConsideredAlternatives()) {
			Experiment exp = alternative.getExperiment();
			for (SampleObject record : sampleRecordsDefinition.getRecords()) {
				// is there any migration-metadata info?
				DetailedExperimentInfo info = exp.getDetailedInfo().get(record);
				if (info != null) {
					for (Measurement m : info.getMeasurements().values()) {
						if (!props.contains(m.getProperty()))
							props.add(m.getProperty());
					}
				}
			}
		}
		Collections.sort(props);
		return props;
	}

	public boolean isFastTrackEvaluationPlan() {
		if (projectBasis.getIdentificationCode() == null) {
			return false;
		}

		return projectBasis.getIdentificationCode().startsWith(
				Plan.fastTrackEvaluationPrefix);
	}

	public void createPPFromFastTrack() {
		String identifcation = projectBasis.getIdentificationCode();

		int index = identifcation.indexOf(Plan.fastTrackEvaluationPrefix);

		// that's not a fast track plan, identification code of a fast track
		// plan starts with
		// Plan.fastTrackEvaluationPrefix
		if (index != 0) {
			return;
		}

		String newIdentificationCode = identifcation
				.substring(Plan.fastTrackEvaluationPrefix.length());

		projectBasis.setIdentificationCode(newIdentificationCode);
	}

	/**
	 * sets primitive default values for all numeric and boolean transformers.
	 * This is a minimalist approach for now, where we can plug in more
	 * sophisticated heuristics in the future. Will on the other hand be less
	 * necessary when we introduce property-specific transformers stored in the
	 * knowledge base.
	 */
	public void calculateDefaultTransformers() {
		for (Leaf leaf : tree.getRoot().getAllLeaves()) {
			Transformer t = leaf.getTransformer();
			if (t instanceof NumericTransformer) {
				// calculate min, max
				// set min,max
				NumericTransformer nt = (NumericTransformer) t;

				// A very specific assumption: the lower the better (!)
				// obviously often not true, e.g. for format/numberOfTools

				double min = Long.MAX_VALUE;
				double max = Long.MIN_VALUE;

				for (Alternative a : alternativesDefinition
						.getConsideredAlternatives()) {
					for (Value v : leaf.getValues(a.getName()).getList()) {
						INumericValue value = (INumericValue) v;
						if (value.value() > max) {
							max = value.value();
						}
						if (value.value() < min) {
							min = value.value();
						}
					}
				}
				nt.defaults(min, max);
				nt.setMode(TransformationMode.LINEAR);
			} else {
				OrdinalTransformer ot = (OrdinalTransformer) t;
				if (leaf.getScale() instanceof BooleanScale) {

					ot.getMapping().put("Yes", new TargetValueObject(5));
					ot.getMapping().put("No", new TargetValueObject(1));
				} else {
					// total nonsense placeholder for setting something
					// until we have proper heuristics and property-specific
					// transformers in the knowledge base
					for (String s : ot.getMapping().keySet()) {
						ot.getMapping().put(s, new TargetValueObject(3.33));
					}
				}
			}
		}
	}

	public List<Alternative> getAcceptableAlternatives() {
		List<Alternative> acceptableAlternatives = new ArrayList<Alternative>();
		ResultNode multNode = new ResultNode(getTree().getRoot(),
				new WeightedMultiplication(), getAlternativesDefinition()
						.getConsideredAlternatives());

		for (Alternative a : getAlternativesDefinition()
				.getConsideredAlternatives()) {
			Double d = multNode.getResults().get(a.getName());
			if (d > 0.0) {
				acceptableAlternatives.add(a);
			}
		}
		return acceptableAlternatives;
	}
	
	/**
	 * returns a list of all digital objects in the plan
	 * @return
	 */
	public List<DigitalObject> getDigitalObjects() {
		List<DigitalObject> list = new ArrayList<DigitalObject>();
		list.add(getPlanProperties().getReportUpload());
		list.addAll(getSampleRecordsDefinition().getRecords());
		list.addAll(getRequirementsDefinition().getUploads());
		for (Alternative a : getAlternativesDefinition().getAlternatives()) {
			for (DigitalObject r :  a.getExperiment().getResults().values()) {
				list.add(r);
				if (r.getXcdlDescription() != null) {
					list.add(r.getXcdlDescription());
				}
			}
		}
		return list;
	}
	
	/**
	 * Method responsible for checking if the given alternative is the current recommended one.
	 * 
	 * @param alternative Alternative to check.
	 * @return True if the given alternative is the current recommended one. False otherwise.
	 */
	public boolean isGivenAlternativeTheCurrentRecommendation(Alternative alternative) {
		if (recommendation.getAlternative() == alternative) {
			return true;
		}
		
		return false;
	}

	public boolean isApproved() {
		return (getPlanProperties().getState() == PlanState.PLAN_VALIDATED);
	}
	
	/**
	 * Method responsible for initializing not yet initialized experiment infos.
	 */
	public void initializeExperimentInfos() {
		for (Alternative alternative : alternativesDefinition.getAlternatives()) {
			for (SampleObject sampleObject : sampleRecordsDefinition.getRecords()) {
				DetailedExperimentInfo experimentInfo = alternative.getExperiment().getDetailedInfo().get(sampleObject);
				
				if (experimentInfo == null) {
					experimentInfo = new DetailedExperimentInfo();
					alternative.getExperiment().getDetailedInfo().put(sampleObject, experimentInfo);
				}
				
				if (experimentInfo.getProgramOutput() == null) {
					experimentInfo.setProgramOutput("");
				}
			}
		}
	}	
}
