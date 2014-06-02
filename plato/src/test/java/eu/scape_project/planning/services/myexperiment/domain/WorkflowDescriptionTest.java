/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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

import eu.scape_project.planning.services.myexperiment.domain.Installation.Dependency;
import eu.scape_project.planning.services.myexperiment.domain.Port.PredefinedParameter;

public class WorkflowDescriptionTest {

    @Test
    public void unmarshallWorkflowDescription_migration() throws JAXBException, URISyntaxException {
        JAXBContext context = JAXBContext.newInstance(WorkflowInfo.class, WorkflowDescription.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("myexperiment/workflow-description-migrationaction.xml");

        WorkflowDescription wf = (WorkflowDescription) unmarshaller.unmarshal(in);
        wf.readMetadata();

        Assert.assertEquals(new URI("http://sandbox.myexperiment.org/workflow.xml?id=3379"), wf.getUri());
        Assert.assertEquals(new URI("http://sandbox.myexperiment.org/workflows/3379/versions/1"), wf.getResource());

        Assert.assertEquals("3379", wf.getId());
        Assert.assertEquals("1", wf.getVersion());
        Assert.assertEquals("Imagemagick convert - tiff2tiff - compression", wf.getName());
        Assert.assertEquals("Converts tiff to tiff using imagemagick convert with the provided compression",
            wf.getDescription());
        Assert
            .assertEquals(
                "http://sandbox.myexperiment.org/workflows/3379/download/imagemagick_convert_-_tiff2tiff_-_compression_800942.t2flow?version=1",
                wf.getContentUri());
        Assert.assertEquals("application/vnd.taverna.t2flow+xml", wf.getContentType());

        Assert.assertNotNull(wf.getType());
        Assert.assertNotNull(wf.getUploader());
        Assert.assertEquals("http://sandbox.myexperiment.org/workflows/3379/versions/1/previews/full", wf.getPreview());
        Assert.assertEquals("http://sandbox.myexperiment.org/workflows/3379/versions/1/previews/svg", wf.getSvg());
        Assert.assertNotNull(wf.getLicenseType());

        Assert.assertEquals(2, wf.getTags().size());
        Assert.assertEquals(0, wf.getRatings().size());

        Assert.assertEquals("http://purl.org/DP/components#MigrationAction", wf.getProfile());

        List<MigrationPath> migrationPaths = wf.getMigrationPaths();
        Assert.assertEquals(1, migrationPaths.size());
        Assert.assertEquals("image/tiff", migrationPaths.get(0).getSourceMimetype());
        Assert.assertEquals("image/tiff", migrationPaths.get(0).getTargetMimetype());

        List<Installation> installations = wf.getInstallations();
        Assert.assertEquals(1, installations.size());
        Assert.assertEquals("http://purl.org/DP/components#Debian", installations.get(0).getEnvironment());
        List<Dependency> dependencies = installations.get(0).getDependencies();
        Assert.assertEquals(1, dependencies.size());
        Assert.assertEquals("imagemagick", dependencies.get(0).getName());
        Assert.assertEquals("5", dependencies.get(0).getVersion());
        Assert.assertEquals("http://opensource.org/licenses/Apache-2.0", dependencies.get(0).getLicense());

        List<Port> inputPorts = wf.getInputPorts();
        Assert.assertEquals(2, inputPorts.size());
        Assert.assertEquals("source_path", inputPorts.get(0).getName());
        Assert.assertEquals("Path to the migration source", inputPorts.get(0).getDescription());
        Assert.assertEquals(ComponentConstants.VALUE_SOURCE_OBJECT, inputPorts.get(0).getValue());

        for (Port p : inputPorts) {
            if (p.isParameterPort()) {
                Assert.assertEquals("compression", p.getName());
                Assert.assertEquals("Imagemagick convert compress parameter", p.getDescription());
                Assert.assertEquals(ComponentConstants.VALUE_PARAMETER, p.getValue());

                List<PredefinedParameter> predefinedParameters = p.getPredefinedParameters();
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

        List<Port> outputPorts = wf.getOutputPorts();
        Assert.assertEquals(2, outputPorts.size());
        Assert.assertEquals("status", outputPorts.get(0).getName());
        Assert.assertEquals("STDOUT and STDERR of the action", outputPorts.get(0).getDescription());
        Assert.assertEquals(ComponentConstants.VALUE_STATUS, outputPorts.get(0).getValue());
        Assert.assertEquals("target_path", outputPorts.get(1).getName());
        Assert.assertEquals("Path to the migration target", outputPorts.get(1).getDescription());
        Assert.assertEquals(ComponentConstants.VALUE_TARGET_OBJECT, outputPorts.get(1).getValue());
    }

    @Test
    public void unmarshallWorkflowDescription_qa() throws JAXBException, URISyntaxException {
        JAXBContext context = JAXBContext.newInstance(WorkflowInfo.class, WorkflowDescription.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("myexperiment/workflow-description-qa.xml");

        WorkflowDescription wf = (WorkflowDescription) unmarshaller.unmarshal(in);
        wf.readMetadata();

        Assert.assertEquals(new URI("http://www.myexperiment.org/workflow.xml?id=4246"), wf.getUri());
        Assert.assertEquals(new URI("http://www.myexperiment.org/workflows/4246/versions/3"), wf.getResource());

        Assert.assertEquals("4246", wf.getId());
        Assert.assertEquals("3", wf.getVersion());
        Assert.assertEquals("Detect similarity in images using peak absolute error", wf.getName());
        Assert.assertEquals("SCAPE QA Object Component", wf.getDescription());
        Assert
            .assertEquals(
                "http://www.myexperiment.org/workflows/4246/download/detect_similarity_in_images_using_peak_absolute_error_725586.t2flow?version=3",
                wf.getContentUri());
        Assert.assertEquals("application/vnd.taverna.t2flow+xml", wf.getContentType());

        Assert.assertNotNull(wf.getType());
        Assert.assertNotNull(wf.getUploader());
        Assert.assertEquals("http://www.myexperiment.org/workflows/4246/versions/3/previews/full", wf.getPreview());
        Assert.assertEquals("http://www.myexperiment.org/workflows/4246/versions/3/previews/svg", wf.getSvg());
        Assert.assertNotNull(wf.getLicenseType());

        Assert.assertEquals(1, wf.getTags().size());
        Assert.assertEquals(0, wf.getRatings().size());

        Assert.assertEquals("http://purl.org/DP/components#QAObjectComparison", wf.getProfile());

        List<AcceptedMimetypes> acceptedMimetypes = wf.getAcceptedMimetypes();
        Assert.assertEquals(1, acceptedMimetypes.size());
        Assert.assertEquals("image/*", acceptedMimetypes.get(0).getLeftMimetype());
        Assert.assertEquals("image/*", acceptedMimetypes.get(0).getRightMimetype());

        List<String> acceptedMimetype = wf.getAcceptedMimetype();
        Assert.assertEquals(1, acceptedMimetype.size());
        Assert.assertEquals("image/tiff", acceptedMimetype.get(0));

        List<Installation> installations = wf.getInstallations();
        Assert.assertEquals(1, installations.size());
        Assert.assertEquals("http://purl.org/DP/components#Debian", installations.get(0).getEnvironment());
        List<Dependency> dependencies = installations.get(0).getDependencies();
        Assert.assertEquals(2, dependencies.size());
        Assert.assertEquals("dcraw", dependencies.get(0).getName());
        Assert.assertEquals("http://opensource.org/licenses/GPL-2.0", dependencies.get(0).getLicense());
        Assert.assertEquals("default-jre", dependencies.get(1).getName());
        Assert.assertEquals("http://opensource.org/licenses/GPL-2.0", dependencies.get(1).getLicense());

        List<Port> inputPorts = wf.getInputPorts();
        Assert.assertEquals(2, inputPorts.size());
        Assert.assertEquals("leftimage", inputPorts.get(0).getName());
        Assert.assertEquals("Path to the left image", inputPorts.get(0).getDescription());
        Assert.assertEquals(ComponentConstants.VALUE_LEFT_OBJECT, inputPorts.get(0).getValue());

        Assert.assertEquals("rightimage", inputPorts.get(1).getName());
        Assert.assertEquals("Path to the right image", inputPorts.get(1).getDescription());
        Assert.assertEquals(ComponentConstants.VALUE_RIGHT_OBJECT, inputPorts.get(1).getValue());

        List<Port> outputPorts = wf.getOutputPorts();
        Assert.assertEquals(1, outputPorts.size());
        Assert.assertEquals("PAE", outputPorts.get(0).getName());
        Assert.assertEquals("http://purl.org/DP/quality/measures#7", outputPorts.get(0).getValue());
    }
}
