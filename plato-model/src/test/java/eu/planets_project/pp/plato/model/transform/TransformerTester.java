/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.model.transform;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


import eu.planets_project.pp.plato.model.values.PositiveFloatValue;
import eu.planets_project.pp.plato.model.values.PositiveIntegerValue;




/**
 * NGTest for the transformation Table 
 */
public class TransformerTester {

	NumericTransformer trans = new NumericTransformer();
	List<Double> thres = new ArrayList<Double>(4);
	

	/* Mode: Threshold Stepping
	 * Value: Positve PositiveFloatValue
	 * Thresholds:  increasing
	 * */
	@Test
	public void testThresholdsteppingPositiveFloatValue() {
		trans.setThreshold1(new Double(0));
		trans.setThreshold2(new Double(1));
		trans.setThreshold3(new Double(2));
		trans.setThreshold4(new Double(3));
		trans.setThreshold5(new Double(4));
		trans.setMode(TransformationMode.THRESHOLD_STEPPING);
		PositiveFloatValue value = new PositiveFloatValue();
		value.setValue(0);
		assert (trans.transform(value).getValue()==(double)1);
		value.setValue(1);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(2);
		assert (trans.transform(value).getValue()==(double)3);
		value.setValue(3);
		assert (trans.transform(value).getValue()==(double)4);
		value.setValue(4);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(5);
		assert (trans.transform(value).getValue()==(double)5);
	}

	/* Mode: Threshold Stepping
	 * Value: PositiveIntegerValues
	 * Thresholds:  increasing
	 * */
	@Test
	public void testThresholdsteppingPositiveIntegerValue() {
		thres.clear();
		trans.setThreshold1((double)0);
		trans.setThreshold2((double)1);
		trans.setThreshold3((double)2);
		trans.setThreshold4((double)3);
		trans.setThreshold5((double)4);
		trans.setMode(TransformationMode.THRESHOLD_STEPPING);
		PositiveIntegerValue value = new PositiveIntegerValue();
		value.setValue(0);
		assert (trans.transform(value).getValue()==(double)1);
		value.setValue(1);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(2);
		assert (trans.transform(value).getValue()==(double)3);
		value.setValue(3);
		assert (trans.transform(value).getValue()==(double)4);
		value.setValue(4);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(5);
		assert (trans.transform(value).getValue()==(double)5);
	}
	
	/* Mode: Threshold Stepping
	 * Value: PositiveIntegerValue 
	 * Thresholds:  decreasing
	 * */
	@Test
	public void testThresholdsteppingPositiveIntegerValueInvert() {
		thres.clear();
		trans.setThreshold1((double)5);
		trans.setThreshold2((double)4);
		trans.setThreshold3((double)3);
		trans.setThreshold4((double)2);
		trans.setThreshold5((double)1);
		trans.setMode(TransformationMode.THRESHOLD_STEPPING);
		PositiveIntegerValue value = new PositiveIntegerValue();
		value.setValue(0);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(1);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(2);
		assert (trans.transform(value).getValue()==(double)4);
		value.setValue(3);
		assert (trans.transform(value).getValue()==(double)3);
		value.setValue(4);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(5);
		assert (trans.transform(value).getValue()==(double)1);
		value.setValue(6);
		assert (trans.transform(value).getValue()==(double)0);
	}
	
	/* Mode: Threshold Stepping
	 * Value: PositiveFloatValue 
	 * Thresholds: decreasing
	 * */
	@Test
	public void testThresholdsteppingPositiveFloatValueInvert() {
		thres.clear();
		trans.setThreshold1((double)5);
		trans.setThreshold2((double)4);
		trans.setThreshold3((double)3);
		trans.setThreshold4((double)2);
		trans.setThreshold5((double)1);
		trans.setMode(TransformationMode.THRESHOLD_STEPPING);
		PositiveFloatValue value = new PositiveFloatValue();
		value.setValue(0);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(0.5);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(1);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(2);
		assert (trans.transform(value).getValue()==(double)4);
		value.setValue(2.5);
		assert (trans.transform(value).getValue()==(double)3);
		value.setValue(3);
		assert (trans.transform(value).getValue()==(double)3);
		value.setValue(3.5);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(3.2);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(4);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(4.999);
		assert (trans.transform(value).getValue()==(double)1);
		value.setValue(5);
		assert (trans.transform(value).getValue()==(double)1);
		value.setValue(5.1);
		assert (trans.transform(value).getValue()==(double)0);
		value.setValue(6);
		assert (trans.transform(value).getValue()==(double)0);
	}
	
	/* Mode: Linear
	 * Value: PositiveIntegerValue 
	 * Thresholds:  increasing
	 * */
	@Test
	public void testLinearPositiveIntegerValue() {
		trans.setThreshold1((double)0);
		trans.setThreshold2((double)1);
		trans.setThreshold3((double)2);
		trans.setThreshold4((double)3);
		trans.setThreshold5((double)4);
		trans.setMode(TransformationMode.LINEAR);
		PositiveIntegerValue value = new PositiveIntegerValue();
		value.setValue(0);
		assert (trans.transform(value).getValue()==(double)1);
		value.setValue(1);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(2);
		assert (trans.transform(value).getValue()==(double)3);
		value.setValue(3);
		assert (trans.transform(value).getValue()==(double)4);
		value.setValue(4);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(5);
		assert (trans.transform(value).getValue()==(double)5);
	}
	
	/* Mode: Linear
	 * Value: PositiveFloatValue 
	 * Thresholds:  increasing
	 * */
	@Test
	public void testLinearPositiveFloatValue() {
		trans.setThreshold1((double)2);//1
		trans.setThreshold2((double)4);//2
		trans.setThreshold3((double)6);//3
		trans.setThreshold4((double)8);//4
		trans.setThreshold5((double)10);//5
		trans.setMode(TransformationMode.LINEAR);
		PositiveFloatValue value = new PositiveFloatValue();
		value.setValue(0);
		assert (trans.transform(value).getValue()==(double)0 );
		value.setValue(1.9999);
		assert (trans.transform(value).getValue()==(double)0 );
		value.setValue(2);
		assert (trans.transform(value).getValue()==(double)1 );
		value.setValue(3);
		assert (trans.transform(value).getValue()==(double)1.5);
		value.setValue(4);
		assert (trans.transform(value).getValue()==(double)2);
		value.setValue(8.5);
		assert (trans.transform(value).getValue()==(double)4.25);
		value.setValue(10);
		assert (trans.transform(value).getValue()==(double)5);		
		value.setValue(1000000);
		assert (trans.transform(value).getValue()==(double)5);
		value.setValue(-100000);
		assert (trans.transform(value).getValue()==(double)0 );
		
	}

}
