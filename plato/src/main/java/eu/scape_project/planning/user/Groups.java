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

import org.slf4j.Logger;

import eu.scape_project.planning.model.GroupInvitation;
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
     * Saves changes.
     */
    public void save() {
        em.merge(user);

        // Save changed users
        for (User changedUser : changedUsers) {
            em.merge(changedUser);
        }

        // Save/delete changed groups
        for (Organisation changedGroup : changedGroups) {
            if (changedGroup.getUsers().size() == 0) {
                deleteGroup(changedGroup);
            } else {
                em.merge(changedGroup);
            }
        }

        log.debug("Saved groupinfo of user " + user.getUsername());
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
     * Sets all invitations of the group to null and deletes the group.
     * 
     * @param group
     *            the group to delete
     */
    private void deleteGroup(Organisation group) {

        List<GroupInvitation> invitations = em
            .createQuery("SELECT i FROM GroupInvitation i WHERE i.invitedGroup = :invitedGroup", GroupInvitation.class)
            .setParameter("invitedGroup", group).getResultList();

        for (GroupInvitation invitation : invitations) {
            invitation.setInvitedGroup(null);
            em.merge(invitation);
        }

        em.remove(em.merge(group));

    }

    /**
     * Switches the group of the current user to a newly created group.
     */
    public void switchGroup() {
        switchGroup(user);
    }

    /**
     * Switches the group of the current user to a new group.
     * 
     * @param group
     *            the group to switch to
     */
    public void switchGroup(Organisation group) {
        switchGroup(user, group);
    }

    /**
     * Switches the group of the privided user to a newly created group.
     * 
     * @param user
     *            the user to change
     */
    public void switchGroup(User user) {
        Organisation group = new Organisation();
        group.setName(user.getUsername());

        switchGroup(user, group);
    }

    /**
     * Switches the group of the provided user to the provided group.
     * 
     * @param user
     *            the user to change
     * @param group
     *            the group to switch to
     */
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
     * Invites users to join the group of the current user.
     * 
     * @param invitedUsers
     *            the users to invite
     * @param serverString
     *            the server string
     */
    public List<String> inviteUsers(List<String> inviteMails, String serverString) {

        List<String> successfullyInvitedMails = new ArrayList<String>(inviteMails.size());

        for (String inviteMail : inviteMails) {
            inviteMail = inviteMail.trim();

            List<User> users = em.createQuery("SELECT u From User u WHERE u.email = :email", User.class)
                .setParameter("email", inviteMail).getResultList();

            if (users.size() > 0) {
                // Users found
                for (User user : users) {
                    if (inviteUser(user, serverString)) {
                        successfullyInvitedMails.add(inviteMail);
                    }
                }
            } else {
                // No user found
                if (inviteUser(inviteMail, serverString)) {
                    successfullyInvitedMails.add(inviteMail);
                }
            }
        }
        return successfullyInvitedMails;
    }

    /**
     * Invites a user to join the group of the current user.
     * 
     * @param inviteUser
     *            the user to invite
     * @param serverString
     *            the server string
     * @return true if the invitation mail was sent, false otherwise
     */
    public boolean inviteUser(User inviteUser, String serverString) {
        GroupInvitation invitation = createInvitation(inviteUser.getEmail());
        return sendInvitationMail(inviteUser, invitation, serverString);
    }

    /**
     * Invites a user to join the group of the current user.
     * 
     * @param inviteMail
     *            the email address of the user to invite
     * @param serverString
     *            the server string
     * @return true if the invitation mail was sent, false otherwise
     */
    public boolean inviteUser(String inviteMail, String serverString) {
        GroupInvitation invitation = createInvitation(inviteMail);
        return sendInvitationMail(invitation, serverString);
    }

    /**
     * Creates a new invitation for the provided email address.
     * 
     * @param inviteMail
     *            the email address of the user to invite
     * @return an invitation
     */
    private GroupInvitation createInvitation(String inviteMail) {

        List<GroupInvitation> existingInvitations = em
            .createQuery("SELECT i FROM GroupInvitation i WHERE i.email = :email AND i.invitedGroup = :invitedGroup",
                GroupInvitation.class).setParameter("email", inviteMail)
            .setParameter("invitedGroup", user.getOrganisation()).getResultList();

        for (GroupInvitation existingInvitation : existingInvitations) {
            em.remove(existingInvitation);
        }

        GroupInvitation invitation = new GroupInvitation();
        invitation.setEmail(inviteMail);
        invitation.setInvitationActionToken(UUID.randomUUID().toString());
        invitation.setInvitedGroup(user.getOrganisation());

        em.merge(invitation);
        log.debug("Created GroupInvitation for mail " + inviteMail);

        return invitation;
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
    private boolean sendInvitationMail(GroupInvitation invitation, String serverString) {
        try {
            Properties props = System.getProperties();

            props.put("mail.smtp.host", mailProperties.getProperty("server.smtp"));
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProperties.getProperty("mail.from")));
            message.setRecipient(RecipientType.TO, new InternetAddress(invitation.getEmail()));
            message.setSubject(user.getFullName() + " invited you to join the Plato group "
                + user.getOrganisation().getName());

            StringBuilder builder = new StringBuilder();
            builder.append("Hello, \n\n");
            builder.append("The Plato user " + user.getFullName() + " has invited you to join the group "
                + user.getOrganisation().getName() + ".\n\n");
            builder
                .append("We could not find your email address in our database. If you would like to accept the invitation, please first create an account at http://"
                    + serverString + "/idp/addUser.jsf.\n");
            builder
                .append("If you have an account, please log in and use the following link to accept the invitation: \n");
            builder.append("http://" + serverString + "/plato/user/acceptGroupInvitation.jsf?uid="
                + invitation.getInvitationActionToken());
            builder.append("\n\n--\n");
            builder.append("Your Planningsuite team");

            message.setText(builder.toString());
            message.saveChanges();

            Transport.send(message);
            log.debug("Group invitation mail sent successfully to " + invitation.getEmail());

            return true;
        } catch (Exception e) {
            log.error("Error sending group invitation mail to " + invitation.getEmail(), e);
            return false;
        }
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
    private boolean sendInvitationMail(User toUser, GroupInvitation invitation, String serverString) {
        try {
            Properties props = System.getProperties();

            props.put("mail.smtp.host", mailProperties.getProperty("server.smtp"));
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProperties.getProperty("mail.from")));
            message.setRecipient(RecipientType.TO, new InternetAddress(invitation.getEmail()));
            message.setSubject(user.getFullName() + " invited you to join the Plato group "
                + user.getOrganisation().getName());

            StringBuilder builder = new StringBuilder();
            builder.append("Dear " + toUser.getFullName() + ", \n\n");
            builder.append("The Plato user " + user.getFullName() + " has invited you to join the group "
                + user.getOrganisation().getName() + ".\n");
            builder.append("Please log in and use the following link to accept the invitation: \n");
            builder.append("http://" + serverString + "/plato/user/acceptGroupInvitation.jsf?uid="
                + invitation.getInvitationActionToken());
            builder.append("\n\n--\n");
            builder.append("Your Planningsuite team");

            message.setText(builder.toString());
            message.saveChanges();

            Transport.send(message);
            log.debug("Group invitation mail sent successfully to " + invitation.getEmail());

            return true;
        } catch (Exception e) {
            log.error("Error sending group invitation mail to " + invitation.getEmail(), e);
            return false;
        }
    }

    /**
     * Accept a group invitation.
     * 
     * @param invitationActionToken
     *            the invitation action token
     * @return true if the group was changed, false otherwise
     * @throws GroupNotFoundException
     *             if the group could not be found
     * @throws TokenNotFoundException
     *             if the token could not be found
     */
    public void acceptInvitation(String invitationActionToken) throws GroupNotFoundException, TokenNotFoundException {

        try {

            GroupInvitation invitation = em
                .createQuery("SELECT i FROM GroupInvitation i WHERE i.invitationActionToken = :invitationActionToken",
                    GroupInvitation.class).setParameter("invitationActionToken", invitationActionToken)
                .getSingleResult();

            em.remove(invitation);

            if (invitation.getInvitedGroup() == null) {
                throw new GroupNotFoundException();
            }

            switchGroup(user, invitation.getInvitedGroup());
            save();

            log.info("Invitation to group " + user.getOrganisation().getName() + " accepted by user "
                + user.getUsername());

        } catch (NoResultException e) {
            log.info("InvitationActionToken for user " + user.getUsername() + " not found.");
            throw new TokenNotFoundException(e);
        }

    }

    /**
     * Declines the group invitation.
     * 
     * @param invitationActionToken
     *            the invitation action token
     * @throws TokenNotFoundException
     *             if the token could not be found
     */
    public void declineInvitation(String invitationActionToken) throws TokenNotFoundException {
        try {

            GroupInvitation invitation = em
                .createQuery("SELECT i FROM GroupInvitation i WHERE i.invitationActionToken = :invitationActionToken",
                    GroupInvitation.class).setParameter("invitationActionToken", invitationActionToken)
                .getSingleResult();

            em.remove(invitation);

            log.info("Invitation to group " + user.getOrganisation().getName() + " declined by user "
                + user.getUsername());

        } catch (NoResultException e) {
            log.info("InvitationActionToken for user " + user.getUsername() + " not found.");
            throw new TokenNotFoundException(e);
        }
    }

    public String getInvitationGroupName(String invitationActionToken) throws TokenNotFoundException,
        GroupNotFoundException {

        try {
            GroupInvitation invitation = em
                .createQuery("SELECT i FROM GroupInvitation i WHERE i.invitationActionToken = :invitationActionToken",
                    GroupInvitation.class).setParameter("invitationActionToken", invitationActionToken)
                .getSingleResult();

            if (invitation.getInvitedGroup() == null) {
                throw new GroupNotFoundException();
            }

            return invitation.getInvitedGroup().getName();
        } catch (NoResultException e) {
            log.info("InvitationActionToken for user " + user.getUsername() + " not found.");
            throw new TokenNotFoundException(e);
        }
    }
}
