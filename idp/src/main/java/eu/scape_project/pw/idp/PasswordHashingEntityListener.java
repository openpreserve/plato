package eu.scape_project.pw.idp;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.codec.digest.DigestUtils;

import eu.scape_project.pw.idp.model.IdpUser;

/**
 * EntityListener class that hashes the plaintext password befor storing the
 * user into the DB.
 */
public class PasswordHashingEntityListener {

    /**
     * Hashes the plainPassword of the user if set and stores it in password.
     * 
     * @param user
     */
    @SuppressWarnings("unused")
    @PreUpdate
    @PrePersist
    private void hashPassword(IdpUser user) {
        String password = user.getPlainPassword();
        if (password != null) {
            user.setPassword(DigestUtils.md5Hex(password));
        }
    }
}
