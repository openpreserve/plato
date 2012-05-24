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
package eu.scape_project.planning.model.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FloatFormatter implements Serializable{
    
    private static final long serialVersionUID = 8876630779501817308L;
    
    private DecimalFormat dfPrec = new DecimalFormat("##############0.0##############", new DecimalFormatSymbols(Locale.US));
    private DecimalFormat df=new DecimalFormat(" ########.##;-########.##");
    private DecimalFormat dfScientific = new DecimalFormat(" 0.########E00;-0.########E00");

    
    /**
     * formats a float value in decimal notation (non scientific)
     * if value is NaN or infinite, Double.toString is used
     * 
     * @param value
     * @return
     */
    public String formatFloatPrecisly(double value){
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return Double.toString(value);
        }
        if (Math.abs(value) > 1000000000000000.0) {
            // ok, this is too much, 
            return dfScientific.format(value);
        }
        return dfPrec.format(value);
    }
    
    /**
     * formats a floating point number
     * if the number has a power > 10, scientific notation is used
     * 
     * @param value
     * @return
     */
    public String formatFloat(double value) {
        double absValue = Math.abs(value);
        if ((absValue >= 10000000000.) || (absValue < 0.01)) {
            return dfScientific.format(value);
        } else {
            return df.format(value);
        }
    }
    
}
