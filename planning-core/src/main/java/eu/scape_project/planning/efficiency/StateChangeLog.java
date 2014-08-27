package eu.scape_project.planning.efficiency;

import java.util.Date;

public class StateChangeLog {
	
	private long planId;
	private int stageNr;
	private double hoursSinceStart;
	private String user;
	private Date timestamp;
	
	
	public StateChangeLog() {
	}
	
	public StateChangeLog(long planId, int stageNr, long day0, long timestamp, String user) {
		this.planId = planId;
		this.stageNr = stageNr;
		this.hoursSinceStart = (timestamp - day0) / 3600000;
		this.timestamp = new Date(timestamp);
		this.user = user;
	}
	
	public Date getTimestamp() {
	    return timestamp;
	}
	
	public long getPlanId() {
		return planId;
	}
	public void setPlanId(long planId) {
		this.planId = planId;
	}
	public int getStageNr() {
		return stageNr;
	}
	public void setStageNr(int stageNr) {
		this.stageNr = stageNr;
	}
	public double getHoursSinceStart() {
		return hoursSinceStart;
	}
	public void setHoursSinceStart(double hoursSinceStart) {
		this.hoursSinceStart = hoursSinceStart;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

}
