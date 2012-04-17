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
package eu.planets_project.pp.plato.services.action.minimee;

import at.tuwien.minimee.emulation.EmulationService;
import eu.planets_project.pp.plato.model.PlatoException;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.interfaces.actions.IEmulationAction;

public class MiniMeeEmulationService implements IEmulationAction {

    private String lastSessionID;
    
    public String startSession(PreservationActionDefinition action,
            SampleObject sampleObject) throws PlatoException {
        if (perform(action, sampleObject))
            return lastSessionID;
        // never reached: perform is only true, if session ID is an URL 
        return "";
    }
    
    public boolean perform(PreservationActionDefinition action,
            SampleObject sampleObject) throws PlatoException {
        
        /*
         * MiniMEE uses GRATE for emulation
         * sessionids stay valid and need not be refreshed every time the service is called
         */
      //  if (action.getParamByName("sessionid") == null) { 
            lastSessionID = new EmulationService().startSession(
                    sampleObject.getShortName().replace(" ","_")+"."+sampleObject.getFormatInfo().getDefaultExtension(),
                    sampleObject.getData().getData(), action.getUrl());
            //lastSessionID = lastSessionID + action.getParamByName("filetype");
            // it is not very nice to store it as action param... but its MiniMEE specific
       //     action.setParamByName("sessionid", lastSessionID);
       // }
        return true;
    }

}
