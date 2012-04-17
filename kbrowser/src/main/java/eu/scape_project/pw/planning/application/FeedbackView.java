package eu.scape_project.pw.planning.application;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import eu.scape_project.pw.planning.utils.FacesMessages;

@ManagedBean(name="feedback")
@ViewScoped
public class FeedbackView implements Serializable {
	private static final long serialVersionUID = -6886808166752517894L;

//	@Inject	private Logger log;

	@Inject private	Feedback feedback;
	
	@Inject
	private FacesMessages facesMessages;
	
	private String userEmail;
	
	private String userComments;

		
	/**
	 * Method responsible for sending user feedback per mail.
	 */
	public void sendFeedback() {
		String host = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getLocalName();
		
		boolean success = feedback.sendFeedback(userEmail, userComments, host);
		
		if (success) {
			facesMessages.addInfo("sendFeedback", "Thank you! Your feedback has been sent.");
		}
		else {
			facesMessages.addError("sendFeedback",
				"Feedback couldn't be sent." +
	            "Because of an internal error your feedback couldn't be sent. We apologise for this and hope you are willing to inform us about this so we can fix the problem. " +
	            "Please send an email to plato@ifs.tuwien.ac.at with a " +
	            "description of what you have been doing at the time of the error." +
	             "Thank you very much!");
		}
	}

	// --------------- getter/setter ---------------

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserComments() {
		return userComments;
	}

	public void setUserComments(String userComments) {
		this.userComments = userComments;
	}
}
