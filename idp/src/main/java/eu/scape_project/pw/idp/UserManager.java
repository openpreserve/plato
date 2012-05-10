package eu.scape_project.pw.idp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;

import eu.scape_project.pw.idp.model.IdpRole;
import eu.scape_project.pw.idp.model.IdpUser;
import eu.scape_project.pw.idp.model.IdpUserState;

@Stateless
public class UserManager {
    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

    /**
     * Method responsible for adding a new user.
     * 
     * @param user User to add.
     */
    public void addUser(IdpUser user) {
        // Set standard role
        IdpRole role = null;
        try {
          role = em.createQuery("SELECT r from IdpRole r WHERE rolename = :rolename", IdpRole.class)
            .setParameter("rolename", "authenticated").getSingleResult();
        } catch (NoResultException e) {
          role = new IdpRole();
          role.setRoleName("authenticated");
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
     */
    public Boolean activateUser(String actionToken) {
        List<IdpUser> matchingUser = (List<IdpUser>) em
            .createQuery("SELECT u FROM IdpUser u WHERE u.actionToken = :actionToken")
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
     * @param user User the activation mail should be sent to
     * @param serverString Name and port of the server the user was added.
     * @return True if activation email was sent. False otherwise
     */
    public boolean sendActivationMail(IdpUser user, String serverString) {
        // EMail settings
        final String toEmail = user.getEmail();
        final String fromEmail = "planningsuite@ifs.tuwien.ac.at";
        final String smtpServer = "mr.tuwien.ac.at";

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
            message.setSubject("Please Confirm your Planningsuite user account");
            
            StringBuilder builder = new StringBuilder();
            builder.append("Dear " + user.getFirstName() + " " + user.getLastName() + ", \n\n");
            builder.append("Please use the following link to confirm your Planningsuite user account: \n");
            builder.append("http://" + serverString + "/idp/activateUser.jsf?uid=" + user.getActionToken());
            builder.append("\n\n--\n");
            builder.append("Your Planningsuite team");

            message.setText(builder.toString());
            message.saveChanges();

            Transport.send(message);
            log.debug("Activation mail sent successfully to " + toEmail);

            return true;
        } catch (Exception e) {
            log.error("Error at sending activation mail to " + toEmail);
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
