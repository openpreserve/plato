package eu.scape_project.planning.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import eu.scape_project.planning.model.Notification;
import eu.scape_project.planning.model.User;

@Named("notifications")
@RequestScoped
public class NotificationView implements Serializable{
    private static final long serialVersionUID = -3729324333030725384L;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private User user;
    
    @Inject
    UserTransaction tx;
    
    private List<Notification> notifications;
    
    public void acceptNotification(Notification note){
        try {
            tx.begin();
            em.remove(em.merge(note));
            notifications = em.createQuery("select n from Notification n where n.recipient.id=:id", Notification.class).setParameter("id", user.getId()).getResultList();
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Notification> getNotifications(){
        if (notifications == null) {
            try {
                tx.begin();
                notifications = em.createQuery("select n from Notification n where n.recipient.id=:id", Notification.class).setParameter("id", user.getId()).getResultList();
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                notifications = new ArrayList<Notification>();
            }
        }
        return notifications;
    }
    
}
