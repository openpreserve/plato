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
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.AcceptedMimetypes;
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
        sourceObject,

        /**
         * The target object of the workflow.
         */
        targetObject
    }

    private static final String SOURCE_PORT_NAME = "source";
    private static final String TARGET_PORT_NAME = "target";

    private static final Pattern PURL_DP_MEASURE_PORT_PATTERN = Pattern.compile("http:\\/\\/purl\\.org\\/DP\\/quality\\/(measures)#(\\d+)");
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
     * @param sourceMimetype
     *            the source mimetype of the plan
     * @param targetMimetype
     *            the target mimetype of the plan
     */
    public T2FlowExecutablePlanGenerator(String name, String author, String sourceMimetype, String targetMimetype) {
        String semanticAnnotations = "&lt;&gt; &lt;http://purl.org/DP/components#fits&gt; &lt;http://purl.org/DP/components#ExecutablePlan&gt; .\n"
            + "&lt;&gt; &lt;http://purl.org/DP/components#migrates&gt;"
            + "[ a &lt;http://purl.org/DP/components#MigrationPath&gt; ;"
            + "&lt;http://purl.org/DP/components#sourceMimetype&gt; \""
            + sourceMimetype
            + "\" ;"
            + "&lt;http://purl.org/DP/components#targetMimetype&gt; \"" + targetMimetype + "\" ] .";

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
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param parameters
     *            map with parameters of the component
     * @param measures
     *            measures of output ports to connect the component to
     */
    public void addQaComponent(final WorkflowDescription workflowDescription, final String workflowContent,
        final Map<String, String> parameters, final List<String> measures) {

        // Dataflow
        NestedWorkflow qa = new NestedWorkflow(createProcessorName(workflowDescription.getName()),
            workflowDescription.getDataflowId());
        workflow.addProcessor(qa);

        String dataflowContent = convertWorkflowToNested(workflowContent);
        workflow.addDataflow(new Dataflow(workflowDescription.getDataflowId(), dataflowContent));

        boolean sourceToLeft = false;
        boolean targetToLeft = false;
        boolean sourceToRight = false;
        boolean targetToRight = false;
        if (acceptsMimetypes(workflowDescription, sourceMimetype, targetMimetype)) {
            sourceToLeft = true;
            targetToRight = true;
        } else if (acceptsMimetypes(workflowDescription, targetMimetype, sourceMimetype)) {
            sourceToRight = true;
            targetToLeft = true;
        } else if (acceptsLeftMimetype(workflowDescription, sourceMimetype)) {
            sourceToLeft = true;
        } else if (acceptsRightMimetype(workflowDescription, sourceMimetype)) {
            sourceToRight = true;
        } else if (hasMigration() && acceptsLeftMimetype(workflowDescription, targetMimetype)) {
            targetToLeft = true;
        } else if (hasMigration() && acceptsRightMimetype(workflowDescription, targetMimetype)) {
            targetToRight = true;
        }

        // Input ports
        List<Port> inputPorts = workflowDescription.getInputPorts();
        for (Port p : inputPorts) {
            if (ComponentConstants.VALUE_LEFT_OBJECT.equals(p.getValue())) {
                if (sourceToLeft) {
                    qa.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, qa, p.getName()));
                } else if (targetToLeft) {
                    qa.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(migration, migrationTargetPortName, qa, p.getName()));
                }
            } else if (ComponentConstants.VALUE_RIGHT_OBJECT.equals(p.getValue())) {
                if (sourceToRight) {
                    qa.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, qa, p.getName()));
                } else if (targetToRight) {
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
                String measurePortName = createMeasurePortName(p.getValue());
                if (!workflow.hasSink(measurePortName)) {
                    addMeasurePort(p.getValue());
                }
                qa.addOutputPort(new OutputPort(p.getName()));
                workflow.addDatalink(new Datalink(qa, p.getName(), workflow, measurePortName));
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
        addCcComponent(workflowDescription, workflowContent, parameters, measures, InputSource.targetObject);
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
                if (inputSource == InputSource.sourceObject
                    && handlesSourceMimetype(workflowDescription, sourceMimetype)) {
                    cc.addInputPort(new InputPort(p.getName(), 0));
                    workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, cc, p.getName()));
                } else if (inputSource == InputSource.targetObject
                    && handlesSourceMimetype(workflowDescription, targetMimetype)) {
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
     * Checks whether the provided workflow accepts the left and right mimetype.
     * 
     * @param workflowDescription
     *            description of the workflow
     * @param leftMimetype
     *            left mimetype to check
     * @param rightMimetype
     *            right mimetype to check
     * @return true if the workflow accepts the mimetypes, false otherwise
     */
    private boolean acceptsMimetypes(final WorkflowDescription workflowDescription, final String leftMimetype,
        final String rightMimetype) {
        if (!hasMigration()) {
            return false;
        }

        String leftWildcard = getMimetypeWildcard(leftMimetype);
        String rightWildcard = getMimetypeWildcard(leftMimetype);

        List<String> acceptedMimetype = workflowDescription.getAcceptedMimetype();
        if ((acceptedMimetype.contains(leftMimetype) || acceptedMimetype.contains(leftWildcard))
            && (acceptedMimetype.contains(rightMimetype) || acceptedMimetype.contains(rightWildcard))) {
            return true;
        }

        List<AcceptedMimetypes> acceptedMimetypes = workflowDescription.getAcceptedMimetypes();
        for (AcceptedMimetypes m : acceptedMimetypes) {
            if ((m.getLeftMimetype().equals(leftMimetype) || m.getLeftMimetype().equals(leftWildcard))
                && (m.getRightMimetype().equals(rightMimetype) || m.getRightMimetype().equals(rightWildcard))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the left part of the provided workflow can handle the mimetype.
     * 
     * @param workflowDescritpion
     *            the workflow to check
     * @param mimetype
     *            the mimetype
     * @return true if the left part can handle the mimetype, false otherwise
     */
    private boolean acceptsLeftMimetype(final WorkflowDescription workflowDescritpion, final String mimetype) {
        String wildcardMimetype = getMimetypeWildcard(mimetype);

        List<String> acceptedMimetype = workflowDescritpion.getAcceptedMimetype();
        if (acceptedMimetype.contains(mimetype) || acceptedMimetype.contains(wildcardMimetype)) {
            return true;
        }

        List<AcceptedMimetypes> acceptedMimetypes = workflowDescritpion.getAcceptedMimetypes();
        for (AcceptedMimetypes m : acceptedMimetypes) {
            if (m.getLeftMimetype().equals(mimetype) || m.getLeftMimetype().equals(wildcardMimetype)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the right part of the provided workflow can handle the
     * mimetype.
     * 
     * @param workflowDescription
     *            the workflow to check
     * @param mimetype
     *            the mimetype
     * @return true if the right part can handle the mimetype, false otherwise
     */
    private boolean acceptsRightMimetype(final WorkflowDescription workflowDescription, final String mimetype) {
        String wildcardMimetype = getMimetypeWildcard(mimetype);

        List<String> acceptedMimetype = workflowDescription.getAcceptedMimetype();
        if (acceptedMimetype.contains(mimetype) || acceptedMimetype.contains(wildcardMimetype)) {
            return true;
        }

        List<AcceptedMimetypes> acceptedMimetypes = workflowDescription.getAcceptedMimetypes();
        for (AcceptedMimetypes m : acceptedMimetypes) {
            if (m.getRightMimetype().equals(mimetype) || m.getRightMimetype().equals(wildcardMimetype)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the provided workflow can handle the mimetype.
     * 
     * @param wf
     *            the workflow to check
     * @param mimetype
     *            the mimetype
     * @return true if the left part can handle the mimetype, false otherwise
     */
    private boolean handlesSourceMimetype(final WorkflowDescription wf, final String mimetype) {
        String wildcardMimetype = getMimetypeWildcard(mimetype);

        List<String> acceptedMimetype = wf.getAcceptedMimetype();
        if (acceptedMimetype.contains(mimetype) || acceptedMimetype.contains(wildcardMimetype)) {
            return true;
        }
        return false;
    }

    /**
     * Creates a wildcard mimetype by using the type of the provided mimetype
     * and '*' as subtype.
     * 
     * @param mimetype
     *            the base mimetype
     * @return the wildcard mimetype
     */
    private String getMimetypeWildcard(String mimetype) {
        if (mimetype == null) {
            return null;
        } else if ("".equals(mimetype)) {
            return "";
        }

        int position = mimetype.indexOf('/');
        return mimetype.substring(0, position >= 0 ? position : mimetype.length()) + "/*";
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
