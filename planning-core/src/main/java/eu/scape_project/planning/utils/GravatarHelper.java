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
package eu.scape_project.planning.utils;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import eu.scape_project.planning.model.User;

import de.bripkens.gravatar.DefaultImage;
import de.bripkens.gravatar.Gravatar;
import de.bripkens.gravatar.Rating;

/**
 * Helper class to get gravatar URL.
 */
@Named("gravatarHelper")
@SessionScoped
public class GravatarHelper implements Serializable {

    private static final long serialVersionUID = -2943870846396992143L;

    /**
     * Default size of the gravatar.
     */
    public static final int DEFAULT_SIZE = 30;

    @Inject
    private User user;

    /**
     * Returns a URL to the gravatar of the current user.
     * 
     * @return the URL
     */
    public String getGravatarURL() {
        return getGravatarURL(user.getEmail());
    }

    /**
     * Returns a URL to the gravatar of the current user for the provided size.
     * 
     * @param size
     *            the size of the gravtar
     * @return the URL
     */
    public String getGravatarURL(int size) {
        return getGravatarURL(user.getEmail(), size);
    }

    /**
     * Returns a URL to the gravatar for the provided email.
     * 
     * @param email
     *            the email of the user
     * @return the RUL
     */
    public String getGravatarURL(String email) {
        return getGravatarURL(email, DEFAULT_SIZE);
    }

    /**
     * Returns a URL to the gravatar for the provided email and size.
     * 
     * @param email
     *            the email of the user
     * @param size
     *            the size of the gravatar
     * @return the URL
     */
    public String getGravatarURL(String email, int size) {
        if (email == null) {
            return new Gravatar().setSize(size).setHttps(true).setRating(Rating.GENERAL_AUDIENCE)
                .setStandardDefaultImage(DefaultImage.MYSTERY_MAN).getUrl("");
        }

        return new Gravatar().setSize(size).setHttps(true).setRating(Rating.GENERAL_AUDIENCE)
            .setStandardDefaultImage(DefaultImage.MYSTERY_MAN).getUrl(email);
    }
}
