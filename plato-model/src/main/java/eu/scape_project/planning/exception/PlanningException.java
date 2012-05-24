package eu.scape_project.planning.exception;

public class PlanningException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5754843705984685011L;
	
	public PlanningException() {
		
	}

	public PlanningException(String msg) {
        super(msg);
    }
	
	public PlanningException(String msg, Throwable t) {
	    super(msg, t);
	}
}
