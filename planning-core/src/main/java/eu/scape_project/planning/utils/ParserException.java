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
package eu.scape_project.planning.utils;

/**
 * A simple exception for parser errors.
 * 
 * @author Petar Petrov - <me@petarpetrov.org>
 * 
 */
public class ParserException extends Exception {

	/**
	 * Auto generated serial version uid.
	 */
	private static final long serialVersionUID = -1919331201703953700L;

	/**
	 * Creates an exception.
	 */
	public ParserException() {
		super();
	}

	/**
	 * Creates an exception with a message.
	 * 
	 * @param msg
	 *            the message.
	 */
	public ParserException(String msg) {
		super(msg);
	}

	/**
	 * Creates an exception based on another one
	 * 
	 * @param cause
	 *            the cause of this exception.
	 */
	public ParserException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an exception with a message and a cause.
	 * 
	 * @param msg
	 *            the message.
	 * @param cause
	 *            the cause.
	 */
	public ParserException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
