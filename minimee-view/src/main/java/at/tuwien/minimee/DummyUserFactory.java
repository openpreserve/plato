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
package at.tuwien.minimee;

import java.io.Serializable;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import eu.scape_project.planning.model.User;

/**
 * creates one(!) user for the session
 * this will be replaced by a login bean 
 * 
 * @author kraxner
 *
 */
@SessionScoped
public class DummyUserFactory implements Serializable {
	private static final long serialVersionUID = -830549797293803656L;
	
	private User user;
	
	@Produces
	@Named // we will need it in the view 
	public User getUser() {
		if (user == null) {
			user = new User();
			user.setUsername("admin");
		}
		return user;
	}
	
	
}
