package eu.scape_project.planning.services.myexperiment.domain;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;

import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Installation;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.Installation.Dependency;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.MigrationPath;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.ParameterPort;
import eu.scape_project.planning.services.myexperiment.domain.WorkflowDescription.ParameterPort.PredefinedParameter;

public class WorkflowDescriptionTest {

    @Test
    public void unmarshallWorkflowDescription() throws JAXBException, URISyntaxException {
        JAXBContext context = JAXBContext.newInstance(WorkflowInfo.class, WorkflowDescription.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("myexperiment/workflow-description-migrationaction.xml");

        WorkflowDescription wf = (WorkflowDescription) unmarshaller.unmarshal(in);

        Assert.assertEquals(new URI("http://sandbox.myexperiment.org/workflow.xml?id=3366"), wf.getUri());
        Assert.assertEquals(new URI("http://sandbox.myexperiment.org/workflows/3366/versions/1"), wf.getResource());

        Assert.assertEquals("3366", wf.getId());
        Assert.assertEquals("1", wf.getVersion());
        Assert.assertEquals("Imagemagick convert - tiff2tiff - compression", wf.getName());
        Assert.assertEquals("Converts tiff to tiff using imagemagick convert with the provided compression",
            wf.getDescription());
        Assert
            .assertEquals(
                "http://sandbox.myexperiment.org/workflows/3366/download/imagemagick_convert_-_tiff2tiff_-_compression_281494.t2flow?version=1",
                wf.getContentUri());
        Assert.assertEquals("application/vnd.taverna.t2flow+xml", wf.getContentType());

        Assert.assertNotNull(wf.getType());
        Assert.assertNotNull(wf.getUploader());
        Assert.assertEquals("http://sandbox.myexperiment.org/workflows/3366/versions/1/previews/full", wf.getPreview());
        Assert.assertEquals("http://sandbox.myexperiment.org/workflows/3366/versions/1/previews/svg", wf.getSvg());
        Assert.assertNotNull(wf.getLicenseType());

        Assert.assertEquals(1, wf.getTags().size());
        Assert.assertEquals(0, wf.getRatings().size());

        Assert.assertEquals("http://purl.org/DP/components#MigrationAction", wf.getProfile());

        List<MigrationPath> migrationPaths = wf.getMigrationPaths();
        Assert.assertEquals(1, migrationPaths.size());
        Assert.assertEquals("image/tiff", migrationPaths.get(0).getFromMimetype());
        Assert.assertEquals("image/tiff", migrationPaths.get(0).getToMimetype());

        List<Installation> installations = wf.getInstallations();
        Assert.assertEquals(1, installations.size());
        Assert.assertEquals("http://purl.org/DP/components#Debian", installations.get(0).getEnvironment());
        List<Dependency> dependencies = installations.get(0).getDependencies();
        Assert.assertEquals(1, dependencies.size());
        Assert.assertEquals("imagemagick", dependencies.get(0).getName());
        Assert.assertEquals("5", dependencies.get(0).getVersion());
        Assert.assertEquals("http://opensource.org/licenses/Apache-2.0", dependencies.get(0).getLicense());

        List<ParameterPort> parameterPorts = wf.getParameterPorts();
        Assert.assertEquals(1, parameterPorts.size());
        Assert.assertEquals("compression", parameterPorts.get(0).getName());
        Assert.assertEquals("Imagemagick convert compress parameter", parameterPorts.get(0).getDescription());
        List<PredefinedParameter> predefinedParameters = parameterPorts.get(0).getPredefinedParameters();
        Assert.assertEquals(3, predefinedParameters.size());

        Assert.assertThat(predefinedParameters, new TypeSafeMatcher<List<PredefinedParameter>>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Predefined parameters does not contain compression type none.");
            }

            @Override
            protected boolean matchesSafely(List<PredefinedParameter> parameters) {
                for (PredefinedParameter p : parameters) {
                    if (p.getValue().equals("none") && p.getDescription().equals("no compression")) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
