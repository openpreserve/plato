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
package eu.scape_project.planning.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Named;

/**
 * Class holding different kinds of messages:
 * <ul>
 *   <li>Error messages. Unexpected errors that occurred during a session. Administrator can
 *   look at them on admin utils site.</li>
 *
 *   <li>News messages. Messages entered by the Administrator intended to the users.</li>
 * <ul>
 * @author Michael, Kraxner, Hannes Kulovits
 *
 */

@Singleton
@Named("allMessages")
@Startup
public class Messages implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * List of all errors since server start.
	 */
	private List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
	
	/**
	 * List of news since server start.
	 */
	private List<NewsMessage> news = new ArrayList<NewsMessage>();

	public void clearErrors(){
		errors.clear();
	}
	
	public void clearNews(){
		news.clear();
	}
	
	/**
	 * Inserts an error message at the beginning of the list.
	 * 
	 * @param error
	 */
    public void addErrorMessage(ErrorMessage error) {
        errors.add(0, error);
    }

    /**
     * Inserts a news message at the beginning of the list.
     *  
     * @param news
     */
    public void addNewsMessage(NewsMessage news) {
        this.news.add(0, news);
    }

    /**
     * NOTE: do not try to alter the returned list  
     * 
     * @return
     */
    public List<ErrorMessage> getErrors() {
        return errors;
    }

    /**
     * NOTE: do not try to alter the returned list
     * @return
     */
    public List<NewsMessage> getNews() {
        return news;
    }	

}
