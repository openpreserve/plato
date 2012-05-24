/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.services.action.minimee;

import at.tuwien.minimee.emulation.EmulationService;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.interfaces.actions.IEmulationAction;

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
