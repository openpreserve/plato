package eu.scape_project.planning.application;

import java.util.Iterator;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Method responsible for handling Exceptions not catched by the application.
 * 
 * @author Markus Hamm
 */
public class DefaultExceptionHandler extends ExceptionHandlerWrapper {

	private static Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);
	
	private ExceptionHandler wrapped;
		
	public DefaultExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() {
		 // for each causing exception
		 for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
			 // get causing exception
			 ExceptionQueuedEvent event = i.next();
			 ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			 Throwable exception = context.getException();
			 
			 log.error("Handling exception", exception);

			 FacesContext fc = FacesContext.getCurrentInstance();

			 // Redirect session/conversation-timeout error to the start-page
			 if ((exception instanceof NonexistentConversationException) || (exception instanceof ViewExpiredException)) {
				 // remove error and all following errors from queue (following errors are deleted/ignored because a session/conversation-timeout anyhow resets the application)
				 i.remove();
				 while (i.hasNext()) {
				 	 i.next();
					 i.remove();
				 }
				 
			     // redirect to start page
				 try{
					 fc.getExternalContext().redirect(fc.getExternalContext().getRequestContextPath() + "/index.jsf?sessionExpired=true");
				 }
				 catch (Exception e) {}
			 }
			 // Redirect all other errors to the bugreport-page
			 else {
				 // remove error from queue
				 i.remove();
				 
				 // put Exception and originating page in flash context to be accessible at next page (bugreport-page)
				 fc.getExternalContext().getFlash().put("exception", exception);
				 fc.getExternalContext().getFlash().put("exceptionPage", fc.getViewRoot().getViewId());
				 
				 // forward/dispatch to bugreport-page
				 // ATTENTION: usually we would have to do a redirect here because ajax-requests cannot be forwarded/dispatched properly.
				 // Thus, if an exception is thrown from an AJAX-request the bugreport-page shows up but the user has to click send-bugreport 
				 // twice to execute the action (and then all exception-information is lost and not propagated via mail).
				 // Redirect cannot be done now because of a bug in JavaServerFaces 2: http://java.net/jira/browse/JAVASERVERFACES-2136
				 // TODO: Switch to redirect here as soon as the bug is fixed!
				 //fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/bugreport.jsf");

				 // TODO: The bug mentioned above needs to be fixed to make the redirect working properly (to add exception info to the bug-report)
				 // redirect which does not work yet because of the bug mentioned above
				 try {
					 fc.getExternalContext().redirect(fc.getExternalContext().getRequestContextPath() + "/bugreport.jsf");
				 }
				 catch (Exception e) {
				 }
			 }
		 }
	 }
}
