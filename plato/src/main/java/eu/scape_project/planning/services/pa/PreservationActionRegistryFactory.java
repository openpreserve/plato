/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.services.PlanningServiceException;
import eu.scape_project.planning.services.action.IPreservationActionRegistry;
import eu.scape_project.planning.xml.StrictErrorHandler;

/**
 * Provides a list of defined preservation service registries. Creates instances
 * of preservation service registries.
 * 
 * @author Michael Kraxner
 * 
 */
public class PreservationActionRegistryFactory {
    private static Logger log = LoggerFactory.getLogger(PreservationActionRegistryFactory.class);

    /**
     * Returns an instance of a IPreservationActionRegistry, which is already
     * connected to the remote endpoint.
     * 
     * @param registry
     *            defines type and loaction of the registry
     * @return {@link IPreservationActionRegistry}
     */
    static public IPreservationActionRegistry getInstance(PreservationActionRegistryDefinition registry)
        throws IllegalArgumentException {
        try {
            Class serviceLocatorClass = Class.forName(registry.getType());

            Object serviceLocator = serviceLocatorClass.newInstance();
            if (serviceLocator instanceof IPreservationActionRegistry) {
                IPreservationActionRegistry locator = null;
                locator = (IPreservationActionRegistry) serviceLocator;

                locator.connect(registry.getUrl());
                return locator;
            } else
                throw new IllegalArgumentException("schema  " + registry.getType()
                    + " is not a IPreservationActionRegistry.");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("schema '" + registry.getType()
                + "' is not a IPreservationActionRegistry", e);
        }
    }

    static public List<PreservationActionRegistryDefinition> getAvailableRegistries() throws PlanningServiceException {
        ArrayList<PreservationActionRegistryDefinition> allRegistries = new ArrayList<PreservationActionRegistryDefinition>();

        String configFile = "data/services/PreservationActionRegistries.xml";
        InputStream config = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);

        if (config == null) {
            throw new PlanningServiceException("Config file not found: " + configFile);
        }

        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setErrorHandler(new StrictErrorHandler());

        digester.push(allRegistries);
        digester.addObjectCreate("*/registry", PreservationActionRegistryDefinition.class);
        digester.addBeanPropertySetter("*/registry/shortname", "shortname");
        digester.addBeanPropertySetter("*/registry/logo", "logo");
        digester.addBeanPropertySetter("*/registry/url", "url");
        digester.addBeanPropertySetter("*/registry/type", "type");
        digester.addBeanPropertySetter("*/registry/active", "active");
        digester.addSetNext("*/registry", "add");

        try {
            digester.setUseContextClassLoader(true);
            digester.parse(config);
        } catch (Exception e) {
            throw new PlanningServiceException(e);
        }
        return allRegistries;

    }

}
