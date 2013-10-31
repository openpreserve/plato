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
package eu.scape_project.planning.services.myexperiment;

import java.io.Serializable;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription;

/**
 * Asynchronous loader for Taverna services.
 */
@Stateless
public class MyExperimentAsyncLoader implements Serializable {

    private static final long serialVersionUID = -7996768888644615697L;

    @Inject
    private Logger log;

    /**
     * Loads the details of a Taverna service asynchronously and returns a
     * future.
     * 
     * @param descriptor
     *            the service to load
     * @return a future of the service details
     */
    @Asynchronous
    public Future<WorkflowDescription> loadWorkflowDescription(String descriptor) {
        log.debug("Loading details of service [{}].", descriptor);
        WorkflowDescription wf = MyExperimentRESTClient.getWorkflow(descriptor);
        if (wf != null) {
            wf.readMetadata();
        }
        return new AsyncResult<WorkflowDescription>(wf);
    }
}
