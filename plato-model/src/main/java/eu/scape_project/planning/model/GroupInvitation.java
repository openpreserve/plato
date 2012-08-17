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
package eu.scape_project.planning.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

@Entity
public class GroupInvitation implements Serializable {

    private static final long serialVersionUID = 1279255719031225123L;

    @Id
    @GeneratedValue
    private long id;

    /**
     * Token required to be allowed to accept an invitation to a group (After a
     * token is used one time it should be deleted)
     */
    @Column(unique = true)
    private String invitationActionToken;

    private String email;

    @ManyToOne
    private UserGroup invitedGroup;

    private Date dateCreated;

    /**
     * Fills the date created before persisting the object.
     */
    @SuppressWarnings("unused")
    @PrePersist
    private void fillDateCreated() {
        dateCreated = new Date();
    }

    // ---------- getter/setter ----------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInvitationActionToken() {
        return invitationActionToken;
    }

    public void setInvitationActionToken(String invitationActionToken) {
        this.invitationActionToken = invitationActionToken;
    }

    public UserGroup getInvitedGroup() {
        return invitedGroup;
    }

    public void setInvitedGroup(UserGroup invitedGroup) {
        this.invitedGroup = invitedGroup;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

}
