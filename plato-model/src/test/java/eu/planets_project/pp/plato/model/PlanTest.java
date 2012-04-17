package eu.planets_project.pp.plato.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import eu.planets_project.pp.plato.exception.PlanningException;

public class PlanTest {
	@Test
	public void initializeExperimentInfos_createNonExistingExperimentInfos() throws PlanningException {
		// set up
		Plan plan = new Plan();
		plan.setAlternativesDefinition(new AlternativesDefinition());
		plan.setSampleRecordsDefinition(new SampleRecordsDefinition());

		Alternative alt1 = new Alternative();
		Alternative alt2 = new Alternative();
		SampleObject obj1 = new SampleObject();
		SampleObject obj2 = new SampleObject();
		
		Experiment exp1 = new Experiment();
		Experiment exp2 = new Experiment();

		HashMap<SampleObject, DetailedExperimentInfo> hm1 = new HashMap<SampleObject, DetailedExperimentInfo>();
		hm1.put(obj1, null);
		hm1.put(obj2, null);
		HashMap<SampleObject, DetailedExperimentInfo> hm2 = new HashMap<SampleObject, DetailedExperimentInfo>();
		hm2.put(obj1, null);
		hm2.put(obj2, null);
		
		exp1.setDetailedInfo(hm1);
		exp2.setDetailedInfo(hm2);
		
		alt1.setExperiment(exp1);
		alt2.setExperiment(exp2);
		
		List<Alternative> alternativeList = new ArrayList<Alternative>();
		alternativeList.add(alt1);
		alternativeList.add(alt2);
		plan.getAlternativesDefinition().setAlternatives(alternativeList);
		
		plan.getSampleRecordsDefinition().addRecord(obj1);
		plan.getSampleRecordsDefinition().addRecord(obj2);
		
		// execute
		plan.initializeExperimentInfos();
		
		// assert
		for (Alternative a : plan.getAlternativesDefinition().getAlternatives()) {
			for (SampleObject s : plan.getSampleRecordsDefinition().getRecords()) {
				DetailedExperimentInfo expInfo = a.getExperiment().getDetailedInfo().get(s);
				assertNotNull(expInfo);
				assertNotNull(expInfo.getProgramOutput());
			}
		}
	}
	
	@Test
	public void initializeExperimentInfos_ExistingExperimentInfosStayUnmodified() throws PlanningException {
		// set up
		Plan plan = new Plan();
		plan.setAlternativesDefinition(new AlternativesDefinition());
		plan.setSampleRecordsDefinition(new SampleRecordsDefinition());

		Alternative alt1 = new Alternative();
		Alternative alt2 = new Alternative();
		SampleObject obj1 = new SampleObject();
		SampleObject obj2 = new SampleObject();
		
		Experiment exp1 = new Experiment();
		Experiment exp2 = new Experiment();

		DetailedExperimentInfo detExpInfo = new DetailedExperimentInfo();
		detExpInfo.setId(88);
		detExpInfo.setProgramOutput("fixed");
		
		HashMap<SampleObject, DetailedExperimentInfo> hm1 = new HashMap<SampleObject, DetailedExperimentInfo>();
		hm1.put(obj1, detExpInfo);
		hm1.put(obj2, detExpInfo);
		HashMap<SampleObject, DetailedExperimentInfo> hm2 = new HashMap<SampleObject, DetailedExperimentInfo>();
		hm2.put(obj1, detExpInfo);
		hm2.put(obj2, detExpInfo);
		
		exp1.setDetailedInfo(hm1);
		exp2.setDetailedInfo(hm2);
		
		alt1.setExperiment(exp1);
		alt2.setExperiment(exp2);
		
		List<Alternative> alternativeList = new ArrayList<Alternative>();
		alternativeList.add(alt1);
		alternativeList.add(alt2);
		plan.getAlternativesDefinition().setAlternatives(alternativeList);
		
		plan.getSampleRecordsDefinition().addRecord(obj1);
		plan.getSampleRecordsDefinition().addRecord(obj2);
		
		// execute
		plan.initializeExperimentInfos();
		
		// assert
		for (Alternative a : plan.getAlternativesDefinition().getAlternatives()) {
			for (SampleObject s : plan.getSampleRecordsDefinition().getRecords()) {
				DetailedExperimentInfo expInfo = a.getExperiment().getDetailedInfo().get(s);
				assertEquals(88, expInfo.getId());
				assertEquals("fixed", expInfo.getProgramOutput());
			}
		}
	}
}
