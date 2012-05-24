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
package eu.planets_project.pp.plato.services.characterisation.fits;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.dom4j.io.OutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.planets_project.pp.plato.util.CommandExecutor;
import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.pw.planning.utils.FileUtils;
import eu.scape_project.pw.planning.utils.OS;

public class FitsIntegration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static Logger log = LoggerFactory.getLogger(FitsIntegration.class);

   // private static final long serialVersionUID = 5183122855613215086L;
    public static OutputFormat prettyFormat = new OutputFormat(" ", true,"ISO-8859-1"); //OutputFormat.createPrettyPrint();

    private static String FITS_HOME;
    
    private static String FITS_COMMAND = "%FITS_EXEC% -i %INPUT% -o %OUTPUT%"; 
    
    public FitsIntegration() throws PlanningException{
        FITS_HOME = System.getenv("FITS_HOME");
        if (FITS_HOME == null) {
        	try {
				InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/fits.properties");
				if (in != null) {
					Properties fitsProp = new Properties();
					fitsProp.load(in);
					FITS_HOME = fitsProp.getProperty("fits.home");
				}
			} catch (IOException e) {
				log.error("FITS_HOME is not defined, and fits.properties not found", e);
			}
            if (FITS_HOME != null) {
            	log.info("FITS_HOME is not defined, using fits.home from fits.properties : " + FITS_HOME);
            } else if ( ! new File(FITS_HOME).exists()) {
                FITS_HOME = null;
            }
        }
        if (FITS_HOME == null) {
            throw new PlanningException("FITS is not propertly configured - FITS_HOME is not defined.");
        }
    }
    
    public String characterise(File input) throws PlanningException{
        CommandExecutor cmdExecutor = new CommandExecutor();
        cmdExecutor.setWorkingDirectory(FITS_HOME);
        String scriptExt;
        if ("Linux".equalsIgnoreCase(System.getProperty("os.name"))){
            scriptExt = "./fits.sh";
        } else {
            scriptExt = "cmd /c %FITS_HOME%/fits";
        }
        File output = new File(OS.getTmpPath() + "fits"+System.nanoTime()+".out");
        try {
            String commandLine = FITS_COMMAND.replace("%FITS_EXEC%", scriptExt)
                .replace("%INPUT%", input.getAbsolutePath())
                .replace("%OUTPUT%", output.getAbsolutePath());
            
            try {
                int exitcode = cmdExecutor.runCommand(commandLine);
                if (exitcode != 0) {
                    String cmdError = cmdExecutor.getCommandError();
                    throw new PlanningException("FITS characterisation for file: " + input + " failed: " + cmdError);
                }
                if (!output.exists()) {
                    throw new PlanningException("FITS characterisation for file: " + input + " failed: no output was written.");
                }
                
                return new String(FileUtils.getBytesFromFile(output));
            } catch (PlanningException e) {
                throw e;
            } catch (Throwable t) {
                throw new PlanningException("FITS characterisation for file: " + input + " failed: " + t.getMessage());            
            }
        } finally {
            output.delete();
        }
    }
    
}
