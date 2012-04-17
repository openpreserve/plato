package eu.scape_project.pw.planning.application;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import eu.planets_project.pp.plato.model.Plan;
import eu.planets_project.pp.plato.model.User;

@RequestScoped
public class BugReport implements Serializable {
	private static final long serialVersionUID = -2769514045862394110L;
	
	@Inject Messages messages;
	
	@Inject private Logger log;
	
	@Inject User user;

	/**
	 * Method responsible for adding an error-message to the messages list.
	 * 
	 * @param errorType
	 * @param errorMessage
	 * @param sessionId
	 * @param userName
	 * @param currentPage
	 * @param plan
	 */
	public void addExeptionToMessages(String errorType, String errorMessage, String sessionId, String userName, String currentPage) {
		ErrorMessage em = new ErrorMessage(errorType, errorMessage, sessionId, userName, currentPage, null);
		messages.addErrorMessage(em);
		log.debug("Added Error to Messages array: " + errorType);
	}
	
	/**
	 * Method responsible for sending a bug report per mail.
	 * 
	 * @param exception Exception causing the bug/error.
	 * @param userDescription Error description given by the user.
	 * @param userEmail Email of the user.
	 * @param host Host-name of the machine where error occurred.
	 * @return True if bug report was sent with success, false otherwise.
	 */
	public boolean sendBugReport(Throwable exception, String userDescription, String userEmail, String host) {
		// EMail settings
		// FIXME
		final String toEmail = "hamm@ifs.tuwien.ac.at";
		final String fromEmail = "plato@ifs.tuwien.ac.at";
		final String smtpServer = "mr.tuwien.ac.at";
		
		// helper constants
		final String separatorLine = "\n-------------------------------------------\n";
				
        try {
            Properties props = System.getProperties();
            
            Properties mailProps = new Properties();
            mailProps.put("TO", toEmail);
            mailProps.put("FROM", fromEmail);
            mailProps.put("SMTPSERVER", smtpServer);
            
            props.put("mail.smtp.host", mailProps.getProperty("SMTPSERVER"));
            Session session = Session.getDefaultInstance(props, null);
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProps.getProperty("FROM")));
            message.setRecipient(RecipientType.TO, new InternetAddress(mailProps.getProperty("TO")));
            
            String exceptionType = "";
            String exceptionMessage = "";
            String exceptionStackTrace = "";
            
            if (exception != null) {
            	exceptionType = exception.getClass().getCanonicalName();
            	exceptionMessage = exception.getMessage();
            	StringWriter writer = new StringWriter();
            	exception.printStackTrace(new PrintWriter(writer));
            	exceptionStackTrace = writer.toString();
            }

            message.setSubject("[KBrowserError] " + exceptionType + " at " + host);
            StringBuilder builder = new StringBuilder();
            builder.append("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "\n");
            builder.append("User: " + ((user==null)? "Unknown" : user.getUsername()) + "\n");
            builder.append("ExceptionType: " + exceptionType + "\n");
            builder.append("ExceptionMessage: " + exceptionMessage + "\n\n");
            builder.append("UserMail:" + separatorLine + userEmail + separatorLine + "\n");
            builder.append("User Description:" + separatorLine + userDescription + separatorLine + "\n");
            builder.append(exceptionStackTrace);
            message.setText(builder.toString());
            message.saveChanges();
            
            Transport.send(message);
            log.debug("Bugreport mail sent successfully to " + toEmail);
            
            return true;
        } catch (Exception e) {
            log.debug("Error at sending bugreport mail to " + toEmail);
            return false;
        }
    }
	
}
