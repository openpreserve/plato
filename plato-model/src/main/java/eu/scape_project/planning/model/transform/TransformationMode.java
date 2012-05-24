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

package eu.scape_project.planning.model.transform;

/**
 * The transformation mode determines the way numeric transformation is being calucalated.
 * {@link #THRESHOLD_STEPPING} indicates a step-approach, while 
 * {@link #LINEAR} indicates that values between steps are linearly interpolated 
 * @author Christoph Becker
 * @author Kevin Stadler (getName/toString)
 *
 */
public enum TransformationMode {
    THRESHOLD_STEPPING ("Steps"),             
    LINEAR ("Linear");

    /**
     * @return all possible enum values
     */
    public TransformationMode[] getEnumValues() {
        return TransformationMode.values();       
    }
    
    /**
     * a more human friendly name for the enum value
     */
    private String niceName;
    
    private TransformationMode(String niceName) {
        this.niceName = niceName;
    }
    
    public String toString() {
        return this.niceName;
    }

    /**
     * Kept for backwards compatibility
     */
    public String getName() {
        return this.toString();
    }

}