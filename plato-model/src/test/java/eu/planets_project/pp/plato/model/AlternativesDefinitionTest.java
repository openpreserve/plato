package eu.planets_project.pp.plato.model;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.AlternativesDefinition;

public class AlternativesDefinitionTest {
	private AlternativesDefinition alternativesDefinition;
	
	public AlternativesDefinitionTest() {
		alternativesDefinition = new AlternativesDefinition();
	}
	
	@SuppressWarnings("deprecation")
	@Test(expected=PlanningException.class)
	public void addAlternative_addAlternativeWithAlreadyExistingNameThrowsException() throws PlanningException {
		// existing alternatives
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
		List<Alternative> existingAlternatives = new ArrayList<Alternative>();
		existingAlternatives.add(alt1);
		existingAlternatives.add(alt2);
		alternativesDefinition.setAlternatives(existingAlternatives);
				
		// new alternative
		Alternative altToAdd = new Alternative();
		altToAdd.setName("alt2");
		
		alternativesDefinition.addAlternative(altToAdd);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void addAlternative_addNewAlternativeWithUniqueNameIsAdded() throws PlanningException {
		// existing alternatives
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
		List<Alternative> existingAlternatives = new ArrayList<Alternative>();
		existingAlternatives.add(alt1);
		existingAlternatives.add(alt2);
		alternativesDefinition.setAlternatives(existingAlternatives);
				
		// new alternative
		Alternative altToAdd = new Alternative();
		altToAdd.setName("unique name");
		
		alternativesDefinition.addAlternative(altToAdd);
		
		assertEquals(3, alternativesDefinition.getAlternatives().size());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void renameAlternative_renameAlternativeToTheSameNameDoesNothing() throws PlanningException {
		// existing alternatives
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
		List<Alternative> existingAlternatives = new ArrayList<Alternative>();
		existingAlternatives.add(alt1);
		existingAlternatives.add(alt2);
		alternativesDefinition.setAlternatives(existingAlternatives);
						
		alternativesDefinition.renameAlternative(alt2, "alt2");
		
		assertEquals("alt2", alt2.getName());
	}	

	@SuppressWarnings("deprecation")
	@Test(expected=PlanningException.class)
	public void renameAlternative_renameAlternativeToNotUniqueNameThrowsException() throws PlanningException {
		// existing alternatives
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
		List<Alternative> existingAlternatives = new ArrayList<Alternative>();
		existingAlternatives.add(alt1);
		existingAlternatives.add(alt2);
		alternativesDefinition.setAlternatives(existingAlternatives);
						
		alternativesDefinition.renameAlternative(alt2, "alt1");
	}
	
	@SuppressWarnings("deprecation")
	@Test(expected=PlanningException.class)
	public void renameAlternative_renameUnknownAlternativeThrowsException() throws PlanningException {
		// existing alternatives
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
		List<Alternative> existingAlternatives = new ArrayList<Alternative>();
		existingAlternatives.add(alt1);
		existingAlternatives.add(alt2);
		alternativesDefinition.setAlternatives(existingAlternatives);

		Alternative unknownAlternative = new Alternative();
		unknownAlternative.setName("unknown");
		
		alternativesDefinition.renameAlternative(unknownAlternative, "xxxx");
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void renameAlternative_renameAlternativeToUniqueNameSucceeds() throws PlanningException {
		// existing alternatives
		Alternative alt1 = new Alternative();
		alt1.setName("alt1");
		Alternative alt2 = new Alternative();
		alt2.setName("alt2");
		List<Alternative> existingAlternatives = new ArrayList<Alternative>();
		existingAlternatives.add(alt1);
		existingAlternatives.add(alt2);
		alternativesDefinition.setAlternatives(existingAlternatives);
						
		alternativesDefinition.renameAlternative(alt2, "newName");
		
		assertEquals("newName", alt2.getName());
	}	
}
