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
package eu.scape_project.planning.taverna.generator.model.processor;

import eu.scape_project.planning.taverna.generator.model.InputPort;
import eu.scape_project.planning.taverna.generator.model.OutputPort;

/**
 * Text constant processor definition.
 */
public class TextConstant extends Processor {
    private static final String TEMPLATE_NAME = "{{> data/t2flow/text-constant}}";

    private String value;

    /**
     * Creates a new text constant.
     * 
     * @param name
     *            the text constant name
     * @param value
     *            the constant value
     */
    public TextConstant(String name, String value) {
        super(name);
        this.value = value;
        super.addOutputPort(new OutputPort("value"));
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    @Override
    public void addInputPort(InputPort inputPort) {
        throw new AssertionError("Cannot add input port to text constant");
    }

    @Override
    public void addOutputPort(OutputPort outputPort) {
        throw new AssertionError("Cannot add output port to text constant");
    }

    // ---------- getter/setter ----------
    public String getValue() {
        return value;
    }
}
