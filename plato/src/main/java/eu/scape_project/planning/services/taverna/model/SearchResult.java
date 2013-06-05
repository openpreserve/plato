package eu.scape_project.planning.services.taverna.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


/**
 * Description of a workflow.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workflows")
public class SearchResult {

    /**
     * Workflow of search result.
     */
    @XmlRootElement(name = "workflow")
    public static class Workflow extends ResourceDescription {
        @XmlValue
        private String name;

        @XmlAttribute
        private String version;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    @XmlElement(name = "workflow")
    private List<Workflow> workflows;

    // ---------- getter/setter ----------
    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }

}
