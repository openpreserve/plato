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
package eu.scape_project.planning.services.pa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.interfaces.actions.IPreservationAction;

/**
 * Factory for preservation actions.
 */
public final class PreservationActionServiceFactory {
    private static final Map<String, String> PRESERVATION_ACTION_SERVICES;

    /**
     * Create an instance of this factory, in the constructor it is populated
     * with services. this instance is never accessed directly, only by
     * getPreservationAction.
     */
    static {
        Map<String, String> services = new HashMap<String, String>();
        services.put("CRiB", "eu.scape_project.planning.services.action.crib_integration.CRiBActionServiceLocator");
        services.put("CRiB-local",
            "eu.scape_project.planning.services.action.crib_integration.TUCRiBActionServiceLocator");
        services.put("Planets-local", "eu.scape_project.planning.services.action.planets.PlanetsMigrationService");
        services.put("Planets-Viewer-local",
            "eu.scape_project.planning.services.action.planets.PlanetsEmulationService");
        services.put("MiniMEE-migration", "at.tuwien.minimee.migration.MiniMeeMigrationService");
        services.put("myExperiment", "eu.scape_project.planning.services.pa.taverna.SSHTavernaMigrationActionService");
        services.put("myExperiment-plan",
            "eu.scape_project.planning.services.pa.taverna.SSHGeneratedTavernaMigrationService");
        PRESERVATION_ACTION_SERVICES = Collections.unmodifiableMap(services);
    }

    /**
     * Private constructor to avoid instantiation.
     */
    private PreservationActionServiceFactory() {
    }

    /**
     * Returns a reference to an executable preservation action which matches
     * the given <code>action</code> definition. The service locator of the
     * action definition must be registered in the application.
     * 
     * @param action
     *            action definition
     * @return {@link IPreservationAction}
     */
    public static IPreservationAction getPreservationAction(final PreservationActionDefinition action) {
        try {
            String actionClassname = PRESERVATION_ACTION_SERVICES.get(action.getActionIdentifier());

            Object serviceLocator = null;
            try {
                serviceLocator = Class.forName(actionClassname).newInstance();
            } catch (ClassNotFoundException e) {
                return null;
            }

            if (serviceLocator instanceof IPreservationAction) {
                IPreservationAction locator = (IPreservationAction) serviceLocator;
                return locator;
            } else {
                throw new IllegalArgumentException(actionClassname + " is not a PreservationAction service.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create preservation action service for '"
                + action.getActionIdentifier() + "'.", e);
        }
    }
}
