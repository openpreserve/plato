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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SessionScoped
@Named("fastTrackTemplates")
public class FastTrackTemplates implements Serializable {
    private static final long serialVersionUID = 8582365566558811935L;
    
    private static final Logger log = LoggerFactory.getLogger(FastTrackTemplates.class);

    private List<FastTrackTemplate> templateList = new ArrayList<FastTrackTemplate>();

    private FastTrackTemplate fastTrackTemplate = null;

    private String directory;

    public FastTrackTemplates() {
    }

    public void init() {
        directory = "templates/fasttrack";
        templateList.clear();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(directory + "/templates.lst")));
            String line = reader.readLine();
            while (line != null) {
                FastTrackTemplate template = new FastTrackTemplate();
                int sepPos = line.indexOf("=");
                template.setPath(directory + "/" + line.substring(0, sepPos));
                template.setDisplayString(line.substring(sepPos+1));
                templateList.add(template);
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.error("Failed to load list of fast track templates.", e);
        }
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
