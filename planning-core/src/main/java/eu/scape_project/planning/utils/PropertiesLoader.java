package eu.scape_project.planning.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader {

    private static Logger log = LoggerFactory.getLogger(PropertiesLoader.class);

    /**
     * Loads the properties for the specified name or the default properties if
     * not found.
     * 
     * @param name
     *            The properties name
     * @return the properties
     * @throws IOException
     *             if the properties and default properties could not be loaded
     */
    public static Properties loadProperties(String name) throws IOException {

        Properties defaultProperties = loadDefaultProperties(name);

        Properties properties = new Properties(defaultProperties);

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(name + ".properties");
        try {
            if (in != null) {
                properties.load(in);
                log.debug("Properties loaded for " + name);
            } else {
                log.warn("Could not find properties for " + name);
            }
        } finally {
            in.close();
        }

        return properties;
    }

    /**
     * Loads the default properties for the specified name.
     * 
     * @param name
     *            The properties name
     * @return the default properties or null if they could not be found
     * @throws IOException
     *             if the properties could not be loaded
     */
    private static Properties loadDefaultProperties(String name) throws IOException {

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(name + ".default.properties");
        try {
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
                log.debug("Defaults loaded for properties " + name);
                return properties;
            } else {
                log.error("Could not find defaults for properties " + name);
                return null;
            }
        } finally {
            in.close();
        }
    }
}
