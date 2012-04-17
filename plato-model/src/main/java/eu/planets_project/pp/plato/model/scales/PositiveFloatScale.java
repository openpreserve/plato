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

import org.slf4j.LoggerFactory;

import eu.planets_project.pp.plato.model.values.PositiveFloatValue;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.validation.ValidationError;

/**
 * Float scale to be used in the leaves of the objective tree
 *
 * @author Christoph Becker
 *
 */
@Entity
@DiscriminatorValue("N")
// We don't use the annotation NotNullField anymore, as the error message doesn't allow
// to specify the name of the leaf. So the error message is not very accurate. As we already
// have all methods available in the base class Scale we use them to check for restriction/unit.
// @NotNullField(fieldname="unit", message="Please enter a unit for the scale of type 'Positive Number'")
public class PositiveFloatScale extends RestrictedScale {

    private static final long serialVersionUID = -4467039282244923690L;

     public  String getDisplayName() {
        return "Positive Number";
    }
    
    @Override
    public ScaleType getType() {
        return ScaleType.restricted;
    }
    
    public PositiveFloatValue createValue() {
        PositiveFloatValue v = new PositiveFloatValue();
        v.setScale(this);
        return v;
    }

    /**
     * The maximum value for DOUBLE in Derby is 1.79769E+308.
     * Node: 1.79769E+308 is smaller than Double.MAX_VALUE.
     * (see http://db.apache.org/derby/manuals/reference/sqlj134.html#SPTSII-SQLJ-DOUBLEPRECISION)
     */
    @Transient
    private double upperBound = Scale.MAX_VALUE;
 
    @Override
    public String getRestriction() {
        if (this.upperBound == Scale.MAX_VALUE) {
            return "";
        } else {
            return Double.toString(this.upperBound);
        }
    }

    @Override
    public String getReadableRestriction() {
        if (this.upperBound == Scale.MAX_VALUE) {
            return "";
        } else {
            return "up to " + this.upperBound;
        }
    }

    @Override
    public void setRestriction(String restriction) {
        if (restriction != null && !"".equals(restriction)) {
            LoggerFactory.getLogger(this.getClass()).debug(
                    "setting restriction: " + restriction);
            try {
                setUpperBound(Double.parseDouble(restriction));

            } catch (NumberFormatException e) {
                LoggerFactory.getLogger(this.getClass()).warn(
                        "ignoring invalid restriction "
                                + "setting in PositiveFloatScale: "
                                + restriction);
            }
        } else {
            setUpperBound(Scale.MAX_VALUE);
        }
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upper) {
        this.upperBound = upper;
    }

    @Override
    public boolean isCorrectlySpecified(String leafName,
            List<ValidationError> errors) {

        if (false == super.isCorrectlySpecified(leafName, errors)) {
            return false;
        }

        // we additionally check for the unit
        if (getUnit() == null || "".equals(getUnit())) {
            errors.add(new ValidationError("Please enter a unit for the scale of type 'Positive Number' at leaf '" + leafName + "'", this));
            return false;
        }

        return true;
    }


    @Override
    protected boolean restrictionIsValid(String leafName, List<ValidationError> errors) {
        if (this.upperBound <= 0.0) {
            errors.add(new ValidationError("The upper bound specified for leaf \"" + leafName + "\" is not greater than zero!", this));
            return false;
        }
        return true;
    }

    @Override
    public boolean isEvaluated(Value value) {
        boolean evaluated = false;
        if ((value != null) && (value instanceof PositiveFloatValue)) {
            PositiveFloatValue v = (PositiveFloatValue)value;

            evaluated = value.isChanged() &&
            (v.getValue() <= getUpperBound() && v.getValue() >= 0);
        }
        return evaluated;
    }

}
