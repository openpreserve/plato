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
package eu.scape_project.planning.services.myexperiment;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.planning.services.IServiceInfo;
import eu.scape_project.planning.services.PlanningServiceException;
import eu.scape_project.planning.services.myexperiment.MyExperimentRESTClient.ComponentQuery;
import eu.scape_project.planning.services.myexperiment.domain.ComponentConstants;
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

    /*
     * Search parameters
     */
    private String profile;

    private String sourceMimetype;

    private String targetMimetype;

    private String dependencyLabel;

    private String environment;

    private String environmentType;

    private String measure;

    /**
     * Searches for migration components using the set search parameters.
     * 
     * @return a list of service infos that match the search parameters
     */
    public List<IServiceInfo> searchMigrationAction() throws PlanningServiceException {
        List<IServiceInfo> services = new ArrayList<IServiceInfo>();

        // Create query
        ComponentQuery query = myExperimentRESTClient.createComponentQuery();
        query.addProfile(profile).addMigrationPath(sourceMimetype).setMigrationPathTargetPattern(targetMimetype)
            .addInputPort(ComponentConstants.VALUE_SOURCE_OBJECT).addOutputPort(ComponentConstants.VALUE_TARGET_OBJECT)
            .addInstallationEnvironment(environment).addInstallationEnvironmentType(environmentType);

        query.setDependencyLabelPattern(dependencyLabel);
        query.finishQuery();

        List<WorkflowInfo> workflows = myExperimentRESTClient.searchComponents(query);
        for (WorkflowInfo workflow : workflows) {
            MyExperimentActionInfo service = new MyExperimentActionInfo();

            service.setShortname(workflow.getName());
            service.setDescriptor(workflow.getDescriptor());
            service.setInfo(workflow.getDescription());
            service.setUrl(workflow.getContentUri());
            service.setContentType(workflow.getContentType());

            services.add(service);
        }

        return services;
    }

    /**
     * Searches for object QA components using the set search parameters.
     * 
     * @return a list of service infos that match the search parameters
     * @throws PlanningServiceException 
     */
    public List<IServiceInfo> searchObjectQa() throws PlanningServiceException {
        List<IServiceInfo> services = new ArrayList<IServiceInfo>();

        ComponentQuery query = myExperimentRESTClient.createComponentQuery();
        query.addProfile(profile);

        query.addHandlesMimetype(sourceMimetype, targetMimetype)
            .addHandlesMimetypeWildcard(sourceMimetype, targetMimetype)
            .addHandlesMimetypes(sourceMimetype, targetMimetype)
            .addHandlesMimetypesWildcard(sourceMimetype, targetMimetype);

        if (sourceMimetype != null && !sourceMimetype.equals(targetMimetype)) {
            query.addHandlesMimetypes(targetMimetype, sourceMimetype).addHandlesMimetypesWildcard(targetMimetype,
                sourceMimetype);
        }
        query.addInputPort(ComponentConstants.VALUE_LEFT_OBJECT).addInputPort(ComponentConstants.VALUE_RIGHT_OBJECT);
        if (measure != null) {
            query.addMeasureOutputPort(measure);
        }

        query.addInstallationEnvironment(environment).addInstallationEnvironmentType(environmentType)
            .setDependencyLabelPattern(dependencyLabel).finishQuery();

        List<WorkflowInfo> workflows = myExperimentRESTClient.searchComponents(query);
        for (WorkflowInfo workflow : workflows) {
            MyExperimentActionInfo service = new MyExperimentActionInfo();

            service.setShortname(workflow.getName());
            service.setDescriptor(workflow.getDescriptor());
            service.setInfo(workflow.getDescription());
            service.setUrl(workflow.getContentUri());
            service.setContentType(workflow.getContentType());

            services.add(service);
        }

        return services;
    }

    /**
     * Searches for characterisation components using the set search parameters.
     * 
     * @return a list of service infos that match the search parameters
     * @throws PlanningServiceException 
     */
    public List<IServiceInfo> searchCc() throws PlanningServiceException {
        List<IServiceInfo> services = new ArrayList<IServiceInfo>();

        ComponentQuery query = myExperimentRESTClient.createComponentQuery();
        query.addProfile(profile).addHandlesMimetype(targetMimetype).addHandlesMimetypeWildcard(targetMimetype)
            .addInputPort(ComponentConstants.VALUE_SOURCE_OBJECT);

        if (measure != null) {
            query.addMeasureOutputPort(measure);
        }

        query.addInstallationEnvironment(environment).addInstallationEnvironmentType(environmentType)
            .setDependencyLabelPattern(dependencyLabel).finishQuery();

        List<WorkflowInfo> workflows = myExperimentRESTClient.searchComponents(query);
        for (WorkflowInfo workflow : workflows) {
            MyExperimentActionInfo service = new MyExperimentActionInfo();

            service.setShortname(workflow.getName());
            service.setDescriptor(workflow.getDescriptor());
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

    public String getSourceMimetype() {
        return sourceMimetype;
    }

    public void setSourceMimetype(String sourceMimetype) {
        this.sourceMimetype = sourceMimetype;
    }

    public String getTargetMimetype() {
        return targetMimetype;
    }

    public void setTargetMimetype(String targetMimetype) {
        this.targetMimetype = targetMimetype;
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

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}
