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
package eu.scape_project.planning.plato.wf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.evaluation.IActionEvaluator;
import eu.scape_project.planning.evaluation.IObjectEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.evaluation.MiniRED;
import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.values.Value;

/**
 * Business logic for workflow step Evaluate Experiments.
 * 
 * @author Michael Kraxner, Markus Hamm
 * 
 */
@Stateful
@ConversationScoped
public class EvaluateExperiments extends AbstractWorkflowStep {

    private static final long serialVersionUID = -2122479998182001334L;

    @Inject
    private Logger log;

    @Inject
    private MiniRED miniRED;

    public EvaluateExperiments() {
        this.requiredPlanState = PlanState.EXPERIMENT_PERFORMED;
        this.correspondingPlanState = PlanState.RESULTS_CAPTURED;
    }

    @Override
    protected void saveStepSpecific() {
        prepareChangesForPersist.prepare(plan);

        // initialising the values for free text transformers
        for (Leaf l : plan.getTree().getRoot().getAllLeaves()) {
            l.initTransformer();
        }
        saveEntity(plan);
    }

    /**
     * evaluates the given leaves automatically. This is only possible for
     * criteria, where information on the measurement has been defined. The
     * registered evaluators are applied one after an other, if an evaluator is
     * able to measure a criterion, its value is applied and the criterion is
     * excluded from further evaluation.
     * 
     * First per alternative all action related evaluators are called.
     * 
     * Then per alternative, for each sample object, all object/runtime related
     * evaluators are called.
     * 
     * @param leaves
     */
    public void evaluateLeaves(List<Leaf> leaves) throws PlanningException {
        // clearLogBuffer();

        // the ejb itself must not implement this interface, else we had to
        // define an interface for the whole bean
        // and we do not need to pass the ejb proxy to the evaluators...
        IStatusListener statusListener = new IStatusListener() {
            @Override
            public void updateStatus(String msg) {
                log.info(msg);
            }
        };

        // we evaluate measurements and have to assign each result to the
        // corresponding leaf: build a map
        HashMap<String, Leaf> measurementOfLeaf = new HashMap<String, Leaf>();

        // list of measurements which shall be evaluated
        List<String> allMeasurementsToEval = new LinkedList<String>();

        for (Leaf l : leaves) {
            if (l.isMapped()) {
                // measure this criterion automatically
                String uri = l.getMeasure().getUri();
                if ((uri != null) && (!uri.isEmpty())) {
                    measurementOfLeaf.put(uri, l);
                    allMeasurementsToEval.add(uri);
                }
            }
        }

        try {
            // start evaluation:
            List<String> measurementsToEval = new ArrayList<String>();
            // first action evaluators
            List<IActionEvaluator> actionEvaluators = miniRED.getActionEvaluationSequence();
            for (Alternative alternative : plan.getAlternativesDefinition().getConsideredAlternatives()) {
                // we want to evaluate each property only once, by the evaluator
                // with the highest priority
                measurementsToEval.clear();
                measurementsToEval.addAll(allMeasurementsToEval);
                for (IActionEvaluator evaluator : actionEvaluators) {
                    Map<String, Value> results = evaluator.evaluate(alternative, measurementsToEval, statusListener);
                    // apply all results
                    for (String m : results.keySet()) {
                        Value value = results.get(m);
                        if (value != null) {
                            Leaf l = measurementOfLeaf.get(m);
                            value.setScale(l.getScale());
                            l.getValues(alternative.getName()).setValue(0, value);
                        }
                    }
                    // exclude evaluated leaves from further evaluation
                    measurementsToEval.removeAll(results.keySet());
                }
            }
            // then object evaluators
            List<IObjectEvaluator> objEvaluators = miniRED.getObjectEvaluationSequence();
            objEvaluators.remove(0);
            for (Alternative alternative : plan.getAlternativesDefinition().getConsideredAlternatives()) {
                // .. for all alternatives
                List<SampleObject> samples = plan.getSampleRecordsDefinition().getRecords();
                for (int i = 0; i < samples.size(); i++) {
                    // we want to evaluate each property only once, by the
                    // evaluator with the highest priority
                    measurementsToEval.clear();
                    measurementsToEval.addAll(allMeasurementsToEval);

                    // prepare sample object with data
                    SampleObject sample = samples.get(i);
                    String samplePid = sample.getPid();
                    if (samplePid != null) {
                        sample.getData().setData(bytestreamManager.load(samplePid));
                    }

                    DigitalObject r = alternative.getExperiment().getResults().get(sample);
                    if ((r != null) && (r.getPid() != null)) {
                        r = digitalObjectManager.getCopyOfDataFilledDigitalObject(r);
                    }

                    try {
                        for (IObjectEvaluator evaluator : objEvaluators) {
                            // DigitalObject r2 = (r == null ? null :
                            // em.merge(r));
                            try {
                                Map<String, Value> results = evaluator.evaluate(alternative, sample, r,
                                    measurementsToEval, statusListener);
                                // apply all results
                                for (String m : results.keySet()) {
                                    Value value = results.get(m);
                                    if (value != null) {
                                        Leaf l = measurementOfLeaf.get(m);
                                        value.setScale(l.getScale());
                                        // add evaluation result for the current
                                        // result-object!
                                        l.getValues(alternative.getName()).setValue(i, value);
                                    }
                                }
                                // exclude evaluated leaves from further
                                // evaluation
                                measurementsToEval.removeAll(results.keySet());
                            } catch (Exception e) {
                                log.error("evaluator failed: " + e.getMessage(), e);
                                continue;
                            }
                        }
                    } finally {
                        // free the bytestream data
                        sample.getData().releaseData();
                    }
                }
            }
        } catch (Exception e) {
            throw new PlanningException("Automated evaluation failed", e);
        }
    }

    /**
     * Determines if there are values which can be determined in an automated
     * way. (dependent if there are any requirements mapped to auto-measurable
     * criteria)
     * 
     * @return
     */
    public boolean isAutoEvaluationAvailable() {
        Iterator<Leaf> iter = plan.getTree().getRoot().getAllLeaves().iterator();
        while (iter.hasNext()) {
            Leaf l = iter.next();
            if (l.isMapped()) {
                return true;
            }
        }
        return false;
    }

    /**
     * We have the rule that all evaluation settings have to be either changed
     * or confirmed once by the user. This approve function makes it easier to
     * confirm the settings for many leaves at once - It touches all currently
     * displayed leaves so that they are marked as confirmed.
     * 
     */
    public void approveAllValues() {
        Iterator<Leaf> iter = plan.getTree().getRoot().getAllLeaves().iterator();
        while (iter.hasNext()) {
            Leaf leaf = iter.next();
            for (Values values : leaf.getValueMap().values()) {
                for (Value value : values.getList()) {
                    value.touch();
                }
            }
        }
    }
}
