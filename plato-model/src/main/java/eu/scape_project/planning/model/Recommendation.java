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
package eu.scape_project.planning.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

/**
 * An object of this class stores the actual recommendation of the preservation plan and
 * is part of the preservation plan. At present this is a certain Alternative
 * {@link #alternative}. However, this will change in future versions.
 *
 * @author Hannes Kulovits
 *
 */
@Entity
public class Recommendation implements Serializable, ITouchable {

    private static final long serialVersionUID = 4855165668724916066L;

    @Id @GeneratedValue
    private int id;

    /**
     * A recommendation is an alternative you go for.
     */
    @OneToOne(cascade=CascadeType.ALL)
    private Alternative alternative;

    /**
     * Reason for this recommendation.
     *
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String reasoning;

    /**
     * Foreseen effects of applying this preservation strategy to the files.
     * This goes back to the "effectiveness"(?) criteria in section B.3 of TRAC 
     */
    @Lob
    private String effects;
    
    
    @OneToOne(cascade=CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();


    public Alternative getAlternative() {
        return alternative;
    }

    public void setAlternative(Alternative alternative) {
        this.alternative = alternative;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
    
    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged(){
        return changeLog.isAltered();
    }
    
    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
    }

    public String getEffects() {
        return effects;
    }

    public void setEffects(String effects) {
        this.effects = effects;
    }
}
