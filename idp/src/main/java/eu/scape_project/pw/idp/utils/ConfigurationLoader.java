/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.pw.idp.utils;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import javax.enterprise.context.SessionScoped;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods to load configuration.
 */
@SessionScoped
public class ConfigurationLoader implements Serializable {

    private static final long serialVersionUID = -6504879602139576367L;

    /**
     * Buffer of loaded Properties.
     */
    private static HashMap<String, CompositeConfiguration> buffer = new HashMap<String, CompositeConfiguration>();

    /**
     * The logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);

    /**
     * Name of the external configuration folder.
     */
    private static final String PROPERTIES_FOLDER = "plato";

    /**
     * Name of the default configuration folder.
     */
    private static final String DEFAULT_PROPERTIES_FOLDER = "config/";

    /**
     * Name of the user properties folder.
     */
    private static final String USER_PROPERTIES_FOLDER = System.getProperty("user.home") + File.separator + "."
        + PROPERTIES_FOLDER;

    /**
     * Name of the system properties folder.
     */
    private static final String SYSTEM_PROPERTIES_FOLDER = "/etc" + File.separator + PROPERTIES_FOLDER;

    /**
     * Loads the configuration for the specified name.
     * 
     * @param name
     *            the configuration name
     * @return the configuration
     */
    public Configuration load(String name) {
        CompositeConfiguration config = buffer.get(name);

        if (config != null) {
            return config;
        }

        config = new CompositeConfiguration();

        Configuration userConfig;
        try {
            userConfig = new PropertiesConfiguration(USER_PROPERTIES_FOLDER + File.separator + name);
            config.addConfiguration(userConfig);
        } catch (ConfigurationException e) {
            LOG.debug("Could not load user properties {}", USER_PROPERTIES_FOLDER + File.separator + name);
        }

        try {
            Configuration systemConfig = new PropertiesConfiguration(SYSTEM_PROPERTIES_FOLDER + File.separator + name);
            config.addConfiguration(systemConfig);
        } catch (ConfigurationException e) {
            LOG.debug("Could not load system properties {}", SYSTEM_PROPERTIES_FOLDER + File.separator + name);
        }

        try {
            Configuration defaultConfig = new PropertiesConfiguration(DEFAULT_PROPERTIES_FOLDER + name);
            config.addConfiguration(defaultConfig);
        } catch (ConfigurationException e) {
            LOG.error("Could not load default properties {}", DEFAULT_PROPERTIES_FOLDER + name);
        }

        buffer.put(name, config);

        return config;
    }
}
