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
 * Accepted mimetypes.
 */
public class AcceptedMimetypes {
    private String leftMimetype;
    private String rightMimetype;

    /**
     * Empty constructor needed for JAXB.
     */
    public AcceptedMimetypes() {
    }

    /**
     * Creates a new accepted mimetypes.
     * 
     * @param leftMimetype
     *            the left mimetype
     * @param rightMimetype
     *            the right mimetype
     */
    public AcceptedMimetypes(String leftMimetype, String rightMimetype) {
        this.leftMimetype = leftMimetype;
        this.rightMimetype = rightMimetype;
    }

    // ---------- getter/setter ----------

    public String getLeftMimetype() {
        return leftMimetype;
    }

    public String getRightMimetype() {
        return rightMimetype;
    }
}
