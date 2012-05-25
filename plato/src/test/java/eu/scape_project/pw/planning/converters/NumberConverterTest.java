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
package eu.scape_project.pw.planning.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.faces.application.FacesMessage;
import javax.faces.convert.ConverterException;

import org.junit.Test;

import eu.scape_project.planning.converters.NumberConverter;

public class NumberConverterTest {

	private NumberConverter numberConverter;
	
	public NumberConverterTest(){
		this.numberConverter = new NumberConverter();
	}
	
	@Test
	public void getAsObject_nullValueThrowsException() {
		try {
			numberConverter.getAsObject(null, null, null);
		}
		catch (ConverterException e) {
			FacesMessage fMessage = e.getFacesMessage();
			assertEquals("Please enter a value", fMessage.getSummary());
			assertEquals(FacesMessage.SEVERITY_ERROR, fMessage.getSeverity());
			return;
		}
		
		fail("Exception is expected.");
	}

	@Test
	public void getAsObject_emptyValueThrowsException() {
		try {
			numberConverter.getAsObject(null, null, "");
		}
		catch (ConverterException e) {
			FacesMessage fMessage = e.getFacesMessage();
			assertEquals("Please enter a value", fMessage.getSummary());
			assertEquals(FacesMessage.SEVERITY_ERROR, fMessage.getSeverity());
			return;
		}
		
		fail("Exception is expected.");
	}
	
	@Test
	public void getAsObject_nonNumericValueThrowsException() {
		try {
			numberConverter.getAsObject(null, null, "12abcd34");
		}
		catch (ConverterException e) {
			FacesMessage fMessage = e.getFacesMessage();
			assertEquals("Please enter a numeric value", fMessage.getSummary());
			assertEquals(FacesMessage.SEVERITY_ERROR, fMessage.getSeverity());
			return;
		}
		
		fail("Exception is expected.");		
	}

	@Test
	public void getAsObject_moreThanOneSeparatorThrowsException() {
		try {
			numberConverter.getAsObject(null, null, "12.000,50");
		}
		catch (ConverterException e) {
			FacesMessage fMessage = e.getFacesMessage();
			assertEquals("Please use . as comma and do not use grouping", fMessage.getSummary());
			assertEquals(FacesMessage.SEVERITY_ERROR, fMessage.getSeverity());
			return;
		}
		
		fail("Exception is expected.");		
	}
	
	@Test
	public void getAsObject_integerNumberConvertsCorrectly() {
		Double result = (Double) numberConverter.getAsObject(null, null, "545");
		assertEquals(545, result, 0.000001);
	}

	@Test
	public void getAsObject_negativeIntegerNumberConvertsCorrectly() {
		Double result = (Double) numberConverter.getAsObject(null, null, "-545");
		assertEquals(-545, result, 0.000001);
	}

	@Test
	public void getAsObject_floatNumberSeparatedByDotConvertsCorrectly() {
		Double result = (Double) numberConverter.getAsObject(null, null, "545.123456");
		assertEquals(545.123456, result, 0.000001);
	}

	@Test
	public void getAsObject_floatNumberSeparatedByCommaConvertsCorrectly() {
		Double result = (Double) numberConverter.getAsObject(null, null, "545,123456");
		assertEquals(545.123456, result, 0.000001);
	}
	
	@Test
	public void getAsString_positiveIntegerAddsComma() {
		String result = numberConverter.getAsString(null, null, 35d);
		assertEquals("35.0" , result);
	}

	@Test
	public void getAsString_negativeIntegerAddsComma() {
		String result = numberConverter.getAsString(null, null, -35d);
		assertEquals("-35.0" , result);
	}
	
	@Test
	public void getAsString_DoubleWith1DecimalConvertsOk() {
		String result = numberConverter.getAsString(null, null, -35.1d);
		assertEquals("-35.1" , result);
	}

	@Test
	public void getAsString_DoubleWith8DecimalConvertsOk() {
		String result = numberConverter.getAsString(null, null, -35000.12345678d);
		assertEquals("-35000.12345678" , result);
	}
}
