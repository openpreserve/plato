package eu.scape_project.pw.idp;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

@Stateful
@SessionScoped
@Named("userManager")
public class UserManager {

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

}
