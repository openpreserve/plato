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
package eu.scape_project.planning.taverna.generator.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import com.google.common.base.Objects;

import eu.scape_project.planning.taverna.generator.model.processor.NestedWorkflow;
import eu.scape_project.planning.taverna.generator.model.processor.Processor;

/**
 * Workflow definition.
 */
public class Workflow extends LinkableElement {
    public static final SimpleDateFormat DATE_FORMATTER;
    private static final String TYPE = "dataflow";

    static {
        DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S 'UTC'");
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private UUID id = UUID.randomUUID();

    private String date;

    private List<Datalink> datalinks = new ArrayList<Datalink>();
    private List<Processor> processors = new ArrayList<Processor>();
    private List<Dataflow> dataflows = new ArrayList<Dataflow>();

    private String author;

    private String semanticAnnotation;

    /**
     * Creates a new workflow definition with no semantic annotation.
     * 
     * @see {@link #Workflow(String, String, String)}
     */
    public Workflow(String name, String author) {
        super(name);
        this.author = author;
        date = Workflow.DATE_FORMATTER.format(new Date());
    }

    /**
     * Creates a new workflow definition.
     * 
     * @param name
     *            name of the workflow
     * @param author
     *            name of the author
     * @param semanticAnnotation
     *            semantic annotation
     */
    public Workflow(String name, String author, String semanticAnnotation) {
        this(name, author);
        this.semanticAnnotation = semanticAnnotation;
    }

    /**
     * Adds a dataflow to this workflow if no workflow with the dataflow's ID
     * exists.
     * 
     * Note that this method only adds the dataflow itself, not the
     * {@link NestedWorkflow} processor.
     * 
     * @param dataflow
     *            the dataflow to add
     */
    public void addDataflow(Dataflow dataflow) {
        for (Dataflow d : dataflows) {
            if (d.getId().equals(dataflow.getId())) {
                return;
            }
        }
        dataflows.add(dataflow);
    }

    /**
     * Adds a datalink to this workflow. If a link to the sink already exists,
     * changes the provided {@code datalink} and the existing link element to
     * "merge".
     * 
     * @param datalink
     *            the datalink to add
     */
    public void addDatalink(Datalink datalink) {
        for (Datalink d : datalinks) {
            if (Objects.equal(d.getSinkProcessor(), datalink.getSinkProcessor())
                && Objects.equal(d.getSinkPort(), datalink.getSinkPort())) {
                d.setSinkType(Datalink.LINKTYPE_MERGE);
                datalink.setSinkType(Datalink.LINKTYPE_MERGE);
            }
        }
        datalinks.add(datalink);
    }

    /**
     * Adds a processor to this workflow.
     * 
     * @param processor
     *            the processor to add
     */
    public void addProcessor(Processor processor) {
        processors.add(processor);
    }

    @Override
    public boolean hasSource(String name) {
        return super.hasSink(name);
    }

    @Override
    public boolean hasSink(String name) {
        return super.hasSource(name);
    }

    // ---------- getter/setter ----------
    @Override
    public String getType() {
        return TYPE;
    }

    public UUID getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public List<Datalink> getDatalinks() {
        return datalinks;
    }

    public List<Processor> getProcessors() {
        return processors;
    }

    public List<Dataflow> getDataflows() {
        return dataflows;
    }

    public String getAuthor() {
        return author;
    }

    public String getSemanticAnnotation() {
        return semanticAnnotation;
    }
}
