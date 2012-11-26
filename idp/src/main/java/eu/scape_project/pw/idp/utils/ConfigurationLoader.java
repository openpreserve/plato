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

import java.io.Serializable;
import java.util.HashMap;

import javax.enterprise.context.SessionScoped;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.event.ConfigurationErrorEvent;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods to load configuration.
 */
@SessionScoped
public class ConfigurationLoader implements Serializable {

    private static final long serialVersionUID = 4327923305206861032L;

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);

    private static final String CONFIGURATION_PATH = "config/configurationBuilder.xml";

    private HashMap<String, CombinedConfiguration> buffer = new HashMap<String, CombinedConfiguration>(2);

    /**
     * Loads the default application configuration.
     * 
     * @return the configuration
     */
    public Configuration load() {
        return load(CONFIGURATION_PATH);
    }

    /**
     * Loads the configuration with the provided name.
     * 
     * @param name
     *            the configuration name
     * @return the configuration
     */
    public Configuration load(String name) {
        return load(name, false);
    }

    /**
     * Loads the configuration with the provided name.
     * 
     * @param name
     *            the configuration name
     * @param ignoreBuffer
     *            true to ignore the internal buffer, false otherwise
     * @return the configuration
     */
    public Configuration load(String name, boolean ignoreBuffer) {
        CombinedConfiguration config = null;

        if (!ignoreBuffer) {
            config = buffer.get(name);
            if (config != null) {
                return config;
            }
        }

        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(name);
            builder.clearErrorListeners();
            builder.addErrorListener(new ConfigurationErrorListener() {
                @Override
                public void configurationError(ConfigurationErrorEvent event) {
                    if (event.getType() == DefaultConfigurationBuilder.EVENT_ERR_LOAD_OPTIONAL) {
                        LOG.debug("Could not load optional configuration file {}", event.getPropertyName(),
                            event.getCause());
                    } else {
                        LOG.warn("Configuration error on {}", event.getPropertyName(), event.getCause());
                    }
                }
            });
            config = builder.getConfiguration(true);
            buffer.put(name, config);
        } catch (ConfigurationException e) {
            LOG.error("Cannot load configuration {}", e, name);
        }
        return config;
    }
}
