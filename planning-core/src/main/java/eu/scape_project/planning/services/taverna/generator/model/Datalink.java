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
package eu.scape_project.planning.services.taverna.generator.model;

/**
 * A datalink definition.
 */
public class Datalink {
    /**
     * Link type for merge links.
     */
    public static final String LINKTYPE_MERGE = "merge";

    private static final String WORKFLOW_TYPE = "dataflow";

    private String sourceType;
    private String sourcePort;
    private String sourceProcessor;
    private String sinkType;
    private String sinkPort;
    private String sinkProcessor;

    /**
     * Creates a new datalink from the provided source to the sink.
     * 
     * @param source
     *            the source object
     * @param sourcePort
     *            the port of the source object
     * @param sink
     *            the sink object
     * @param sinkPort
     *            the port of the sink object
     */
    public Datalink(LinkableElement source, String sourcePort, LinkableElement sink, String sinkPort) {

        if (!source.hasSource(sourcePort)) {
            throw new IllegalArgumentException("The source has no port with name " + sourcePort);
        }

        if (!sink.hasSink(sinkPort)) {
            throw new IllegalArgumentException("The sink has no port with name " + sinkPort);
        }

        this.sourceType = source.getType();
        this.sourcePort = sourcePort;
        this.sourceProcessor = WORKFLOW_TYPE.equals(source.getType()) ? null : source.getName();

        this.sinkType = sink.getType();
        this.sinkPort = sinkPort;
        this.sinkProcessor = WORKFLOW_TYPE.equals(sink.getType()) ? null : sink.getName();
    }

    // ---------- getter/setter ----------
    public String getSourceType() {
        return sourceType;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public String getSourceProcessor() {
        return sourceProcessor;
    }

    public String getSinkType() {
        return sinkType;
    }

    public String getSinkPort() {
        return sinkPort;
    }

    public String getSinkProcessor() {
        return sinkProcessor;
    }

    public void setSinkType(String sinkType) {
        this.sinkType = sinkType;
    }
}
