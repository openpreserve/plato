package eu.scape_project.planning.manager;

import eu.scape_project.planning.exception.PlanningException;

public class StorageException extends PlanningException {

    private static final long serialVersionUID = 3271479259198896581L;

    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable t) {
        super(msg, t);
    }
}
