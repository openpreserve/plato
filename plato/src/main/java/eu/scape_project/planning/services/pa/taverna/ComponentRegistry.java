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

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.interfaces.actions.IPreservationActionRegistry;
import eu.scape_project.planning.utils.JGet;

public class ComponentRegistry implements IPreservationActionRegistry {
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final int READ_TIMEOUT = 10 * 1000;

    private static Logger log = Logger.getLogger(ComponentRegistry.class);

    private static final String ME_SPARQL_ENDPOINT = "http://rdf.myexperiment.org/sparql";
    private static final String ENCODING_UTF8 = "UTF-8";

    private List<PreservationActionDefinition> preservationActions;

    @Override
    public void connect(String URL) throws ServiceException, MalformedURLException {

    }

    /**
     * 
     * http://rdf.myexperiment.org/sparql
     * 
     * PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> PREFIX mebase:
     * <http://rdf.myexperiment.org/ontologies/base/> PREFIX rdf:
     * <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX meannot:
     * <http://rdf.myexperiment.org/ontologies/annotations/> PREFIX dcterms:
     * <http://purl.org/dc/terms/> PREFIX mecontrib:
     * <http://rdf.myexperiment.org/ontologies/contributions/> SELECT distinct
     * ?w ?wt ?wdesc WHERE{ ?w a mecontrib:Workflow ; dcterms:title ?wt ;
     * dcterms:description ?wdesc ; meannot:has-tagging ?tscape ;
     * meannot:has-tagging ?tmigration ; meannot:has-tagging ?tcomponent ;
     * meannot:has-tagging ?mimetypeTagging ; mebase:has-current-version
     * ?wcurrentversion . ?tscape meannot:uses-tag
     * <http://www.myexperiment.org/tags/3108> . ?tmigration meannot:uses-tag
     * <http://www.myexperiment.org/tags/2681> . ?tcomponent meannot:uses-tag
     * <http://www.myexperiment.org/tags/3214> . ?wcurrentversion
     * mebase:content-url ?wurl . ?mimetypeTagging meannot:uses-tag ?mimeTypeTag
     * . ?mimeTypeTag dcterms:title ?title FILTER regex(?title,'^image','i') }
     * ORDER BY ?w ?wt
     */
    @Override
    public List<PreservationActionDefinition> getAvailableActions(FormatInfo sourceFormat) throws PlatoException {
        preservationActions = new ArrayList<PreservationActionDefinition>();

        StringBuilder query = new StringBuilder();
        query.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>").append("\n")
            .append("PREFIX dcterms: <http://purl.org/dc/terms/>").append("\n")
            .append("PREFIX meannot: <http://rdf.myexperiment.org/ontologies/annotations/>").append("\n")
            .append("PREFIX mecontrib: <http://rdf.myexperiment.org/ontologies/contributions/>").append("\n")
            .append("PREFIX mebase: <http://rdf.myexperiment.org/ontologies/base/>").append("\n")
            .append("SELECT ?w ?wt ?wdesc ?wurl").append("\n").append("WHERE {").append("\n")
            .append("  ?w a mecontrib:Workflow ;").append("\n").append("     dcterms:title ?wt ;").append("\n")
            .append("     dcterms:description ?wdesc ;").append("\n").append("     meannot:has-tagging ?tscape ;")
            .append("\n").append("     meannot:has-tagging ?tmigration ;").append("\n")
            .append("     meannot:has-tagging ?tcomponent ;").append("\n")
            .append("     meannot:has-tagging ?mimetypeTagging ;").append("\n")
            .append("     mebase:has-current-version ?wcurrentversion .").append("\n")
            .append("  ?tscape meannot:uses-tag <http://www.myexperiment.org/tags/3108> .").append("\n")
            .append("  ?tmigration meannot:uses-tag <http://www.myexperiment.org/tags/2681> .").append("\n")
            .append("  ?tcomponent meannot:uses-tag <http://www.myexperiment.org/tags/3214> .").append("\n")
            .append("  ?wcurrentversion mebase:content-url ?wurl .").append("\n")
            .append("  ?mimetypeTagging meannot:uses-tag ?mimeTypeTag .").append("\n")
            .append("  ?mimeTypeTag dcterms:title ?mimeType ").append("\n")
            .append("  FILTER regex('" + sourceFormat.getMimeType() + "', concat(?mimeType, '*'), 'i')").append("\n")
            .append("}").append("\n").append("ORDER BY ?w ?wt").append("\n");
        try {
            String url = ME_SPARQL_ENDPOINT + "?query=" + URLEncoder.encode(query.toString(), ENCODING_UTF8)
                + "&formatting=XML&reasoning=1";
            String response = JGet.wget(url, CONNECT_TIMEOUT, READ_TIMEOUT);
            log.debug(response);

            new SparqlResultComponentsParser().addComponentsFromSparqlResult(preservationActions, new StringReader(
                response));

            for (PreservationActionDefinition def : preservationActions) {
                def.setActionIdentifier("myExperiment");
            }

            return preservationActions;

        } catch (MalformedURLException e) {
            throw new PlatoException("Component registry is not configured properly.", e);
        } catch (SocketTimeoutException e) {
            throw new PlatoException("Component registry did not respond.", e);
        } catch (IOException e) {
            throw new PlatoException("Failed to retrieve list of components.", e);
        }
    }

    @Override
    public String getLastInfo() {
        return null;
    }

    @Override
    public String getToolIdentifier(String url) {
        return "myExperiment";
    }

    @Override
    public String getToolParameters(String url) {
        return null;
    }

}
