/*******************************************************************************
 * Copyright 2006 - 2014 Vienna University of Technology,
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
package eu.scape_project.planning.xml;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.manager.DigitalObjectManager;
import eu.scape_project.planning.manager.StorageException;
import eu.scape_project.planning.model.CollectionProfile;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.ExecutablePlanDefinition;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.services.taverna.parser.T2FlowParser;
import eu.scape_project.planning.services.taverna.parser.TavernaParserException;
import eu.scape_project.planning.sla.QLDGenerator;
import eu.scape_project.planning.utils.ParserException;

/**
 * Generator for preservation action plans.
 */
public class PreservationActionPlanGenerator implements Serializable {

    private static final long serialVersionUID = 1201408409334201384L;
    
    public static final String FULL_NAME = "PreservationActionPlan.xml";    

    private static Namespace xsi = new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    private static Namespace platoNS = new Namespace("", PlanXMLConstants.PLATO_NS);

    @Inject
    private DigitalObjectManager digitalObjectManager;

    private boolean addDigitalObjectData = true;


    /**
     * Generates a preservation action plan as dom4j document.
     * 
     * @return the preservation action plan
     * @throws PlanningException
     *             if an error occurred during generation
     */
    public Document generatePreservationActionPlanDocument(CollectionProfile collectionProfile, ExecutablePlanDefinition executablePlanDefinition, Plan plan) throws PlanningException {
        Document doc = createPapDoc();
        Element preservationActionPlan = doc.getRootElement();

        // Preservation action plan
        try {
            addPreservationActionPlanData(collectionProfile, preservationActionPlan);
            addPreservationActionPlanT2flow(executablePlanDefinition.getT2flowExecutablePlan(), preservationActionPlan);
            
            
            QLDGenerator qldGen = new QLDGenerator();
            qldGen.generateQLD(plan);
            Element qualityLevelDescription = preservationActionPlan.addElement("qualityLevelDescription");
            Element root = qldGen.getQldNode().getRootElement();
            Element schema = qualityLevelDescription.addElement("schema", "http://purl.oclc.org/dsdl/schematron");
            schema.addAttribute("xmlns", "http://purl.oclc.org/dsdl/schematron");
            schema.add(root.element("pattern").detach());
        } catch (ParserException e) {
            throw new PlanningException("Error parsing collection profile", e);
        } catch (TavernaParserException e) {
            throw new PlanningException("Error parsing executable plan", e);
        }

        return doc;
    }

    /**
     * Creates a document for the preservation action plan.
     * 
     * @return the document
     */
    private Document createPapDoc() {
        Document doc = DocumentHelper.createDocument();

        Element root = doc.addElement(new QName("preservationActionPlan", platoNS));

        root.add(xsi);
        root.add(platoNS);
        root.addAttribute(xsi.getPrefix() + ":schemaLocation", PlanXMLConstants.PLATO_NS + " "
            + PlanXMLConstants.PAP_SCHEMA);

        // Set version of corresponding schema
        root.addAttribute("version", "1.0");

        return doc;
    }

    /**
     * Adds data for the preservation action plan to the parent element.
     * 
     * @param collectionProfile
     *            source of the data
     * @param parent
     *            the parent element of the element to create
     * @return the newly created element or null if none was created
     * @throws ParserException
     *             if the collection profile could not be parsed
     * @throws StorageException
     *             if the data could not be loaded
     */
    private Element addPreservationActionPlanData(CollectionProfile collectionProfile, Element parent)
        throws ParserException, StorageException {

        Element collection = null;
        if (collectionProfile != null) {
            collection = parent.addElement("collection");
            collection.addAttribute("uid", collectionProfile.getCollectionID());
            // TODO: Collection has no name
            // collection.addAttribute("name",
            // p.getSampleRecordsDefinition().getCollectionProfile().getCollectionID());

            // Objects
            Element objects = parent.addElement("objects");

            DigitalObject profileObject = collectionProfile.getProfile();
            try {
                if (profileObject != null && profileObject.isDataExistent()) {

                    if (!addDigitalObjectData) {
                        objects.setText(String.valueOf(profileObject.getId()));
                    } else {

                        if (profileObject.getData().getData() == null || profileObject.getData().getData().length == 0) {
                            profileObject = digitalObjectManager.getCopyOfDataFilledDigitalObject(profileObject);
                        }

                        C3POProfileParser parser = new C3POProfileParser();
                        parser.read(new ByteArrayInputStream(profileObject.getData().getData()), false);

                        List<String> objectIdentifiers = parser.getObjectIdentifiers();

                        for (String objectIdentifier : objectIdentifiers) {
                            objects.addElement("object").addAttribute("uid", objectIdentifier);
                        }
                    }
                }
            } finally {
                profileObject = null;
            }
        }
        return collection;
    }

    /**
     * Adds an executable plan t2flow to the parent.
     * 
     * @param t2flow
     *            the t2flow to add
     * @param parent
     *            the parent element of the element to create
     * @return the newly created element or null if none was created
     * @throws TavernaParserException
     *             if the workflow could not be parsed
     * @throws StorageException
     *             if the data could not be loaded
     */
    private Element addPreservationActionPlanT2flow(DigitalObject t2flow, Element parent)
        throws TavernaParserException, StorageException {
        Element executablePlan = null;

        if (t2flow != null && t2flow.isDataExistent()) {

            executablePlan = parent.addElement("executablePlan").addAttribute("type", "t2flow");

            if (!addDigitalObjectData) {
                // Add only DigitalObject ID, it can be replaced later
                executablePlan.setText(String.valueOf(t2flow.getId()));
            } else {
                DigitalObject t2flowObject = t2flow;
                try {
                    if (t2flowObject.getData().getData() == null || t2flowObject.getData().getData().length == 0) {
                        t2flowObject = digitalObjectManager.getCopyOfDataFilledDigitalObject(t2flowObject);
                    }
                    T2FlowParser parser = T2FlowParser.createParser(new ByteArrayInputStream(t2flowObject.getData()
                        .getData()));
                    Document doc = parser.getDoc();
                    executablePlan.add(doc.getRootElement());
                } finally {
                    t2flowObject = null;
                }
            }
        }
        return executablePlan;
    }
}
