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
package eu.scape_project.planning.services.taverna.generator.model.processor;

/**
 * Dataflow processor definition.
 */
public class NestedWorkflow extends Processor {
    private static final String TEMPLATE_NAME = "{{> data/t2flow/dataflow}}";

    private String id;

    /**
     * Creates a new dataflow.
     * 
     * @param name
     *            the dataflow name
     * @param id
     *            the dataflow id
     */
    public NestedWorkflow(String name, String id) {
        super(name);
        this.id = id;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    // ---------- getter/setter ----------
    @Override
    public String getType() {
        return "processor";
    }

    public String getId() {
        return id;
    }
}
