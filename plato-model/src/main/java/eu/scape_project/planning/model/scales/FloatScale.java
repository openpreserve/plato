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

import eu.scape_project.planning.model.values.FloatValue;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.validation.ValidationError;

/**
 * An float value to be used in the leaves of the objective tree
 * @author Michael Kraxner
 *
 */
@Entity
@DiscriminatorValue("F")
// We don't use the annotation NotNullField anymore, as the error message doesn't allow
// to specify the name of the leaf. So the error message is not very accurate. As we already
// have all methods available in the base class Scale we use them to check for restriction/unit.
// @NotNullField(fieldname="unit", message="Please enter a unit for the scale of type 'Number'")
public class FloatScale extends Scale {

    private static final long serialVersionUID = -1933589584196479881L;

    /**
     * @see eu.scape_project.planning.model.scales.Scale#createValue()
     */
    @Override
    public Value createValue() {
        Value v = new FloatValue();
        v.setScale(this);
        return v;
    }

    /**
     * @see eu.scape_project.planning.model.scales.Scale#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return "Number";
    }

    /**
     * @see eu.scape_project.planning.model.scales.Scale#getType()
     */
    @Override
    public ScaleType getType() {
        return ScaleType.value;
    }

    /**
     * @see eu.scape_project.planning.model.scales.Scale#isCorrectlySpecified(java.lang.String, List)
     */
    @Override
    public boolean isCorrectlySpecified(String leafName,
            List<ValidationError> errors) {

        if (getUnit() == null || "".equals(getUnit())) {
            errors.add(new ValidationError("Please enter a unit for the scale of type 'Number' at leaf '" + leafName + "'", this));
            return false;
        }

        return true;
    }

    /**
     * @see eu.scape_project.planning.model.scales.Scale#isEvaluated(eu.scape_project.planning.model.values.Value)
     */
    @Override
    public boolean isEvaluated(Value value) {
        boolean evaluated = false;
        if ((value != null) && (value instanceof FloatValue)) {
            FloatValue v = (FloatValue)value;

            evaluated = v.isChanged();
        }
        return evaluated;
    }

    /**
     * An {@link FloatScale} is not restricted.
     * @see eu.scape_project.planning.model.scales.Scale#isRestricted()
     */
    @Override
    public boolean isRestricted() {
        return false;
    }

}
