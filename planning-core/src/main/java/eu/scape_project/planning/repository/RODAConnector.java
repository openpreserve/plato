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
package eu.scape_project.planning.repository;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.api.RepositoryConnectorApi;
import eu.scape_project.planning.utils.ConfigurationLoader;
import eu.scape_project.planning.utils.RepositoryConnectorException;

/**
 * @author Petar Petrov - <me@petarpetrov.org>
 * 
 */
public class RODAConnector implements RepositoryConnectorApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RODAConnector.class);

    private static final String RODA_NAME = "RODA";

    public static final String ENDPOINT_KEY = "repository.endpoint";

    public static final String USER_KEY = "repository.user";

    public static final String PASS_KEY = "repository.pass";

    private Map<String, String> config;

    /**
     * Loads the config using the {@link ConfigurationLoader}
     */
    public RODAConnector() {
        this.config = loadConfig();
    }
    
    public void updateConfig(Map<String, String> config) {
        for (String key : config.keySet()) {
            this.config.put(key, config.get(key));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRepositoryIdentifier() {
        return RODA_NAME + " " + this.config.get(ENDPOINT_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream downloadFile(String identifier) throws RepositoryConnectorException {
        return downloadFile(config, identifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream downloadFile(Map<String, String> config, String identifier) throws RepositoryConnectorException {
        String user = config.get(USER_KEY);
        String pass = config.get(PASS_KEY);

        this.isRequredConfigSet(ENDPOINT_KEY,
            "The RODA endpoint is not set. Cannot connect to " + this.getRepositoryIdentifier());
        this.isRequredConfigSet(user,
            "The user config parameter was not set. Cannot connect to " + this.getRepositoryIdentifier());
        this.isRequredConfigSet(pass,
            "The pass config parameter was not set. Cannot connect to " + this.getRepositoryIdentifier());

        Authenticator.setDefault(new RODAAuthenticator(user, pass));

        try {
            URL url = new URL(identifier);
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            throw new RepositoryConnectorException(e);
        }
    }

    /**
     * Loads the config file and exposes it as a Map.
     * 
     * @return the map of properties.
     */
    private Map<String, String> loadConfig() {
        if (this.config != null) {
            return this.config;
        }

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.load();
        if (configuration == null) {
            LOGGER.warn("An error occurred while reading the properties file.");
            return new HashMap<String, String>();
        }

        Map<String, String> map = new HashMap<String, String>();
        Iterator<String> configIt = configuration.getKeys();
        while (configIt.hasNext()) {
            String key = configIt.next();
            map.put(key, configuration.getString(key));
        }

        return map;
    }

    /**
     * Checks if the required configs are set and throws an error if not.
     * 
     * @param credential
     *            the credential to check.
     * @param error
     *            the error message
     * @throws RepositoryConnectorException
     *             if the required credential is not set.
     */
    private void isRequredConfigSet(String credential, String error) throws RepositoryConnectorException {
        if (credential == null) {
            throw new RepositoryConnectorException(error);
        }
    }

    /**
     * A simple authenticator for the RODA instances, where Basic Http
     * Authentication is in place.
     * 
     * @author Petar Petrov - <me@petarpetrov.org>
     * 
     */
    private class RODAAuthenticator extends Authenticator {

        private String user;

        private String pass;

        public RODAAuthenticator(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        // This method is called when a password-protected URL is accessed
        protected PasswordAuthentication getPasswordAuthentication() {
            // Get information about the request
            String hostname = getRequestingHost();
            InetAddress ipaddr = getRequestingSite();
            LOGGER.info("Connection to '{}' [{}] is about to be established", hostname, ipaddr);
            String prompt = getRequestingPrompt();
            LOGGER.info("Authentication required: {}", prompt);

            // Return the information
            return new PasswordAuthentication(this.user, this.pass.toCharArray());
        }
    }

}
