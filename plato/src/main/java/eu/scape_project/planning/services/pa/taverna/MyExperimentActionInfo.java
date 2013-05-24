package eu.scape_project.planning.services.pa.taverna;

import eu.scape_project.planning.services.action.IActionInfo;

/**
 * A Taverna preservation action service information.
 */
public class MyExperimentActionInfo implements IActionInfo {
    private static final String SERVICE_IDENTIFIER = "myExperiment";

    private String shortname;
    private String descriptor;
    private String info;
    private String url;
    private String contentType;

    @Override
    public String getServiceIdentifier() {
        return SERVICE_IDENTIFIER;
    }

    @Override
    public String getShortname() {
        return shortname;
    }

    @Override
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
