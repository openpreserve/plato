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
package eu.scape_project.planning.converters;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter responsible for transforming between input number as double and its
 * string representation.
 */
@FacesConverter(value = "StringTrimConverter")
public class StringTrimConverter implements Converter, Serializable {

    private static final long serialVersionUID = 8557945861418423829L;

    /**
     * Converts the provided string value by trimming it.
     * 
     * @param context
     *            FacesContext for the request being processed
     * @param component
     *            UIComponent with which this model object value is associated
     * @param value
     *            Input string to be converted.
     * @return Trimmed input string.
     * 
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return value != null ? value.trim() : null;
    }

    /**
     * Method responsible for converting a double value to the representative
     * display string.
     * 
     * @param context
     *            FacesContext for the request being processed
     * @param component
     *            UIComponent with which this model object value is associated
     * @param value
     *            Integer or Double representation of the entered value.
     * 
     * @return String representation of the value for user display.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (String) value;
    }
}
