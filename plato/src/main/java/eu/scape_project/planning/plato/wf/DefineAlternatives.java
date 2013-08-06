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

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.AlternativesDefinition;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.services.IServiceInfo;
import eu.scape_project.planning.services.PlanningServiceException;
import eu.scape_project.planning.services.action.IPreservationActionRegistry;
import eu.scape_project.planning.services.pa.PreservationActionRegistryDefinition;
import eu.scape_project.planning.services.pa.PreservationActionRegistryFactory;

import org.slf4j.Logger;

/**
 * Classed containing the business logic for defining alternatives.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@Stateful
@ConversationScoped
public class DefineAlternatives extends AbstractWorkflowStep {
    private static final long serialVersionUID = 1966353830236679395L;

    @Inject
    private Logger log;

    /**
     * Creates a new define alternative object.
     */
    public DefineAlternatives() {
        this.requiredPlanState = PlanState.TREE_DEFINED;
        this.correspondingPlanState = PlanState.ALTERNATIVES_DEFINED;
    }

    @Override
    protected void saveStepSpecific() {
        prepareChangesForPersist.prepare(plan);

        saveEntity(plan.getTree());
        plan.setAlternativesDefinition((AlternativesDefinition) saveEntity(plan.getAlternativesDefinition()));

        saveEntity(plan.getRecommendation());
    }

    /**
     * Provides a list of actions available in the given registry for the given
     * formatInfo.
     * 
     * @param formatInfo
     *            format info used to query
     * @param registry
     *            registry to query
     * @return a list of preservation action infos
     * @throws PlatoException
     *             if the registry is not properly configured
     */
    public List<IServiceInfo> queryRegistry(FormatInfo formatInfo, PreservationActionRegistryDefinition registry)
        throws PlatoException {
        log.debug("Loading preservation action services from registry [{}]", registry.getShortname());
        IPreservationActionRegistry serviceLocator = null;

        try {
            serviceLocator = PreservationActionRegistryFactory.getInstance(registry);
        } catch (IllegalArgumentException e1) {
            throw new PlatoException("Registry:  " + registry.getShortname()
                + " has changed and needs to be reconfigured.");
        }

        if (serviceLocator == null) {
            throw new PlatoException("Failed to access " + registry.getShortname());
        }

        return serviceLocator.getAvailableActions(formatInfo);
    }

    /**
     * Returns a list of registered preservation action registries.
     * 
     * @return a list of registries
     * @throws PlanningServiceException
     *             if the registries could not be found
     */
    public List<PreservationActionRegistryDefinition> getPreservationActionRegistries() throws PlanningServiceException {
        return PreservationActionRegistryFactory.getAvailableRegistries();
    }

    /**
     * Creates an alternative from a action info and adds it to the plan.
     * 
     * The provided name is converted to a unique name for the plan.
     * 
     * @param actionInfo
     *            the source action info
     * @return the new alternative
     * @throws PlanningException
     *             if the alternative could not be added
     */
    public Alternative addAlternative(IServiceInfo actionInfo) throws PlanningException {
        PreservationActionDefinition actionDefinition = new PreservationActionDefinition();
        actionDefinition.setActionIdentifier(actionInfo.getServiceIdentifier());
        actionDefinition.setShortname(actionInfo.getShortname());
        actionDefinition.setDescriptor(actionInfo.getDescriptor());
        actionDefinition.setUrl(actionInfo.getUrl());
        actionDefinition.setInfo(actionInfo.getInfo());

        String uniqueName = plan.getAlternativesDefinition().createUniqueName(actionDefinition.getShortname());
        Alternative a = Alternative.createAlternative(uniqueName, actionDefinition);
        plan.getAlternativesDefinition().addAlternative(a);
        return a;
    }

    /**
     * Creates an alternative with the provided name and description and adds it
     * to the plan.
     * 
     * The provided name is converted to a unique name for the plan.
     * 
     * @param name
     *            alternative name
     * @param description
     *            alternative description
     * @return the new alternative
     * @throws PlanningException
     *             if the alternative could not be added
     */
    @SuppressWarnings("deprecation")
    public Alternative addAlternative(String name, String description) throws PlanningException {
        Alternative a = Alternative.createAlternative();
        a.setName(plan.getAlternativesDefinition().createUniqueName(name));
        a.setDescription(description);
        plan.getAlternativesDefinition().addAlternative(a);
        return a;
    }

    /**
     * Adds the provided alternative to the plan.
     * 
     * @param alternative
     *            the alternative to add
     * @throws PlanningException
     *             if the alternative could not be added
     */
    public void addAlternative(Alternative alternative) throws PlanningException {
        plan.getAlternativesDefinition().addAlternative(alternative);
    }
}
