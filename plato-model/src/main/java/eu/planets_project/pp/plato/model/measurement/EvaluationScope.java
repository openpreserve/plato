package eu.planets_project.pp.plato.model.measurement;

import java.io.Serializable;

/**
 * EvaluationScope of a measurement is either an action, or the outcome of an action
 * 
 * @author Michael Kraxner
 *
 */
public enum EvaluationScope implements Serializable {
	ALTERNATIVE,
	OUTCOME;
}
