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
package eu.scape_project.planning.plato.wf.beans;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named("fastTrackTemplates")
public class FastTrackTemplates implements Serializable {
	private static final long serialVersionUID = 8582365566558811935L;

	private List<FastTrackTemplate> templateList = new ArrayList<FastTrackTemplate>();
    
    private FastTrackTemplate fastTrackTemplate = null;
    
    private String directory;
    
    public FastTrackTemplates() {
    }
    
    public void init() {
        directory = "templates/fasttrack";
        
        URL url = Thread.currentThread().getContextClassLoader().getResource(directory);
        
        File dir = new File(url.getFile());
        
        templateList = iterateFiles(dir.listFiles());
    }
    
    private List<FastTrackTemplate> iterateFiles(File[] files) {
        List<FastTrackTemplate> list = new ArrayList<FastTrackTemplate>();
        
        if (files == null) {
            return list;
        }
        
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(iterateFiles(f.listFiles()));
            } else {          
                FastTrackTemplate ftt  = new FastTrackTemplate();
                ftt.setAbsolutePath(f.getAbsolutePath());
                
                String absolutePath = f.getAbsolutePath();
                int start = absolutePath.lastIndexOf(directory) + directory.length();
                
                
                String displayString = absolutePath.substring(start+1, absolutePath.length()-f.getName().length()-1);
                
                ftt.setDisplayString(displayString);
                list.add(ftt);
            }
        }
        
        return list;
    }
    
    // --------------- getter/setter ---------------
    
    public List<FastTrackTemplate> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<FastTrackTemplate> templateList) {
        this.templateList = templateList;
    }

    public FastTrackTemplate getFastTrackTemplate() {
        return fastTrackTemplate;
    }

    public void setFastTrackTemplate(FastTrackTemplate fastTrackTemplate) {
        this.fastTrackTemplate = fastTrackTemplate;
    }    
}
