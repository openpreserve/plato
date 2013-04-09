package eu.scape_project.planning.services.pa.taverna;

import eu.scape_project.planning.model.interfaces.actions.IPreservationActionInfo;

public class MyExperimentPreservationActionInfo implements IPreservationActionInfo {

    private static final String ACTION_IDENTIFIER = "myExperiment";

    private String shortname;

    private String descriptor;

    private String info;

    private String url;

    @Override
    public String getActionIdentifier() {
        return ACTION_IDENTIFIER;
    }

    @Override
    public String getShortname() {
        return shortname;
    }

    @Override
    public void setShortname(String shortName) {
        this.shortname = shortName;
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

}
