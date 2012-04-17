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
package at.tuwien.minimee.migration.evaluators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.util.CommandExecutor;
import at.tuwien.minimee.util.OS;
import eu.planets_project.pp.plato.model.measurement.Measurement;
import eu.planets_project.pp.plato.model.scales.Scale;

/**
 * This evaluator uses the ImageMagick COMPARE script to measure the difference
 * of two images. For this, ImageMagick must be defined properly!
 * That means, you need to set the IMAGEMAGICK_HOME environment variable 
 * to point to your installation directory.
 * @author cb
 *
 */
public class ImageCompareEvaluator implements IMinimeeEvaluator {
    private String name;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private String compareCommand = "%IMAGEMAGICK_HOME%/compare %FILE1% %FILE2% -metric %METRIC% tempimagefile";

    private String IMAGEMAGICK_HOME;
    
    /**
     * @throws IllegalStateException if IMAGEMAGICK_HOME is not set
     */
    public ImageCompareEvaluator()  {
        IMAGEMAGICK_HOME = System.getenv("IMAGEMAGICK_HOME");
        
        //
        // IMAGEMAGICK_HOME is set and the directory exists
        if (IMAGEMAGICK_HOME != null && new File(IMAGEMAGICK_HOME).exists()) {
            compareCommand = compareCommand.replace("%IMAGEMAGICK_HOME%", new File(IMAGEMAGICK_HOME).getAbsolutePath());
            
            return;
        //
        // IMAGEMAGICK_HOME is not set, but the operating system is Linux and the ImageMagick is installed
        } else if (IMAGEMAGICK_HOME == null && ("Linux".compareTo(System.getProperty("os.name")) == 0)
                && new File("/usr/bin/compare").exists()) {
            compareCommand = compareCommand.replace("%IMAGEMAGICK_HOME%", "/usr/bin");
            
            return;
        }
        
        // 
        // in all other cases, where IMAGEMAGICK_HOME is not set, we throw an exception.
        if (IMAGEMAGICK_HOME == null) {
            throw new IllegalStateException("ImageMagick is not propertly configured - IMAGEMAGICK_HOME is not defined.");
        }
    }
    
    public List<Measurement> evaluate(String tempDir, String file1,String file2) {
        List<Measurement> list = new ArrayList<Measurement>();
        String[] metrics = new String[] { // see http://www.imagemagick.org/Usage/compare/#statistics
                "AE",   // Absolute Error count of the number of different pixels (0=equal)
                "PAE",  // Peak Absolute Error
                "PSNR", // Peak Signal to noise ratio 
                "MAE",  // Mean absolute error    (average channel error distance)
                "MSE",  // Mean squared error     (averaged squared error distance)
                "RMSE", //  (sq)root mean squared error -- IE:  sqrt(MSE) 
                "MEPP" // Normalized Mean Error AND Normalized Maximum Error
        };
        
        for (String metric: metrics) {
            Double value = evaluate(tempDir,file1,file2,metric);
            list.add(new Measurement("imagequality:"+metric,value));
       }
        return list;
    }
    
    public Double evaluate(String tempDir, String file1,String file2, String metric) {
        String commandTemplate = compareCommand.replace("%FILE1%", file1)
                                               .replace("%FILE2%", file2);
        
        CommandExecutor cmdEx = new CommandExecutor();
        cmdEx.setWorkingDirectory(OS.getTmpPath());
        String command = commandTemplate.replace("%METRIC%", metric);
        try {
            cmdEx.runCommand(command);
            String out = cmdEx.getCommandError();
            log.debug(command+ " == "+out);
            Double value = Scale.MAX_VALUE;

            try {
                if (out.contains(" ")) {
                    out = out.substring(0,out.indexOf(" "));
                } 
                value = Double.valueOf(out.trim());
            } catch (NumberFormatException e) {
                log.error("unknown numberformat: "+out+" "+e.getMessage());
            }
            return value;
        } catch (Exception e) {
            log.error("error during image comparison "+command+" : "+cmdEx.getCommandError(),e);
            return null;
        }
    }

    /**
     * unused
     */
    public String getConfigParam() {
        return null;
    }

    /**
     * unused
     */
    public void setConfigParam(String configParam) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
