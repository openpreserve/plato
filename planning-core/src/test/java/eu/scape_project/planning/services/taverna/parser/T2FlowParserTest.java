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
package eu.scape_project.planning.services.taverna.parser;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

public class T2FlowParserTest {
    @Test
    public void getIdTest() throws Exception {

        // Migration Action
        InputStream t2flow = getClass().getResourceAsStream("/taverna/WorkflowAnnotations.t2flow");
        assertTrue(t2flow != null);

        T2FlowParser t2flowParser = T2FlowParser.createParser(t2flow);

        assertTrue(t2flowParser.getId().equals("e0089782-e811-48c7-b72d-2b84ddfdeb11"));
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
}
