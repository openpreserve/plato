package eu.scape_project.planning.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Class for user centered notifications. 
 *  
 * @author Michael Kraxner
 *
 */
@Entity
public class Notification implements Serializable {
    private static final long serialVersionUID = -1680368399851995147L;
    
    @Id @GeneratedValue
    private long id;

    /**
     * Identifies notifications which are sent to multiple users.
     */
    private String uuid;

    /**
     * The message sent to the user.
     */
    @Lob
    private String message;
    
    /**
     * Timestamp of message creation. 
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    
    /**
     * Specifies the source of the information. 
     */
    private String source;
    
    // regarding plan
    // regarding policies?
    // regarding content set?
    
    @ManyToOne
    private User recipient;
    
    public Notification() {
    }
    
    public Notification(final String uuid, final Date timestamp, final String source, final String message, final User recipient) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.source = source;
        this.message = message;
        this.recipient = recipient;
    }
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String sender) {
        this.source = sender;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

}
