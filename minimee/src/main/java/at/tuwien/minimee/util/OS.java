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
package at.tuwien.minimee.util;

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
        FileUtils.log.debug("deleting directory ... " + dir.getAbsolutePath());
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
