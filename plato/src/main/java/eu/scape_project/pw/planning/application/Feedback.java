package eu.scape_project.pw.planning.application;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;

import eu.scape_project.planning.model.User;

@RequestScoped
public class Feedback implements Serializable {
	private static final long serialVersionUID = 2140517067442736238L;

	@Inject private Logger log;
	
	@Inject User user;

	/**
	 * Method responsible for sending feedback per mail.
	 * 
	 * @param userEmail Email of the user.
	 * @param userComments Textual feedback of the user.
	 * @param host Host-name of the machine where error occurred.
	 * @return True if bug report was sent with success, false otherwise.
	 */
	public boolean sendFeedback(String userEmail, String userComments, String host) {
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
            
            message.setSubject("[PlatoFeedback] " + " from " + host);
            StringBuilder builder = new StringBuilder();
            builder.append("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "\n");
            builder.append("User: " + ((user==null)? "Unknown" : user.getUsername()) + "\n" + "\n");
            builder.append("UserMail:" + separatorLine + userEmail + separatorLine + "\n");
            builder.append("Comments:" + separatorLine + userComments + separatorLine + "\n");
            message.setText(builder.toString());
            message.saveChanges();
            
            Transport.send(message);
            log.debug("Feedback mail sent successfully to " + toEmail);
            
            return true;
        } catch (Exception e) {
            log.debug("Error at sending feedback mail to " + toEmail);
            return false;
        }
    }	
}
