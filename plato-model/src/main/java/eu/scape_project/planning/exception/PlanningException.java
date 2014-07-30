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
package eu.scape_project.planning.exception;

/**
 * Thrown to indicate an exception in the planning process.
 */
public class PlanningException extends Exception {
    private static final long serialVersionUID = -5754843705984685011L;

    /**
     * Constructs a new planning exception with no message.
     */
    public PlanningException() {
        super();
    }

    /**
     * Constructs a new planning exception with the provided message.
     * 
     * @param msg
     *            the message of the excpetion
     */
    public PlanningException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new planning exception with the provided message and cause.
     * 
     * @param msg
     *            the message of the exception
     * @param t
     *            the cause of the exception
     */
    public PlanningException(String msg, Throwable t) {
        super(msg, t);
    }
}
