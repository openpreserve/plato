package eu.scape_project.planning.policies;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.xml.TreeLoader;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Organisation;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.tree.PolicyTree;

@Stateful
@SessionScoped
public class OrganisationalPolicies implements Serializable {
	private static final long serialVersionUID = 1811189638942547758L;

	@Inject private Logger log;
	
	@Inject private TreeLoader treeLoader;
	
	@Inject private EntityManager em;
	
	@Inject	private User user;
	
	/**
	 * Method responsible for importing a policy tree from a a given FreeMind file.
	 * 
	 * @param file FreeMind file to import the policy tree from.
	 * @return True if the import was successful. False otherwise.
	 */
	public boolean importPolicyTreeFromFreemind(DigitalObject file) {
        PolicyTree newtree = null;
        
        try {
            InputStream istream = new ByteArrayInputStream(file.getData().getData());
            newtree = treeLoader.loadFreeMindPolicyTree(istream);
        } catch (Exception e) {
        	log.info("Policy import from file " + file.getFullname() + " FAILED");
            log.error(e.getMessage(),e);
            return false;
        }
        
        if (newtree == null) {
        	return false;
        }

        Organisation org = user.getOrganisation();
        
        // TODO: Check if really necessary
        /*
        if (org == null) {
            org = new Organisation();
            user.setOrganisation(org);
        }
        */
        
        org.setPolicyTree(newtree);
        log.info("Policy import from file " + file.getFullname() + " successful");
		
		return true;
	}
	
	/**
	 * Method responsible for removing the current set policy tree.
	 */
	public void removePolicyTree() {
        Organisation org = user.getOrganisation();
        
        if (org != null) {
            org.setPolicyTree(null);
        }
        
        log.info("Removed policy tree");
	}
	
	/**
	 * Method responsible for saving the made changes.
	 */
	public void save() {
        Organisation org = user.getOrganisation();
        
        // TODO: check if really necessary
        // nothing to save
        /*
        if (org == null) {
            return;
        }
        
        if (org.getId() == 0) {
            em.persist(org);
        }
        */
        
        em.persist(em.merge(org));
        
        log.info("Policies saved");
	}
	
	/**
	 * Method responsible for discarding the made changes.
	 */
    public void discard() {
    	Organisation oldOrganisation = em.find(Organisation.class, user.getOrganisation().getId());
    	user.setOrganisation(oldOrganisation);
    	
    	//user = em.find(User.class, user.getId());
        
    	log.info("Policies discarded");
    }
}
