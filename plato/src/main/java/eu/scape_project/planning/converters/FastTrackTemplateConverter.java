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
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import eu.scape_project.planning.plato.wf.beans.FastTrackTemplate;


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
