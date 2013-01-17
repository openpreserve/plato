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
package eu.scape_project.planning.policies;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

import eu.scape_project.planning.model.RDFPolicy;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.policy.ControlPolicy;
import eu.scape_project.planning.utils.FacesMessages;

@Named("organisationalPolicies")
@SessionScoped
public class OrganisationalPoliciesView implements Serializable {
    private static final long serialVersionUID = 1949891454912441259L;

    @Inject
    private Logger log;

    @Inject
    private FacesMessages facesMessages;

    @Inject
    private OrganisationalPolicies policies;

    @Inject
    private User user;

    private UploadedFile importFile = null;

    /**
     * Method responsible for initializing all properties with proper values -
     * so the page can be displayed correctly.
     * 
     * @return OutcomeString which navigates to this page
     */
    public String init() {
        policies.init();
        return "/user/organisationalpolicies.jsf";
    }

    /**
     * Imports a policy
     * 
     * @param event
     *            Richfaces FileUploadEvent data
     */
    public void importPolicy(FileUploadEvent event) {
        importFile = event.getUploadedFile();

        try {
            if (policies.importPolicy(importFile.getInputStream())) {
                facesMessages.addInfo("Policy imported successfully");
                importFile = null;
            } else {
                facesMessages.addError("The uploaded policy file is not valid");
            }
        } catch (IOException e) {
            facesMessages.addError("An error occured during uploading the file, please try again.");
            log.error("Policy upload faild.", e);
        }

    }

    /**
     * Deletes all policies from the current user
     */
    public void clearPolicies() {
        policies.clearPolicies();
    }

    /**
     * Method responsible for saving the made changes
     * 
     * @return Outcome String redirecting to start page.
     */
    public String save() {
        policies.save();
        init();
        return "/index.jsp";
    }

    /**
     * Method responsible for discarding the made changes
     * 
     * @return Outcome String redirecting to start page.
     */
    public String discard() {
        policies.discard();
        init();
        return "/index.jsp";
    }

    /**
     * Returns the policies of the current user
     * 
     * @return the policies
     */
    public OrganisationalPolicies getPolicies() {
        return policies;
    }

    /**
     * Initiates a download for the provided policy
     * 
     * @param policy
     *            the policy to download
     */
    public void downloadPolicy(RDFPolicy policy) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_kkmmss");

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
            .getResponse();
        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition",
            "attachement; filename=\"Policy_" + formatter.format(policy.getDateCreated()) + ".rdf\"");
        response.setContentLength(policy.getPolicy().length());
        try {
            PrintWriter writer = new PrintWriter(response.getOutputStream());
            writer.write(policy.getPolicy());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            facesMessages.addError("An error occured while generating the policy file");
        }
        FacesContext.getCurrentInstance().responseComplete();
    }

    public String controlPolicyToString(ControlPolicy controlPolicy) {
        String text = "The control policy identified by URI " + controlPolicy.getUri() + " ";

        text += "indicates that measure " + controlPolicy.getMeasure().getName() + " ";
        text += controlPolicy.getModality().toString() + " have a value " + controlPolicy.getQualifier().toString()
            + " " + controlPolicy.getValue();

        return text;
    }

    /**
     * Creates a header string for the provided policy.
     * 
     * @param policy
     *            the policy to use
     * @return the header string
     */
    public String getPolicyHeaderText(RDFPolicy policy) {
        if (policy.getDateCreated() == null) {
            return "New";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk-mm-ss");
            return "Uploaded: " + formatter.format(policy.getDateCreated());
        }
    }

    // --------------- getter/setter ---------------

    public UploadedFile getImportFile() {
        return importFile;
    }

    public void setImportFile(UploadedFile importFile) {
        this.importFile = importFile;
    }

    public OrganisationalPolicies getOrganisationalPolicies() {
        return policies;
    }

    public void setOrganisationalPolicies(OrganisationalPolicies organisationalPolicies) {
        this.policies = organisationalPolicies;
    }

    public User getUser() {
        return user;
    }
}
