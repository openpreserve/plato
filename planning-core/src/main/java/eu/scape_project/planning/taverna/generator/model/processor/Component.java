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

/**
 * Component processor definition.
 */
public class Component extends Processor {
    private static final String TEMPLATE_NAME = "{{> data/t2flow/component}}";

    private String registryBase;
    private String familyName;
    private String componentName;
    private String componentVersion;

    /**
     * Creates a new component processor.
     * 
     * @param name
     *            component name
     * @param registryBase
     *            registry base
     * @param familyName
     *            family name
     * @param componentName
     *            component name
     * @param componentVersion
     *            component version
     */
    public Component(String name, String registryBase, String familyName, String componentName, String componentVersion) {
        super(name);
        this.registryBase = registryBase;
        this.familyName = familyName;
        this.componentName = componentName;
        this.componentVersion = componentVersion;

    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    // ---------- getter/setter ----------
    public String getRegistryBase() {
        return registryBase;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getComponentVersion() {
        return componentVersion;
    }
}
