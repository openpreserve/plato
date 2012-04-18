package eu.scape_project.pw.idp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * User object used in the identity provider.
 */
@Entity
public class IdpRole {

  /**
   * Unique Id of the user.
   */
  @Id
  @GeneratedValue
  private long id;

  /**
   * Unique username of the user.
   */
  @Column(unique = true)
  private String roleName;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

}
