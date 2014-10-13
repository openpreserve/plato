/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.model;

/**
 * This enum represents the possible states of a planning project.
 * 
 * @author Christoph Becker
 * @author Michael Kraxner
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
    PLAN_VALIDATED(15, "Plan Validated");

    private int value;

    /**
     * Name of the state for convenience, e.g. logging output.
     */
    private String name;

    /**
     * Constructs a new plan state.
     * 
     * @param value
     *            the value of the state
     * @param name
     *            the name of the state
     */
    private PlanState(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * returns the PlanState corresponding to the given value
     * 
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

    // ********** getter/setter **********
    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
