package eu.scape_project.planning.application;

import javax.enterprise.inject.Produces;

import eu.scape_project.planning.model.User;

public interface IAuthenticatedUserProvider {

    @Produces
    public abstract User getUser();

}