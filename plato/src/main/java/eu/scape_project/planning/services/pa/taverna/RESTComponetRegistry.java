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
package eu.scape_project.planning.services.pa.taverna;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.interfaces.actions.IPreservationActionInfo;
import eu.scape_project.planning.model.interfaces.actions.IPreservationActionRegistry;
import eu.scape_project.planning.services.taverna.MyExperimentRESTClient;
import eu.scape_project.planning.services.taverna.MyExperimentRESTClient.ComponentQuery;
import eu.scape_project.planning.services.taverna.model.SearchResult.Workflow;
import eu.scape_project.planning.taverna.PortType;

public class RESTComponetRegistry implements IPreservationActionRegistry {

    private MyExperimentRESTClient client = new MyExperimentRESTClient();

    @Override
    public void connect(String URL) throws ServiceException, MalformedURLException {
    }

    public List<IPreservationActionInfo> getAvailableActions(FormatInfo sourceFormat) throws PlatoException {
        List<IPreservationActionInfo> preservationActions = new ArrayList<IPreservationActionInfo>();

        ComponentQuery query = client.createComponentQuery();
        query.addInputPortType(PortType.FromURIPort).addInputPortType(PortType.ToURIPort);
        List<Workflow> workflows = client.searchComponents(query);

        for (Workflow workflow : workflows) {
            MyExperimentPreservationActionInfo actionInfo = new MyExperimentPreservationActionInfo();

            actionInfo.setShortname(workflow.getName());
            actionInfo.setUrl(workflow.getResource().toASCIIString());
            actionInfo.setInfo(workflow.getName());
            actionInfo.setDescriptor(workflow.getUri().toASCIIString());
        }

        return preservationActions;
    }

    @Override
    public String getLastInfo() {
        return null;
    }

    @Override
    public String getToolIdentifier(String url) {
        return "myExperiment";
    }

    @Override
    public String getToolParameters(String url) {
        return null;
    }

}
