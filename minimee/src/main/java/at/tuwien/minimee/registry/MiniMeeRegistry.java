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
package at.tuwien.minimee.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.digester3.Digester;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import at.tuwien.minimee.MiniMeeException;
import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.registry.xml.PreservationActionServiceFactory;
import at.tuwien.minimee.util.StrictErrorHandler;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.utils.ConfigurationLoader;

/**
 * This class is the external entry point for MiniMEE - it contains all
 * {@link PreservationActionService services} that are configured in this
 * instance. The services have to have a corresponding back part configured in
 * the {@link ToolRegistry}
 * 
 * @author cbu
 * 
 */
public class MiniMeeRegistry {
    private static Logger log = LoggerFactory.getLogger(MiniMeeRegistry.class);
    
    private List<PreservationActionService> services = new ArrayList<PreservationActionService>();

    private static final String MINIMEE_HOME = "minimee.home"; 
    private static final String ACTIONS_CONFIG = "actions-config.xml";
    
    public MiniMeeRegistry() {
    }

    /**
     * Returns all services for the given source- and target format. Currently
     * not exposed as a web service since it has been integrated with Plato.
     * 
     * @param sourceFormat
     * @param targetFormat
     * @return
     */
    public List<PreservationActionService> findServices(FormatInfo sourceFormat, FormatInfo targetFormat) {
        ArrayList<PreservationActionService> matching = new ArrayList<PreservationActionService>();

        for (PreservationActionService service : services) {
            boolean doesSourceMatch = false;
            // check if at least one source format of the service matches
            // sourceFormat
            for (FormatInfo info : service.getSourceFormats()) {
                if (doMatch(sourceFormat, info))
                    doesSourceMatch = true;

            }
            if (doesSourceMatch) {
                // check if the target format matches too
                if (doMatch(targetFormat, service.getTargetFormat()))
                    matching.add(service);
            }
        }
        return matching;
    }

    /**
     * Checks if the pattern matches the format: - partially true, if info is
     * missing in format or pattern (e.g: null, empty string) - if PUIDs are
     * equal AND - (format.name contains(!) pattern.name OR pattern.name
     * contains(!) format.name) AND - defaultExtensions are equal
     */
    private boolean doMatch(FormatInfo srcFormat, FormatInfo regInfo) {
        if (srcFormat == null || regInfo == null) {
            return true;
        }
        return
        // one of the puids is null OR they are equal
        (StringUtils.isEmpty(srcFormat.getPuid()) || 
         StringUtils.isEmpty(regInfo.getPuid()) || 
         srcFormat.getPuid().equals(regInfo.getPuid())) &&
        // one of the formats is null OR one of them is contained in the other
        (StringUtils.isEmpty(srcFormat.getName()) || 
         StringUtils.isEmpty(regInfo.getName()) || 
         regInfo.getName().toUpperCase().contains(srcFormat.getName().toUpperCase()) || 
         srcFormat.getName().toUpperCase().contains(regInfo.getName().toUpperCase())) &&
        // one of the extensions is null OR they are equal
        (StringUtils.isEmpty(srcFormat.getDefaultExtension()) || 
         StringUtils.isEmpty(regInfo.getDefaultExtension()) || 
         srcFormat.getDefaultExtension().toUpperCase().equals(regInfo.getDefaultExtension().toUpperCase()));
    }

    /**
     * Trashes current registry information and loads the information of the XML
     * file, which is provided by the input stream <param>config</config>.
     * 
     * @throws MiniMeeException
     *             if the stream can't be parsed.
     */
    public void reloadFrom(InputStream config) throws MiniMeeException {
        Digester d = new Digester();
        d.setValidating(false);
        StrictErrorHandler errorHandler = new StrictErrorHandler();
        d.setErrorHandler(errorHandler);
        d.setClassLoader(PreservationActionServiceFactory.class.getClassLoader());

        services.clear();

        d.push(services);
        d.addFactoryCreate("*/preservationActionService", PreservationActionServiceFactory.class);
        d.addSetNext("*/preservationActionService", "add");
        d.addCallMethod("*/preservationActionService/name", "setName", 0);
        d.addCallMethod("*/preservationActionService/description", "setDescription", 0);
        d.addCallMethod("*/preservationActionService/descriptor", "setDescriptor", 0);

        d.addObjectCreate("*/preservationActionService/sourceFormats", ArrayList.class);
        d.addSetNext("*/preservationActionService/sourceFormats", "setSourceFormats");

        d.addObjectCreate("*/preservationActionService/sourceFormats/format", FormatInfo.class);
        d.addBeanPropertySetter("*/format/puid", "puid");
        d.addBeanPropertySetter("*/format/name", "name");
        d.addBeanPropertySetter("*/format/extension", "defaultExtension");
        d.addSetNext("*/preservationActionService/sourceFormats/format", "add");

        d.addObjectCreate("*/preservationActionService/targetFormat", FormatInfo.class);
        d.addBeanPropertySetter("*/targetFormat/puid", "puid");
        d.addBeanPropertySetter("*/targetFormat/name", "name");
        d.addBeanPropertySetter("*/targetFormat/extension", "defaultExtension");
        d.addSetNext("*/preservationActionService/targetFormat", "setTargetFormat");

        d.addCallMethod("*/preservationActionService/url", "setUrl", 0);

        d.addObjectCreate("*/preservationActionService/externalInfo", ArrayList.class);
        d.addSetNext("*/preservationActionService/externalInfo", "setExternalInfo");
        d.addCallMethod("*/preservationActionService/externalInfo/url", "add", 0);

        try {
            d.parse(config);
        } catch (IOException e) {
            throw new MiniMeeException("Could not read registry data.", e);
        } catch (SAXException e) {
            throw new MiniMeeException("Could not read registry data.", e);
        }
    }

    /**
     * Reloads registry data, at the moment from an XML file
     */
    public void reload() throws MiniMeeException {
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.load();
        if (configuration == null) {
            log.error("An error occurred while reading the properties file.");
            return;
        }
        String home = configuration.getString(MINIMEE_HOME);
        if (StringUtils.isEmpty(home)) {
            log.error("minimee.home is not defined. cannot initialize minimee-registry.");
            return;
        }
        File configFile = new File(home + File.separator + ACTIONS_CONFIG);
        try {
            reloadFrom(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            log.error("Could not find " + configFile.getAbsolutePath() + ". Cannot initialize minimee-registry.");
        }
    }

    public String getToolIdentifier(String url) {

        ToolRegistry toolRegistry = ToolRegistry.getInstance();

        ToolConfig toolConfig = toolRegistry.getToolConfig(ToolRegistry.getToolKey(url));

        if (toolConfig == null) {
            return "";
        }

        return toolConfig.getTool().getIdentifier();
    }

    public String getToolParameters(String url) {

        ToolRegistry toolRegistry = ToolRegistry.getInstance();

        ToolConfig toolConfig = toolRegistry.getToolConfig(ToolRegistry.getToolKey(url));

        return toolConfig.getParams();
    }

    /**
     * For testing only: Creates a MiniMEE registry, data is loaded from the XML
     * file which location is passed as first command line parameter.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            MiniMeeRegistry registry = new MiniMeeRegistry();
            registry.reloadFrom(new FileInputStream(args[0]));

            List<PreservationActionService> services = registry.findServices(null, null);

            for (PreservationActionService service : services) {
                System.out.println(service.getName());
            }
        } catch (MiniMeeException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
