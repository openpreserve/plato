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
package eu.scape_project.planning.model.measurement;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import eu.scape_project.planning.model.ChangeLog;
import eu.scape_project.planning.model.IChangesHandler;
import eu.scape_project.planning.model.ITouchable;
import eu.scape_project.planning.model.scales.Scale;

@Entity
public class Measure implements Comparable<Measure>, Serializable, ITouchable {
    private static final long serialVersionUID = -3942656115528678720L;

    @Id
    @GeneratedValue
    private long id;

    private String uri;

    @OneToOne
    private Scale scale;

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();


    public Scale getScale() {
    	return scale;
    }

    public String getUnit() {
        return scale.getUnit();
    }


    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public ChangeLog getChangeLog() {
        return this.changeLog;
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    @Override
    public void handleChanges(final IChangesHandler h) {
        h.visit(this);
        // call handleChanges of all properties
        scale.handleChanges(h);
    }

    @Override
    public boolean isChanged() {
        return this.changeLog.isAltered();
    }

    @Override
    public void touch() {
        this.changeLog.touch();

    }

    public void setChangeLog(final ChangeLog changeLog) {
        this.changeLog = changeLog;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public int compareTo(final Measure c) {
        return this.uri.toLowerCase().compareTo(c.getUri().toLowerCase());
        // return
        // property.getName().toLowerCase().compareTo(c.getProperty().getName().toLowerCase());
    }

	public void setScale(Scale scale) {
		this.scale = scale;
	}
}
