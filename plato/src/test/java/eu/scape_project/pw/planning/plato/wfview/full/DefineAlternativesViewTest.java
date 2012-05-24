package eu.scape_project.pw.planning.plato.wfview.full;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.pw.planning.utils.FacesMessages;

public class DefineAlternativesViewTest {

	DefineAlternativesView defineAlternativesView;
	
	public DefineAlternativesViewTest() {
		defineAlternativesView = new DefineAlternativesView();
		defineAlternativesView.setPlan(mock(Plan.class));
		defineAlternativesView.setFacesMessages(mock(FacesMessages.class));
		defineAlternativesView.setLog(mock(Logger.class));
	}
	
	@Test
	public void tryRemoveAlternative_clickAlternativeOnceDoesNotRemove() {
		Alternative clickedAlternative = new Alternative();
		clickedAlternative.setId(1);
		
		defineAlternativesView.tryRemoveAlternative(clickedAlternative);

		verify(defineAlternativesView.getPlan(), times(0)).removeAlternative(clickedAlternative);
	}

	@Test
	public void tryRemoveAlternative_clickAlternativeTwiceRemovesTheAlternative_notCurrentRecommendationNoMessage() {	
		Alternative clickedAlternative = new Alternative();
		clickedAlternative.setId(1);
		
		when(defineAlternativesView.getPlan().isGivenAlternativeTheCurrentRecommendation(clickedAlternative)).thenReturn(false);
		
		defineAlternativesView.tryRemoveAlternative(clickedAlternative);
		defineAlternativesView.tryRemoveAlternative(clickedAlternative);

		verify(defineAlternativesView.getPlan(), times(1)).removeAlternative(clickedAlternative);		
		verify(defineAlternativesView.getFacesMessages(), times(0)).addInfo(anyString());
	}

	@Test
	public void tryRemoveAlternative_clickAlternativeTwiceRemovesTheAlternative_currentRecommendationShowMessage() {	
		Alternative clickedAlternative = new Alternative();
		clickedAlternative.setId(1);
		
		when(defineAlternativesView.getPlan().isGivenAlternativeTheCurrentRecommendation(clickedAlternative)).thenReturn(true);
		
		defineAlternativesView.tryRemoveAlternative(clickedAlternative);
		defineAlternativesView.tryRemoveAlternative(clickedAlternative);

		verify(defineAlternativesView.getPlan(), times(1)).removeAlternative(clickedAlternative);		
		verify(defineAlternativesView.getFacesMessages(), times(1)).addInfo(anyString());
	}
	
	@Test
	public void tryRemoveAlternative_clickTwoDifferentAlternativesDoesNotRemove() {
		Alternative clickedAlternativeOne = new Alternative();
		clickedAlternativeOne.setId(1);
		Alternative clickedAlternativeTwo = new Alternative();
		clickedAlternativeTwo.setId(2);
		
		defineAlternativesView.tryRemoveAlternative(clickedAlternativeOne);
		defineAlternativesView.tryRemoveAlternative(clickedAlternativeTwo);

		verify(defineAlternativesView.getPlan(), times(0)).removeAlternative(clickedAlternativeOne);
		verify(defineAlternativesView.getPlan(), times(0)).removeAlternative(clickedAlternativeTwo);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void editAlternative_editExistingAlternativeChangesExistingOne() throws PlanningException {
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
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
	
	@SuppressWarnings("deprecation")
	@Test
	public void editAlternative_editNewAlternativeAddsNewAlternativeToList() throws PlanningException {
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
		List<Alternative> existingAlternatives = new ArrayList<Alternative>();
		existingAlternatives.add(alt1);
		existingAlternatives.add(alt2);
		defineAlternativesView.setAlternatives(existingAlternatives);
		
		Alternative altNew = new Alternative();
		altNew.setName("altNew");
		defineAlternativesView.setEditableAlternative(altNew);
		defineAlternativesView.editAlternative();
		
		verify(defineAlternativesView.getPlan(), times(1)).addAlternative(altNew);
		verify(defineAlternativesView.getPlan(), times(0)).renameAlternative(altNew, "newName");
	}	
}
