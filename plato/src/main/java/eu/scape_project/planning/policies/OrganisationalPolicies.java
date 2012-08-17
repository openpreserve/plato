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
package eu.scape_project.planning.policies;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.UserGroup;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.tree.PolicyTree;
import eu.scape_project.planning.xml.TreeLoader;

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

        UserGroup org = user.getUserGroup();
        
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
        UserGroup org = user.getUserGroup();
        
        if (org != null) {
            org.setPolicyTree(null);
        }
        
        log.info("Removed policy tree");
	}
	
	/**
	 * Method responsible for saving the made changes.
	 */
	public void save() {
        UserGroup org = user.getUserGroup();
        
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
    	UserGroup oldUserGroup = em.find(UserGroup.class, user.getUserGroup().getId());
    	user.setUserGroup(oldUserGroup);
    	
    	//user = em.find(User.class, user.getId());
        
    	log.info("Policies discarded");
    }
}
