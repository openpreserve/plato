/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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
package eu.scape_project.planning.plato.wfview.full;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.ByteStreamManager;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.plato.bean.MyExperimentServices;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.DevelopExperiments;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.services.action.ActionInfo;
import eu.scape_project.planning.services.action.ActionInfoFactory;
import eu.scape_project.planning.services.myexperiment.MyExperimentAsyncBuilder;
import eu.scape_project.planning.utils.Downloader;

/**
 * Class used as backing-bean for the view developexperiments.xhtml.
 * 
 * @author Markus Hamm
 */
@Named("developExperiments")
@ConversationScoped
public class DevelopExperimentsView extends AbstractView {

    private static final long serialVersionUID = -4042576732990053101L;

    @Inject
    private DevelopExperiments developExperiments;

    @Inject
    private ByteStreamManager byteStreamManager;

    @Inject
    private Downloader downloader;

    private List<Alternative> alternatives;

    private List<Leaf> mappedLeaves;

    private List<String> measures;

    /**
     * Cache for myExperiment service details.
     */
    @Inject
    private MyExperimentServices myExperimentServices;

    private String sourceMimetype;

    private HashMap<Alternative, String> targetMimetypes;

    private HashMap<Alternative, ActionInfo> actionInfos;

    @Inject
    private MyExperimentAsyncBuilder asyncBuilder;

    /**
     * Default constructor.
     */
    public DevelopExperimentsView() {
        currentPlanState = PlanState.GO_CHOSEN;
        name = "Develop Experiments";
        viewUrl = "/plan/developexperiments.jsf";
        group = "menu.evaluateAlternatives";
    }

    @Override
    public void init(Plan plan) {
        super.init(plan);
        alternatives = plan.getAlternativesDefinition().getConsideredAlternatives();

        targetMimetypes = new HashMap<Alternative, String>(alternatives.size());
        actionInfos = new HashMap<Alternative, ActionInfo>(alternatives.size());
        for (Alternative a : alternatives) {
            PreservationActionDefinition pad = a.getAction();
            if (pad != null) {
                targetMimetypes.put(a, "");
                ActionInfo actionInfo = ActionInfoFactory.createActionInfo(pad);
                actionInfos.put(a, actionInfo);
                myExperimentServices.load(actionInfo);
            }
        }

        measures = new ArrayList<String>();
        mappedLeaves = plan.getTree().getRoot().getAllLeaves();
        Iterator<Leaf> mappedLeavesIt = mappedLeaves.iterator();
        while (mappedLeavesIt.hasNext()) {
            Leaf leaf = mappedLeavesIt.next();
            if (leaf.isMapped()) {
                measures.add(leaf.getMeasure().getUri());
            } else {
                mappedLeavesIt.remove();
            }
        }

        sourceMimetype = plan.getSampleRecordsDefinition().getFirstSampleWithFormat().getFormatInfo().getMimeType();
    }

    /**
     * Generates an experiment workflow for the provided alternative.
     * 
     * @param alternative
     *            the alternative
     */
    public void generateExperimentWorkflow(Alternative alternative) {
        if (targetMimetypes.get(alternative) == null || targetMimetypes.get(alternative).isEmpty()) {
            facesMessages.addError("Target mimetype is empty. Please specify a target mimetype.");
            return;
        }

        try {
            Future<DigitalObject> generatedWorkflow = asyncBuilder.generateExecutablePlan(plan.getPlanProperties()
                .getName(), alternative, measures, sourceMimetype, targetMimetypes.get(alternative));
            DigitalObject workflow = generatedWorkflow.get();
            developExperiments.setAlternativeWorkflow(alternative, workflow);
            facesMessages.addInfo("Experiment workflow generated.");
        } catch (PlanningException e) {
            facesMessages.addError("An error occured generating the executable plan.");
        } catch (InterruptedException e) {
            facesMessages.addError("Generating the experiment plan was interrupted. Please try again.");
        } catch (ExecutionException e) {
            facesMessages.addError("The experiment plan could not be generated. Please try again.");
        }
    }

    /**
     * Starts a download for the workflow of the provided alternative. Uses
     * {@link eu.scape_project.planning.util.Downloader} to perform the
     * download.
     * 
     * @param alternative
     *            the alternative download the workflow from
     */
    public void downloadExperimentWorkflow(final Alternative alternative) {
        DigitalObject workflow = alternative.getExperiment().getWorkflow();
        File file = byteStreamManager.getTempFile(workflow.getPid());
        downloader.download(workflow, file);
    }

    // --------------- getter/setter ---------------
    @Override
    protected AbstractWorkflowStep getWfStep() {
        return developExperiments;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public MyExperimentServices getMyExperimentServices() {
        return myExperimentServices;
    }

    public HashMap<Alternative, ActionInfo> getActionInfos() {
        return actionInfos;
    }

    public List<Leaf> getMappedLeaves() {
        return mappedLeaves;
    }

    public String getSourceMimetype() {
        return sourceMimetype;
    }

    public HashMap<Alternative, String> getTargetMimetypes() {
        return targetMimetypes;
    }
}
