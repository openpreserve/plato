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

package eu.scape_project.pw.planning.services.preservationaction;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.interfaces.actions.IPreservationAction;
/**
 * 
 *  
 * @author Michael Kraxner
 *
 */
public class PreservationActionServiceFactory {
    private static Logger log = LoggerFactory.getLogger(PreservationActionServiceFactory.class);

    private static Map<String, String> preservationActionServices;

    private static PreservationActionServiceFactory me;

    /** 
     * create an instance of this factory, in the constructor it is populated with services. 
     * this instance is never accessed directly, only by getPreservationAction   
     */  
    static {
        me = new PreservationActionServiceFactory();
    }

    /**
     * Creates a mapping of service locator names to corresponding preservation actions for
     * all known preservation action remote-endpoints.
     *
     */
    private PreservationActionServiceFactory() {
        // this map is populated once, then only read - it needs not to be a synchronised map 
        preservationActionServices = new HashMap<String, String>();
        preservationActionServices
                .put("CRiB", "eu.planets_project.pp.plato.services.action.crib_integration.CRiBActionServiceLocator");
        preservationActionServices
                .put("CRiB-local", "eu.planets_project.pp.plato.services.action.crib_integration.TUCRiBActionServiceLocator");
        preservationActionServices
                .put("Planets-local", "eu.planets_project.pp.plato.services.action.planets.PlanetsMigrationService");
        preservationActionServices
                .put("Planets-Viewer-local", "eu.planets_project.pp.plato.services.action.planets.PlanetsEmulationService");
        preservationActionServices
                .put("MiniMEE-migration", "at.tuwien.minimee.migration.MiniMeeMigrationService");
        preservationActionServices
                .put("MiniMEE-emulation", "eu.planets_project.pp.plato.services.action.minimee.MiniMeeEmulationService");

    }

    /**
     * Returns a reference to an executable preservation action which matches the given <code>action</code> definition.
     * The service locator of the action definition must be registered in the application. 
     *   
     * @param action
     * @return {@link IPreservationAction}
     */
    public static IPreservationAction getPreservationAction(
            PreservationActionDefinition action) {
        try {

            String actionClassname = preservationActionServices.get(action
                    .getActionIdentifier()); //
            
            Object serviceLocator = null;
            try {
                serviceLocator = Class.forName(actionClassname).newInstance();
            } catch(ClassNotFoundException e) {
                return null;
            }
            
            if (serviceLocator instanceof IPreservationAction) {
                IPreservationAction locator = (IPreservationAction) serviceLocator;
                return locator;
            } else
                throw new IllegalArgumentException(actionClassname
                        + " is not a PreservationAction service.");
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Could not create preservation action service for '" + action.getActionIdentifier() + "'.", e);
        }
    }
}
