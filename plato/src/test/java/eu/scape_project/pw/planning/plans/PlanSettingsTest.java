package eu.scape_project.pw.planning.plans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;

import eu.planets_project.pp.plato.model.Alternative;
import eu.planets_project.pp.plato.model.AlternativesDefinition;
import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.scape_project.pw.planning.utils.FacesMessages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlanSettingsTest {
//
//	PlanSettingsView planSettings;
//	
//	public PlanSettingsTest() {
//		planSettings = new PlanSettingsView();
//	}
//	
//	public void auth_correctpassordActivatesActions() {
//		PreservationActionDefinition action1 = mock(PreservationActionDefinition.class);
//		when(action1.getActionIdentifier()).thenReturn("unknown");
//		PreservationActionDefinition action2 = mock(PreservationActionDefinition.class);
//		when(action2.getActionIdentifier()).thenReturn("XXminimeeXX");
//		PreservationActionDefinition action3 = mock(PreservationActionDefinition.class);
//		when(action3.getActionIdentifier()).thenReturn("XXminimeeXX");
//		
//		Alternative alt1 = new Alternative();
//		Alternative alt2 = new Alternative();
//		Alternative alt3 = new Alternative();
//		alt1.setAction(action1);
//		alt2.setAction(action2);
//		alt3.setAction(action3);
//
//		List<Alternative> alternatives = new ArrayList<Alternative>();
//		
//		AlternativesDefinition altDef = new AlternativesDefinition();
//		altDef.setAlternatives(alternatives);
//		
//		Plan plan = new Plan();
//		plan.setAlternativesDefinition(altDef);
//		
//		FacesMessages facesMessages = mock(FacesMessages.class);
//		
//		planSettings.setPlan(plan);
//		planSettings.setFacesMessages(facesMessages);
//		planSettings.set
//		
//		planSettings.authenticate();
//	}
}
