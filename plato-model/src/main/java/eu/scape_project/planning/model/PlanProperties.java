/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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
 * Holds attributes of a preservation plan. Attributes such as the plans author
 * or whether the plan is read-only.
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
     * Identifies this plan in a repository.
     */
    private String repositoryIdentifier;

    /**
     * Author of the preservation project.
     */
    private String author;

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

    /**
     * Indicates that this plan is only for playing around. Note: This property
     * is not exported/imported, as soon as it is stored externally, it is seen
     * as important.
     */
    private boolean playground = false;

    /**
     * Indicates whether the plan may be unlocked. As the plan is locked when a
     * user is working with it we needed a mechanism to prevent a plan from
     * being permanently locked. This may occur when the user doesn't logout
     * properly or some unexpected error occurs. If a plan may be unlocked is
     * determined in
     * {@link eu.scape_project.planning.action.project.LoadPlanAction#list()}
     */
    @Transient
    private boolean allowUnlock = false;

    @Transient
    private boolean mayEdit = false;

    /**
     * Constructs a new plan properties entity.
     */
    public PlanProperties() {
        this.reportUpload = new DigitalObject();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    @Override
    public void handleChanges(IChangesHandler h) {
        h.visit(this);

        reportUpload.handleChanges(h);
    }

    @Override
    public void touch() {
        this.changeLog.touch();
    }

    @Override
    public boolean isChanged() {
        return changeLog.isAltered();
    }

    /**
     * States if the plan is currently closed.
     * 
     * @return
     */
    public boolean isClosed() {
        return (openHandle == 0);
    }

    // ********** getter/setter **********
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

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
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

    public boolean isAllowUnlock() {
        return allowUnlock;
    }

    public void setAllowUnlock(boolean value) {
        this.allowUnlock = value;
    }

    public String getOpenedByUser() {
        return openedByUser;
    }

    public void setOpenedByUser(String openedByUser) {
        this.openedByUser = openedByUser;
    }

    public String getRepositoryIdentifier() {
        return repositoryIdentifier;
    }

    public void setRepositoryIdentifier(String repositoryIdentifier) {
        this.repositoryIdentifier = repositoryIdentifier;
    }

    public boolean isMayEdit() {
        return mayEdit;
    }

    public void setMayEdit(boolean mayEdit) {
        this.mayEdit = mayEdit;
    }

    public boolean isPlayground() {
        return playground;
    }

    public void setPlayground(boolean playground) {
        this.playground = playground;
    }
}
