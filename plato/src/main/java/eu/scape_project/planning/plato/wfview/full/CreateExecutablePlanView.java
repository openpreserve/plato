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

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.manager.DigitalObjectManager;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.plato.wf.AbstractWorkflowStep;
import eu.scape_project.planning.plato.wf.CreateExecutablePlan;
import eu.scape_project.planning.plato.wfview.AbstractView;
import eu.scape_project.planning.utils.ParserException;
import eu.scape_project.planning.xml.C3POProfileParser;

/**
 * View bean for step Create Executable Plan.
 */
@Named("createExecutablePlan")
@ConversationScoped
public class CreateExecutablePlanView extends AbstractView {
    private static final long serialVersionUID = 1L;

    @Inject
    private CreateExecutablePlan createExecutablePlan;

    @Inject
    private DigitalObjectManager digitalObjectManager;

    /**
     * Default constructor.
     */
    public CreateExecutablePlanView() {
        currentPlanState = PlanState.ANALYSED;
        name = "Create Executable Plan";
        viewUrl = "/plan/createexecutableplan.jsf";
        group = "menu.buildPreservationPlan";
    }

    @Override
    protected AbstractWorkflowStep getWfStep() {
        return createExecutablePlan;
    }

    public boolean isCollectionProfileDefined() {
        return plan.getSampleRecordsDefinition().getCollectionProfile() != null;
    }

    /**
     * Returns the list of objects specified in the collection profile.
     * 
     * @return the list of objects or null
     */
    public List<String> getCollectionProfileElements() {

        DigitalObject profile = plan.getSampleRecordsDefinition().getCollectionProfile().getProfile();

        try {
            DigitalObject datafilledProfile = digitalObjectManager.getCopyOfDataFilledDigitalObject(profile);

            C3POProfileParser parser = new C3POProfileParser();
            parser.read(new ByteArrayInputStream(datafilledProfile.getData().getRealByteStream().getData()), false);

            List<String> elements = parser.getObjectIdentifiers();
            elements.addAll(elements);
            elements.addAll(elements);
            elements.addAll(elements);
            elements.addAll(elements);
            elements.addAll(elements);
            elements.addAll(elements);
            elements.addAll(elements);
            elements.addAll(elements);
            elements.addAll(elements);
            return elements;

        } catch (StorageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }
}
