package eu.scape_project.planning.services.action;

import eu.scape_project.planning.model.PreservationActionDefinition;

/**
 * Information about a preservation action service.
 */
public class ActionInfo extends PreservationActionDefinition implements IActionInfo {

    private static final long serialVersionUID = -7502826760939283906L;

    @Override
    public String getServiceIdentifier() {
        return super.getActionIdentifier();
    }

    /**
     * Sets the service identifier of the action info.
     * 
     * @param serviceIdentifier
     *            the service identifier
     */
    public void setServiceIdentifier(String serviceIdentifier) {
        super.setActionIdentifier(serviceIdentifier);
    }

}
