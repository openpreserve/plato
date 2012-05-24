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

package eu.scape_project.planning.model.values;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
public class BooleanValue extends OrdinalValue {

    /**
     * 
     */
    private static final long serialVersionUID = 2289628831596905214L;

    public void bool(boolean b) {
        setValue(b ? "Yes" : "No");
    }
    
    @Override
    public void parse(String text) {
        bool("Yes".equalsIgnoreCase(text)
          || "true".equalsIgnoreCase(text)
          || "Y".equalsIgnoreCase(text));
    }
}
