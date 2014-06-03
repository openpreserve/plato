package eu.scape_project.planning.services.myexperiment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.ByteStream;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.services.IServiceInfo;
import eu.scape_project.planning.services.myexperiment.domain.ComponentConstants;
import eu.scape_project.planning.services.myexperiment.domain.Port;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator.InputSource;
import eu.scape_project.planning.services.taverna.generator.T2FlowExecutablePlanGenerator.RelatedObject;
import eu.scape_project.planning.utils.FileUtils;

public class MyExperimentExecutablePlanGenerator {

    @Inject
    private Logger log;

    private T2FlowExecutablePlanGenerator generator;

    private String name;

    public MyExperimentExecutablePlanGenerator(final String name, final String author) {
        this.name = name;
        generator = new T2FlowExecutablePlanGenerator(name, author);
        generator.addSourcePort();
        generator.addTargetPort();
    }

    public DigitalObject generateExecutablePlan() throws PlanningException {
        DigitalObject workflow = new DigitalObject();

        // Create digital object
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);

        try {
            generator.generate(writer);
        } catch (IOException e) {
            log.warn("An error occured generating the executable plan.", e.getMessage());
            throw new PlanningException("An error occured generating the executable plan.", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    log.warn("An error occured closing the executable plan generator writer.", e.getMessage());
                    throw new PlanningException("An error occured closing the executable plan generator writer.", e);
                }
            }
        }

        byte[] data = out.toByteArray();
        ByteStream bsData = new ByteStream();
        bsData.setData(data);

        workflow.setContentType("application/vnd.taverna.t2flow+xml");
        workflow.setData(bsData);
        workflow.setFullname(FileUtils.makeFilename(name + ".t2flow"));

        return workflow;
    }

    public void addMigrationAction(IServiceInfo migrationAction, Map<String, String> parameters)
        throws PlanningException {
        try {
            WorkflowDescription wf = MyExperimentRESTClient.getWorkflow(migrationAction.getDescriptor());
            wf.readMetadata();
            String workflowContent = MyExperimentRESTClient.getWorkflowContent(wf);
            generator.setMigrationComponent(wf, workflowContent, parameters);
        } catch (Exception e) {
            log.warn("An error occured querying myExperiment migration component.", e.getMessage());
            throw new PlanningException("An error occured querying myExperiment migration component", e);
        }
    }

    public void addQaComponent(List<RecommendedComponent> recommendedComponents) {
        addQaComponent(recommendedComponents.toArray(new RecommendedComponent[recommendedComponents.size()]));
    }

    public void addQaComponent(RecommendedComponent... recommendedComponents) {
        for (RecommendedComponent recommendedComponent : recommendedComponents) {
            String workflowContent = MyExperimentRESTClient.getWorkflowContent(recommendedComponent.workflow);
            if (ComponentConstants.PROFILE_OBJECT_QA.equals(recommendedComponent.workflow.getProfile())) {
                generator.addQaComponent(recommendedComponent.workflow, workflowContent,
                    recommendedComponent.leftSource, recommendedComponent.rightSource, new HashMap<String, String>(0),
                    recommendedComponent.measures, recommendedComponent.relatedObject);
            } else if (ComponentConstants.PROFILE_CC.equals(recommendedComponent.workflow.getProfile())) {
                generator.addCcComponent(recommendedComponent.workflow, workflowContent,
                    new HashMap<String, String>(0), recommendedComponent.measures);
            } else {
                log.debug("Component search returned component with invalid or no profile.");
            }
        }
    }

    public static RecommendedComponent recommendComponent(IServiceInfo workflowInfo, List<String> measures,
        String targetMimetype) {
        RecommendedComponent recommendedComponent = null;

        WorkflowDescription wfd = MyExperimentRESTClient.getWorkflow(workflowInfo.getDescriptor());
        wfd.readMetadata();
        List<Port> outputPorts = wfd.getOutputPorts();

        List<String> leftMeasures = new ArrayList<String>();
        List<String> rightMeasures = new ArrayList<String>();

        for (Port port : outputPorts) {
            if (measures.contains(port.getValue())) {
                if (port.getRelatedObject() == null) {
                    leftMeasures.add(port.getValue());
                    rightMeasures.add(port.getValue());
                } else if (ComponentConstants.VALUE_LEFT_OBJECT.equals(port.getRelatedObject())) {
                    leftMeasures.add(port.getValue());
                } else if (ComponentConstants.VALUE_RIGHT_OBJECT.equals(port.getRelatedObject())) {
                    rightMeasures.add(port.getValue());
                }
            }
        }

        boolean acceptsLeftMimetype = wfd.acceptsLeftMimetype(targetMimetype);
        boolean acceptsRightMimetype = wfd.acceptsRightMimetype(targetMimetype);
        if (acceptsLeftMimetype && acceptsRightMimetype) {
            if (leftMeasures.size() > rightMeasures.size()) {
                recommendedComponent = new RecommendedComponent(wfd, leftMeasures, InputSource.TARGET_OBJECT,
                    InputSource.SOURCE_OBJECT, RelatedObject.LEFT_OBJECT);
            } else {
                recommendedComponent = new RecommendedComponent(wfd, rightMeasures, InputSource.SOURCE_OBJECT,
                    InputSource.TARGET_OBJECT, RelatedObject.RIGHT_OBJECT);
            }
        } else if (acceptsLeftMimetype) {
            recommendedComponent = new RecommendedComponent(wfd, leftMeasures, InputSource.TARGET_OBJECT,
                InputSource.SOURCE_OBJECT, RelatedObject.LEFT_OBJECT);
        } else if (acceptsRightMimetype) {
            recommendedComponent = new RecommendedComponent(wfd, rightMeasures, InputSource.SOURCE_OBJECT,
                InputSource.TARGET_OBJECT, RelatedObject.RIGHT_OBJECT);
        }

        return recommendedComponent;
    }
}
