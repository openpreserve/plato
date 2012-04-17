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
package at.tuwien.minimee.migration;

import eu.planets_project.pp.plato.model.DigitalObject;
import eu.planets_project.pp.plato.model.PlatoException;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.SampleObject;
import eu.planets_project.pp.plato.model.beans.MigrationResult;
import eu.planets_project.pp.plato.model.interfaces.actions.IMigrationAction;
import eu.planets_project.pp.plato.model.measurement.Measurement;

public class MiniMeeMigrationService implements IMigrationAction {

    /**
     * @return null
     */
    public MigrationResult getLastResult() {
        return null;
    }

    public MigrationResult migrate(PreservationActionDefinition action,
            DigitalObject digitalObject) throws PlatoException {
        MigrationService service = new MigrationService();
        long start = System.nanoTime();
        String settings = "";
        if (action.isExecute()) {
            settings = action.getParamByName("settings");
        }
        MigrationResult result = service.migrate(digitalObject.getData().getData(),
                        action.getUrl(),
                        settings);
        // provide a nice name for the resulting object 
        setResultName(result, digitalObject);
        long duration = (System.nanoTime()-start)/(1000000);
        service.addExperience(result.getFeedbackKey(), action.getUrl(), 
                new Measurement("roundtripTimeMS",new Double(duration)));
        return result;
    }
    
    /**
     * The name of the resultObject is not very nice, and sometime not set at all.
     * Therefore we create a new name, based on the name of the sampleObject 
     * and the name of the target format 
     * 
     * @param result
     * @param sampleObject
     */
    private void setResultName(MigrationResult result, DigitalObject sampleObject) {
        DigitalObject resultObject = result.getMigratedObject();
        if (resultObject != null) {
            
            String resultName = "result.";
            if (sampleObject.getFullname() != null) {
                resultName = sampleObject.getFullname() + ".";
            } 
            if (result.getTargetFormat() != null) {
                resultName = resultName + result.getTargetFormat().getDefaultExtension();
                resultObject.getFormatInfo().assignValues(result.getTargetFormat());
            }
            resultObject.setFullname(resultName);
        }        
    }

    public boolean perform(PreservationActionDefinition action,
            SampleObject sampleObject) throws PlatoException {
        migrate(action,sampleObject);
       return true;
    }

}
