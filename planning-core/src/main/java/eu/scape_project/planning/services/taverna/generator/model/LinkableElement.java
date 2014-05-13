package eu.scape_project.planning.services.taverna.generator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of a linkable element.
 */
public abstract class LinkableElement {

    private String name;

    private List<InputPort> inputPorts = new ArrayList<InputPort>();
    private List<OutputPort> outputPorts = new ArrayList<OutputPort>();

    /**
     * Creates a new linkable element.
     * 
     * @param name
     *            the element name
     */
    public LinkableElement(String name) {
        this.name = name;
    }

    /**
     * Adds a new input port to the element.
     * 
     * @param inputPort
     *            the input port to add
     */
    public void addInputPort(InputPort inputPort) {
        this.inputPorts.add(inputPort);
    }

    /**
     * Creates a new output port for the element.
     * 
     * @param outputPort
     *            the output port to add
     */
    public void addOutputPort(OutputPort outputPort) {
        this.outputPorts.add(outputPort);
    }

    /**
     * Checks if this element has a source with the provided name.
     * 
     * @param sourceName
     *            the name to check
     * @return true if a source was added, false otherwise
     */
    public boolean hasSource(String sourceName) {
        for (OutputPort p : outputPorts) {
            if (p.getName().equals(sourceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this element has a sink with the provided name.
     * 
     * @param sinkName
     *            the name to check
     * @return true if a sink was added, false otherwise
     */
    public boolean hasSink(String sinkName) {
        for (InputPort p : inputPorts) {
            if (p.getName().equals(sinkName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the type of this element.
     * 
     * @return the type
     */
    public abstract String getType();

    // ---------- getter/setter ----------
    public String getName() {
        return name;
    }

    public List<InputPort> getInputPorts() {
        return inputPorts;
    }

    public List<OutputPort> getOutputPorts() {
        return outputPorts;
    }
}
