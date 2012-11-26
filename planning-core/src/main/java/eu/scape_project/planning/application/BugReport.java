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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
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

import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.utils.ConfigurationLoader;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

/**
 * Sends a bug report.
 */
@RequestScoped
public class BugReport implements Serializable {
    private static final long serialVersionUID = -2769514045862394110L;

    private static final String SEPARATOR_LINE = "-------------------------------------------\n";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Inject
    private Logger log;

    @Inject
    private User user;

    @Inject
    private Messages messages;

    @Inject
    private ConfigurationLoader configurationLoader;

    private Configuration config;

    /**
     * Initialises the class.
     */
    @PostConstruct
    public void init() {
        config = configurationLoader.load();
    }

    /**
     * Method responsible for sending a bug report per mail.
     * 
     * @param userEmail
     *            email address of the user.
     * @param errorDescription
     *            error description given by the user.
     * @param exception
     *            the exception causing the bug/error.
     * @param location
     *            the location of the application where the error occurred
     * @throws MailException
     *             if the bug report could not be sent
     */
    public void sendBugReport(String userEmail, String errorDescription, Throwable exception, String location)
        throws MailException {
        sendBugReport(userEmail, errorDescription, exception, location, "PlanningSuite", null);
    }

    /**
     * Method responsible for sending a bug report per mail.
     * 
     * @param userEmail
     *            email address of the user.
     * @param errorDescription
     *            error description given by the user.
     * @param exception
     *            the exception causing the bug/error.
     * @param requestUri
     *            request URI where the error occurred
     * @param location
     *            the location of the application where the error occurred
     * @param applicationName
     *            application name
     * @throws MailException
     *             if the bug report could not be sent
     */
    public void sendBugReport(String userEmail, String errorDescription, Throwable exception, String requestUri,
        String location, String applicationName) throws MailException {
        sendBugReport(userEmail, errorDescription, exception, requestUri, location, applicationName, null);
    }

    /**
     * Method responsible for sending a bug report per mail.
     * 
     * @param userEmail
     *            email address of the user.
     * @param errorDescription
     *            error description given by the user.
     * @param exception
     *            the exception causing the bug/error.
     * @param requestUri
     *            request URI where the error occurred
     * @param location
     *            the location of the application where the error occurred
     * @param applicationName
     *            application name
     * @param plan
     *            the plan where the exception occurred
     * @throws MailException
     *             if the bug report could not be sent
     */
    public void sendBugReport(String userEmail, String errorDescription, Throwable exception, String requestUri,
        String location, String applicationName, Plan plan) throws MailException {

        try {
            Properties props = System.getProperties();

            props.put("mail.smtp.host", config.getString("mail.smtp.host"));
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getString("mail.from")));
            message.setRecipient(RecipientType.TO, new InternetAddress(config.getString("mail.feedback")));

            message.setSubject("[" + applicationName + "] from " + location);

            StringBuilder builder = new StringBuilder();
            // Date
            builder.append("Date: ").append(DATE_FORMAT.format(new Date())).append("\n\n");

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

            // Plan
            if (plan == null) {
                builder.append("No plan available.").append("\n\n");
            } else {
                builder.append("Plan type: ").append(plan.getPlanProperties().getPlanType()).append("\n");
                builder.append("Plan ID: ").append(plan.getPlanProperties().getId()).append("\n");
                builder.append("Plan name: ").append(plan.getPlanProperties().getName()).append("\n\n");
            }

            // Description
            builder.append("Description:\n");
            builder.append(SEPARATOR_LINE);
            builder.append(errorDescription).append("\n");
            builder.append(SEPARATOR_LINE).append("\n");

            // Request URI
            builder.append("Request URI: ").append(requestUri).append("\n\n");

            // Exception
            if (exception == null) {
                builder.append("No exception available.").append("\n");
            } else {
                builder.append("Exception type: ").append(exception.getClass().getCanonicalName()).append("\n");
                builder.append("Exception message: ").append(exception.getMessage()).append("\n");

                StringWriter writer = new StringWriter();
                exception.printStackTrace(new PrintWriter(writer));

                builder.append("Stacktrace:\n");
                builder.append(SEPARATOR_LINE);
                builder.append(writer.toString());
                builder.append(SEPARATOR_LINE);
            }

            message.setText(builder.toString());
            message.saveChanges();

            Transport.send(message);
            log.debug("Bug report mail sent successfully to {}", config.getString("mail.feedback"));
        } catch (MessagingException e) {
            log.error("Error sending bug report mail to {}", config.getString("mail.feedback"), e);
            throw new MailException("Error sending bug report mail to " + config.getString("mail.feedback"), e);
        }
    }

    /**
     * Adds the provided throwable and session information to the messages list.
     * 
     * @param throwable
     *            the throwable
     * @param sessionId
     *            the current session ID
     * @param currentPage
     *            the current page
     */
    public void addExeptionToMessages(Throwable throwable, String sessionId, String currentPage) {
        addExeptionToMessages(throwable, sessionId, currentPage, null);
    }

    /**
     * Adds the provided throwable and session information to the messages list.
     * 
     * @param throwable
     *            the throwable
     * @param sessionId
     *            the current session ID
     * @param currentPage
     *            the current page
     * @param currentPlan
     *            the current plan
     */
    public void addExeptionToMessages(Throwable throwable, String sessionId, String currentPage, Plan currentPlan) {

        String username = null;
        if (user != null) {
            username = user.getUsername();
        } else {
            username = "";
        }

        ErrorMessage em = new ErrorMessage(throwable.getClass().getCanonicalName(), throwable.getMessage(), sessionId,
            username, currentPage, currentPlan);
        messages.addErrorMessage(em);
        log.debug("Added Error to Messages array: " + throwable.getClass().getCanonicalName());
    }
}
