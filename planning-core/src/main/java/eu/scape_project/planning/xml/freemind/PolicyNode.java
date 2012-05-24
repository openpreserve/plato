/*******************************************************************************
 * Copyright 2012 Vienna University of Technology
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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.xml.freemind;

import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.IntRangeScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.RestrictedScale;
import eu.scape_project.planning.model.scales.Scale;


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
