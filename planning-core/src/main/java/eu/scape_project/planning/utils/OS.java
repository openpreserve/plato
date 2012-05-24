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
package eu.scape_project.planning.utils;

import java.io.File;

public class OS {
    
    public final static String getTmpPath(){
        String tempDir = System.getProperty("java.io.tmpdir").replace('\\', '/');
        if (!tempDir.endsWith("/")) {
            tempDir += "/";
        }
        return tempDir;
    }
    
    public final static String getJhoveTmpPath(){
        String tmpPath = System.getProperty("java.io.tmpdir");
        int slashb=tmpPath.indexOf("\\");
        int slashf=tmpPath.indexOf("/");
        if(slashb>0 && 
                (slashf<0 || slashb<slashf))     //first slash is a win slash
            tmpPath+="\\";
        else 
            tmpPath+="/";
        
        return tmpPath;            
    }
    
    public final static String completePathWithSeparator(String path) {
        if (!path.endsWith("/")) {
            return path + "/";
        } else {
            return path;
        }
    }

    /**
     * Deletes the given directory and its content recursively 
     * @param dir
     */
    public static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
    

}
