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
