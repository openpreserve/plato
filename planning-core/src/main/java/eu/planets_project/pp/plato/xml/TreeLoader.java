/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/

package eu.planets_project.pp.plato.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.planets_project.pp.plato.xml.freemind.MindMap;
import eu.scape_project.planning.model.kbrowser.CriteriaHierarchy;
import eu.scape_project.planning.model.tree.ObjectiveTree;
import eu.scape_project.planning.model.tree.PolicyTree;
import eu.scape_project.pw.planning.manager.CriteriaManager;
import eu.scape_project.pw.planning.xml.SchemaResolver;
import eu.scape_project.pw.planning.xml.StrictErrorHandler;
import eu.scape_project.pw.planning.xml.ValidatingParserFactory;

/**
 * This class uses the Jakarta Commons Digester to load an objective tree from
 * an xml file. At the moment we support the DELOS Testbed file format and
 * FreeMind mindmaps.
 */
public class TreeLoader implements Serializable {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(TreeLoader.class);

    private ValidatingParserFactory validatingParserFactory = new ValidatingParserFactory();

    public TreeLoader() {

    }

    private MindMap loadFreeMindMap(InputStream in) {
        try {
            MindMap map = new MindMap();

            SAXParser parser = validatingParserFactory.getValidatingParser();
            parser.setProperty(ValidatingParserFactory.JAXP_SCHEMA_SOURCE,
                "http://freemind.sourceforge.net/freemind.xsd");
            // load content into temporary structure
            Digester digester = new Digester(parser);
            digester.setEntityResolver(new SchemaResolver().addSchemaLocation(
                "http://freemind.sourceforge.net/freemind.xsd", "data/schemas/freemind.xsd"));
            digester.setErrorHandler(new StrictErrorHandler());

            digester.push(map);

            digester.addObjectCreate("*/node", "eu.scape_project.planning.xml.freemind.Node");
            digester.addSetProperties("*/node");
            digester.addCallMethod("*/node/hook/text", "setDESCRIPTION", 0);
            digester.addSetNext("*/node", "addChild");

            digester.setUseContextClassLoader(true);
            digester.parse(in);
            return map;
        } catch (IOException e) {
            log.error("Error loading Freemind file.", e);
        } catch (SAXException e) {
            log.error("Document is not a valid Freemind file.", e);
        } catch (ParserConfigurationException e) {
            log.error("Parser not properly configured.", e);
        }
        return null;
    }

    /**
     * This method imports a FreeMind MindMap defined in an XML file into the
     * objective tree structure defined by Plato.
     * 
     * @param in
     *            an InputStream which contains a FreeMind XML mindmap
     * @return {@link ObjectiveTree} created from the xml file, or
     *         <code>null</code> if there was an error.
     * @param hasUnits
     *            If this is set to <code>true</code>, we assume that every leaf
     *            node is a measurement unit, and the objective tree leaves are
     *            one level higher. So we stop one level earlier, the units are
     *            not imported at the moment.
     * @throws URISyntaxException
     */
    public ObjectiveTree loadFreeMindObjectiveTree(InputStream in, boolean hasUnits, boolean hasLeaves) {
        MindMap map = loadFreeMindMap(in);
        if (map != null) {
            // traverse temp structure of map and nodes and create ObjectiveTree
            ObjectiveTree tree = new ObjectiveTree();
            tree.setRoot(map.getObjectiveTreeRoot(hasUnits, hasLeaves));
            if (tree.getRoot().isLeaf()) {
                return null;
            }
            return tree;
        }
        return null;
    }

    /**
     * Loads a PolicyTree serialized as freemind map
     * 
     * @param in
     * @return
     */
    public PolicyTree loadFreeMindPolicyTree(InputStream in) {
        MindMap map = loadFreeMindMap(in);
        if (map != null) {
            PolicyTree tree = new PolicyTree();
            tree.setRoot(map.getPolicyTreeRoot());
            return tree;
        }
        return null;
    }

    /**
     * Loads a criteria hierarchy serialized as freemind map. - the resulting
     * criteria hierarchy consists of criteria from the given criteriaManager.
     * 
     * @param in
     * @param criteriaManager
     * @return
     */
    public CriteriaHierarchy loadFreeMindCriteriaHierarchy(InputStream in, CriteriaManager criteriaManager) {
        MindMap map = loadFreeMindMap(in);
        if (map != null) {
            CriteriaHierarchy criteriaHierarchy = map.getRepresentingCriteriaHierarchy(criteriaManager);
            return criteriaHierarchy;
        }
        return null;
    }
}
