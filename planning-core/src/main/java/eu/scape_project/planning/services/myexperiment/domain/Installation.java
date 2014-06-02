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
 * Installation.
 */
public class Installation {

    /**
     * Dependency.
     */
    public static class Dependency {
        private String name;
        private String version;
        private String license;

        /**
         * Empty constructor needed for JAXB.
         */
        public Dependency() {
        }

        /**
         * Creates a new dependency.
         * 
         * @param name
         *            the dependency name
         * @param version
         *            the version
         * @param license
         *            the license
         */
        public Dependency(String name, String version, String license) {
            this.name = name;
            this.version = version;
            this.license = license;
        }

        // ---------- getter/setter ----------

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getLicense() {
            return license;
        }
    }

    private List<Installation.Dependency> dependencies;
    private String environment;

    /**
     * Empty constructor needed for JAXB.
     */
    public Installation() {
    }

    /**
     * Creates a new installation.
     * 
     * @param dependencies
     *            the dependencies
     * @param environment
     *            the environment
     */
    public Installation(List<Installation.Dependency> dependencies, String environment) {
        this.dependencies = dependencies;
        this.environment = environment;
    }

    // ---------- getter/setter ----------

    public List<Installation.Dependency> getDependencies() {
        return dependencies;
    }

    public String getEnvironment() {
        return environment;
    }
}
