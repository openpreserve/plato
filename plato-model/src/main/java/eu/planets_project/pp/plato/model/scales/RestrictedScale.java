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

package eu.planets_project.pp.plato.model.scales;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import eu.planets_project.pp.plato.validation.ValidationError;


@Entity
@DiscriminatorValue("E")
public abstract class RestrictedScale extends Scale {
    /**
     * 
     */
    private static final long serialVersionUID = 1729926168748385807L;
    /**
     * This field is only used for the annotation, to prevent hibernate from
     * trying to persist the field. The point is we need the getter and setter.
     * Handling is done differently depending on the subclass, which have to take care
     * of persisting the restriction settings themselves
     */
    @Transient
    private String restriction;

    public abstract void setRestriction(String restriction);
    
    public abstract String getRestriction();

    /**
     * This returns a String for displaying the restriction settings in a readable form
     */
    public abstract String getReadableRestriction();


    @Override
    public boolean isRestricted() {
        return true;
    }

    /**
     * There are some scales with predefined restrictions - their restrictions must not be changed.
     * This field indicates that.
     */
    @Transient
    protected boolean immutableRestriction = false;

    public boolean isImmutableRestriction() {
        return immutableRestriction;
    }

    @Override
    public ScaleType getType() {
        return ScaleType.restricted;
    }

    @Override
    public boolean isCorrectlySpecified(String leafName,
            List<ValidationError> errors) {
        return this.restrictionIsValid(leafName, errors);
    }

    /**
     * Returns true if the restriction is correctly specified.
     */
    protected abstract boolean restrictionIsValid(String leafName,
            List<ValidationError> errors);
 
}
