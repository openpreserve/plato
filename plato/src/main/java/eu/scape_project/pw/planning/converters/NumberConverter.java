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
package eu.scape_project.pw.planning.converters;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * Converter responsible for transforming between input number as double and its string representation
 *
 * @author Hannes Kulovits, Markus Hamm
 */
public class NumberConverter implements Converter, Serializable {

    private static final long serialVersionUID = -6674250183273455339L;

    /**
     * Method responsible for converting the given input string to the wanted double number.
     *
     * @param value Input string to be converted.
     *
     * @throws ConverterException if the input cannot be converted to double.
     * @return Input converted to double value. 
     *
     */
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	// no value provided
        if (value == null  || value.trim().length() == 0) {
            FacesMessage message = new FacesMessage();
            message.setSummary("Please enter a value");
            message.setDetail("No value provided");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(message);
        }
        
        // be aware of grouping separators (like 1000 dots, etc.)
        int nrOfCommasAndDots = value.replaceAll("[^.,]", "").length();
        if (nrOfCommasAndDots > 1) {
            FacesMessage message = new FacesMessage();
            message.setSummary("Please use . as comma and do not use grouping");
            message.setDetail("Only one comma allowed");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(message);
        }
        
        // try to convert the given input
	    try {
	    	// to be able to convert numbers separated by , -> convert , to .
	    	value = value.replace(',', '.');
	    	
	        return Double.valueOf(Double.parseDouble(value));
	    } 
	    catch (NumberFormatException e) { // not a number
	        FacesMessage message = new FacesMessage();
	        message.setSummary("Please enter a numeric value");
	        message.setDetail("Provided value is not a double");
	        message.setSeverity(FacesMessage.SEVERITY_ERROR);
	        throw new ConverterException(message);
	    }
    }

    /**
     * Method responsible for converting a double value to the representative display string.
     * 
     * @param value Integer or Double representation of the entered value.
     *
     * @return String representation of the value for user display.
     */
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null){
            return "";
        }
        return Double.toString((Double)value);
    }
}
