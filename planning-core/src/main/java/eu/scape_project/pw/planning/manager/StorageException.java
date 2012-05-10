package eu.scape_project.pw.planning.manager;

import eu.planets_project.pp.plato.exception.PlanningException;

public class StorageException extends PlanningException {

    private static final long serialVersionUID = 3271479259198896581L;

    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg, Throwable t) {
        super(msg, t);
    }
}
