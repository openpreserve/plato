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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Result of a search response of the myExperiment REST API.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workflows")
public class SearchResult {

    @XmlElement(name = "workflow")
    private List<WorkflowInfo> workflows = new ArrayList<WorkflowInfo>();

    // ---------- getter/setter ----------
    public List<WorkflowInfo> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<WorkflowInfo> workflows) {
        this.workflows = workflows;
    }

}
