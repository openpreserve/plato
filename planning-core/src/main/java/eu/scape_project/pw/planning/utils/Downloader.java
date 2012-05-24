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

package eu.scape_project.pw.planning.utils;

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
