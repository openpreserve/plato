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


public enum TriggerType {
    NEW_COLLECTION ("New Collection"),
    PERIODIC_REVIEW ("Periodic Review"),
    CHANGED_ENVIRONMENT ("Changed Environment"),
    CHANGED_OBJECTIVE ("Changed Objective"),
    CHANGED_COLLECTION_PROFILE ("Changed Collection Profile");
    
    /**
     * @return all possible enum values
     */
    public TriggerType[] getEnumValues() {
        return TriggerType.values();       
    }
    
    /**
     * a more human friendly name for the enum value
     */
    private String niceName;
    
    private TriggerType(String niceName) {
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
