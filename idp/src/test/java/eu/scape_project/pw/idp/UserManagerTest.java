package eu.scape_project.pw.idp;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;
import org.slf4j.Logger;

import eu.scape_project.pw.idp.model.IdpUser;
import eu.scape_project.pw.idp.model.IdpUserState;

public class UserManagerTest {
    private UserManager userManager;
    
    public UserManagerTest() {
        this.userManager = new UserManager();
        
        Logger log = mock(Logger.class);
        this.userManager.setLog(log);
    }
    
    @Test
    public void activateUser_actionTokenNotMatching_fail() {
        EntityManager em = mock(EntityManager.class);
        Query query = mock(Query.class);
        Query parameterQuery = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyObject())).thenReturn(parameterQuery);
        
        // token does not exist in db
        List<IdpUser> matchingUser = new ArrayList<IdpUser>();
        when(parameterQuery.getResultList()).thenReturn(matchingUser);
        userManager.setEntityManager(em);

        Boolean success = userManager.activateUser("not-existing-token");
        assertFalse(success);
    }
    
    @Test
    public void activateUser_actionTokenMatchingTwice_fail() {
        EntityManager em = mock(EntityManager.class);
        Query query = mock(Query.class);
        Query parameterQuery = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyObject())).thenReturn(parameterQuery);
        
        // token exist duplicate in db
        List<IdpUser> matchingUser = new ArrayList<IdpUser>();
        IdpUser user1 = new IdpUser();
        IdpUser user2 = new IdpUser();
        matchingUser.add(user1);
        matchingUser.add(user2);
        when(parameterQuery.getResultList()).thenReturn(matchingUser);
        userManager.setEntityManager(em);

        Boolean success = userManager.activateUser("duplicate-token");
        assertFalse(success);        
    }
    
    @Test
    public void activateUser_actionTokenOK_userIsActivated_success() {
        EntityManager em = mock(EntityManager.class);
        Query query = mock(Query.class);
        Query parameterQuery = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyObject())).thenReturn(parameterQuery);
        
        // token exists exactly once in db
        List<IdpUser> matchingUser = new ArrayList<IdpUser>();
        IdpUser user = mock(IdpUser.class);
        matchingUser.add(user);
        when(parameterQuery.getResultList()).thenReturn(matchingUser);
        userManager.setEntityManager(em);

        Boolean success = userManager.activateUser("duplicate-token");
        assertTrue(success);
        
        verify(user).setStatus(IdpUserState.ACTIVE);
        verify(user).setActionToken("");
        verify(em).persist(user);
    }
}
