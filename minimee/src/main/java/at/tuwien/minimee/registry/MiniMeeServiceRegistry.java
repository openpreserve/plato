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
package at.tuwien.minimee.registry;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import at.tuwien.minimee.MiniMeeException;
import eu.planets_project.pp.plato.model.FormatInfo;
import eu.planets_project.pp.plato.model.PlatoException;
import eu.planets_project.pp.plato.model.PreservationActionDefinition;
import eu.planets_project.pp.plato.model.interfaces.actions.IPreservationActionRegistry;

public class MiniMeeServiceRegistry implements IPreservationActionRegistry {
    private MiniMeeRegistry registry = new MiniMeeRegistry(); 

    public void connect(String URL) throws ServiceException,
            MalformedURLException {
        
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
    

    public List<PreservationActionDefinition> getAvailableActions(
            FormatInfo sourceFormat) throws PlatoException {
        List<PreservationActionService> services = registry.findServices(sourceFormat, null);
        ArrayList<PreservationActionDefinition> result = new ArrayList<PreservationActionDefinition>();
        PreservationActionDefinition def;
        for (PreservationActionService service : services) {
            def = new PreservationActionDefinition();
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
               def.setActionIdentifier("MiniMEE-emulation" );
               // TODO: refine setting type according to sourceFormat
               if ("avi".equals(sourceFormat.getDefaultExtension()) ||
                   "mpg".equals(sourceFormat.getDefaultExtension()) ||
                   "mpeg".equals(sourceFormat.getDefaultExtension())) {
                   def.setParamByName("filetype", "1");
               }
               else if ("jpg".equals(sourceFormat.getDefaultExtension()) ||
                   "gif".equals(sourceFormat.getDefaultExtension())||
                   "tif".equals(sourceFormat.getDefaultExtension())) { 
                  def.setParamByName("filetype", "2");
               }
               else if ("pdf".equals(sourceFormat.getDefaultExtension()) ||
                        "sam".equals(sourceFormat.getDefaultExtension())) { 
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
