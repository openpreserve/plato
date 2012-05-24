package eu.scape_project.pw.planning;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.pw.planning.plato.wfview.ViewWorkflowManager;
import java.io.Serializable;

/**
 * Factory class responsible for producing/injecting conversation-scoped objects.
 * 
 * @author Markus Hamm
 */
@ConversationScoped
public class ConversationScopeProducer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private Conversation conversation;
	
	@Inject
	private ViewWorkflowManager viewWorkflowManager;
	
	@Produces
	@LoadedPlan
	public Plan getLoadedPlan() {
		if (conversation.isTransient()) {
			return null;
		}
		
		return viewWorkflowManager.getPlan();
	}
}
