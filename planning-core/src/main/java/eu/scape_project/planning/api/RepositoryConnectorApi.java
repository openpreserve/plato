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
package eu.scape_project.planning.api;

import java.io.InputStream;
import java.util.Map;

import eu.scape_project.planning.utils.RepositoryConnectorException;

/**
 * Should abstract a connection to a repository and allow some basic interaction
 * with it.
 * 
 * @author Petar Petrov - <me@petarpetrov.org>
 * 
 */
public interface RepositoryConnectorApi {

    /**
     * Retrieves a human-readable repository identifier.
     * 
     * @return the identifier.
     */
    String getRepositoryIdentifier();

    /**
     * Downloads a file from the repository with the given identifier.
     * 
     * @param identifier
     *            the repository specific identifier.
     * @return the input stream of the downloaded file.
     */
    InputStream downloadFile(String identifier) throws RepositoryConnectorException;

    /**
     * Downloads a file from the repository with the given configuration. The
     * configuration might require different parameters based on the underlying
     * implementation.
     * 
     * @param config
     *            the configuration
     * @param identifier
     *            the repository specific identifier.
     * @return the input stream of the downloaded file.
     */
    InputStream downloadFile(Map<String, String> config, String identifier) throws RepositoryConnectorException;
}
