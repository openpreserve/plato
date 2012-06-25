package eu.scape_project.planning.model.measurement;

import org.junit.Assert;
import org.junit.Test;

import eu.scape_project.planning.model.values.FloatValue;

public class MeasurementsTest {

	@Test
	public void testGetAverage() {
		Measurements mes = new Measurements();
		Measurement m1 = new Measurement("value",10);
		Measurement m2 = new Measurement("value",6);
		mes.addMeasurement(m1);
		mes.addMeasurement(m2);
		Measurement res = mes.getAverage();
		FloatValue fv = (FloatValue) res.getValue();
		Assert.assertTrue(fv.getValue()==8);
	}
	
}
