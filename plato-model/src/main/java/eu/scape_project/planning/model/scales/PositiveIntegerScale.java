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

package eu.scape_project.planning.model.scales;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.values.PositiveIntegerValue;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.validation.ValidationError;

/**
 * An integer value with lower and upper bounds to be used in the leaves of the objective tree
 * @author Christoph Becker
 *
 */
@Entity
@DiscriminatorValue("P")
// We don't use the annotation NotNullField anymore, as the error message doesn't allow
// to specify the name of the leaf. So the error message is not very accurate. As we already
// have all methods available in the base class Scale we use them to check for restriction/unit.
//@NotNullField(fieldname="unit", message="Please enter a unit for the scale of type 'Positive Integer'")
public class PositiveIntegerScale extends RestrictedScale {

    private static final long serialVersionUID = 7455117412684178182L;

    public PositiveIntegerValue createValue() {
        PositiveIntegerValue v = new PositiveIntegerValue();
        v.setScale(this);
        return v;
    }

    public  String getDisplayName() {
        return "Positive Integer";
    }
    private int upperBound = Integer.MAX_VALUE;

    @Override
    public boolean isInteger() {
        return true;
    }
    
    @Override
    public String getRestriction() {
        if (upperBound == Integer.MAX_VALUE)
            return "";
        else
            return Integer.toString(upperBound);
    }

    @Override
    public String getReadableRestriction() {
        if (this.upperBound == Integer.MAX_VALUE) {
            return "";
        } else {
            return "up to " + this.upperBound;
        }
    }

    @Override
    public void setRestriction(String restriction) {
        if (restriction != null && !"".equals(restriction)) {
            LoggerFactory.getLogger(this.getClass()).debug("setting restriction: "+restriction);
            try {
                setUpperBound(Integer.parseInt(restriction));

            } catch (NumberFormatException e) {
                LoggerFactory.getLogger(this.getClass()).warn("ignoring invalid restriction " +
                        "setting in PositiveFloatValue: "+restriction);
            }
        } else {
            setUpperBound(Integer.MAX_VALUE);
        }
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upper) {
        this.upperBound = upper;
    }

    @Override
    protected boolean restrictionIsValid(String leafName, List<ValidationError> errors) {
        if (this.upperBound <= 0) {
            errors.add(new ValidationError("The upper bound specified for leaf \"" + leafName + "\" is not greater than zero!", this));
            return false;
        }
        return true;
    }

    @Override
    public boolean isCorrectlySpecified(String leafName,
            List<ValidationError> errors) {

        if (false == super.isCorrectlySpecified(leafName, errors)) {
            return false;
        }

        // we additionally check for the unit
        if (getUnit() == null || "".equals(getUnit())) {
            errors.add(new ValidationError("Please enter a unit for the scale of type 'Positive Integer' at leaf '" + leafName + "'", this));
            return false;
        }

        return true;
    }


    @Override
    public boolean isEvaluated(Value value) {
        boolean evaluated = false;
        if ((value != null) && (value instanceof PositiveIntegerValue)) {
            PositiveIntegerValue v = (PositiveIntegerValue)value;

            evaluated = v.isChanged() &&
            (v.getValue() <= getUpperBound() && v.getValue() >= 0 );
        }
        return evaluated;
    }
}
