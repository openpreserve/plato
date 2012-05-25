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
package eu.scape_project.planning;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.ViewWorkflowManager;

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
