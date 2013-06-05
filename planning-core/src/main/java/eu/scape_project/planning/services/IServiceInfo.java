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
package eu.scape_project.planning.services;

/**
 * Information about a service.
 */
public interface IServiceInfo {

    /**
     * Returns a service identifier.
     * 
     * @return the service identifier
     */
    String getServiceIdentifier();

    /**
     * Returns a short name of the service.
     * 
     * @return a service name
     */
    String getShortname();

    /**
     * Sets the short name of the service.
     * 
     * @param shortName
     *            service name
     */
    void setShortname(String shortName);

    /**
     * Returns a descriptor of the service.
     * 
     * @return a service descriptor
     */
    String getDescriptor();

    /**
     * Sets the descriptor of the service.
     * 
     * @param descriptor
     *            a service descriptor
     */
    void setDescriptor(String descriptor);

    /**
     * Returns a description of the service.
     * 
     * @return a service description
     */
    String getInfo();

    /**
     * Sets the description of the service.
     * 
     * @param info
     *            a service descriptor
     */
    void setInfo(String info);

    /**
     * Returns a url of the service.
     * 
     * @return a service url
     */
    String getUrl();

    /**
     * Sets the url of the service.
     * 
     * @param url
     *            a service url
     */
    void setUrl(String url);

}
