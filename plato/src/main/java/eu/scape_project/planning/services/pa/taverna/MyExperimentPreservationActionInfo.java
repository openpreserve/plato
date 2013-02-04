package eu.scape_project.planning.services.pa.taverna;

import java.net.URL;

import eu.scape_project.planning.model.interfaces.actions.IPreservationActionInfo;

public class MyExperimentPreservationActionInfo implements IPreservationActionInfo {

    private String shortname;

    private URL descriptor;

    private String info;

    private URL url;

    @Override
    public String getShortname() {
        return shortname;
    }

    @Override
    public void setShortname(String shortName) {
        this.shortname = shortName;
    }

    @Override
    public URL getDescriptor() {
        return descriptor;
    }

    @Override
    public void setDescriptor(URL descriptor) {
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

}
