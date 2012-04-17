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
package eu.planets_project.pp.plato.xml.freemind;

import org.slf4j.LoggerFactory;

import eu.planets_project.pp.plato.model.scales.BooleanScale;
import eu.planets_project.pp.plato.model.scales.IntRangeScale;
import eu.planets_project.pp.plato.model.scales.OrdinalScale;
import eu.planets_project.pp.plato.model.scales.PositiveFloatScale;
import eu.planets_project.pp.plato.model.scales.RestrictedScale;
import eu.planets_project.pp.plato.model.scales.Scale;


public class PolicyNode extends Node {

    public Scale createScale() {
        try {
            if ("Y".equals(getTEXT())) {
                return new BooleanScale();
            }

            if ("N".equals(getTEXT())) {
                return new BooleanScale();
            }

            if ("?".equals(getTEXT())) {
                return null;
            }

            if (getTEXT().indexOf(Scale.SEPARATOR) != -1 ) {
                RestrictedScale v = null;
                v = new IntRangeScale();
                if (! ((IntRangeScale)v).validateAndSetRestriction(getTEXT())) {
                    v= new OrdinalScale();
                    v.setRestriction(getTEXT());
                }
                return v;
            }
        } catch (Exception e) {
        	LoggerFactory.getLogger(PolicyNode.class).warn("invalid scale format, ignoring: "+getTEXT(),e);
        }

        //default behaviour: float scale and the TEXT as unit
        PositiveFloatScale v = new PositiveFloatScale();
        v.setUnit(getTEXT());
        return v;
    }
}
