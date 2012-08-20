package eu.scape_project.planning.taverna;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class T2FlowParserFallback extends T2FlowParser {

    private static final String FROM_OBJECT_PATH_URI = "http://scape-project.eu/components/FromObject";
    private static final String FROM_OBJECT_PATH_NAME = "path_from";

    private static final String TO_OBJECT_PATH_URI = "http://scape-project.eu/components/ToObject";
    private static final String TO_OBJECT_PATH_NAME = "path_to";

    private static final String MEASURES_URI_PREFIX = "http://scape-project.eu/pw/vocab/measures/";

    /**
     * Creates a T2FlowParserFallback from the inputstream
     * 
     * @param t2flow
     *            the inputstream to parse
     * @return the parser
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static T2FlowParserFallback createParser(InputStream t2flow) throws ParserConfigurationException,
        SAXException, IOException {
        T2FlowParserFallback parser = new T2FlowParserFallback();
        parser.initialise(t2flow);
        return parser;
    }

    /**
     * Checks if using the fallback methods are applicable for this workflow.
     * 
     * @return true if fallback can be used, false otherwise
     * @throws TavernaParserException
     */
    private boolean isFallbackApplicable() throws TavernaParserException {
        return (super.getProfile() == ComponentProfile.NoProfile && getProfile() != ComponentProfile.NoProfile);
    }

    @Override
    public ComponentProfile getProfile() throws TavernaParserException {
        ComponentProfile profile = super.getProfile();

        // Check name for hints of profile
        if (profile == ComponentProfile.NoProfile) {
            String lcName = super.getName().toLowerCase();

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
        if (isFallbackApplicable() && profileVersion.equals("")) {
            return "0.1";
        }

        return profileVersion;
    }

    @Override
    public String getVersion() throws TavernaParserException {
        String version = super.getVersion();

        // Use fixed version if workflow fallback applicable
        if (isFallbackApplicable() && version.equals("")) {
            return "1.0";
        }

        return version;
    }

    @Override
    public String getOwner() throws TavernaParserException {
        String owner = super.getOwner();

        // Use author if workflow fallback applicable
        if (isFallbackApplicable() && owner.equals("")) {
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
     * Returns a port of the port set with the provided name
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
     * Returns a port of the port set that fits the provided URI
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
        } else if (uriString.startsWith(MEASURES_URI_PREFIX)) {
            foundPort = findPortByName(ports, uriString.substring(MEASURES_URI_PREFIX.length()));
            if (foundPort != null) {
                foundPort.getUris().add(uri);
            }

        }
        return foundPort;
    }
}
