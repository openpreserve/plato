package eu.scape_project.planning.model;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SampleRecordsDefinitionTest {

	@Test 
	public void testAddRemoveRecord() {
		
		SampleRecordsDefinition srd = new SampleRecordsDefinition();
		
		SampleObject obj1 = new SampleObject();
		SampleObject obj2 = new SampleObject();
		srd.addRecord(obj1);
		srd.addRecord(obj2);
		Assert.assertTrue(srd.getRecords().contains(obj1));
		Assert.assertTrue(srd.getRecords().contains(obj2));
		srd.removeRecord(obj2);
		Assert.assertTrue(srd.getRecords().contains(obj1));
		Assert.assertFalse(srd.getRecords().contains(obj2));
		srd.removeRecord(obj1);
		Assert.assertTrue(srd.getRecords().size()==0);
		
	}
	
	@Test 
	public void testAddRemoveDuplicate() {
		
		SampleRecordsDefinition srd = new SampleRecordsDefinition();
		
		SampleObject obj1 = new SampleObject();
		SampleObject obj2 = new SampleObject();
		srd.addRecord(obj1);
		srd.addRecord(obj2);
		srd.addRecord(obj2);
		Assert.assertTrue(srd.getRecords().contains(obj1));
		Assert.assertTrue(srd.getRecords().contains(obj2));
		Assert.assertTrue(srd.getRecords().size()==2); 
		srd.removeRecord(obj2);
		Assert.assertTrue(srd.getRecords().contains(obj1));
		Assert.assertFalse(srd.getRecords().contains(obj2));
		srd.removeRecord(obj1);
		Assert.assertTrue(srd.getRecords().size()==0);
		
		
	}
	

	@Test 
	public void testSampleObjectIndexes() {
		SampleRecordsDefinition srd = new SampleRecordsDefinition();
		
		SampleObject obj1 = new SampleObject();
		SampleObject obj2 = new SampleObject();
		SampleObject obj3 = new SampleObject();
		srd.addRecord(obj1);
		srd.addRecord(obj2);
		srd.addRecord(obj3);
		Assert.assertTrue(obj1.getSampleIndex()==1);
		Assert.assertTrue(obj2.getSampleIndex()==2);
		Assert.assertTrue(obj3.getSampleIndex()==3);
	}
}

