package eu.scape_project.pw.planning.converters;

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import eu.scape_project.pw.planning.plato.wf.beans.FastTrackTemplate;


public class FastTrackTemplateConverter  implements Converter, Serializable {
    
    private static final long serialVersionUID = 5134289952432499559L;
    
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        
        List<FastTrackTemplate> list = null;
        for (int i =0 ; i < component.getChildCount(); i++) {
            if (component.getChildren().get(i) instanceof UIParameter) {
                UIParameter param = (UIParameter)component.getChildren().get(i);
                if ("templateList".equals(param.getName())) {
                    list = (List<FastTrackTemplate>)param.getValue();
                }
            }
        }
        
        for (FastTrackTemplate ftt : list) {
            if (value.equals(ftt.getDisplayString())) {
                return ftt;
            }
        }
        
        return null; 
    }
    
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        
        if (value instanceof FastTrackTemplate) {
            return ((FastTrackTemplate)value).getDisplayString();
        }
        
        return "";
    }
}
