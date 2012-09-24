package eu.scape_project.pw.planning.taverna;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import eu.scape_project.planning.taverna.TavernaPort;
import eu.scape_project.planning.taverna.parser.T2FlowParser.ComponentProfile;
import eu.scape_project.planning.taverna.parser.T2FlowParserFallback;

import org.junit.Test;

public class T2FlowParserFallbackTest {

    @Test
    public void getIdTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParserFallback t2flowParser = T2FlowParserFallback.createParser(t2flow);

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        assertTrue(t2flowParser.getId().equals("e0089782-e811-48c7-b72d-2b84ddfdeb11"));
    }

    @Test
    public void getVersionTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParserFallback t2flowParser = T2FlowParserFallback.createParser(t2flow);

        // TODO: Fix XPath to use correct annotation class after annotation
        // is implemented in Taverna
        assertTrue(t2flowParser.getVersion() == null);
    }

    @Test
    public void getProfileTest() throws Exception {

        // Migration Action
        InputStream migrationActionT2flow = getClass().getResourceAsStream(
            "/taverna/ProfileMigrationActionFallback.t2flow");
        assertTrue(migrationActionT2flow != null);

        T2FlowParserFallback MigrationActionT2flowParser = T2FlowParserFallback.createParser(migrationActionT2flow);

        ComponentProfile migrationProfile = MigrationActionT2flowParser.getProfile();
        assertTrue(migrationProfile == ComponentProfile.MigrationAction);

        // Characterisation
        InputStream characterisationT2flow = getClass().getResourceAsStream(
            "/taverna/ProfileCharacterisationFallback.t2flow");
        assertTrue(characterisationT2flow != null);

        T2FlowParserFallback CharacterisationT2flowParser = T2FlowParserFallback.createParser(characterisationT2flow);

        ComponentProfile characterisationProfile = CharacterisationT2flowParser.getProfile();
        assertTrue(characterisationProfile == ComponentProfile.Characterisation);

        // QA
        InputStream qaT2flow = getClass().getResourceAsStream("/taverna/ProfileQAFallback.t2flow");
        assertTrue(qaT2flow != null);

        T2FlowParserFallback qaT2flowParser = T2FlowParserFallback.createParser(qaT2flow);

        ComponentProfile qaProfile = qaT2flowParser.getProfile();
        assertTrue(qaProfile == ComponentProfile.QA);

        // Executable Plang
        InputStream execuatblePlanT2flow = getClass().getResourceAsStream(
            "/taverna/ProfileExecutablePlanFallback.t2flow");
        assertTrue(execuatblePlanT2flow != null);

        T2FlowParserFallback executablePlanT2flowParser = T2FlowParserFallback.createParser(execuatblePlanT2flow);

        ComponentProfile executablePlanProfile = executablePlanT2flowParser.getProfile();
        assertTrue(executablePlanProfile == ComponentProfile.ExecutablePlan);
    }

    @Test
    public void getNameTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParserFallback t2flowParser = T2FlowParserFallback.createParser(t2flow);

        assertTrue(t2flowParser.getName().equals("Workflow Title"));
    }

    @Test
    public void getDescriptionTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParserFallback t2flowParser = T2FlowParserFallback.createParser(t2flow);

        assertTrue(t2flowParser.getDescription().equals("Workflow Description"));
    }

    @Test
    public void getAuthorTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotationsFallback.t2flow");
        assertTrue(t2flow != null);

        T2FlowParserFallback t2flowParser = T2FlowParserFallback.createParser(t2flow);

        assertTrue(t2flowParser.getAuthor().equals("Workflow Author"));
    }

    @Test
    public void getOwnerTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotationsFallback.t2flow");
        assertTrue(t2flow != null);

        T2FlowParserFallback t2flowParser = T2FlowParserFallback.createParser(t2flow);

        assertTrue(t2flowParser.getOwner().equals("Workflow Author"));
    }

    @Test
    public void getLicenseTest() throws Exception {
        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotationsFallback.t2flow");
        assertTrue(t2flow != null);

        T2FlowParserFallback t2flowParser = T2FlowParserFallback.createParser(t2flow);

        assertTrue(t2flowParser.getLicense() == null);
    }

    @Test
    public void getInputPortsTest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPortsFallback.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParserFallback inOutT2flowParser = T2FlowParserFallback.createParser(inOutT2flow);
        Set<TavernaPort> inputPorts = inOutT2flowParser.getInputPorts();

        assertTrue(inputPorts.size() == 3);

        for (TavernaPort inputPort : inputPorts) {
            if (inputPort.getName().equals("path_from")) {
                assertTrue(inputPort.getDepth() == 1);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/FromObject"));
                }
            } else if (inputPort.getName().equals("path_to")) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/ToObject"));
                }
            } else if (inputPort.getName().equals("111")) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/pw/vocab/measures/111"));
                }
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void getInputPortsByURITest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPortsFallback.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParserFallback inOutT2flowParser = T2FlowParserFallback.createParser(inOutT2flow);

        // FromObject
        Set<TavernaPort> FromInputPorts = inOutT2flowParser.getInputPorts(new URI(
            "http://scape-project.eu/components/FromObject"));

        assertTrue(FromInputPorts.size() == 1);

        for (TavernaPort inputPort : FromInputPorts) {
            if (inputPort.getName().equals("path_from")) {
                assertTrue(inputPort.getDepth() == 1);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/FromObject"));
                }
            } else {
                assertTrue(false);
            }
        }

        // ToObject
        Set<TavernaPort> toInputPorts = inOutT2flowParser.getInputPorts(new URI(
            "http://scape-project.eu/components/ToObject"));

        assertTrue(toInputPorts.size() == 1);

        for (TavernaPort inputPort : toInputPorts) {
            if (inputPort.getName().equals("path_to")) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/ToObject"));
                }
            } else {
                assertTrue(false);
            }
        }

        // Measure
        Set<TavernaPort> measureInputPort = inOutT2flowParser.getInputPorts(new URI(
            "http://scape-project.eu/pw/vocab/measures/111"));

        assertTrue(toInputPorts.size() == 1);

        for (TavernaPort inputPort : measureInputPort) {
            if (inputPort.getName().equals("111")) {
                assertTrue(inputPort.getDepth() == 0);
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/pw/vocab/measures/111"));
                }
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void getOutputPortsTest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPortsFallback.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParserFallback inOutT2flowParser = T2FlowParserFallback.createParser(inOutT2flow);
        Set<TavernaPort> outputPorts = inOutT2flowParser.getOutputPorts();

        assertTrue(outputPorts.size() == 3);

        for (TavernaPort outputPort : outputPorts) {
            if (outputPort.getName().equals("path_from")) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/FromObject"));
                }
            } else if (outputPort.getName().equals("path_to")) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/ToObject"));
                }
            } else if (outputPort.getName().equals("111")) {
                Set<URI> uris = outputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/pw/vocab/measures/111"));
                }
            } else {
                assertTrue(false);
            }
        }
    }

    @Test
    public void getOutputPortsByURITest() throws Exception {

        // Migration Action
        InputStream inOutT2flow = getClass().getResourceAsStream("/taverna/InOutPortsFallback.t2flow");
        assertTrue(inOutT2flow != null);

        T2FlowParserFallback inOutT2flowParser = T2FlowParserFallback.createParser(inOutT2flow);

        // FromObject
        Set<TavernaPort> FromInputPorts = inOutT2flowParser.getOutputPorts(new URI(
            "http://scape-project.eu/components/FromObject"));

        assertTrue(FromInputPorts.size() == 1);

        for (TavernaPort inputPort : FromInputPorts) {
            if (inputPort.getName().equals("path_from")) {
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/FromObject"));
                }
            } else {
                assertTrue(false);
            }
        }

        // ToObject
        Set<TavernaPort> toInputPorts = inOutT2flowParser.getOutputPorts(new URI(
            "http://scape-project.eu/components/ToObject"));

        assertTrue(toInputPorts.size() == 1);

        for (TavernaPort inputPort : toInputPorts) {
            if (inputPort.getName().equals("path_to")) {
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/components/ToObject"));
                }
            } else {
                assertTrue(false);
            }
        }

        // Measure
        Set<TavernaPort> measureInputPort = inOutT2flowParser.getOutputPorts(new URI(
            "http://scape-project.eu/pw/vocab/measures/111"));

        assertTrue(toInputPorts.size() == 1);

        for (TavernaPort inputPort : measureInputPort) {
            if (inputPort.getName().equals("111")) {
                Set<URI> uris = inputPort.getUris();
                assertTrue(uris.size() == 1);
                for (URI uri : uris) {
                    assertTrue(uri.toString().equals("http://scape-project.eu/pw/vocab/measures/111"));
                }
            } else {
                assertTrue(false);
            }
        }
    }
}
