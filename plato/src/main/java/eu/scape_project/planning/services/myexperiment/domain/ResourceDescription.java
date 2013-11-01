/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
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

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Resources description of a myExperiment REST API response.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public class ResourceDescription {

    @XmlAttribute
    private URI uri;
    @XmlAttribute
    private URI resource;

    /**
     * Creates a new resource description.
     */
    public ResourceDescription() {
        super();
    }

    /**
     * Returns the ID of the resource on the myExperiment instance.
     * 
     * @return the ID of the resource
     */
    public String getId() {
        return uri.toString().substring(uri.toString().indexOf("id=") + 3);
    }

    // ---------- getter/setter ----------
    public URI getUri() {
        return uri;
    }

    public URI getResource() {
        return resource;
    }
}
