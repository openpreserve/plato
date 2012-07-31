package eu.scape_project.planning.utils;

/**
 * A simple exception for parser errors.
 * 
 * @author Petar Petrov - <me@petarpetrov.org>
 * 
 */
public class ParserException extends Exception {

	/**
	 * Auto generated serial version uid.
	 */
	private static final long serialVersionUID = -1919331201703953700L;

	/**
	 * Creates an exception.
	 */
	public ParserException() {
		super();
	}

	/**
	 * Creates an exception with a message.
	 * 
	 * @param msg
	 *            the message.
	 */
	public ParserException(String msg) {
		super(msg);
	}

	/**
	 * Creates an exception based on another one
	 * 
	 * @param cause
	 *            the cause of this exception.
	 */
	public ParserException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an exception with a message and a cause.
	 * 
	 * @param msg
	 *            the message.
	 * @param cause
	 *            the cause.
	 */
	public ParserException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
