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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model.scales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import eu.scape_project.planning.model.values.FreeStringValue;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.validation.ValidationError;


/**
 * This is a free text Scale, brand new, available since 2010.
 * Might be obtained from automatic services, such as hardware description info of the
 * execution environments of migration services, etc.
 * @author Christoph Becker
 * 
 * this scale is of TYPE ORDINAL! but it is NOT RESTRICTED, unlike the OrdinalScale that provides
 * a list of possible values.
 * Basically it is an priori unrestrained ordinal scale. That means the scale TYPE is Ordinal,
 * but the CLASS is not derived from ordinal because it is not restricted (OrdinalScale extends RestrictedScale).
 * The Transformer again is ordinal, naturally 
 * - it maps an ordinal range of distinct values to the target scale. 
 * The LIST of possible values, however, is derived from the range of actually obtained values.
 * 
 */
@Entity
@DiscriminatorValue("S")
public class FreeStringScale extends Scale {

    public FreeStringScale() {
        list = new ArrayList<String>();
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = -3878622271778070882L;

    @Override
    public Value createValue() {
        FreeStringValue v = new FreeStringValue();
        v.setScale(this);
        return v;
    }

    @Override
    public String getDisplayName() {
        return "Free Text";
    }

    @Override
    public ScaleType getType() {
        return ScaleType.ordinal;
    }

    @Override
    public boolean isCorrectlySpecified(String leafName,
            List<ValidationError> errors) {
        return true;
    }

    @Override
    public boolean isEvaluated(Value v) {
        if (v == null || ! (v instanceof FreeStringValue)) {
            return false;
        }
        FreeStringValue sv= (FreeStringValue)v;
        return (sv.getValue() != null && (!"".equals(sv.getValue())));
    }

    @Override
    public boolean isRestricted() {
        return false;
    }
    
    public void setPossibleValues(HashSet<String> values) {
        list.clear();
        list.addAll(values);
        Collections.sort(list);
    }

}
