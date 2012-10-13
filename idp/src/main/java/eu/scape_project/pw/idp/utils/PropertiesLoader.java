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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods to load properties.
 */
public class PropertiesLoader {

    /**
     * Buffer of loaded Properties.
     */
    private static HashMap<String, Properties> buffer = new HashMap<String, Properties>();

    /**
     * The logger for this class.
     */
    private static Logger log = LoggerFactory.getLogger(PropertiesLoader.class);

    /**
     * Name of the external configuration folder.
     */
    private static final String PROPERTIES_FOLDER = "plato";

    /**
     * Name of the default configuration folder.
     */
    private static final String DEFAULT_PROPERTIES_FOLDER = "config";

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
     * Loads the properties for the specified name.
     * 
     * @param name
     *            the properties name
     * @return the properties or null if none could be loaded
     */
    public Properties load(String name) {
        Properties properties = buffer.get(name);

        if (properties != null) {
            return properties;
        }

        properties = loadResourceProperties(DEFAULT_PROPERTIES_FOLDER, name, properties);
        properties = loadFileProperties(SYSTEM_PROPERTIES_FOLDER, name, properties);
        properties = loadFileProperties(USER_PROPERTIES_FOLDER, name, properties);

        properties.put(name, properties);

        return properties;
    }

    /**
     * Loads the properties resource from the provided folder with defaults.
     * 
     * @param folder
     *            the folder properties
     * @param name
     *            the properties name
     * @param defaults
     *            the defaults or null
     * @return the loaded properties or null
     */
    private Properties loadResourceProperties(String folder, String name, Properties defaults) {

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(folder + File.separator + name);

        if (in != null) {
            try {
                try {
                    Properties properties = new Properties(defaults);
                    properties.load(in);
                    log.debug("Loaded resource properties " + folder + File.separator + name);
                    return properties;
                } finally {
                    in.close();
                }
            } catch (IOException e) {
                log.warn("Could not read resource properties " + folder + File.separator + name);
                return defaults;
            }
        } else {
            log.warn("Could not find resource properties " + folder + File.separator + name);
            return defaults;
        }

    }

    /**
     * Loads the properties file from the provided folder with defaults.
     * 
     * @param folder
     *            the folder properties
     * @param name
     *            the properties name
     * @param defaults
     *            the defaults or null
     * @return the loaded properties or null
     */
    private Properties loadFileProperties(String folder, String name, Properties defaults) {
        File f = new File(folder + File.separator + name);
        try {
            InputStream in = new FileInputStream(f);
            try {
                Properties properties = new Properties(defaults);
                properties.load(in);
                log.debug("Loaded file properties " + folder + File.separator + name);
                return properties;
            } finally {
                in.close();
            }
        } catch (IOException e) {
            log.debug("Could not read file properties " + folder + File.separator + name);
            return defaults;
        }
    }
}
