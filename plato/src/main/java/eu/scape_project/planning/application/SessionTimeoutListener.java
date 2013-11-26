/**
 * 
 */
package eu.scape_project.planning.application;

import java.util.Enumeration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.weld.context.api.ContextualInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.manager.PlanManager;

/**
 * Listener to clean up session when timed out.
 * 
 * @author Michael Kraxner
 *
 */
public class SessionTimeoutListener implements HttpSessionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTimeoutListener.class);

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        LOGGER.info("created session {}",  event.getSession().getId());

    }


    /** 
     * Is called before the session is destroyed.
     *  
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        LOGGER.info("ending session {}", event.getSession().getId());
        Enumeration<String> names = event.getSession().getAttributeNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Object attribute = event.getSession().getAttribute(name);
            if (attribute instanceof ContextualInstance) {
                ContextualInstance s = (ContextualInstance)attribute;
                if (s.getInstance() instanceof PlanManager){
                    // unlock all plans opened and locked in this session
                    PlanManager planManager = (PlanManager) s.getInstance();
                    planManager.unlockSessionPlans();
                }
            }
        }
    }

}
