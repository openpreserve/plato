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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils implements Serializable {
    private static final long serialVersionUID = -2554713564317100326L;
    
    static final Logger log = LoggerFactory.getLogger(FileUtils.class);
    
    public static File getResourceFile(String name) throws URISyntaxException {
        URI uri = Thread.currentThread().getContextClassLoader().getResource(name).toURI();
        return new File(uri);
    }

    public static byte[] inputStreamToBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0)
        out.write(buffer, 0, len);

        in.close();
        out.close();
        return out.toByteArray();
      } 
    
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] bytes;
        try {
     
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        } finally {
            // Close the input stream and return bytes
            is.close();
        }
        return bytes;
    }
    
    public static String replaceExtension(String filename, String newExtension){
        return filename.substring(0, filename.lastIndexOf(".")) + "." + newExtension;
    }
    
    public static String makeFilename(String value) {
        if (value == null)
            return "";
        // \w .. [a-zA-Z_0-9]
        return value.replaceAll("\\s", "_").replaceAll("[^\\w-]", "");
    }
    
    public static void writeToFile(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (FileNotFoundException ex) {
            log.debug("Error copying file "+ex.getMessage(),ex);
        }finally{
            try{in.close();}catch(Exception skip){}
            try{out.close();}catch(Exception skip){}
            
        }
    }
}
