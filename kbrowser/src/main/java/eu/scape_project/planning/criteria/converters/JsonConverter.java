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
package eu.scape_project.planning.criteria.converters;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@FacesConverter(value = "eu.scape_project.planning.criteria.converters.JsonConverter")
public class JsonConverter implements Converter {
	@Override
	public Object getAsObject(final FacesContext context,
			final UIComponent component, final String value) {

		final ValueExpression valueExpression = component
				.getValueExpression("value");

		return new JSONDeserializer().use(null,
				valueExpression.getType(context.getELContext())).deserialize(
				value);

	}

	@Override
	public String getAsString(final FacesContext context,
			final UIComponent component, final Object value) {
		return new JSONSerializer().exclude("*.class").serialize(value);
	}
}
