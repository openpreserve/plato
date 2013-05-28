package eu.scape_project.planning.services.pa.taverna;

import eu.scape_project.planning.services.action.ActionInfo;

/**
 * A Taverna preservation action service information.
 */
public class TavernaActionInfo extends ActionInfo {
    private static final long serialVersionUID = -8247024711926820024L;

    private static final String SERVICE_IDENTIFIER = "myExperiment";

    /**
     * Creates a new instance.
     */
    public TavernaActionInfo() {
        super.setActionIdentifier(SERVICE_IDENTIFIER);
    }
}
