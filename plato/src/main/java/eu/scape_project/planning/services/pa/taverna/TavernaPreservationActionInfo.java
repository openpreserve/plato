package eu.scape_project.planning.services.pa.taverna;

import eu.scape_project.planning.services.action.PreservationActionInfo;

/**
 * A Taverna preservation action service information.
 */
public class TavernaPreservationActionInfo extends PreservationActionInfo {
    private static final long serialVersionUID = -8247024711926820024L;

    private static final String SERVICE_IDENTIFIER = "myExperiment";

    /**
     * Creates a new instance.
     */
    public TavernaPreservationActionInfo() {
        super.setActionIdentifier(SERVICE_IDENTIFIER);
    }
}
