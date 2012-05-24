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
