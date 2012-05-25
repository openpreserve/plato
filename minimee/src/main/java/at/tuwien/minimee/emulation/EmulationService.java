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
package at.tuwien.minimee.emulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import at.tuwien.minimee.ActionService;
import at.tuwien.minimee.MiniMeeException;
import at.tuwien.minimee.model.ToolConfig;

/**
 * Currently unused emulation connector that provided the first remote
 * access to GRATE. Now not needed since this became part of the Planets IF
 * @author cbu
 *
 */
@Deprecated
public class EmulationService extends ActionService {

    /**
     * Currently not exposed as a web service since miniMEE
     * has been integrated with Plato.
     * This starts a session with GRATE
     * @param samplename filename of the object to be rendered remotely
     * @param data the file to be rendered remotely
     * @param toolID pointing to the corresponding minimee configuration
     * @return a URL to be posted to the browser for opening a GRATE session.
     * This URL points to a GRATE session that contains the object readily waiting
     * to be rendered, already injected into the appropriate environment.
     * @throws MiniMeeException if the connection to the GRATE server failed
     */
    public String startSession (String samplename, byte[] data, String toolID) {
        ToolConfig config = getToolConfig(toolID);
        //throws MiniMeeException
        
//        String response;
//        try {
//            HttpClient client = new HttpClient();
//            MultipartPostMethod mPost = new MultipartPostMethod(config.getTool().getExecutablePath());
//            client.setConnectionTimeout(8000);
//
//
//            // MultipartPostMethod needs a file instance
//            File sample = File.createTempFile(samplename+System.nanoTime(), "tmp");
//            OutputStream out = new BufferedOutputStream(new FileOutputStream(sample));
//            out.write(data);
//            out.close();
//            
//            mPost.addParameter("datei", samplename, sample);
//            
//            int statusCode = client.executeMethod(mPost);
//            
//            response = mPost.getResponseBodyAsString();
//            
//            return response+ config.getParams();
//
//        } catch (HttpException e) {
//            throw new MiniMeeException("Could not connect to GRATE.", e);
//        } catch (FileNotFoundException e) {
//            throw new MiniMeeException("Could not create temp file.", e);
//        } catch (IOException e) {
//            throw new MiniMeeException("Could not connect to GRATE.", e);
//        }        
        return  null;
    }

    
    /**
     * A small test method
     * @param args not used
     */
    public static void main(String[] args) {
//        String url = "http://planets.ruf.uni-freiburg.de/~randy/plato_interface/plato_uploader.php";
//        EmulationService emu = new EmulationService();
//        File sample = new File("D:/projects/ifs/workspace/plato/data/samples/polarbear1.jpg");
//
//        try {
//            byte[] data = getBytesFromFile(sample);
//            
//            String sessionid = emu.startSession("polarbear1.jpg", data, url);
//            System.out.println(sessionid);
//        } catch (IOException e) {
//            LogFactory.getLog(EmulationService.class).error(e.getMessage(),e);
//        } catch (MiniMeeException e) {
//            LogFactory.getLog(EmulationService.class).error(e.getMessage(),e);
//        }
        
    }
    
    /**
     * utility method to read a bytestream from a file
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
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
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
}
