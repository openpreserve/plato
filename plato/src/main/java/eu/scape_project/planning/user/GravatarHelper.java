package eu.scape_project.planning.user;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.bripkens.gravatar.DefaultImage;
import de.bripkens.gravatar.Gravatar;
import de.bripkens.gravatar.Rating;
import eu.scape_project.planning.model.User;

@Named("gravatarHelper")
@SessionScoped
public class GravatarHelper implements Serializable {

    private static final long serialVersionUID = -2943870846396992143L;

    public static final int DEFAULT_SIZE = 30;

    @Inject
    private User user;

    public String getGravatarURL() {
        return getGravatarURL(user.getEmail());
    }

    public String getGravatarURL(int size) {
        return getGravatarURL(user.getEmail(), size);
    }

    public String getGravatarURL(String email) {
        return getGravatarURL(email, DEFAULT_SIZE);
    }

    public String getGravatarURL(String email, int size) {
        return new Gravatar().setSize(size).setHttps(true).setRating(Rating.GENERAL_AUDIENCE)
            .setStandardDefaultImage(DefaultImage.MYSTERY_MAN).getUrl(email);
    }
}
