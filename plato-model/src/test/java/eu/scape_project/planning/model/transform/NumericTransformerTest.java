package eu.scape_project.planning.model.transform;

import junit.framework.Assert;

import org.junit.Test;

import eu.scape_project.planning.model.values.FloatValue;
import eu.scape_project.planning.model.values.TargetValue;

public class NumericTransformerTest {

	@Test
	public void testDefaults() {
		NumericTransformer numTran = new NumericTransformer();
		numTran.defaults(5, 1);
		Assert.assertTrue(numTran.getThreshold1()==1);
		Assert.assertTrue(numTran.getThreshold2()==2);
		Assert.assertTrue(numTran.getThreshold3()==3);
		Assert.assertTrue(numTran.getThreshold4()==4);
		Assert.assertTrue(numTran.getThreshold5()==5);
		numTran.defaults(1.2, 0.8);
		Assert.assertTrue(numTran.getThreshold1()==0.8);
		Assert.assertTrue(numTran.getThreshold2()==0.9);
		Assert.assertTrue(numTran.getThreshold3()==1.0);
		Assert.assertTrue(numTran.getThreshold4()==1.1);
		Assert.assertTrue(numTran.getThreshold5()==1.2);
	}
	
	@Test
	public void testTransformBack_increasingOrder() {
		NumericTransformer numTran = new NumericTransformer();
		numTran.defaults(1.2, 0.8);
		double res = numTran.transformBack(4.5);
		Assert.assertTrue(res==1.2);
	}
	
	@Test
	public void testTransformBack_decreasingOrder() {
		NumericTransformer numTran = new NumericTransformer();
		numTran.defaults(0.8, 1.2);
		double res = numTran.transformBack(4.5);
		Assert.assertTrue(res==0.8);
	}
	
	@Test 
	public void testTransformThresholdStepping_increasingOrder() {
		NumericTransformer numTran = new NumericTransformer();
		numTran.setMode(TransformationMode.THRESHOLD_STEPPING);
		numTran.defaults(1.2, 0.8);
		FloatValue value = new FloatValue();
		value.setValue(1.12);
		TargetValue res = numTran.transform(value);
		Assert.assertTrue(res.getValue()==4);
		value.setValue(0.81);
		res = numTran.transform(value);
		Assert.assertTrue(res.getValue()==1);
	}
	
	@Test 
	public void testTransformThresholdStepping_decreasingOrder() {
		NumericTransformer numTran = new NumericTransformer();
		numTran.setMode(TransformationMode.THRESHOLD_STEPPING);
		numTran.defaults(0.8, 1.2);
		FloatValue value = new FloatValue();
		value.setValue(1.201);
		TargetValue res = numTran.transform(value);
		Assert.assertTrue(res.getValue()==0);
		value.setValue(1.02);
		res = numTran.transform(value);
		Assert.assertTrue(res.getValue()==2);
	}
	
	@Test 
	public void testTransformLinear_increasingOrder() {
		NumericTransformer numTran = new NumericTransformer();
		numTran.setMode(TransformationMode.LINEAR);
		numTran.defaults(1.2, 0.8);
		FloatValue value = new FloatValue();
		value.setValue(0.5);
		TargetValue res = numTran.transform(value);
		Assert.assertTrue(res.getValue()==0);
		value.setValue(0.85);
		res = numTran.transform(value);
		//System.out.println(res.getValue());
		Assert.assertTrue(Math.abs(res.getValue()-1.5)<=10e-8);
		
		FloatValue x1 = new FloatValue();
		x1.setValue(0.91);
		FloatValue x2 = new FloatValue();
		x2.setValue(1.07);
		TargetValue y1 = numTran.transform(x1);
		TargetValue y2 = numTran.transform(x2);
		FloatValue  xt = new FloatValue();
		xt.setValue(1.02);
		double expected = ((y2.getValue()-y1.getValue())/(x2.getValue()-x1.getValue()))
				*(xt.getValue()-x1.getValue())+y1.getValue();
		TargetValue rez = numTran.transform(xt);
		//System.out.println(expected + " " + rez.getValue());
		Assert.assertTrue(Math.abs(rez.getValue()-expected)<=10e-8);
	}
	
	@Test
	public void testTransformLinear_decreasingOrder() {
		NumericTransformer numTran = new NumericTransformer();
		numTran.setMode(TransformationMode.LINEAR);
		numTran.defaults(0.8, 1.2);
		
		FloatValue x1 = new FloatValue();
		x1.setValue(0.91);
		FloatValue x2 = new FloatValue();
		x2.setValue(1.07);
		TargetValue y1 = numTran.transform(x1);
		TargetValue y2 = numTran.transform(x2);
		FloatValue  xt = new FloatValue();
		xt.setValue(1.02);
		double expected = ((y2.getValue()-y1.getValue())/(x2.getValue()-x1.getValue()))
				*(xt.getValue()-x1.getValue())+y1.getValue();
		TargetValue rez = numTran.transform(xt);
		//System.out.println(expected + " " + rez.getValue());
		Assert.assertTrue(Math.abs(rez.getValue()-expected)<=10e-8);
	}
}
