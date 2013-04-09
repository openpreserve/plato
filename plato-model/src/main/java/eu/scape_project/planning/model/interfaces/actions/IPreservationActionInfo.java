package eu.scape_project.planning.model.interfaces.actions;

public interface IPreservationActionInfo {

    public String getActionIdentifier();

    public String getShortname();

    public void setShortname(String shortName);

    public String getDescriptor();

    public void setDescriptor(String descriptor);

    public String getInfo();

    public void setInfo(String info);

    public String getUrl();

    public void setUrl(String url);

}
