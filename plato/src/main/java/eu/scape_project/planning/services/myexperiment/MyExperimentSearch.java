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

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.planning.services.action.IActionInfo;
import eu.scape_project.planning.services.myexperiment.MyExperimentRESTClient.ComponentQuery;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowInfo;
import eu.scape_project.planning.services.pa.taverna.MyExperimentActionInfo;

/**
 * Search for myExperiment.
 */
public class MyExperimentSearch {

    /**
     * Rest client for myExperiment.
     */
    private MyExperimentRESTClient myExperimentRESTClient = new MyExperimentRESTClient();

    private String profile;

    private String fromMimetype;

    private String migrationPathTo;

    private String dependencyLabel;

    private String environment;

    private String environmentType;

    public List<IActionInfo> search() {
        List<IActionInfo> services = new ArrayList<IActionInfo>();

        // Create query
        ComponentQuery query = myExperimentRESTClient.createComponentQuery();
        query.addProfile(profile).addMigrationPath(fromMimetype).setMigrationPathToPattern(migrationPathTo)
            .addInstallationEnvironment(environment).addInstallationEnvironmentType(environmentType);

        query.setDependencyLabelPattern(dependencyLabel);
        query.finishQuery();

        List<WorkflowInfo> workflows = myExperimentRESTClient.searchComponents(query);
        for (WorkflowInfo workflow : workflows) {
            MyExperimentActionInfo service = new MyExperimentActionInfo();

            service.setShortname(workflow.getName());
            service.setDescriptor(workflow.getUri().toASCIIString());
            service.setInfo(workflow.getDescription());
            service.setUrl(workflow.getContentUri());
            service.setContentType(workflow.getContentType());

            services.add(service);
        }

        return services;
    }

    // ---------- getter/setter ----------
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getFromMimetype() {
        return fromMimetype;
    }

    public void setFromMimetype(String fromMimetype) {
        this.fromMimetype = fromMimetype;
    }

    public String getMigrationPathTo() {
        return migrationPathTo;
    }

    public void setMigrationPathTo(String migrationPathTo) {
        this.migrationPathTo = migrationPathTo;
    }

    public String getDependencyLabel() {
        return dependencyLabel;
    }

    public void setDependencyLabel(String dependencyLabel) {
        this.dependencyLabel = dependencyLabel;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getEnvironmentType() {
        return environmentType;
    }

    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
    }
}
