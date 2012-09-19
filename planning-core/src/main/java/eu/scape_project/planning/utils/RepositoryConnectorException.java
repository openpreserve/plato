/**
 * 
 */
package eu.scape_project.planning.utils;

import eu.scape_project.planning.api.RepositoryConnectorApi;

/**
 * An exception thrown when some problem occurrs during connection to a
 * repository via the {@link RepositoryConnectorApi}
 * 
 * @author Petar Petrov - <me@petarpetrov.org>
 * 
 */
public class RepositoryConnectorException extends Exception {

    /**
     * A generated serial version identifier.
     */
    private static final long serialVersionUID = -566517220934279492L;

    /**
     * Default constructor.
     */
    public RepositoryConnectorException() {
        super();
    }

    /**
     * String constructor.
     * 
     * @param msg
     *            the message to print.
     */
    public RepositoryConnectorException(String msg) {
        super(msg);
    }

    /**
     * A throwable constructor.
     * 
     * @param cause
     *            the cause of the exception.
     */
    public RepositoryConnectorException(Throwable cause) {
        super(cause);
    }

    /**
     * A constructor with a message and a cause of the exception.
     * 
     * @param msg
     *            the message of the exception.
     * @param cause
     *            the cause of the exception.
     */
    public RepositoryConnectorException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
