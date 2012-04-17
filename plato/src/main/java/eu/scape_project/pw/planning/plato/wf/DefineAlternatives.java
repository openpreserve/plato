/**
 * 
 */
package eu.scape_project.pw.planning.plato.wf;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.exception.PlanningException;
import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.PlanState;
import eu.planets_project.pp.plato.model.PlatoException;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.interfaces.actions.IPreservationActionRegistry;
import eu.planets_project.pp.plato.services.PlanningServiceException;
import eu.scape_project.pw.planning.services.preservationaction.PreservationActionRegistryDefinition;
import eu.scape_project.pw.planning.services.preservationaction.PreservationActionRegistryFactory;

/**
 * Classed containing the business logic for defining alternatives.
 * 
 * @author Markus Hamm, Michael Kraxner
 */
@Stateful
@ConversationScoped
public class DefineAlternatives extends AbstractWorkflowStep {
	private static final long serialVersionUID = 1966353830236679395L;

	@Inject private Logger log;
	
	public DefineAlternatives() {
		this.requiredPlanState = PlanState.TREE_DEFINED;
		this.correspondingPlanState = PlanState.ALTERNATIVES_DEFINED;
	}

	@Override
	protected void saveStepSpecific() {
		prepareChangesForPersist.prepare(plan);
		
		saveEntity(plan.getTree());
		saveEntity(plan.getAlternativesDefinition());
		saveEntity(plan.getRecommendation());
	}
	
	/**
	 * Provides a list of actions available in the given registry for the given formatInfo
	 * 
	 * @param formatInfo
	 * @param registry
	 * @return
	 * @throws PlatoException
	 */
    public List<PreservationActionDefinition> queryRegistry(FormatInfo formatInfo,
            PreservationActionRegistryDefinition registry) 
            throws PlatoException 
    {
        IPreservationActionRegistry serviceLocator = null;
        try {
            serviceLocator = PreservationActionRegistryFactory.getInstance(registry);
        } catch (IllegalArgumentException e1) {
            throw new PlatoException( "Registry:  " + registry.getShortname() + " has changed and needs to be reconfigured.");
        }
        if (serviceLocator == null) {
            throw new PlatoException ("Failed to access " + registry.getShortname());
        }
        // query the registry
        return serviceLocator.getAvailableActions(formatInfo);
    }
	
    public List<PreservationActionRegistryDefinition> getPreservationActionRegistries() throws PlanningServiceException{
    	return PreservationActionRegistryFactory.getAvailableRegistries();
    }
    
    public void createAlternativesForPreservationActions(List<PreservationActionDefinition> selectedActions){
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
