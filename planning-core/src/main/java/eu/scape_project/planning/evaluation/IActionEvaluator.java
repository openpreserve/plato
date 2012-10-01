/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
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
package eu.scape_project.planning.evaluation;

import java.util.HashMap;
import java.util.List;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.values.Value;

/**
 * This is the interface for all evaluation plugins that are providing
 * measurements which do not require looking at a specific object or the
 * experiment produced by applying an action to a specific object, but instead
 * look at the actions or the format that results from applying certain actions.
 * Correspondingly, these will usually belong to the categories
 * <ul>
 * <li>outcome format and</li>
 * <li>action static.</li>
 * </ul>
 * The interface defines constant URIs for core properties that are implemented
 * and integrated in Plato.
 * 
 * @author cb
 */
public interface IActionEvaluator extends IEvaluator {

    /**
     * evaluates an action with regard to the given critera defined in leaves
     * returns a list of values, one per leaf
     * 
     * It is not nice that leaves are passed to the evaluator, and a map of
     * leaves to values is returned
     * 
     * This information is really needed: - how this criterion is measured
     * (Criterion) - what is type of the evaluated value (Scale)
     * 
     * @param alternative
     * @param measureUris
     * @param listener
     * @return
     * @throws EvaluatorException
     */
    public HashMap<String, Value> evaluate(Alternative alternative, List<String> measureUris, IStatusListener listener)
        throws EvaluatorException;

}
