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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Holds attributes of a preservation project. Attributes such as the projects
 * author or whether the project is read-only.
 * 
 * @author Hannes Kulovits
 */
@Entity
public class PlanProperties implements Serializable, ITouchable {

    private static final long serialVersionUID = -8462944745153839130L;

    @Id
    @GeneratedValue
    private int id;

    /**
     * Author of the preservation project.
     */
    private String author;

    /**
     * This flag is used to be able to determine if a user is allowed to load a
     * project. It is set in LoadPlanAction
     */
    @Transient
    private boolean readOnly = false;

    /**
     * Hibernate note: standard length for a string column is 255 validation is
     * broken because we use facelet templates (issue resolved in Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String description;

    /**
     * The final report of the preservation plan can be uploaded.
     */
    @OneToOne(cascade = CascadeType.ALL)
    private DigitalObject reportUpload = new DigitalObject();

    /**
     * Name of the preservation plan. (Need not to be unique.)
     */
    @NotNull(message = "Please provide a project name")
    private String name;

    /**
     * Organisation the author belongs to.
     */
    private String organization;

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    /**
     * Used to realize the project-lock mechanism
     */
    private int openHandle = 0;

    /**
     * Name of the user that has opened the project last. When the project is
     * closed this property is reset to an empty string.
     */
    private String openedByUser = "";


    /**
     * Indicates if the project is set to private which means that only the user
     * who created it can open and edit it.
     */
    private boolean privateProject = false;

    /**
     * Although a project is set to private the uploaded report
     * {@link #reportUpload} may be opened by other users. If
     * {@link #reportPublic} is set to true this is the case.
     */
    private boolean reportPublic = false;

    /**
     * Name of the user that has created the project and owns it.
     */
    private String owner = "";

    @Enumerated(EnumType.STRING)
    private PlanState state = PlanState.CREATED;
    
    @Enumerated(EnumType.STRING)
    private PlanType planType = PlanType.FULL; 
    

    /**
     * Indicates whether the project may be realoded again. As the project is
     * locked when a user is working with it we needed a mechanism to prevent a
     * project from being permanently locked. This may occur when the user
     * doesn't logout properly or some unexpected error occurs. When a project
     * may be reloaded is determined in
     * {@link eu.scape_project.planning.action.project.LoadPlanAction#list()}
     */
    @Transient
    private boolean allowReload = false;

    public PlanProperties() {
        this.reportUpload = new DigitalObject();
    }

    public PlanState getState() {
        return state;
    }

    public void setState(PlanState state) {
        this.state = state;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isPrivateProject() {
        return privateProject;
    }

    public void setPrivateProject(boolean privateProject) {
        this.privateProject = privateProject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void touch() {
        this.changeLog.touch();
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);

        reportUpload.handleChanges(h);
    }

    /**
     * Actually, this is a "may open" or an "is closed"
     * 
     * @return
     */
    public boolean isOpen() {
        return (openHandle == 0);
    }

    public int getOpenHandle() {
        return this.openHandle;
    }

    public void setOpenHandle(int value) {
        this.openHandle = value;
    }

    public DigitalObject getReportUpload() {
        return reportUpload;
    }

    public void setReportUpload(DigitalObject reportUpload) {
        this.reportUpload = reportUpload;
    }

    public boolean isReportPublic() {
        return reportPublic;
    }

    public void setReportPublic(boolean reportPublic) {
        this.reportPublic = reportPublic;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isAllowReload() {
        return allowReload;
    }

    public void setAllowReload(boolean allowReload) {
        this.allowReload = allowReload;
    }

    public String getOpenedByUser() {
        return openedByUser;
    }

    public void setOpenedByUser(String openedByUser) {
        this.openedByUser = openedByUser;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }
}
