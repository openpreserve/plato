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
package eu.scape_project.pw.idp;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;

import eu.scape_project.pw.idp.model.IdpRole;
import eu.scape_project.pw.idp.model.IdpUser;
import eu.scape_project.pw.idp.model.IdpUserState;
import eu.scape_project.pw.idp.utils.PropertiesLoader;

/**
 * Class responsible for managing users in the identity provider.
 */
@Stateless
public class UserManager {

    /**
     * Name of the configuration file.
     */
    private static final String CONFIG_NAME = "mail.properties";

    /**
     * Entitymanager.
     */
    @Inject
    private EntityManager em;

    /**
     * Logger for this class.
     */
    @Inject
    private Logger log;

    /**
     * Standard rolename for a user.
     */
    private final String idpUserStandardRoleName = "authenticated";

    /**
     * Properties for activation mail.
     */
    private Properties mailProperties;

    /**
     * Init this class.
     */
    @PostConstruct
    public void init() {
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        mailProperties = propertiesLoader.load(CONFIG_NAME);
    }

    /**
     * Method responsible for adding a new user.
     * 
     * @param user
     *            User to add.
     * @throws IdpException
     */
    public void addUser(IdpUser user) {
        // Set standard role
        IdpRole role = null;
        try {
            role = em.createQuery("SELECT r from IdpRole r WHERE rolename = :rolename", IdpRole.class)
                .setParameter("rolename", idpUserStandardRoleName).getSingleResult();
        } catch (NoResultException e) {
            role = new IdpRole();
            role.setRoleName(idpUserStandardRoleName);
        }

        List<IdpRole> roles = user.getRoles();
        roles.add(role);
        user.setRoles(roles);

        // create a user actionToken which is needed for activation
        user.setActionToken(UUID.randomUUID().toString());
        em.persist(user);
        log.info("Added user with username " + user.getUsername());
    }

    /**
     * Method responsible for activating an already created user.
     * 
     * @param actionToken
     *            Unique-id (only known by the wanted user itself) used to
     *            identify the user to activate.
     * @return true if the user was activated
     */
    public Boolean activateUser(String actionToken) {
        List<IdpUser> matchingUser = em
            .createQuery("SELECT u FROM IdpUser u WHERE u.actionToken = :actionToken", IdpUser.class)
            .setParameter("actionToken", actionToken).getResultList();

        if (matchingUser.size() != 1) {
            log.error("Activate user failed: " + matchingUser.size() + " user matching given actionToken "
                + actionToken);
            return false;
        }

        // activate actionToken relating user
        IdpUser userToActivate = matchingUser.get(0);
        userToActivate.setStatus(IdpUserState.ACTIVE);
        userToActivate.setActionToken("");
        em.persist(userToActivate);

        log.info("Activated user with username " + userToActivate.getUsername());

        return true;
    }

    /**
     * Method responsible for sending a email to the user, including a link to
     * activate his user account.
     * 
     * @param user
     *            User the activation mail should be sent to
     * @param serverString
     *            Name and port of the server the user was added.
     * @return True if activation email was sent. False otherwise
     */
    public boolean sendActivationMail(IdpUser user, String serverString) {

        try {
            Properties props = System.getProperties();

            props.put("mail.smtp.host", mailProperties.getProperty("server.smtp"));
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProperties.getProperty("mail.from")));
            message.setRecipient(RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject("Please confirm your Planningsuite user account");

            StringBuilder builder = new StringBuilder();
            builder.append("Dear " + user.getFirstName() + " " + user.getLastName() + ", \n\n");
            builder.append("Please use the following link to confirm your Planningsuite user account: \n");
            builder.append("http://" + serverString + "/idp/activateUser.jsf?uid=" + user.getActionToken());
            builder.append("\n\n--\n");
            builder.append("Your Planningsuite team");

            message.setText(builder.toString());
            message.saveChanges();

            Transport.send(message);
            log.debug("Activation mail sent successfully to " + user.getEmail());

            return true;
        } catch (Exception e) {
            log.error("Error at sending activation mail to " + user.getEmail());
            return false;
        }
    }

    // ---------- getter/setter ----------

    // Method used to make this class Unit-testable
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    // Method used to make this class Unit-testable
    public void setLog(Logger log) {
        this.log = log;
    }
}
