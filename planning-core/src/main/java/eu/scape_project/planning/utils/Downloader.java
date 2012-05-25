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
package eu.scape_project.planning.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import eu.scape_project.planning.model.DigitalObject;

/**
 * Starts download of a file.
 *
 * @author Hannes Kulovits
 */
public class Downloader implements Serializable {
	private static final long serialVersionUID = 1L;

    @Inject private Logger log;
    
    private Downloader(){
    }

    public void downloadMM(String xml,String name) {
        download(xml.getBytes(),name,"application/freemind");
    }
    
    // FIXME should use ByteStreamManager to retrieve data
    public void download(DigitalObject object) {
        download(object.getData().getData(),object.getFullname(),object.getContentType());
    }
    
    public void download(DigitalObject object, File file) {
        byte[] data;
        try {
            data = FileUtils.getBytesFromFile(file);
            download(data,
                    object.getFullname(),
                    object.getContentType());
        } catch (IOException e) {
            log.error("failed to retrieve digital object.", e);
        }
    }
    
//    public void download(ByteStream data, String fileName, String contentType){
//        download(data.getData(),fileName,contentType);
//    }

    /**
     * Starts a client side download. All information provided by parameters.
     *
     * @param file data file contains
     * @param fileName name of the file (e.g. report.pdf)
     * @param contentType mime type of the content to be downloaded
     */
    public void download(byte[] file, String fileName, String contentType){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext context = facesContext.getExternalContext();

        HttpServletResponse response = (HttpServletResponse) context
                .getResponse();
        response.setHeader("Content-Disposition", "attachment;filename=\""
                + fileName + "\"");
        response.setContentLength((int) file.length);
        response.setContentType(contentType);

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(file);
            OutputStream out = response.getOutputStream();

            // Copy the contents of the file to the output stream
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
            in.close();
            out.flush();
            out.close();
            facesContext.responseComplete();
        } catch (IOException ex) {
            log.error("Error in downloadFile: " + ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, 
            		new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Download couldn't be executed", ""));
            ex.printStackTrace();
        }
    }
}
