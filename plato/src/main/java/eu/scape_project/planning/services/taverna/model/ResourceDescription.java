package eu.scape_project.planning.services.taverna.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public class ResourceDescription {

    @XmlAttribute
    private URI uri;
    @XmlAttribute
    private URI resource;

    public ResourceDescription() {
        super();
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public URI getResource() {
        return resource;
    }

    public void setResource(URI resource) {
        this.resource = resource;
    }

    public String getId() {
        return uri.toString().substring(uri.toString().indexOf("id=") + 3);
    }

}