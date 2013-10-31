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

import com.github.mustachejava.TemplateFunction;

import eu.scape_project.planning.taverna.generator.model.LinkableElement;

/**
 * Processor definition.
 */
public class Processor extends LinkableElement {
    private static final String TYPE = "processor";
    private static final String TEMPLATE_NAME = "{{> data/t2flow/processor}}";

    private TemplateFunction template;

    /**
     * Creates a new processor.
     * 
     * @param name
     *            the processor name
     */
    public Processor(String name) {
        super(name);
        template = new TemplateFunction() {
            @Override
            public String apply(String input) {
                return getTemplateName();
            }
        };
    }

    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    // ---------- getter/setter ----------
    @Override
    public String getType() {
        return TYPE;
    }

    public TemplateFunction getTemplate() {
        return template;
    }

}
