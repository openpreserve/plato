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
/**
 * 
 */
package eu.scape_project.planning.plato.wf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.commons.configuration.Configuration;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.repository.SCAPEPlanManagementClient;
import eu.scape_project.planning.sla.TriggerGenerator;
import eu.scape_project.planning.utils.ConfigurationLoader;
import eu.scape_project.planning.utils.OS;
import eu.scape_project.planning.validation.ValidationError;
import eu.scape_project.planning.xml.ProjectExportAction;
import eu.scape_project.watch.domain.AsyncRequest;
import eu.scape_project.watch.domain.Trigger;
import eu.scape_project.watch.utils.KBUtils;

/**
 * @author Michael Kraxner
 * 
 */
@Stateful
@ConversationScoped
public class ValidatePlan extends AbstractWorkflowStep {
    private static final long serialVersionUID = 7862746302624511130L;

    @Inject
    private Logger log;

    @Inject
    private ProjectExportAction projectExport;
    

    public ValidatePlan() {
        requiredPlanState = PlanState.PLAN_DEFINED;
        correspondingPlanState = PlanState.PLAN_VALIDATED;
    }

    public void init(Plan p) {
        super.init(p);
        for (Leaf l : plan.getTree().getRoot().getAllLeaves()) {
            l.initTransformer();
        }
    }

    /**
     * Method responsible for approving the current plan.
     */
    public void approvePlan() {
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        // proceed planstate to PLAN_VALIDATED
        boolean success = proceed(validationErrors);

        if (success) {
            log.info("Approved plan with id " + plan.getId());
        } else {
            log.warn("Approvement of plan with id " + plan.getId() + " failed");
        }
    }

    /**
     * Method responsible for revising the current approved plan.
     */
    public void revisePlan() {
        // save does the reset of planstate to PLAN_DEFINED for us
        save();
        log.info("Revised plan with id " + plan.getId());
    }

    @Override
    protected void saveStepSpecific() {
        // no custom save operation is needed here
    }

    public void deployPlan(String endpoint, String user, String password) throws PlanningException {

        SCAPEPlanManagementClient planManagement = new SCAPEPlanManagementClient(endpoint, user, password);
        
        String planIdentifier;
        try {
            planIdentifier = planManagement.reservePlanIdentifier();
        } catch (Exception e) {
            throw new PlanningException("Could not reserve Identifier.", e);
        } catch (Throwable e) {
            throw new PlanningException("Could not reserve Identifier.", e);
        }
        this.plan.getPlanProperties().setRepositoryIdentifier(planIdentifier);
        saveWithoutModifyingPlanState();
        

        String binarydataTempPath = OS.getTmpPath() + planIdentifier + "/";
        File binarydataTempDir = new File(binarydataTempPath);
        binarydataTempDir.mkdirs();
        File planFile = new File(binarydataTempPath + "plan.xml");
        try {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(planFile));
                projectExport.exportComplete(plan.getPlanProperties().getId(), out, binarydataTempPath);
                out.flush();
                out.close();
                
                planManagement.deployPlan(planIdentifier, new FileInputStream(planFile));
            } catch (Exception e) {
                throw new PlanningException("Failed to generate plan.", e);
            }
        } finally {
            OS.deleteDirectory(binarydataTempDir);
        }
    }
    
    public void deployTriggers() throws PlanningException{
    	
    	TriggerGenerator triggerGen = new TriggerGenerator();
    	List<Trigger> triggers = triggerGen.generateTriggers(user.getEmail(), 24*3600*1000, plan);
    	
    	if (triggers.size() > 0) {
	        try {
	            ConfigurationLoader configurationLoader = new ConfigurationLoader();
	            Configuration config = configurationLoader.load();
	            String watchEndpoint = config.getString("watch.rest.uri");
	            String watchUser = config.getString("watch.rest.user");
	            String watchPassword = config.getString("watch.rest.password");
	        	
				final ClientConfig cc = new DefaultClientConfig();
				cc.getClasses().add(JacksonJsonProvider.class);
				final Client clientWithJacksonSerializer = Client.create(cc);
				clientWithJacksonSerializer.addFilter(new LoggingFilter());
				clientWithJacksonSerializer.addFilter(new HTTPBasicAuthFilter(watchUser, watchPassword));
				WebResource resource = clientWithJacksonSerializer.resource(watchEndpoint);

				final AsyncRequest areq = new AsyncRequest("monitor plan: " + plan.getPlanProperties().getName(), triggers);
				final AsyncRequest areq2 = resource.path(KBUtils.ASYNC_REQUEST + ".json/new").accept(MediaType.APPLICATION_JSON)
					      .post(AsyncRequest.class, areq);
			} catch (Exception e) {
				throw new PlanningException("Failed to access endpoint.", e);
			}
    	} else {
    		throw new PlanningException("No triggers to deploy.");
    	}
    }
}
