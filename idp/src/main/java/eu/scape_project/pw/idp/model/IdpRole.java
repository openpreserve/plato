package eu.scape_project.pw.idp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * User object used in the identity provider.
 */
@Entity
public class IdpRole {
    @Id
    @GeneratedValue
    private int id;
    
    @Column(unique = true)
    private String roleName;

    /**
     * User having this role assigned.
     */
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.REFRESH)
    private List<IdpUser> user = new ArrayList<IdpUser>();

    // ---------- getter/setter ----------
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public List<IdpUser> getUser() {
        return user;
    }

    public void setUser(List<IdpUser> user) {
        this.user = user;
    }
}
