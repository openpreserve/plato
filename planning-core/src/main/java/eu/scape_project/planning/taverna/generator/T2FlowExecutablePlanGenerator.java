/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
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

    private static final String SOURCE_PORT_NAME = "source";
    private static final String TARGET_PORT_NAME = "target";

    private static final Pattern MEASURE_PORT_PATTERN = Pattern.compile(".+\\/(.+)#(\\d+)");

    private Workflow workflow;

    /**
     * Creates a new Executable Plan generator as t2flow workflow.
     * 
     * @param name
     *            the name of the plan
     * @param author
     *            the author of the plan
     */
    public T2FlowExecutablePlanGenerator(String name, String author) {
        String semanticAnnotations = "&lt;&gt; &lt;http://purl.org/DP/components#fits&gt; &lt;http://purl.org/DP/components#ExecutablePlan&gt; .\n"
            + "&lt;&gt; &lt;http://purl.org/DP/components#migrates&gt;\n"
            + "  [ a &lt;http://purl.org/DP/components#MigrationPath&gt; ;\n"
            + "    &lt;http://purl.org/DP/components#sourceMimetype&gt; \"image/tiff\" ;\n"
            + "    &lt;http://purl.org/DP/components#targetMimetype&gt; \"image/tiff\" ] .";

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
            "&lt;&gt;    &lt;http://purl.org/DP/components#portType&gt;\n"
                + "              &lt;http://purl.org/DP/components#SourcePathPort&gt; .");
        workflow.addInputPort(inputPort);
    }

    /**
     * Adds a target port.
     */
    public void addTargetPort() {
        OutputPort outputPort = new OutputPort(TARGET_PORT_NAME,
            "&lt;&gt; &lt;http://purl.org/DP/components#portType&gt; &lt;http://purl.org/DP/components#TargetPathPort&gt; .");
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
        OutputPort outputPort = new OutputPort(portName,
            "&lt;&gt; &lt;http://purl.org/DP/components#providesMeasure&gt; &lt;" + measure + "&gt; .");
        workflow.addOutputPort(outputPort);
    }

    /**
     * Adds a migration component.
     * 
     * @param workflowDescription
     *            workflow description of the migration component
     * @param workflowContent
     *            the actual workflow content of the component
     * @param parameters
     *            map with parameters of the component
     */
    public void addMigrationComponent(WorkflowDescription workflowDescription, String workflowContent,
        Map<String, String> parameters) {
        // Dataflow
        NestedWorkflow migration = new NestedWorkflow("Migration", workflowDescription.getDataflowId());
        workflow.addProcessor(migration);

        workflowContent = workflowContent.replaceAll("<workflow.+?>", "");
        workflowContent = workflowContent.replaceAll("</workflow>", "");
        workflowContent = workflowContent.replaceAll("role=\"top\"", "role=\"nested\"");

        workflow.addDataflow(new Dataflow(workflowDescription.getDataflowId(), workflowContent));

        // Input ports
        List<Port> inputPorts = workflowDescription.getInputPorts();
        for (Port p : inputPorts) {
            if ("http://purl.org/DP/components#SourcePathPort".equals(p.getPortType())) {
                migration.addInputPort(new InputPort(p.getName(), 0));
                workflow.addDatalink(new Datalink(workflow, SOURCE_PORT_NAME, migration, p.getName()));
            } else if ("http://purl.org/DP/components#ParameterPort".equals(p.getPortType())) {
                migration.addInputPort(new InputPort(p.getName(), 0));
                TextConstant c = new TextConstant(p.getName(), parameters.get(p.getName()));
                workflow.addProcessor(c);
                workflow.addDatalink(new Datalink(c, "value", migration, p.getName()));
            }
        }

        // Output ports
        List<Port> outputPorts = workflowDescription.getOutputPorts();
        for (Port p : outputPorts) {
            if ("http://purl.org/DP/components#TargetPathPort".equals(p.getPortType())) {
                migration.addOutputPort(new OutputPort(p.getName()));
                workflow.addDatalink(new Datalink(migration, p.getName(), workflow, TARGET_PORT_NAME));
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
    private String createMeasurePortName(String measure) {
        Matcher m = MEASURE_PORT_PATTERN.matcher(measure);
        if (m.matches()) {
            return m.group(1) + "_" + m.group(2);
        }
        return null;
    }
}
