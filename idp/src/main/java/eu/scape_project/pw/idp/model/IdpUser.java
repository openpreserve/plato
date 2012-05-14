package eu.scape_project.pw.idp.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

/**
 * User object used in the identity provider.
 */
@Entity
public class IdpUser {
    @Id
    @GeneratedValue
    private int id;

    /**
     * Unique name of the user.
     */
    @Size(min = 4)
    @Column(unique = true)
    private String username;

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * E-Mail of the user.
     */
    @Email
    private String email;

    /**
     * Password of the user.
     */
    @Size(min = 6)
    private String password;

    /**
     * Roles of this user.
     */
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, targetEntity = IdpRole.class, fetch = FetchType.EAGER)
    private List<IdpRole> roles = new ArrayList<IdpRole>();

    /**
     * State of the user. Used e.g. to identify if a user is just created or
     * already activated (e.g. by email validation)
     */
    @Enumerated(EnumType.STRING)
    private IdpUserState status;

    /**
     * Token required to be allowed to execute specific actions on the user
     * (e.g. activate a created user) (After a token is used one time it should
     * be deleted)
     */
    private String actionToken;

    public IdpUser() {
        this.status = IdpUserState.CREATED;
    }

    // ---------- getter/setter ----------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public List<IdpRole> getRoles() {
        return roles;
    }

    public void setRoles(final List<IdpRole> roles) {
        this.roles = roles;
    }

    public IdpUserState getStatus() {
        return status;
    }

    public void setStatus(IdpUserState status) {
        this.status = status;
    }

    public String getActionToken() {
        return actionToken;
    }

    public void setActionToken(String actionToken) {
        this.actionToken = actionToken;
    }
}
