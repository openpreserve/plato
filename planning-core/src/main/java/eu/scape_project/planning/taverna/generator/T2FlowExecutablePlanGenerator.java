/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.taverna.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import eu.scape_project.planning.services.myexperiment.domain.ComponentConstants;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Port;
import eu.scape_project.planning.taverna.generator.model.Dataflow;
import eu.scape_project.planning.taverna.generator.model.Datalink;
import eu.scape_project.planning.taverna.generator.model.InputPort;
import eu.scape_project.planning.taverna.generator.model.OutputPort;
import eu.scape_project.planning.taverna.generator.model.Workflow;
import eu.scape_project.planning.taverna.generator.model.processor.NestedWorkflow;
import eu.scape_project.planning.taverna.generator.model.processor.TextConstant;

/**
 * Generator for executable plans in Taverna t2flow format.
 */
public class T2FlowExecutablePlanGenerator {

    /**
     * The input source of a component.
     */
    public enum InputSource {
        /**
         * The source object of the workflow.
         */
        SOURCE_OBJECT,

        /**
         * The target object of the workflow.
         */
        TARGET_OBJECT
    }

    /**
     * The related object of a port.
     */
    public enum RelatedObject {
        /**
         * The left object of the workflow.
         */
        LEFT_OBJECT {
            public String toString() {
                return ComponentConstants.VALUE_LEFT_OBJECT;
            }
        },

        /**
         * The right object of the workflow.
         */
        RIGHT_OBJECT {
            public String toString() {
                return ComponentConstants.VALUE_RIGHT_OBJECT;
            }
        }
    }

    private static final String SOURCE_PORT_NAME = "source";
    private static final String TARGET_PORT_NAME = "target";

    private static final Pattern PURL_DP_MEASURE_PORT_PATTERN = Pattern
        .compile("http:\\/\\/purl\\.org\\/DP\\/quality\\/(measures)#(\\d+)");
    private static final Pattern MEASURE_PORT_PATTERN = Pattern.compile("http.?:\\/\\/(.+)");

    private final String sourceMimetype;
    private final String targetMimetype;

    private Workflow workflow;

    private NestedWorkflow migration;
    private String migrationTargetPortName;

    /**
     * Creates a new Executable Plan generator as t2flow workflow.
     * 
     * @param name
     *            the name of the plan
     * @param author
     *            the author of the plan
     */
    public T2FlowExecutablePlanGenerator(String name, String author) {
        this(name, author, null, null);
    }

    /**
     * Creates a new Executable Plan generator as t2flow workflow.
     * 
     * @param name
     *            the name of the plan
     * @param author
     *            the author of the plan
     * @param sourceMimetype
     *            the source mimetype of the plan
     * @param targetMimetype
     *            the target mimetype of the plan
     */
    public T2FlowExecutablePlanGenerator(String name, String author, String sourceMimetype, String targetMimetype) {
        String semanticAnnotations = "&lt;&gt; &lt;http://purl.org/DP/components#fits&gt; &lt;http://purl.org/DP/components#ExecutablePlan&gt; .\n";

        if (sourceMimetype != null && targetMimetype != null) {
            semanticAnnotations += "&lt;&gt; &lt;http://purl.org/DP/components#migrates&gt;"
                + "[ a &lt;http://purl.org/DP/components#MigrationPath&gt; ;"
                + "&lt;http://purl.org/DP/components#sourceMimetype&gt; \"" + sourceMimetype + "\" ;"
                + "&lt;http://purl.org/DP/components#targetMimetype&gt; \"" + targetMimetype + "\" ] .";
        }

        this.sourceMimetype = sourceMimetype;
        this.targetMimetype = targetMimetype;
        workflow = new Workflow(name, author, semanticAnnotations);
    }

    /**
     * Adds a source port with 0 as port depth.
     * 
     * @see {@link #addSourcePort(int)}
     */
    public void addSourcePort() {
        addSourcePort(0);
    }

    /**
     * Adds a source port with the provided depth.
     * 
     * @param depth
     *            the port depth
     */
    public void addSourcePort(int depth) {
        InputPort inputPort = new InputPort(SOURCE_PORT_NAME, depth,
            "&lt;&gt;    &lt;http://purl.org/DP/components#accepts&gt;\n"
                + "              &lt;http://purl.org/DP/components#SourceObject&gt; .");
        workflow.addInputPort(inputPort);
    }

