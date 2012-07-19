package eu.scape_project.planning.xml;


public class PreservationPlanXML {

    protected static final String SCHEMA_LOCATION = "data/schemas/";
    public static final String PLATO_NS = "http://ifs.tuwien.ac.at/dp/plato/";
    public static final String PLATO_SCHEMA = "plato-V4.0.0.xsd";
    public static final String PLATO_SCHEMA_URI = PLATO_NS + PLATO_SCHEMA;
    public static final String PAP_SCHEMA = "preservationActionPlan-V1.xsd";
    public static final String PAP_SCHEMA_URI = PLATO_NS + PAP_SCHEMA;
    public static final String TAVERNA_SCHEMA = "t2flow.xsd";
    public static final String TAVERNA_SCHEMA_URI = PLATO_NS + TAVERNA_SCHEMA;
    protected static final String[] PLAN_SCHEMAS = {ProjectImporter.PLATO_SCHEMA_URI};

}