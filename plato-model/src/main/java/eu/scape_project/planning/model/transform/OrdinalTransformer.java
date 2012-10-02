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
package eu.scape_project.planning.model.transform;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import eu.scape_project.planning.model.TargetValueObject;
import eu.scape_project.planning.model.values.INumericValue;
import eu.scape_project.planning.model.values.IOrdinalValue;
import eu.scape_project.planning.model.values.OrdinalValue;
import eu.scape_project.planning.model.values.TargetValue;
import eu.scape_project.planning.validation.ValidationError;

/**
 * Transforms {@link OrdinalValue ordinal values} by defining a mapping from their values
 * to a numeric target value.
 * @author Hannes Kulovits
 */
@Entity
@DiscriminatorValue("O")
public class OrdinalTransformer extends Transformer {

    private static final long serialVersionUID = 8652623297851724108L;

    /**
     * The key member of Map must be renamed to key_name otherwise derby complains.
     * 'key' seems to be a keyword. We use a LinkedHashMap, to preserve the order of
     * the entries during ex-/import.
     */
//    @IndexColumn(name="key_name")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private Map<String, TargetValueObject> mapping = new LinkedHashMap<String, TargetValueObject>();

    public Map<String, TargetValueObject> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, TargetValueObject> mapping) {
        this.mapping = mapping;
    }

    /**
    * This operation is not supported. Always throws an {@link UnsupportedOperationException}
     * @throws UnsupportedOperationException because this is the ordinal transformer.
     */
    public TargetValue transform(INumericValue v) {
        throw new UnsupportedOperationException(
                "Cannot transform numeric values. only ordinal ones!");
    }

    /**
     * Returns the transformed value, i.e. the numeric target value corresponding to the 
     * provided ordinal value according to the specified {@link #mapping}. 
     * @param v the ordinal value to be transformed
     * @return {@link TargetValue} transformed value.
     */
    public TargetValue transform(IOrdinalValue v) {
        TargetValue t = new TargetValue();
        TargetValueObject o = mapping.get(v.getValue());
        t.setValue(o.getValue());
        return t;
    }

    /**
     * Checks if all mappings are set and correctly specified, i.e. between 0.0 and 5.0.
     * This is now a double-check as we already have a {@link TargetValueValidator} that takes care of that.
     * We still leave the checks in because it can't hurt.
     */
    public boolean isTransformable(List<ValidationError> errors) {
        boolean result = true;
        for (String key: mapping.keySet()) {
            TargetValueObject value = mapping.get(key);
            //  Target values are initialised and set to 0.0, so we can assume it is not null:
            if ((value.getValue() < 0.0) ||
                (value.getValue() > 5.0)) {
                result = false;
                errors.add(new ValidationError("Transformation of '" + key + "' to " + value.getValue() + 
                        ": For ordinal values only target values between 0.0 and 5.0 are allowed.", this));
            }
        }
        return result;
    }

    @Override
    public Transformer clone() {
        OrdinalTransformer t = new OrdinalTransformer();
        t.setId(0);
        for (String s : mapping.keySet()) {
            TargetValueObject tvo = mapping.get(s);
            t.getMapping().put(s, tvo.clone());
        }
        return t;
    }
}
