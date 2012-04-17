package eu.planets_project.pp.plato.model.measurement;

import java.io.Serializable;

/**
 * Subject of a measurement is either an action, or the outcome of an action
 * 
 * @author Michael Kraxner
 *
 */
public enum Subject implements Serializable {
	ACTION,
	OUTCOME;
}
