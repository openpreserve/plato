package eu.scape_project.planning.services.notification;


public class WatchNotification {
	private String planId;
	
	private String message;
	
	private String measureUri;
	
	private String value;

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMeasureUri() {
		return measureUri;
	}

	public void setMeasureUri(String measureUri) {
		this.measureUri = measureUri;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	

}
