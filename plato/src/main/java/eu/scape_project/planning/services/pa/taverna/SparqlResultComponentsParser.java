/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
 * Department of Software Technology and Interactive Systems, IFS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package eu.scape_project.planning.services.pa.taverna;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.services.action.IActionInfo;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class SparqlResultComponentsParser {

    public void addComponentsFromSparqlResult(List<IActionInfo> components, Reader sparqlResult)
        throws PlatoException {

        SAXReader reader = new SAXReader();
        try {
            Document sparqlDoc = reader.read(sparqlResult);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("sparql", "http://www.w3.org/2005/sparql-results#");

            XPath xpath = DocumentHelper.createXPath("sparql:results/*");
            XPath selectDescriptorXpath = DocumentHelper.createXPath("sparql:binding[@name='w']/sparql:uri");
            XPath selectTitleXpath = DocumentHelper.createXPath("sparql:binding[@name='wt']/sparql:literal");
            XPath selectDescriptionXpath = DocumentHelper.createXPath("sparql:binding[@name='wdesc']/sparql:literal");
            XPath selectUrlXpath = DocumentHelper.createXPath("sparql:binding[@name='wurl']/sparql:uri");
            XPath selectCurrentVersionXpath = DocumentHelper
                .createXPath("sparql:binding[@name='wcurrentversion']/sparql:uri");
            XPath selectCurrentVersionNumberXpath = DocumentHelper
                .createXPath("sparql:binding[@name='wcurrentversionnumber']/sparql:literal");

            xpath.setNamespaceURIs(map);
            selectDescriptorXpath.setNamespaceURIs(map);
            selectTitleXpath.setNamespaceURIs(map);
            selectDescriptionXpath.setNamespaceURIs(map);
            selectUrlXpath.setNamespaceURIs(map);
            selectCurrentVersionXpath.setNamespaceURIs(map);
            selectCurrentVersionNumberXpath.setNamespaceURIs(map);

            @SuppressWarnings("rawtypes")
            List componentsNodes = xpath.selectNodes(sparqlDoc.getRootElement());

            for (int i = 0; i < componentsNodes.size(); i++) {
                Element component = (Element) componentsNodes.get(i);
                TavernaPreservationActionInfo def = new TavernaPreservationActionInfo();

                def.setShortname(selectTitleXpath.selectSingleNode(component).getText());
                // def.setUrl(selectUrlXpath.selectSingleNode(component).getText());
                def.setUrl(selectDescriptorXpath.selectSingleNode(component).getText() + "/download?version="
                    + selectCurrentVersionNumberXpath.selectSingleNode(component).getText());
                def.setInfo(selectDescriptionXpath.selectSingleNode(component).getText());
                def.setDescriptor(selectCurrentVersionXpath.selectSingleNode(component).getText());

                components.add(def);
            }
        } catch (DocumentException e) {
            throw new PlatoException("An error occured while reading the sparql result: {}", e);
        }
    }
}
