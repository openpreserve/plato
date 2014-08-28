package eu.scape_project.planning.services.notification;


public class WatchNotification {
    
    private String planId;

    private String message;

    private String measureUri;

    private String value;

    private String plannerEmail;
    
    public WatchNotification(String planId, String message, String measureUri, String value, String plannerEmail) {
        super();
        this.planId = planId;
        this.message = message;
        this.measureUri = measureUri;
        this.value = value;
        this.plannerEmail = plannerEmail;
    }
    
    public WatchNotification() {
    }
    
    
    public String getPlanId() {
        return planId;
    }

    public String getMessage() {
        return message;
    }

    public String getMeasureUri() {
        return measureUri;
    }

    public String getValue() {
        return value;
    }

    public String getPlannerEmail() {
        return plannerEmail;
    }
}
