/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.application;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import eu.scape_project.planning.model.User;
import eu.scape_project.planning.utils.ConfigurationLoader;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

/**
 * Sends feedback from the user.
 */
@RequestScoped
public class Feedback implements Serializable {
    private static final long serialVersionUID = 2140517067442736238L;

    private static final String SEPARATOR_LINE = "-------------------------------------------\n";

    @Inject
    private Logger log;

    @Inject
    private User user;

    @Inject
    private ConfigurationLoader configurationLoader;

    private Configuration config;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Initialises the class.
     */
    @PostConstruct
    public void init() {
        config = configurationLoader.load();
    }

    /**
     * Method responsible for sending feedback per mail.
     * 
     * @param userEmail
     *            email address of the user.
     * @param userComments
     *            comments from the user
     * @param location
     *            the location of the application where the error occurred
     * @throws MailException
     *             if the feedback could not be sent
     */
    public void sendFeedback(String userEmail, String userComments, String location) throws MailException {
        sendFeedback(userEmail, userComments, location, "PlanningSuite");
    }

    /**
     * Method responsible for sending feedback per mail.
     * 
     * @param userEmail
     *            email address of the user.
     * @param userComments
     *            comments from the user
     * @param location
     *            the location of the application where the error occurred
     * @param applicationName
     *            the name of the application
     * @throws MailException
     *             if the feedback could not be sent
     */
    public void sendFeedback(String userEmail, String userComments, String location, String applicationName)
        throws MailException {

        try {
            Properties props = System.getProperties();

            props.put("mail.smtp.host", config.getString("mail.smtp.host"));
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getString("mail.from")));
            message.setRecipient(RecipientType.TO, new InternetAddress(config.getString("mail.feedback")));

            message.setSubject("[" + applicationName + "] from " + location);

            StringBuilder builder = new StringBuilder();
            builder.append("Date: ").append(dateFormat.format(new Date())).append("\n\n");

            // User info
            if (user == null) {
                builder.append("No user available.\n\n");
            } else {
                builder.append("User: ").append(user.getUsername()).append("\n");
                if (user.getUserGroup() != null) {
                    builder.append("Group: ").append(user.getUserGroup().getName()).append("\n");
                }
            }
            builder.append("UserMail: ").append(userEmail).append("\n\n");

            // Comments
            builder.append("Comments:\n");
            builder.append(SEPARATOR_LINE);
            builder.append(userComments).append("\n");
            builder.append(SEPARATOR_LINE).append("\n");
            message.setText(builder.toString());
            message.saveChanges();

            Transport.send(message);

            log.debug("Feedback mail sent successfully to {}", config.getString("mail.feedback"));
        } catch (MessagingException e) {
            log.error("Error sending feedback mail to {}", config.getString("mail.feedback"), e);
            throw new MailException("Error sending feedback mail to " + config.getString("mail.feedback"), e);
        }
    }
}
