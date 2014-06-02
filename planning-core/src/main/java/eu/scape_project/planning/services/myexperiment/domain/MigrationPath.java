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

/**
 * Migration path.
 */
public class MigrationPath {
    private String sourceMimetype;
    private String targetMimetype;

    /**
     * Empty constructor needed for JAXB.
     */
    public MigrationPath() {
    }

    /**
     * Creates a new migration path.
     * 
     * @param sourceMimetype
     *            the source mimetype
     * @param targetMimetype
     *            the target mimetype
     */
    public MigrationPath(String sourceMimetype, String targetMimetype) {
        this.sourceMimetype = sourceMimetype;
        this.targetMimetype = targetMimetype;
    }

    // ---------- getter/setter ----------

    public String getSourceMimetype() {
        return sourceMimetype;
    }

    public String getTargetMimetype() {
        return targetMimetype;
    }
}
