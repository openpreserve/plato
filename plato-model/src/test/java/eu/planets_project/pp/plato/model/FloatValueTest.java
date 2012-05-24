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
package eu.planets_project.pp.plato.model;


import org.junit.Test;

import eu.scape_project.planning.model.values.FloatValue;


public class FloatValueTest {
    
    @Test
    public void testFormattedValue() {
        FloatValue fv = new FloatValue();
        
        double d = 1.234567890123450;
        double d2 = d;
        
        for (int i = 1; i < 30; i++) {
            fv.setValue(d);
//            System.out.println(fv.getFormattedValue());
            fv.setValue(-d);
//            System.out.println(fv.getFormattedValue());
            fv.setValue(d2);
//            System.out.println(fv.getFormattedValue());
            fv.setValue(-d2);
//            System.out.println(fv.getFormattedValue());
            d = d * 10;
            d2 = d2 / 10.;
        }
        
        
    }

}
