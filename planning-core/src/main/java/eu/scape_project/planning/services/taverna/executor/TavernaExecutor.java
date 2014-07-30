/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.services.taverna.executor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Executor for taverna workflows.
 */
public interface TavernaExecutor {

    /**
     * Executes the workflow.
     * 
     * @throws IOException
     *             if data could not be read
     * @throws TavernaExecutorException
     *             if an error occurs during execution
     */
    void execute() throws IOException, TavernaExecutorException;

    /**
     * Returns the output data of the previous workflow run.
     * 
     * @return a map of port names with the output data
     */
    Map<String, ?> getOutputData();

    /**
     * Returns the output files of the previous workflow run.
     * 
     * @return a map of port names with the output files
     */
    HashMap<String, ?> getOutputFiles();

}
