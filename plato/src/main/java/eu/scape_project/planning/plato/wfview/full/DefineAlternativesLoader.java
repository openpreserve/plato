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
package eu.scape_project.planning.plato.wfview.full;

import java.io.Serializable;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.scape_project.planning.model.interfaces.actions.IPreservationActionInfo;
import eu.scape_project.planning.services.taverna.MyExperimentRESTClient;
import eu.scape_project.planning.services.taverna.model.WorkflowDescription;

import org.slf4j.Logger;

/**
 * Class used as backing-bean for the view definealternatives.xhtml
 * 
 * @author Markus Hamm
 */
@Stateless
public class DefineAlternativesLoader implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;

    @Asynchronous
    public Future<WorkflowDescription> loadWorkflowDescription(MyExperimentRESTClient myExperimentRESTClient,
        IPreservationActionInfo actionInfo) {

        log.error("Loader");

        // FIXME: Remove
        // try {
        // Thread.sleep(5000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        log.error("Loader doing");
        return new AsyncResult<WorkflowDescription>(myExperimentRESTClient.getWorkflow(actionInfo.getDescriptor()));

    }
}
