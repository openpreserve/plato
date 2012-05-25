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
package eu.scape_project.planning.utils;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

/**
 * From {@link http://docs.jboss.org/weld/reference/1.1.0.Final/en-US/html/injection.html#d0e1624}
 *  
 * Creates a logger for the class where it should be injected.
 * This way we don not have specify the log category (class) every time - eases refactoring and configuration.
 *   
 * usage: @Inject Logger log;
 * 
 * @author Michael Kraxner 
 * 
 */
public class LoggerFactory {
	@Produces
	public Logger createLogger(InjectionPoint injectionPoint) {
		return org.slf4j.LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
	}
}
