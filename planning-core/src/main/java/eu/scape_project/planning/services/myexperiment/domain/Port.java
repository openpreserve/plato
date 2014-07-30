/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.services.myexperiment.domain;

import java.util.List;

/**
 * Port.
 */
public class Port {

    /**
     * Predefined parameters.
     */
    public static class PredefinedParameter {
        private String value;
        private String description;

        /**
         * Empty constructor needed for JAXB.
         */
        public PredefinedParameter() {
        }

        /**
         * Creates a new predefined parameter.
         * 
         * @param value
         *            the value of the parameter
         * @param description
         *            the description of the parameter
         */
        public PredefinedParameter(String value, String description) {
            this.value = value;
            this.description = description;
        }

        // ---------- getter/setter ----------

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

    }

    private String name;

    private String description;

    private String value;

    String relatedObject;

    private List<Port.PredefinedParameter> predefinedParameters;

    /**
     * Empty constructor needed for JAXB.
     */
    public Port() {
    }

    /**
     * Creates a new port.
     * 
     * @param name
     *            the port name
     * @param description
     *            the port description
     */
    public Port(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Creates a new port.
     * 
     * @param name
     *            the port name
     * @param description
     *            the port description
     * @param value
     *            port type
     */
    public Port(String name, String description, String value) {
        this(name, description);
        this.value = value;
    }

    /**
     * Creates a new port.
     * 
     * @param name
     *            the port name
     * @param description
     *            the port description
     * @param value
     *            port type
     * @param relatedObject
     *            the related object
     */
    public Port(String name, String description, String value, String relatedObject) {
        this(name, description, value);
        this.relatedObject = relatedObject;
    }

    /**
     * Checks if this port is a parameter port.
     * 
     * @return true if this port is a parameter port, false otherwise
     */
    public boolean isParameterPort() {
        return ComponentConstants.VALUE_PARAMETER.equals(value) || predefinedParameters != null;
    }

    // ---------- getter/setter ----------

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public String getRelatedObject() {
        return relatedObject;
    }

    public List<Port.PredefinedParameter> getPredefinedParameters() {
        return predefinedParameters;
    }

    void setPredefinedParameters(List<Port.PredefinedParameter> predefinedParameters) {
        this.predefinedParameters = predefinedParameters;
    }
}
