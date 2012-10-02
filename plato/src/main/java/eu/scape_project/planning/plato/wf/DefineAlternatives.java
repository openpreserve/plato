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

import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.AlternativesDefinition;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.interfaces.actions.IPreservationActionRegistry;
import eu.scape_project.planning.services.PlanningServiceException;
import eu.scape_project.planning.services.pa.PreservationActionRegistryDefinition;
import eu.scape_project.planning.services.pa.PreservationActionRegistryFactory;

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

    public DefineAlternatives() {
        this.requiredPlanState = PlanState.TREE_DEFINED;
        this.correspondingPlanState = PlanState.ALTERNATIVES_DEFINED;
    }

    @Override
    protected void saveStepSpecific() {
        prepareChangesForPersist.prepare(plan);

        saveEntity(plan.getTree());
        AlternativesDefinition storedAltDef = (AlternativesDefinition)saveEntity(plan.getAlternativesDefinition());
        
        for (Alternative alt : plan.getAlternativesDefinition().getAlternatives()) {
            Alternative storedAlt = storedAltDef.alternativeByName(alt.getName());
            if (storedAlt != null) {
                alt.setId(storedAlt.getId());
            }
        }
        
        saveEntity(plan.getRecommendation());
    }

    /**
     * Provides a list of actions available in the given registry for the given
     * formatInfo
     * 
     * @param formatInfo
     * @param registry
     * @return
     * @throws PlatoException
     */
    public List<PreservationActionDefinition> queryRegistry(FormatInfo formatInfo,
        PreservationActionRegistryDefinition registry) throws PlatoException {
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
        // query the registry
        return serviceLocator.getAvailableActions(formatInfo);
    }

    public List<PreservationActionRegistryDefinition> getPreservationActionRegistries() throws PlanningServiceException {
        return PreservationActionRegistryFactory.getAvailableRegistries();
    }

    public void createAlternativesForPreservationActions(List<PreservationActionDefinition> selectedActions) {
        for (PreservationActionDefinition action : selectedActions) {
            /*
             * Create a new alternative for this service
             */
            String uniqueName = plan.getAlternativesDefinition().createUniqueName(action.getShortname());
            Alternative a = Alternative.createAlternative(uniqueName, action);

            // and add it to the preservation planning project
            try {
                plan.getAlternativesDefinition().addAlternative(a);
            } catch (PlanningException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
