package eu.scape_project.planning.services.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;

import eu.scape_project.planning.model.Notification;
import eu.scape_project.planning.model.User;

@Path("/")
@Consumes({"application/json"})
@Produces({"application/json"})
public class NotificationResource {
    static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationResource.class);

    @PersistenceContext
    private EntityManager em;

    @Resource
    UserTransaction ut;    
    

    /**
     * adds the notification in the system.
     * If there is a user with email equal to plannerEmail registered, a notification is stored.
     * 
     * @param notification
     * @return
     */
    @PUT
    @Path("/notification")
    public boolean addNotification(WatchNotification notification) {
        LOGGER.info("recieved a notification: {}", notification.getMessage());

        List<User> recipients = new ArrayList<User>();
        
        if (notification.getPlannerEmail() != null) {
            User recipient = em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", notification.getPlannerEmail()).getSingleResult();
            if (recipient != null) {
                recipients.add(recipient);
            }
        }
        // TODO add users related to plan with repository id planId
        // ..
        if (recipients.size() > 0) {
            String notificationID = UUID.randomUUID().toString();
            Date notificationTime = new Date();
            
            String message = notification.getMessage();
            
            try {
                ut.begin();
                for (User recipient : recipients) {
                    Notification n = new Notification(notificationID, notificationTime, "SCOUT", message, recipient);
                    em.persist(n);
                }
                ut.commit();
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    @GET
    @Path("/notifications")
    public Collection<WatchNotification> getNotifications() {
        ArrayList<WatchNotification> list = new ArrayList<WatchNotification>();
        WatchNotification n = new WatchNotification(null, "test message", null, null, "test@test.com");
        list.add(n);
        return list;
    }
}
