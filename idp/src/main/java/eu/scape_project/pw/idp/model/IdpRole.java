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
   * Unique name of the user.
   */
  @Id
  @Column(unique = true)
  private String roleName;

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(final String roleName) {
    this.roleName = roleName;
  }

}
