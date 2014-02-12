package eu.scape_project.planning.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author Michael Kraxner
 *
 */
@Entity
public class Notification implements Serializable {
    private static final long serialVersionUID = -1680368399851995147L;
    
    @Id @GeneratedValue
    private long id;
    
    private String message;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    
    private String source;
    
    // regarding plan
    // regarding policies?
    // regarding content set?
    
    @ManyToOne
    private User recipient;
    
    public Notification() {
    }
    
    public Notification(final Date timestamp, final String source, final String message, final User recipient) {
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

}
