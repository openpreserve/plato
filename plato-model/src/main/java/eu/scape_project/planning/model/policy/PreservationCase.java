package eu.scape_project.planning.model.policy;

import java.util.ArrayList;
import java.util.List;

/**
 * A PreservationCase pulls together user community and content set.
 * 
 * @author hku
 * 
 */
public class PreservationCase {

    /**
     * URI of the defined preservation case.
     */
    private String uri;

    /**
     * Name of the preservation case.
     */
    private String name;
    /**
     * Names of user communities.
     * 
     * This ought to be replaced by an object.
     */
    private String userCommunities;

    /**
     * Name of the content set.
     * 
     * This ought to be replaced by an object.
     */
    private String contentSet;

    /**
     * A preservation case refers to a set of control policies.
     */
    private List<ControlPolicy> controlPolicies = new ArrayList<ControlPolicy>();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserCommunities() {
        return userCommunities;
    }

    public void setUserCommunities(String userCommunities) {
        this.userCommunities = userCommunities;
    }

    public List<ControlPolicy> getControlPolicies() {
        return controlPolicies;
    }

    public void setControlPolicies(List<ControlPolicy> policies) {
        this.controlPolicies = policies;
    }

    public String getContentSet() {
        return contentSet;
    }

    public void setContentSet(String contentSet) {
        this.contentSet = contentSet;
    }
}
