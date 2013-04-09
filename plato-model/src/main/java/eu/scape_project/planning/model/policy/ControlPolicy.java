package eu.scape_project.planning.model.policy;

import eu.scape_project.planning.model.measurement.Measure;

/**
 * A control policy, which is of type {@link #controlPolicyType}, refers to a
 * measure {@link #measure} which must/should ({@link #modality}) have a certain
 * {@link #value}.
 * 
 * @author hku
 * 
 */
public class ControlPolicy {

    public enum ControlPolicyType {
        FORMAT_OBJECTIVE("Format Objective"),
        AUTHENTICITY_OBJECTIVE("Authenticity Objective"),
        ACTION_OBJECTIVE("Action Objective"),
        REPRESENTATION_INSTANCE_OBJECTIVE("Representation Instance Objective"),
        ACCESS_OBJECTIVE("Access Objective");

        private ControlPolicyType(final String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }

        private final String text;
    }

    public enum Modality {

        MUST("must"),
        SHOULD("should");

        private Modality(final String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }

        private final String text;
    }

    public enum Qualifier {
        GT("greater than"),
        LT("lower than"),
        EQ("equal"),
        GE("greater or equal"),
        LE("lower or equal");

        private Qualifier(final String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }

        private final String text;
    }

    /**
     * URI of the control policy.
     */
    private String uri;

    /**
     * Human understandable name of the control policy
     */
    private String name;

    /**
     * Type of control policy.
     */
    private ControlPolicyType controlPolicyType;

    /**
     * modality that describes whether the particular property-value pair is
     * present or not.
     */
    private Modality modality;

    /**
     * A qualifier (equals, greater than, less than etc).
     */
    private Qualifier qualifier;

    /**
     * A value associated with the measure.
     */
    private String value;

    /**
     * A measure that the control policy pertains to
     */
    private Measure measure;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Qualifier getQualifier() {
        return qualifier;
    }

    public void setQualifier(Qualifier qualifier) {
        this.qualifier = qualifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Modality getModality() {
        return modality;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }

    public ControlPolicyType getControlPolicyType() {
        return controlPolicyType;
    }

    public void setControlPolicyType(ControlPolicyType controlPolicyType) {
        this.controlPolicyType = controlPolicyType;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
