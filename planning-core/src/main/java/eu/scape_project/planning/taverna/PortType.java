package eu.scape_project.planning.taverna;


/**
 * Component profiles.
 */
public enum PortType {

    ParameterPort("ParameterPort"), ObjectURIPort("ObjectURIPort"), ToURIPort("ToURIPort"), FromURIPort("FromURIPort"),
    MeasurePort("MeasurePort"), FromMeasurePort("FromMeasurePort"), ToMeasurePort("ToMeasurePort");

    private final String uri;

    /**
     * Creates a port type based on the provided URI.
     * 
     * @param uri
     *            the URI of the port type
     */
    PortType(String uri) {
        this.uri = "http://purl.org/DP/components#" + uri;
    }

    @Override
    public String toString() {
        return uri;
    }

    /**
     * Returns the ComponentProfile corresponding to the provided text.
     * 
     * @param text
     *            the text of the ComponentProfile
     * @return the ComponentProfile
     */
    public static PortType fromString(String text) {
        if (text != null) {
            for (PortType b : PortType.values()) {
                if (text.equalsIgnoreCase(b.toString())) {
                    return b;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
