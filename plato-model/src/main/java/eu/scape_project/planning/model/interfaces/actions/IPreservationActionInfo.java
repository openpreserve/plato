package eu.scape_project.planning.model.interfaces.actions;

import java.net.URL;

public interface IPreservationActionInfo {

    public String getShortname();

    public void setShortname(String shortName);

    public URL getDescriptor();

    public void setDescriptor(URL descriptor);

    public String getInfo();

    public void setInfo(String info);

    public String getUrl();

    public void setUrl(String url);

}
