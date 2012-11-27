package eu.scape_project.planning.taverna;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.planning.taverna.parser.T2FlowParser;
import eu.scape_project.planning.taverna.parser.T2FlowParser.ComponentProfile;

import org.junit.Test;

public class T2FlowParserTest {

    private static final String FROM_OBJECT_PATH_URI = "http://scape-project.eu/components/FromObject";
    private static final String FROM_OBJECT_PATH_NAME = "path_from";

    private static final String TO_OBJECT_PATH_URI = "http://scape-project.eu/components/ToObject";
    private static final String TO_OBJECT_PATH_NAME = "path_to";

    private static final String MEASURES_URI_PREFIX = "http://scape-project.eu/pw/vocab/measures/";

    @Test
    public void getIdTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        assertTrue(t2flowParser.getId().equals("e0089782-e811-48c7-b72d-2b84ddfdeb11"));
    }

    @Test
    public void getVersionTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        assertTrue(t2flowParser.getVersion() == null);
    }

    @Test
    public void getProfileTest() throws Exception {

        // Migration Action
        InputStream migrationActionT2flow = getClass().getResourceAsStream("/taverna/ProfileMigrationAction.t2flow");
        assertTrue(migrationActionT2flow != null);

        T2FlowParser MigrationActionT2flowParser = T2FlowParser.createParser(migrationActionT2flow);

        ComponentProfile migrationProfile = MigrationActionT2flowParser.getProfile();
        assertTrue(migrationProfile == ComponentProfile.MigrationAction);

        // Characterisation
        InputStream characterisationT2flow = getClass().getResourceAsStream("/taverna/ProfileCharacterisation.t2flow");
        assertTrue(characterisationT2flow != null);

        T2FlowParser CharacterisationT2flowParser = T2FlowParser.createParser(characterisationT2flow);

        ComponentProfile characterisationProfile = CharacterisationT2flowParser.getProfile();
        assertTrue(characterisationProfile == ComponentProfile.Characterisation);

        // QA
        InputStream qaT2flow = getClass().getResourceAsStream("/taverna/ProfileQA.t2flow");
        assertTrue(qaT2flow != null);

        T2FlowParser qaT2flowParser = T2FlowParser.createParser(qaT2flow);

        ComponentProfile qaProfile = qaT2flowParser.getProfile();
        assertTrue(qaProfile == ComponentProfile.QA);

        // Executable Plang
        InputStream execuatblePlanT2flow = getClass().getResourceAsStream("/taverna/ProfileExecutablePlan.t2flow");
        assertTrue(execuatblePlanT2flow != null);

        T2FlowParser executablePlanT2flowParser = T2FlowParser.createParser(execuatblePlanT2flow);

        ComponentProfile executablePlanProfile = executablePlanT2flowParser.getProfile();
        assertTrue(executablePlanProfile == ComponentProfile.ExecutablePlan);
    }

    @Test
    public void getNameTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        assertTrue(t2flowParser.getName().equals("Workflow Title"));
    }

    @Test
    public void getDescriptionTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        assertTrue(t2flowParser.getDescription().equals("Workflow Description"));
    }

    @Test
    public void getAuthorTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        assertTrue(t2flowParser.getAuthor().equals("Workflow Author"));
    }

    @Test
    public void getOwnerTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        // TODO: Fix expected value after annotation is implemented in Taverna
        assertTrue(t2flowParser.getOwner() == null);
    }

    @Test
    public void getLicenseTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        // TODO: Fix expected value after annotation is implemented in Taverna
        assertTrue(t2flowParser.getLicense() == null);
    }

    @Test
    public void getInputPortsTest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPorts.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParser inOutT2flowParser = T2FlowParser.createParser(inOutT2flow);
        Set<TavernaPort> inputPorts = inOutT2flowParser.getInputPorts();

        assertTrue(inputPorts.size() == 4);

        for (TavernaPort inputPort : inputPorts) {
            if (inputPort.getName().equals(FROM_OBJECT_PATH_NAME)) {
                assertTrue(inputPort.getDepth() == 1);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(FROM_OBJECT_PATH_URI));
                }
            } else if (inputPort.getName().equals(TO_OBJECT_PATH_NAME)) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(TO_OBJECT_PATH_URI));
                }
            } else if (inputPort.getName().equals("111")) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 2);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(TO_OBJECT_PATH_URI)
                        || uri.toString().equals(MEASURES_URI_PREFIX + "111"));
                }
            } else if (inputPort.getName().equals("other_in")) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 0);
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void getInputPortsByURITest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPorts.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParser inOutT2flowParser = T2FlowParser.createParser(inOutT2flow);

        // FromObject
        Set<TavernaPort> FromInputPorts = inOutT2flowParser.getInputPorts(new URI(FROM_OBJECT_PATH_URI));

        assertTrue(FromInputPorts.size() == 1);

        for (TavernaPort inputPort : FromInputPorts) {
            if (inputPort.getName().equals(FROM_OBJECT_PATH_NAME)) {
                assertTrue(inputPort.getDepth() == 1);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(FROM_OBJECT_PATH_URI));
                }
            } else {
                assertTrue(false);
            }
        }

        // ToObject
        Set<TavernaPort> toInputPorts = inOutT2flowParser.getInputPorts(new URI(TO_OBJECT_PATH_URI));

        assertTrue(toInputPorts.size() == 2);

        for (TavernaPort inputPort : toInputPorts) {
            if (inputPort.getName().equals(TO_OBJECT_PATH_NAME)) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(TO_OBJECT_PATH_URI));
                }
            } else if (inputPort.getName().equals("111")) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 2);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(TO_OBJECT_PATH_URI)
                        || uri.toString().equals(MEASURES_URI_PREFIX + "111"));
                }
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void getOutputPortsTest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPorts.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParser inOutT2flowParser = T2FlowParser.createParser(inOutT2flow);
        Set<TavernaPort> outputPorts = inOutT2flowParser.getOutputPorts();

        assertTrue(outputPorts.size() == 3);

        for (TavernaPort outputPort : outputPorts) {
            if (outputPort.getName().equals(FROM_OBJECT_PATH_NAME)) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(FROM_OBJECT_PATH_URI));
                }
            } else if (outputPort.getName().equals(TO_OBJECT_PATH_NAME)) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(TO_OBJECT_PATH_URI));
                }
            } else if (outputPort.getName().equals("112")) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(MEASURES_URI_PREFIX + "112"));
                }
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void getOutputPortsByURITest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPorts.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParser inOutT2flowParser = T2FlowParser.createParser(inOutT2flow);

        // FromObject
        Set<TavernaPort> FromOutputPorts = inOutT2flowParser.getOutputPorts(new URI(FROM_OBJECT_PATH_URI));

        assertTrue(FromOutputPorts.size() == 1);

        for (TavernaPort outputPort : FromOutputPorts) {
            if (outputPort.getName().equals(FROM_OBJECT_PATH_NAME)) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(FROM_OBJECT_PATH_URI));
                }
            } else {
                assertTrue(false);
            }
        }

        // ToObject
        Set<TavernaPort> toOutputPorts = inOutT2flowParser.getOutputPorts(new URI(TO_OBJECT_PATH_URI));

        assertTrue(toOutputPorts.size() == 1);

        for (TavernaPort outputPort : toOutputPorts) {
            if (outputPort.getName().equals(TO_OBJECT_PATH_NAME)) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(TO_OBJECT_PATH_URI));
                }
            } else {
                assertTrue(false);
            }
        }

        // Measure
        Set<TavernaPort> measureOutputPorts = inOutT2flowParser.getOutputPorts(new URI(MEASURES_URI_PREFIX + "112"));

        assertTrue(measureOutputPorts.size() == 1);

        for (TavernaPort outputPort : measureOutputPorts) {
            if (outputPort.getName().equals("112")) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals(MEASURES_URI_PREFIX + "112"));
                }
            } else {
                assertTrue(false);
            }
        }
    }
}
