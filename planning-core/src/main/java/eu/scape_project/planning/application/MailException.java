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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.application;

/**
 * Exception for for messaging.
 */
public class MailException extends Exception {
    private static final long serialVersionUID = -5754843705984685011L;

    /**
     * Creates a new object.
     */
    public MailException() {
        super();
    }

    /**
     * Creates a new object with the provided message.
     * 
     * @param msg
     *            the message of the exception
     */
    public MailException(String msg) {
        super(msg);
    }

    /**
     * Creates a new object with the provided message and throwable.
     * 
     * @param msg
     *            the message of the exception
     * @param t
     *            the cause
     */
    public MailException(String msg, Throwable t) {
        super(msg, t);
    }
}
