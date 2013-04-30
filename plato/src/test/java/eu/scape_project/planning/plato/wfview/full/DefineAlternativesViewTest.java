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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.utils.FacesMessages;

import org.junit.Test;
import org.slf4j.Logger;

public class DefineAlternativesViewTest {

    DefineAlternativesView defineAlternativesView;

    public DefineAlternativesViewTest() {
        defineAlternativesView = new DefineAlternativesView();
        defineAlternativesView.setPlan(mock(Plan.class));
        defineAlternativesView.setFacesMessages(mock(FacesMessages.class));
        defineAlternativesView.setLog(mock(Logger.class));
    }

    @Test
    public void tryRemoveAlternative_clickAlternativeRemovesTheAlternative_notCurrentRecommendationNoMessage() {
        Alternative clickedAlternative = new Alternative("alt1", "alt1");
        clickedAlternative.setId(1);

        when(defineAlternativesView.getPlan().isGivenAlternativeTheCurrentRecommendation(clickedAlternative))
            .thenReturn(false);

        defineAlternativesView.removeAlternative(clickedAlternative);

        verify(defineAlternativesView.getPlan(), times(1)).removeAlternative(clickedAlternative);
        verify(defineAlternativesView.getFacesMessages(), times(0)).addInfo(anyString());
    }

    @Test
    public void tryRemoveAlternative_clickAlternativeRemovesTheAlternative_currentRecommendationShowMessage() {
        Alternative clickedAlternative = new Alternative("alt1", "alt1");
        clickedAlternative.setId(1);

        when(defineAlternativesView.getPlan().isGivenAlternativeTheCurrentRecommendation(clickedAlternative))
            .thenReturn(true);

        defineAlternativesView.removeAlternative(clickedAlternative);

        verify(defineAlternativesView.getPlan(), times(1)).removeAlternative(clickedAlternative);
        verify(defineAlternativesView.getFacesMessages(), times(1)).addInfo(anyString());
    }

    @Test
    public void editAlternative_editExistingAlternativeChangesExistingOne() throws PlanningException {
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");

        List<Alternative> existingAlternatives = new ArrayList<Alternative>();
        existingAlternatives.add(alt1);
        existingAlternatives.add(alt2);
        defineAlternativesView.setAlternatives(existingAlternatives);

        defineAlternativesView.setEditableAlternative(alt1);
        defineAlternativesView.setEditableAlternativeName("newName");

        defineAlternativesView.editAlternative();

        verify(defineAlternativesView.getPlan(), times(0)).addAlternative(alt1);
        verify(defineAlternativesView.getPlan(), times(1)).renameAlternative(alt1, "newName");
    }

    @Test
    public void editAlternative_editNewAlternativeAddsNewAlternativeToList() throws PlanningException {
        Alternative alt1 = new Alternative("alt1", "alt1");
        Alternative alt2 = new Alternative("alt2", "alt2");

        List<Alternative> existingAlternatives = new ArrayList<Alternative>();
        existingAlternatives.add(alt1);
        existingAlternatives.add(alt2);
        defineAlternativesView.setAlternatives(existingAlternatives);

        Alternative altNew = new Alternative("alt new", "altnew");
        defineAlternativesView.setEditableAlternative(altNew);
        defineAlternativesView.editAlternative();

        verify(defineAlternativesView.getPlan(), times(1)).addAlternative(altNew);
        verify(defineAlternativesView.getPlan(), times(0)).renameAlternative(altNew, "newName");
    }
}
