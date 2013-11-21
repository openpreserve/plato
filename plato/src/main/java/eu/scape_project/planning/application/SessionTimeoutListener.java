/**
 * 
 */
package eu.scape_project.planning.application;

import java.util.Enumeration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.weld.context.SerializableContextualInstanceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.plato.wfview.ViewWorkflowManager;

/**
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

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        LOGGER.info("ending session {}", event.getSession().getId());
        Enumeration<String> names = event.getSession().getAttributeNames();
        
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Object attribute = event.getSession().getAttribute(name);
            if (attribute instanceof SerializableContextualInstanceImpl) {
                SerializableContextualInstanceImpl s = (SerializableContextualInstanceImpl)attribute;
                if (s.getInstance() instanceof ViewWorkflowManager){
                    ViewWorkflowManager wfManager = (ViewWorkflowManager) s.getInstance();
                    Plan plan = wfManager.getPlan();
                    if (plan != null) {
                        LOGGER.info("closing plan {} (prop-id: {})", plan.getPlanProperties().getName(), plan.getPlanProperties().getId());
//                        wfManager.endWorkflow();
                    }
                    
                }
            }
        }
    }

}
