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
package eu.scape_project.planning.user;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.com.sun.net.httpserver.Authenticator.Success;
import org.slf4j.Logger;

import eu.scape_project.planning.model.Organisation;
import eu.scape_project.planning.model.User;

@Stateful
@SessionScoped
public class Groups implements Serializable {
    private static final long serialVersionUID = 1811189638942547758L;

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private User user;

    private Properties mailProperties;

    private Set<User> changedUsers = new HashSet<User>();

    private Set<Organisation> changedGroups = new HashSet<Organisation>();

    @PostConstruct
    public void init() {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mail.properties");
            if (in != null) {
                mailProperties = new Properties();
                mailProperties.load(in);
            } else {
                log.error("Could not find mail.properties");
            }
        } catch (IOException e) {
            log.error("Error while loading mail.properties", e);
        }

    }

    /**
     * Invites users to join the group of the current user.
     * 
     * @param invitedUsers
     *            the users to invite
     * @param serverString
     *            the server string
     */
    public List<String> inviteUsers(List<String> inviteMails, String serverString) {

        List<String> successfullyInvitedMails = new ArrayList<String>(inviteMails.size());

        for (String invitedMail : inviteMails) {
            invitedMail = invitedMail.trim();
            try {
                User user = em.createQuery("SELECT u From User u WHERE u.email = :email", User.class)
                    .setParameter("email", invitedMail).getSingleResult();

                if (inviteUser(user, serverString)) {
                    successfullyInvitedMails.add(invitedMail);
                }

            } catch (NoResultException e) {
                // TODO: Ignore/Return error?
            }
        }
        return successfullyInvitedMails;
    }

    /**
     * Invites a user to join the group of the current user.
     * 
     * @param inviteUsers
     *            the users to invite
     * @param serverString
     *            the server string
     */
    public boolean inviteUser(User inviteUser, String serverString) {

        inviteUser.setInvitationActionToken(UUID.randomUUID().toString());
        inviteUser.setInvitedGroup(user.getOrganisation());
        em.merge(inviteUser);
        log.debug("Set invitationActionToken for user " + inviteUser.getUsername());

        return sendInvitationMail(inviteUser, serverString);
    }

    /**
     * Sends an invitation mail to the user.
     * 
     * @param toUser
     *            the recipient of the mail
     * @param serverString
     *            the server string
     * @return true if the mail was sent successfully, false otherwise
     */
    private boolean sendInvitationMail(User toUser, String serverString) {
        try {
            Properties props = System.getProperties();

            props.put("mail.smtp.host", mailProperties.getProperty("server.smtp"));
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProperties.getProperty("mail.from")));
            message.setRecipient(RecipientType.TO, new InternetAddress(toUser.getEmail()));
            message.setSubject(user.getFullName() + " invited you to join the Plato group "
                + user.getOrganisation().getName());

            StringBuilder builder = new StringBuilder();
            builder.append("Dear " + toUser.getFullName() + ", \n\n");
            builder.append("The Plato user " + user.getFullName() + " has invited you to join the group "
                + user.getOrganisation().getName() + ".\n");
            builder.append("Please use the following link to accept the invitation: \n");
            builder.append("http://" + serverString + "/plato/user/acceptGroupInvitation.jsf?uid="
                + toUser.getInvitationActionToken());
            builder.append("\n\n--\n");
            builder.append("Your Planningsuite team");

            message.setText(builder.toString());
            message.saveChanges();

            Transport.send(message);
            log.debug("Group invitation mail sent successfully to " + toUser.getEmail());

            return true;
        } catch (Exception e) {
            log.error("Error sending group invitation mail to " + toUser.getEmail(), e);
            return false;
        }
    }

    /**
     * Saves changes.
     */
    public void save() {
        em.merge(user);

        for (User changedUser : changedUsers) {
            em.merge(changedUser);
        }

        for (Organisation changedGroup : changedGroups) {
            if (changedGroup.getUsers().size() == 0) {
                em.remove(em.merge(changedGroup));
            } else {
                em.merge(changedGroup);
            }
        }

        log.debug("Saved user " + user.getUsername());
    }

    public void switchGroup() {
        switchGroup(user);
    }

    public void leaveGroup(Organisation group) {
        switchGroup(user, group);
    }

    public void switchGroup(User user) {
        Organisation group = new Organisation();
        group.setName(user.getUsername());

        switchGroup(user, group);
    }

    public void switchGroup(User user, Organisation group) {
        addChangedUser(user);

        user.getOrganisation().getUsers().remove(user);
        changedGroups.add(user.getOrganisation());

        group.getUsers().add(user);
        user.setOrganisation(group);

        log.debug("Switched user " + user.getUsername() + " to group " + group.getName());
    }

    /**
     * Marks a user as changed
     * 
     * @param user
     */
    private void addChangedUser(User user) {
        if (!user.equals(this.user) && !changedUsers.contains(user)) {
            changedUsers.add(user);
        }
    }

    /**
     * Discards changes.
     */
    public void discard() {

        User origUser = em.find(User.class, user.getId());
        user.setOrganisation(origUser.getOrganisation());

        changedUsers.clear();
        changedGroups.clear();

        log.debug("Groups changes discarted for user " + user.getUsername());
    }

    /**
     * Accept a group invitation.
     * 
     * @param invitationActionToken
     *            the invitation action token
     * @return true if the group was changed, false otherwise
     */
    public boolean acceptInvitation(String invitationActionToken) {

        try {

            User invitedUser = em
                .createQuery("SELECT u From User u WHERE u.invitationActionToken = :invitationActionToken", User.class)
                .setParameter("invitationActionToken", invitationActionToken).getSingleResult();

            if (!invitedUser.getUsername().equals(user.getUsername())) {
                log.info("InvitationActionToken for user " + user.getUsername() + " did not match.");
                return false;
            }

            switchGroup(user, invitedUser.getInvitedGroup());
            user.setInvitationActionToken("");
            user.setInvitedGroup(null);

            save();

            log.info("Invitation to group " + user.getOrganisation().getName() + " accepted by user "
                + user.getUsername());

            return true;

        } catch (NoResultException e) {
            log.info("InvitationActionToken for user " + user.getUsername() + " not found.");
            return false;
        }

    }
}
