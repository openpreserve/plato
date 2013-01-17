package eu.scape_project.planning.model.policy;

import java.util.ArrayList;
import java.util.List;

/**
 * A Scenario pulls together user community and content set.
 * 
 * @author hku
 *
 */
public class Scenario {
	
	/**
	 * URI of the defined scenario.
	 */
	private String uri;
	
	/**
	 * Name of the scenario.
	 */
	private String name;
	/**
	 * Name of the user community. 
	 * 
	 * This ought to be replaced by an object. 
	 */
	private String userCommunity;
	
	/**
	 * Name of the content set.
	 * 
	 * This ought to be replaced by an object.
	 */
	private String contentSet;
	
	/**
	 * A scenario refers to a set of control policies.
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

	public String getUserCommunity() {
		return userCommunity;
	}

	public void setUserCommunity(String userCommunity) {
		this.userCommunity = userCommunity;
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
