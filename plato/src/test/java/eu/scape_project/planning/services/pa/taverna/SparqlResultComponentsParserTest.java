package eu.scape_project.planning.services.pa.taverna;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import eu.scape_project.planning.services.IServiceInfo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SparqlResultComponentsParserTest {
    private SparqlResultComponentsParser parser;

    @Before
    public void setup() {
        parser = new SparqlResultComponentsParser();
    }

    @Test
    public void testSuccessfulParsing() throws Exception {
        List<IServiceInfo> components = new ArrayList<IServiceInfo>();

        Reader reader = new FileReader("src/test/resources/data/component/lookup-response-sample1.xml");

        parser.addComponentsFromSparqlResult(components, reader);

        Assert.assertEquals(8, components.size());

        Assert.assertEquals("http://www.myexperiment.org/workflows/2639/versions/4", components.get(0).getDescriptor());
        Assert
            .assertEquals("http://www.myexperiment.org/workflows/2639/download?version=4", components.get(0).getUrl());
        Assert.assertEquals("Mock-Up mp3 To Wav Migrate And QA", components.get(0).getShortname());
        Assert.assertNotNull(components.get(0).getInfo());
        Assert.assertEquals("myExperiment", components.get(0).getServiceIdentifier());
    }
}
