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
package eu.scape_project.planning.services.taverna.parser;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import eu.scape_project.planning.services.taverna.TavernaPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for t2flow files that uses fallback mechanisms.
 */
public class T2FlowParserFallback extends T2FlowParser {

    private static Logger log = LoggerFactory.getLogger(T2FlowParser.class);

    public static final String FROM_OBJECT_PATH_URI = "http://scape-project.eu/components/FromObject";
    public static final String FROM_OBJECT_PATH_NAME = "path_from";

    public static final String TO_OBJECT_PATH_URI = "http://scape-project.eu/components/ToObject";
    public static final String TO_OBJECT_PATH_NAME = "path_to";

    public static final String PARAMETER_URI = "http://scape-project.eu/components/Parameter";
    public static final String PARAMETER_NAME = "parameter";

    private static final String MEASURES_URI_PREFIX = "http://scape-project.eu/pw/vocab/measures/";

    /**
     * Creates a T2FlowParserFallback from the inputstream.
     * 
     * @param t2flow
     *            the inputstream to parse
     * @return the parser
     * @throws TavernaParserException
     */
    public static T2FlowParserFallback createParser(InputStream t2flow) throws TavernaParserException {
        T2FlowParserFallback parser = new T2FlowParserFallback();
        try {
            parser.initialise(t2flow);
        } catch (Exception e) {
            log.error("Error initialising T2FlowParser: {}", e.getMessage());
            throw new TavernaParserException("Error initialising T2FlowParser", e);
        }
        return parser;
    }

    /**
     * Checks if using the fallback methods are applicable for this workflow.
     * 
     * @return true if fallback can be used, false otherwise
     * @throws TavernaParserException
     */
    private boolean isFallbackApplicable() throws TavernaParserException {
        return super.getProfile() == ComponentProfile.NoProfile && getProfile() != ComponentProfile.NoProfile;
    }

    @Override
    public ComponentProfile getProfile() throws TavernaParserException {
        ComponentProfile profile = super.getProfile();

        // Check name for hints of profile
        if (profile == ComponentProfile.NoProfile) {
            String lcName = super.getName();
            if (lcName == null) {
                return ComponentProfile.NoProfile;
            }
            lcName = lcName.toLowerCase();

            if (lcName.contains("migration")) {
                return ComponentProfile.MigrationAction;
            } else if (lcName.contains("characterisation")) {
                return ComponentProfile.Characterisation;
            } else if (lcName.contains("qa") || lcName.contains("qualityassurance")
                || lcName.contains("quality assurance")) {
                return ComponentProfile.QA;
            } else if (lcName.contains("executable plan")) {
                return ComponentProfile.ExecutablePlan;
            }
        }

        return profile;
    }

    @Override
    public String getProfileVersion() throws TavernaParserException {
        String profileVersion = super.getProfileVersion();

        // Use fixed version if workflow fallback applicable
        if (isFallbackApplicable() && profileVersion == null) {
            return "0.1";
        }

        return profileVersion;
    }

    @Override
    public String getVersion() throws TavernaParserException {
        String version = super.getVersion();

        // Use fixed version if workflow fallback applicable
        if (isFallbackApplicable() && version == null) {
            return "1.0";
        }

        return version;
    }

    @Override
    public String getOwner() throws TavernaParserException {
        String owner = super.getOwner();

        // Use author if workflow fallback applicable
        if (isFallbackApplicable() && owner == null) {
            return getAuthor();
        }

        return owner;
    }

    @Override
    public Set<TavernaPort> getInputPorts() throws TavernaParserException {
        Set<TavernaPort> ports = super.getInputPorts();

        for (TavernaPort port : ports) {
            if (port.getName().equals(FROM_OBJECT_PATH_NAME)) {
                try {
                    port.getUris().add(new URI(FROM_OBJECT_PATH_URI));
                } catch (URISyntaxException e) {
                    throw new TavernaParserException(e);
                }
            } else if (port.getName().equals(TO_OBJECT_PATH_NAME)) {
                try {
                    port.getUris().add(new URI(TO_OBJECT_PATH_URI));
                } catch (URISyntaxException e) {
                    throw new TavernaParserException(e);
                }
            } else {
                try {
                    port.getUris().add(new URI(MEASURES_URI_PREFIX + port.getName()));
                } catch (URISyntaxException e) {
                    throw new TavernaParserException(e);
                }
            }
        }

        return ports;
    }

