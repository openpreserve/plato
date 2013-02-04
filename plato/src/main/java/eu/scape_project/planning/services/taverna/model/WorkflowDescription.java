package eu.scape_project.planning.services.taverna.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


/**
 * Description of a workflow.
 */
@XmlRootElement
public class WorkflowDescription extends ResourceDescription {

    /**
     * Type of a workflow.
     */
    @XmlRootElement(name = "type")
    public static class Type extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String content) {
            this.name = content;
        }
    }

    /**
     * Uploader of a workflow.
     */
    @XmlRootElement(name = "uploader")
    public static class Uploader extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String content) {
            this.name = content;
        }
    }

    /**
     * License type of a workflow.
     */
    @XmlRootElement(name = "license-type")
    public static class LicenseType extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String content) {
            this.name = content;
        }
    }

    /**
     * Tag of a workflow.
     */
    @XmlRootElement(name = "tag")
    public static class Tag extends ResourceDescription {
        @XmlValue
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String content) {
            this.name = content;
        }
    }

    @XmlAttribute
    private String version;
    private String id;
    private String title;

    private String description;

    @XmlElement
    private WorkflowDescription.Type type;

    @XmlElement
    private WorkflowDescription.Uploader uploader;

    private String preview;

    private String svg;

    @XmlElement(name = "license-type")
    private WorkflowDescription.LicenseType licenseType;

    @XmlElement(name = "content-uri")
    private String contentUri;

    @XmlElement(name = "content-type")
    private String contentType;

    @XmlElementWrapper
    @XmlElement(name = "tag")
    private List<Tag> tags;

    // ---------- getter/setter ----------

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkflowDescription.Type getType() {
        return type;
    }

    public void setType(WorkflowDescription.Type type) {
        this.type = type;
    }

    public WorkflowDescription.Uploader getUploader() {
        return uploader;
    }

    public void setUploader(WorkflowDescription.Uploader uploader) {
        this.uploader = uploader;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public WorkflowDescription.LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(WorkflowDescription.LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public String getContentUri() {
        return contentUri;
    }

    public void setContentUri(String contentUri) {
        this.contentUri = contentUri;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
