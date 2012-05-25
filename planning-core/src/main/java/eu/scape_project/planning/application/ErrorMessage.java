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
package eu.scape_project.planning.application;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;

import eu.scape_project.planning.model.Plan;

/**
 * 
 * Class for storing Errors to display on admin-utils page
 *
 * @author Florian Motlik
 * 
 */
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 508829244883298069L;

    /**
     * Type of error. In case of an exception this is the exception class (e.g.
     * NullPointerException)
     */
    private String type = "";

    /**
     * Error message.
     */
    private String message = "";

    /**
     * Time when error occurred.
     */
    private String timestamp = "";

    private String sessionID = "";

    private String user = "";

    private String step = "";

    private String site;

    private int projectId;

    private String projectName;
    
    private String userAgent;
    
    public String getUserAgent() {
        return userAgent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getStep() {
        return step;
    }

    public String getUser() {
        return user;
    }

    
    /**
     * Creates an ErrorMessage based on the given parameters.
     * - Additionally it tries to look up information about the used browser
     *   (that is entry "user-agent" from the servlet request)
     * 
     * @param type error type. In case of an exception this is the canonical name of the exception class.
     * @param message
     * @param sessionID
     * @param user
     * @param site
     * @param selectedPlan
     */
    public ErrorMessage(String type, String message, String sessionID,
            String user, String site, Plan selectedPlan) {
        this.type = type;
        this.message = message;
        SimpleDateFormat format = new SimpleDateFormat("dd.MMMM.yyyy kk:mm:ss");
        this.timestamp = format.format(new Date(System.currentTimeMillis()));
        this.sessionID = sessionID;
        this.user = user;
        this.site = site;
        
        Map<String,String> headers = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
        String userAgent =  headers.get("user-agent");
        
        if (userAgent == null) {
            userAgent = "Unknown";
        }
        
        this.userAgent = userAgent;
        
        if (selectedPlan != null) {
            this.step = selectedPlan.getStateName();
            this.projectId = selectedPlan.getId();
            this.projectName = selectedPlan.getPlanProperties().getName();
        }
    }

    public String getSite() {
        return site;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

}