    @Override
    public Set<TavernaPort> getInputPorts(URI uri) throws TavernaParserException {
        Set<TavernaPort> ports = super.getInputPorts(uri);

        // Use port name if workflow fallback applicable
        if (isFallbackApplicable() && ports.size() == 0) {
            Set<TavernaPort> allPorts = getInputPorts();
            TavernaPort foundPort = findPortByURI(allPorts, uri);
            if (foundPort != null) {
                ports.add(foundPort);
            }
        }

        return ports;
    }

    @Override
    public Set<TavernaPort> getOutputPorts() throws TavernaParserException {
        Set<TavernaPort> ports = super.getOutputPorts();

        for (TavernaPort port : ports) {
            if (port.getName().equals(FROM_OBJECT_PATH_NAME)) {
                try {
                    port.getUris().add(new URI(FROM_OBJECT_PATH_URI));
                } catch (URISyntaxException e) {
                    throw new TavernaParserException(e);
                }
            } else if (port.getName().equals(TO_OBJECT_PATH_NAME)) {
                try {
                    port.getUris().add(new URI(TO_OBJECT_PATH_URI));
                } catch (URISyntaxException e) {
                    throw new TavernaParserException(e);
                }
            } else {
                try {
                    port.getUris().add(new URI(MEASURES_URI_PREFIX + port.getName()));
                } catch (URISyntaxException e) {
                    throw new TavernaParserException(e);
                }
            }
        }

        return ports;
    }

    @Override
    public Set<TavernaPort> getOutputPorts(URI uri) throws TavernaParserException {
        Set<TavernaPort> ports = super.getOutputPorts(uri);

        // Use port name if workflow fallback applicable
        if (isFallbackApplicable() && ports.size() == 0) {
            Set<TavernaPort> allPorts = getOutputPorts();
            TavernaPort foundPort = findPortByURI(allPorts, uri);
            if (foundPort != null) {
                ports.add(foundPort);
            }
        }

        return ports;
    }

    /**
     * Returns a port of the port set with the provided name.
     * 
     * @param ports
     *            a set of port to search
     * @param name
     *            name of the port to search.
     * @return the port if found or null otherwise
     */
    private TavernaPort findPortByName(Set<TavernaPort> ports, String name) {

        for (TavernaPort port : ports) {
            if (port.getName().equals(name)) {
                return port;
            }
        }

        return null;
    }

    /**
     * Returns a port of the port set that fits the provided URI.
     * 
     * @param ports
     *            a set of ports to search
     * @param uri
     *            the uri to search
     * @return the port if found or null otherwise
     */
    private TavernaPort findPortByURI(Set<TavernaPort> ports, URI uri) {
        String uriString = uri.toASCIIString();

        TavernaPort foundPort = null;

        if (uriString.equals(FROM_OBJECT_PATH_URI)) {
            // Path from port
            foundPort = findPortByName(ports, FROM_OBJECT_PATH_NAME);
            if (foundPort != null) {
                foundPort.getUris().add(uri);
            }
        } else if (uriString.equals(TO_OBJECT_PATH_URI)) {
            // Path to port
            foundPort = findPortByName(ports, TO_OBJECT_PATH_NAME);
            if (foundPort != null) {
                foundPort.getUris().add(uri);
            }
        } else if (uriString.equals(PARAMETER_URI)) {
            // Path to port
            foundPort = findPortByName(ports, PARAMETER_NAME);
            if (foundPort != null) {
                foundPort.getUris().add(uri);
            }
        } else if (uriString.startsWith(MEASURES_URI_PREFIX)) {
            foundPort = findPortByName(ports, uriString.substring(MEASURES_URI_PREFIX.length()));
            if (foundPort != null) {
                foundPort.getUris().add(uri);
            }

        }
        return foundPort;
    }
}
