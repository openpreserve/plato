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
package eu.scape_project.planning.services.characterisation.fits;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.dom4j.io.OutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.utils.CommandExecutor;
import eu.scape_project.planning.utils.FileUtils;
import eu.scape_project.planning.utils.OS;

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
