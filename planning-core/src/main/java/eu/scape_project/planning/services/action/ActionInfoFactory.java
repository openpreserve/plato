package eu.scape_project.planning.services.action;

import eu.scape_project.planning.model.PreservationActionDefinition;

/**
 * Factory method for action info.
 */
public final class ActionInfoFactory {

    /**
     * Hides constructor of utility class.
     */
    private ActionInfoFactory() {
    }

    /**
     * Creates a action info based on a PreservationActionDefinition.
     * 
     * @param preservationActionDefinition
     *            the preservation action definition
     * @return a action info
     */
    public static IActionInfo createActionInfo(PreservationActionDefinition preservationActionDefinition) {
        ActionInfo actionInfo = new ActionInfo();
        actionInfo.setServiceIdentifier(preservationActionDefinition.getActionIdentifier());
        actionInfo.setDescriptor(preservationActionDefinition.getDescriptor());
        actionInfo.setInfo(preservationActionDefinition.getInfo());
        actionInfo.setShortname(preservationActionDefinition.getShortname());
        actionInfo.setUrl(preservationActionDefinition.getUrl());
        return actionInfo;
    }
}
