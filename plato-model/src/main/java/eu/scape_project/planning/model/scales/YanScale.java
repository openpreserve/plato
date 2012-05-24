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
package eu.scape_project.planning.model.scales;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eu.scape_project.planning.model.values.YanValue;


@Entity
@DiscriminatorValue("Y")
public class YanScale extends OrdinalScale {

    private static final long serialVersionUID = 595959962684173519L;

    public  String getDisplayName() {
        return "Yes, Acceptable, No";
    }
    
    public YanValue createValue() {
        YanValue v = new YanValue();
        v.setScale(this);
        return v;
    }
    
    
    public YanScale() {
/*        list.add("Yes");
        list.add("Acceptable");
        list.add("No");
*/
        super.setRestriction("Yes/Acceptable/No");
        // this is a Yan-value, the restrictions above must not be changed
        immutableRestriction = true;
    }
    /*
     * this restriction cannot be changed
     */
    @Override
    public void setRestriction(String restriction) {
    }

}
