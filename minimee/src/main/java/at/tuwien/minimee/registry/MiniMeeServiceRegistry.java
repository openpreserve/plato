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
package at.tuwien.minimee.registry;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import at.tuwien.minimee.MiniMeeException;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.services.action.IActionInfo;
import eu.scape_project.planning.services.action.IPreservationActionRegistry;
import eu.scape_project.planning.services.action.ActionInfo;

public class MiniMeeServiceRegistry implements IPreservationActionRegistry {
    private MiniMeeRegistry registry = new MiniMeeRegistry();

    public void connect(String URL) throws ServiceException, MalformedURLException {

        try {
            registry.reload();
        } catch (MiniMeeException e) {
            throw new ServiceException("Could  not connect to MiniMEE.", e);
        }
    }

    public String getToolIdentifier(String url) {
        return registry.getToolIdentifier(url);
    }

    public String getToolParameters(String url) {
        return registry.getToolParameters(url);
    }

    public List<IActionInfo> getAvailableActions(FormatInfo sourceFormat) throws PlatoException {
        List<PreservationActionService> services = registry.findServices(sourceFormat, null);
        ArrayList<IActionInfo> result = new ArrayList<IActionInfo>();
        for (PreservationActionService service : services) {
            ActionInfo def = new ActionInfo();
            def.setShortname(service.getName());
            if (service.getTargetFormat() != null) {
                def.setTargetFormat(service.getTargetFormat().getDefaultExtension());
                def.setTargetFormatInfo(service.getTargetFormat());
            }
            def.setInfo(service.getDescription());
            def.setUrl(service.getUrl());
            def.setDescriptor(service.getDescriptor());
            if (service.isMigration())
                def.setActionIdentifier("MiniMEE-migration");
            else {
                def.setEmulated(true);
                def.setActionIdentifier("MiniMEE-emulation");
                // TODO: refine setting type according to sourceFormat
                if ("avi".equals(sourceFormat.getDefaultExtension())
                    || "mpg".equals(sourceFormat.getDefaultExtension())
                    || "mpeg".equals(sourceFormat.getDefaultExtension())) {
                    def.setParamByName("filetype", "1");
                } else if ("jpg".equals(sourceFormat.getDefaultExtension())
                    || "gif".equals(sourceFormat.getDefaultExtension())
                    || "tif".equals(sourceFormat.getDefaultExtension())) {
                    def.setParamByName("filetype", "2");
                } else if ("pdf".equals(sourceFormat.getDefaultExtension())
                    || "sam".equals(sourceFormat.getDefaultExtension())) {
                    def.setParamByName("filetype", "3");
                }
            }
            result.add(def);
        }

        return result;
    }

    public String getLastInfo() {
        return "";
    }

}
