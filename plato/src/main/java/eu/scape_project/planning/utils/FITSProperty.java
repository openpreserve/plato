package eu.scape_project.planning.utils;

import java.util.List;

public class FITSProperty {
    private String name;
    private String status;
    private String value;
    
    private List<FITSToolInfo> tools;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public List<FITSToolInfo> getTools() {
        return tools;
    }
    public void setTools(List<FITSToolInfo> tools) {
        this.tools = tools;
    }
    
    
}
