package eu.scape_project.pw.idp.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 * User object used in the identity provider.
 */
@Entity
public class IdpUser {

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
  private String email;

  /**
   * Password of the user.
   */
  private String password;

  /**
   * Roles of this user.
   */
  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, targetEntity = IdpRole.class, fetch = FetchType.EAGER)
  @JoinTable(name = "IdpUserRoles", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "roleId"))
  private List<IdpRole> roles = new ArrayList<IdpRole>();

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

  public long getId() {
    return id;
  }

  public void setId(final long id) {
    this.id = id;
  }

}
