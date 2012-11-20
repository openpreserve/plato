package eu.scape_project.planning.application;

import eu.scape_project.planning.model.User;

public interface IAuthenticatedUserProvider {

    public abstract User getUser();

}