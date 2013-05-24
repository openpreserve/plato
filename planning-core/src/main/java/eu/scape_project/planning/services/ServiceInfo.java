package eu.scape_project.planning.services;

/**
 * Base implementation of the service info.
 */
public class ServiceInfo implements IServiceInfo {

    private String serviceidentifier;
    private String shortname;
    private String descriptor;
    private String info;
    private String url;
    
    @Override
    public String getServiceIdentifier() {
        return serviceidentifier;
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

}
