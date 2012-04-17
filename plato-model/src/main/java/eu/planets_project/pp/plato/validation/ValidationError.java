package eu.planets_project.pp.plato.validation;

import java.io.Serializable;

/**
 * Represents a validation error.
 * 
 * TODO For multi-lingual error messages we have to add a message identifier rather than the message itself. 
 *  
 * @author Michael Kraxner
 *
 */
public class ValidationError implements Serializable {
	private static final long serialVersionUID = 3615070773368229163L;
	
	private String reason;
	
	private Object invalidObject;
	
	public ValidationError(String reason) {
		this.reason = reason;
	}
	public ValidationError(String reason, Object invalidObject) {
		this.reason = reason;
		this.invalidObject = invalidObject;
	}

	public String getMessage() {
		return reason;
	}

	public void setMessage(String message) {
		this.reason = message;
	}
	public Object getInvalidObject() {
		return invalidObject;
	}

}
