/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.model;

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
