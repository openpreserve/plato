/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.model;


/**
 * This enum represents the possible states of a planning project.
 *
 * FIXME: we should consider to name the states after the current task, not what is already finished
 *        more like the names of the FTE steps  
 *  
 * @author Christoph Becker
 * @author Michael Kraxner
 *
 */


public enum PlanState {
	CREATED(0, "Created"),
	INITIALISED(1, "Initialised"),
	BASIS_DEFINED(2, "Basis Defined"),
	RECORDS_CHOSEN(3, "Records Chosen"),
	TREE_DEFINED(4, "Tree Defined"),
	ALTERNATIVES_DEFINED(5, "Alternatives Defined"),
	GO_CHOSEN(6, "Go Decision Taken"),
	EXPERIMENT_DEFINED(7, "Experiments Defined"),
	EXPERIMENT_PERFORMED(8, "Experiments Performed"),
	RESULTS_CAPTURED(9, "Results Captured"),
	TRANSFORMATION_DEFINED(10, "Transformations Defined"),
	WEIGHTS_SET(11, "Weights Set"),
	ANALYSED(12, "Analyzed"),
	EXECUTEABLE_PLAN_CREATED(13, "Executable Plan Created"),
	PLAN_DEFINED(14, "Plan Defined"),
	PLAN_VALIDATED(15, "Plan Validated"),
	
	FTE_INITIALISED(16, "Define Requirements (Fast-track evaluation)", true),
	FTE_REQUIREMENTS_DEFINED(17, "Evaluate Alternatives (Fast-track evaluation)", true),
	FTE_ALTERNATIVES_EVALUATED(18, "Analyse Results (Fast-track evaluation)", true),
	FTE_RESULTS_ANALYSED(19, "Completed fast-track evaluation", true)
	;
	
	private int value;
    /**
     * for convenience, e.g. logging output
     */
	private String name;
	
	/**
	 * denotes states of fast track evaluation 
	 */
	private boolean fasttrack;
	
	private PlanState(int value, String name){
		this.value = value;
		this.name = name;
		fasttrack = false;
	}
	
	private PlanState(int value, String name, boolean fasttrack){
		this.value = value;
		this.name = name;
		this.fasttrack = fasttrack;
	}
	
	/**
	 * returns the PlanState corresponding to the given value
	 * @param value
	 * @return
	 */
	public static PlanState valueOf(int value) {
		for (PlanState state : PlanState.values()) {
			if (state.value == value) {
				return state;
			}
		}
		throw new IllegalArgumentException("Unkown value for PlanState: " + value);
	}

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
    
    public boolean isFasttrack(){
    	return fasttrack;
    }
}
