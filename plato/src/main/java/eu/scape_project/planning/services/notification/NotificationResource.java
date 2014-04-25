package eu.scape_project.planning.services.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;


@Consumes({"application/json"})
@Produces({"application/json"})
public class NotificationResource {
	static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(NotificationResource.class);
	
	
	@POST
	@Path("/notification")
	public WatchNotification addNotification(WatchNotification notification){
		LOGGER.info("recieved a notification: {}", notification.getMessage());
		return null;
	}

	@GET
	@Path("/notifications")
	public Collection<WatchNotification> getNotifications(){
		ArrayList<WatchNotification> list = new ArrayList<WatchNotification>();
		WatchNotification n = new WatchNotification();
		n.setMessage("test message");
		list.add(n);
		return list;
	}
}
