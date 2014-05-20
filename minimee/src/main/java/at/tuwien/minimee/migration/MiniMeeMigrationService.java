/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
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
 ******************************************************************************/
package at.tuwien.minimee.migration;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.interfaces.actions.IMigrationAction;
import eu.scape_project.planning.model.measurement.Measurement;

/**
 * A MiniMee migration action.
 */
public class MiniMeeMigrationService implements IMigrationAction {

    @Override
    public MigrationResult migrate(Alternative alternative, DigitalObject digitalObject) throws PlatoException {
        PreservationActionDefinition action = alternative.getAction();

        MigrationService service = new MigrationService();
        long start = System.nanoTime();
        String settings = "";
        if (action.isExecute()) {
            settings = action.getParamByName("settings");
        }
        MigrationResult result = service.migrate(digitalObject.getData().getData(), action.getUrl(), settings);
        setResultName(result, digitalObject);

        long duration = (System.nanoTime() - start) / (1000000);
        service.addExperience(result.getFeedbackKey(), action.getUrl(), new Measurement("roundtripTimeMS", new Double(
            duration)));
        return result;
    }

    /**
     * The name of the resultObject is not very nice, and sometime not set at
     * all. Therefore we create a new name, based on the name of the
     * sampleObject and the name of the target format.
     * 
     * @param result
     *            the result object
     * @param sampleObject
     *            the sample object
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

    @Override
    public boolean perform(Alternative alternative, SampleObject sampleObject) throws PlatoException {
        MigrationResult result = migrate(alternative, sampleObject);
        return result.isSuccessful();
    }

}