    /**
     * Adds a target port.
     */
    public void addTargetPort() {
        OutputPort outputPort = new OutputPort(TARGET_PORT_NAME,
            "&lt;&gt; &lt;http://purl.org/DP/components#provides&gt; &lt;http://purl.org/DP/components#TargetObject&gt; .");
        workflow.addOutputPort(outputPort);
    }

    /**
     * Adds a measure port for the provided measure.
     * 
     * @param measure
     *            the measure
     */
    public void addMeasurePort(String measure) {
        String portName = createMeasurePortName(measure);
        if (portName == null) {
            throw new IllegalArgumentException("The provided measure " + measure + " is not valid");
        }
        OutputPort outputPort = new OutputPort(portName, "&lt;&gt; &lt;http://purl.org/DP/components#provides&gt; &lt;"
            + measure + "&gt; .");
        workflow.addOutputPort(outputPort);
    }

    /**
     * Sets the migration component.
     * 
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param parameters
     *            map with parameters of the component
     */
    public void setMigrationComponent(WorkflowDescription workflowDescription, String workflowContent,
        Map<String, String> parameters) {
        // Dataflow
        migration = new NestedWorkflow("Migration", workflowDescription.getDataflowId());
        workflow.addProcessor(migration);

        workflowContent = convertWorkflowToNested(workflowContent);
        workflow.addDataflow(new Dataflow(workflowDescription.getDataflowId(), workflowContent));

        // Input ports
        List<Port> inputPorts = workflowDescription.getInputPorts();
        for (Port p : inputPorts) {
            if (ComponentConstants.VALUE_SOURCE_OBJECT.equals(p.getValue())) {
                migration.addInputPort(new InputPort(p.getName(), 0));
                workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, migration, p.getName()));
            } else if (ComponentConstants.VALUE_PARAMETER.equals(p.getValue())) {
                migration.addInputPort(new InputPort(p.getName(), 0));
                TextConstant c = new TextConstant(p.getName(), parameters.get(p.getName()));
                workflow.addProcessor(c);
                workflow.addDatalink(new Datalink(c, "value", migration, p.getName()));
            }
        }

        // Output ports
        List<Port> outputPorts = workflowDescription.getOutputPorts();
        for (Port p : outputPorts) {
            if (ComponentConstants.VALUE_TARGET_OBJECT.equals(p.getValue())) {
                migrationTargetPortName = p.getName();
                migration.addOutputPort(new OutputPort(migrationTargetPortName));
                workflow.addDatalink(new Datalink(migration, migrationTargetPortName, workflow, TARGET_PORT_NAME));
            }
        }
    }

    /**
     * Adds a QA component.
     * 
     * Adds measure ports for the provided {@code measures} to the workflow if
     * they are provided by the QA component and are not already present.
     * 
     * If a port specifies the related object, only
     * {@link RelatedObject#RIGHT_OBJECT} is considered.
     * 
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param parameters
     *            map with parameters of the component
     * @param measures
     *            measures of output ports to connect the component to
     * 
     * @see {@link #addQaComponent(WorkflowDescription, String, Map, List, RelatedObject)}
     */
    public void addQaComponent(final WorkflowDescription workflowDescription, final String workflowContent,
        final Map<String, String> parameters, final List<String> measures) {
        addQaComponent(workflowDescription, workflowContent, parameters, measures, RelatedObject.RIGHT_OBJECT);
    }

    /**
     * Adds a QA component.
     * 
     * Adds measure ports for the provided {@code measures} to the workflow if
     * they are provided by the QA component and are not already present.
     * 
     * If an output port specifies a related object, the port will only be
     * connected if the {@code relatedObject} matches.
     * 
     * The sources for the left and right inputs are set according to supported
     * mimetypes of the workflow.
     * 
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param parameters
     *            map with parameters of the component
     * @param measures
     *            measures of output ports to connect the component to
     * @param relatedObject
     *            the related object of measures to use if present
     * @see {@link #addQaComponent(WorkflowDescription, String, InputSource, InputSource, Map, List, RelatedObject)}
     */
    public void addQaComponent(final WorkflowDescription workflowDescription, final String workflowContent,
        final Map<String, String> parameters, final List<String> measures, RelatedObject relatedObject) {

        InputSource leftSource = null;
        InputSource rightSource = null;

        if (hasMigration() && workflowDescription.acceptsMimetypes(sourceMimetype, targetMimetype)) {
            leftSource = InputSource.SOURCE_OBJECT;
            rightSource = InputSource.TARGET_OBJECT;
        } else if (hasMigration() && workflowDescription.acceptsMimetypes(targetMimetype, sourceMimetype)) {
            leftSource = InputSource.TARGET_OBJECT;
            rightSource = InputSource.SOURCE_OBJECT;
        } else if (workflowDescription.acceptsLeftMimetype(sourceMimetype)) {
            leftSource = InputSource.SOURCE_OBJECT;
        } else if (workflowDescription.acceptsRightMimetype(sourceMimetype)) {
            rightSource = InputSource.SOURCE_OBJECT;
        } else if (hasMigration() && workflowDescription.acceptsLeftMimetype(targetMimetype)) {
            leftSource = InputSource.TARGET_OBJECT;
        } else if (hasMigration() && workflowDescription.acceptsRightMimetype(targetMimetype)) {
            rightSource = InputSource.TARGET_OBJECT;
        }

        addQaComponent(workflowDescription, workflowContent, leftSource, rightSource, parameters, measures,
            relatedObject);
    }

    /**
     * Adds a QA component.
     * 
     * The source for the left input is set to {@code leftSource}, the source
     * for the right input is set to {@code rightSource}. If these parameters
     * are null, the input are not connected.
     * 
     * Adds measure ports for the provided {@code measures} to the workflow if
     * they are provided by the QA component and are not already present.
     * 
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param leftSource
     *            the source of the left input or null
     * @param rightSource
     *            the source of the right input or null
     * @param parameters
     *            map with parameters of the component
     * @param measures
     *            measures of output ports to connect the component to
     * @param relatedObject
     *            the related object of measures to use if present
     */
    public void addQaComponent(final WorkflowDescription workflowDescription, final String workflowContent,
        final InputSource leftSource, final InputSource rightSource, final Map<String, String> parameters,
        final List<String> measures, RelatedObject relatedObject) {

        // Dataflow
        NestedWorkflow qa = new NestedWorkflow(createProcessorName(workflowDescription.getName()),
            workflowDescription.getDataflowId());
        workflow.addProcessor(qa);

        String dataflowContent = convertWorkflowToNested(workflowContent);
        workflow.addDataflow(new Dataflow(workflowDescription.getDataflowId(), dataflowContent));

        // Input ports
        List<Port> inputPorts = workflowDescription.getInputPorts();
        for (Port p : inputPorts) {
            if (ComponentConstants.VALUE_LEFT_OBJECT.equals(p.getValue())) {
                if (leftSource == InputSource.SOURCE_OBJECT) {
                    qa.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, qa, p.getName()));
                } else if (leftSource == InputSource.TARGET_OBJECT) {
                    qa.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(migration, migrationTargetPortName, qa, p.getName()));
                }
            } else if (ComponentConstants.VALUE_RIGHT_OBJECT.equals(p.getValue())) {
                if (rightSource == InputSource.SOURCE_OBJECT) {
                    qa.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, qa, p.getName()));
                } else if (rightSource == InputSource.TARGET_OBJECT) {
                    qa.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(migration, migrationTargetPortName, qa, p.getName()));
                }
            } else if (ComponentConstants.VALUE_PARAMETER.equals(p.getValue())) {
                qa.addInputPort(new InputPort(p.getName(), 0));
                TextConstant c = new TextConstant(p.getName(), parameters.get(p.getName()));
                workflow.addProcessor(c);
                workflow.addDatalink(new Datalink(c, "value", qa, p.getName()));
            }
        }

        // Output ports
        List<Port> outputPorts = workflowDescription.getOutputPorts();
        for (Port p : outputPorts) {
            if (measures.contains(p.getValue())) {
                if (p.getRelatedObject() == null || p.getRelatedObject().equals(relatedObject.toString())) {
                    String measurePortName = createMeasurePortName(p.getValue());
                    if (!workflow.hasSink(measurePortName)) {
                        addMeasurePort(p.getValue());
                    }
                    qa.addOutputPort(new OutputPort(p.getName()));
                    workflow.addDatalink(new Datalink(qa, p.getName(), workflow, measurePortName));
                }
            }
        }
    }

    /**
     * Adds a CC component connected to the workflow's target object.
     * 
     * Adds measure ports for the provided {@code measures} to the workflow if
     * they are provided by the CC component and are not already present.
     * 
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param parameters
     *            map with parameters of the component
     * @param measures
     *            measures of output ports to connect the component to
     * 
     * @see {@link #addCcComponent(WorkflowDescription, String, Map, List, InputSource)}
     */
    public void addCcComponent(final WorkflowDescription workflowDescription, final String workflowContent,
        final Map<String, String> parameters, final List<String> measures) {
        addCcComponent(workflowDescription, workflowContent, parameters, measures, InputSource.TARGET_OBJECT);
    }

    /**
     * Adds a CC component connected to the provided {@code inputSource}.
     * 
     * Adds measure ports for the provided {@code measures} to the workflow if
     * they are provided by the CC component and are not already present.
     * 
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param parameters
     *            map with parameters of the component
     * @param measures
     *            measures of output ports to connect the component to
     * @param inputSource
     *            the input source of the CC component
     */
    public void addCcComponent(final WorkflowDescription workflowDescription, final String workflowContent,
        final Map<String, String> parameters, final List<String> measures, final InputSource inputSource) {
        // Dataflow
        NestedWorkflow cc = new NestedWorkflow(createProcessorName(workflowDescription.getName()),
            workflowDescription.getDataflowId());
        workflow.addProcessor(cc);

        String dataflowContent = convertWorkflowToNested(workflowContent);
        workflow.addDataflow(new Dataflow(workflowDescription.getDataflowId(), dataflowContent));

        // Input ports
        List<Port> inputPorts = workflowDescription.getInputPorts();
        for (Port p : inputPorts) {
            if (ComponentConstants.VALUE_SOURCE_OBJECT.equals(p.getValue())) {
                if (inputSource == InputSource.SOURCE_OBJECT && workflowDescription.handlesMimetype(sourceMimetype)) {
                    cc.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, cc, p.getName()));
                } else if (inputSource == InputSource.TARGET_OBJECT
                    && workflowDescription.handlesMimetype(targetMimetype)) {
                    cc.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(migration, migrationTargetPortName, cc, p.getName()));
                }
            } else if (ComponentConstants.VALUE_PARAMETER.equals(p.getValue())) {
                cc.addInputPort(new InputPort(p.getName(), 0));
                TextConstant c = new TextConstant(p.getName(), parameters.get(p.getName()));
                workflow.addProcessor(c);
                workflow.addDatalink(new Datalink(c, "value", cc, p.getName()));
            }
        }

        // Output ports
        List<Port> outputPorts = workflowDescription.getOutputPorts();
        for (Port p : outputPorts) {
            if (measures.contains(p.getValue())) {
                String measurePortName = createMeasurePortName(p.getValue());
                if (!workflow.hasSink(measurePortName)) {
                    addMeasurePort(p.getValue());
                }
                cc.addOutputPort(new OutputPort(p.getName()));
                workflow.addDatalink(new Datalink(cc, p.getName(), workflow, measurePortName));
            }
        }
    }

    /**
     * Generates the executable plan from this description and writes it to the
     * provided writer.
     * 
     * @param writer
     *            the writer where the plan is written to
     * @throws IOException
     *             if an error occurred during write
     */
    public void generate(Writer writer) throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("data/t2flow/workflow.mustache");
        mustache.execute(writer, workflow).flush();
    }

    /**
     * Creates a valid port name from the provided measure URI.
     * 
     * @param measure
     *            the measure to use
     * @return the port name or null if the measure format is not recognised
     */
    private String createMeasurePortName(final String measure) {
        Matcher purlMatcher = PURL_DP_MEASURE_PORT_PATTERN.matcher(measure);
        if (purlMatcher.matches()) {
            return purlMatcher.group(1) + "_" + purlMatcher.group(2);
        }

        Matcher genericMatcher = MEASURE_PORT_PATTERN.matcher(measure);
        if (genericMatcher.matches()) {
            return genericMatcher.group(1).replaceAll("\\s", "_").replaceAll("\\W", "_");
        }
        return null;
    }

    /**
     * Creates a valid processor name from the provided name.
     * 
     * @param name
     *            the name to use
     * @return a valid processor name
     */
    private String createProcessorName(final String name) {
        return name.replaceAll("\\s", "_").replaceAll("\\W", "");
    }

    /**
     * Converts the workflow content string to a nested dataflow string.
     * 
     * Note that the returned string is not a valid workflow by itself anymore
     * but can be added as a dataflow element to an enclosing workflow.
     * 
     * @param workflowContent
     *            the workflow to convert
     * @return the converted dataflow
     */
    private String convertWorkflowToNested(final String workflowContent) {
        return workflowContent.replaceAll("<\\?xml.*>", "").replaceAll("<workflow.+?>", "")
            .replaceAll("</workflow>", "").replaceAll("role=\"top\"", "role=\"nested\"");
    }

    /**
     * Checks if this generator has a migration.
     * 
     * @return true if a migration was added, false otherwise
     */
    private boolean hasMigration() {
        return migration != null && migrationTargetPortName != null && !migrationTargetPortName.isEmpty();
    }
}
