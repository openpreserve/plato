package eu.scape_project.pw.planning.criteria.converters;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@FacesConverter(value = "eu.scape_project.pw.planning.criteria.converters.JsonConverter")
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