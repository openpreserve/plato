/*******************************************************************************
 * Copyright 2006 - 2012 Vienna University of Technology,
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
package eu.scape_project.planning.policies;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

import eu.scape_project.planning.manager.CriteriaManager;
import eu.scape_project.planning.model.RDFPolicy;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.UserGroup;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.policy.ControlPolicy;
import eu.scape_project.planning.model.policy.PreservationCase;

@Stateful
@SessionScoped
public class OrganisationalPolicies implements Serializable {
    private static final long serialVersionUID = 1811189638942547758L;

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private CriteriaManager criteriaManager;

    @Inject
    private User user;

    private String organisation;

    private List<PreservationCase> preservationCases = new ArrayList<PreservationCase>();

    public List<PreservationCase> getPreservationCases() {
        return preservationCases;
    }

    public void setPreservationCases(List<PreservationCase> preservationCases) {
        this.preservationCases = preservationCases;
    }

    public void init() {
        preservationCases.clear();
        RDFPolicy policy = user.getUserGroup().getLatestPolicy();

        if (policy == null) {
            return;
        }

        try {
            resolvePreservationCases(policy.getPolicy());
        } catch (Exception e) {
            log.error("Failed to load policy preservationCases.", e);
        }
    }

    /**
     * Imports a new policy to the users group.
     * 
     * @param input
     *            the policy
     * @throws IOException
     *             if the polify could not be read
     */
    public boolean importPolicy(InputStream input) {
        try {
            String content = IOUtils.toString(input, "UTF-8");
            input.close();

            resolvePreservationCases(content);
            user.getUserGroup().getPolicies().add(new RDFPolicy(content));
            log.info("Imported new policies for user " + user.getUsername());
            return true;
        } catch (Exception e) {
            log.error("Failed to import policies for user " + user.getUsername(), e);
        }
        return false;
    }

    private void resolvePreservationCases(String rdfPolicies) throws Exception {
        preservationCases.clear();
        Model model = ModelFactory.createMemModelMaker().createDefaultModel();
        Reader reader = new StringReader(rdfPolicies);
        model = model.read(reader, null);
        reader.close();

        // String cpModelFile = POLICY_ONTOLOGY_DIR + File.separator +
        // CONTROL_POLICY_FILE;
        Model cpModel = FileManager.get().loadModel("data/vocabulary/control-policy.rdf");
        cpModel.add(FileManager.get().loadModel("data/vocabulary/control-policy_modalities.rdf"));
        cpModel.add(FileManager.get().loadModel("data/vocabulary/control-policy_qualifiers.rdf"));

        model = model.add(cpModel);

        // query organisation from rdf
        String statement =
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX org: <http://www.w3.org/ns/org#> " 
            + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
            + "SELECT ?organisation WHERE { " 
            + "?org rdf:type owl:NamedIndividual ."
            + "?org org:identifier ?organisation } ";

        Query orgQuery = QueryFactory.create(statement, Syntax.syntaxARQ);
        QueryExecution orgQe = QueryExecutionFactory.create(orgQuery, model);
        ResultSet orgResults = orgQe.execSelect();

        try {
            if ((orgResults != null) && (orgResults.hasNext())) {
                QuerySolution orgQs = orgResults.next();
                this.organisation = orgQs.getLiteral("organisation").toString();
            }
        } finally {
            orgQe.close();
        }

        // query all preservationCases
        statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
            + "PREFIX pc: <http://purl.org/DP/preservation-case#> "
            + "SELECT ?preservationcase ?name ?contentset WHERE { "
            + "?preservationcase rdf:type pc:PreservationCase . " 
            + "?preservationcase skos:prefLabel ?name . "
            + "?preservationcase pc:hasContentSet ?contentset } ";

        Query pcQuery = QueryFactory.create(statement, Syntax.syntaxARQ);
        QueryExecution pcQe = QueryExecutionFactory.create(pcQuery, model);
        ResultSet pcResults = pcQe.execSelect();

        try {
            while ((pcResults != null) && (pcResults.hasNext())) {
                QuerySolution pcQs = pcResults.next();
                PreservationCase pc = new PreservationCase();
                pc.setName(pcQs.getLiteral("name").toString());
                pc.setUri(pcQs.getResource("preservationcase").getURI());
                pc.setContentSet(pcQs.getResource("contentset").getURI());
                preservationCases.add(pc);

                // determine user communities
                statement = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                    + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                    + "PREFIX pc: <http://purl.org/DP/preservation-case#> "
                    + "SELECT ?usercommunity WHERE { "
                    + "<" + pc.getUri() + ">" + " pc:hasUserCommunity ?usercommunity } ";

                Query ucQuery = QueryFactory.create(statement, Syntax.syntaxARQ);

                QueryExecution ucQe = QueryExecutionFactory.create(ucQuery, model);
                ResultSet ucResults = ucQe.execSelect();

                try {
                    String ucs = "";
                    while ((ucResults != null) && ucResults.hasNext()) {
                        QuerySolution ucQs = ucResults.next();

                        ucs += "," + ucQs.getResource("usercommunity").getLocalName();
                    }
                    if (StringUtils.isNotEmpty(ucs)) {
                        pc.setUserCommunities(ucs.substring(1));
                    }
                } finally {
                    ucQe.close();
                }

                // query objectives
                statement = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                    + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
                    + "PREFIX pc: <http://purl.org/DP/preservation-case#> "
                    + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
                    + "PREFIX cp: <http://purl.org/DP/control-policy#> "
                    + "SELECT ?objective ?objective_label ?objectiveType ?measure ?modality ?value ?qualifier WHERE { "
                    + "<" + pc.getUri() + ">" + " pc:hasObjective ?objective . " 
                    + "?objective rdf:type ?objectiveType . "
                    + "?objectiveType rdfs:subClassOf cp:Objective . "
                    + "?objective skos:prefLabel ?objective_label . " 
                    + "?objective cp:measure ?measure . "
                    + "?objective cp:value ?value . " + "OPTIONAL {?objective cp:qualifier ?qualifier} . "
                    + "OPTIONAL {?objective cp:modality ?modality} }";

                Query query = QueryFactory.create(statement, Syntax.syntaxARQ);
                QueryExecution qe = QueryExecutionFactory.create(query, model);
                ResultSet results = qe.execSelect();

                try {

                    while ((results != null) && (results.hasNext())) {
                        QuerySolution qs = results.next();
                        ControlPolicy cp = new ControlPolicy();

                        String controlPolicyUri = qs.getResource("objective").getURI();
                        String controlPolicyName = qs.getLiteral("objective_label").toString();
                        String measureUri = qs.getResource("measure").toString();
                        String modality = qs.getResource("modality").getLocalName();
                        String value = qs.getLiteral("value").getString();
                        Resource qualifier = qs.getResource("qualifier");

                        Measure m = criteriaManager.getMeasure(measureUri);

                        cp.setUri(controlPolicyUri);
                        cp.setName(controlPolicyName);
                        cp.setValue(value);
                        cp.setMeasure(m);

                        if (qualifier != null) {
                            cp.setQualifier(ControlPolicy.Qualifier.valueOf(qualifier.getLocalName()));
                        } else {
                            cp.setQualifier(ControlPolicy.Qualifier.EQ);
                        }
                        cp.setModality(ControlPolicy.Modality.valueOf(modality));

                        pc.getControlPolicies().add(cp);
                    }
                } finally {
                    qe.close();
                }
            }
        } finally {
            pcQe.close();
        }
    }

    public PreservationCase getPreservationCase(String preservationCaseUri) {
        if (!StringUtils.isEmpty(preservationCaseUri)) {
            for (PreservationCase s : preservationCases) {
                if (preservationCaseUri.equalsIgnoreCase(s.getUri())) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Clears the policies of the users group.
     */
    public void clearPolicies() {
        user.getUserGroup().getPolicies().clear();
        log.info("Cleared policies of user " + user.getUsername());
        init();
    }

    /**
     * Method responsible for saving the made changes.
     */
    public void save() {
        UserGroup group = user.getUserGroup();

        log.info("size=" + group.getPolicies().size());
        user.setUserGroup(em.merge(group));

        log.info("Policies saved for user " + user.getUsername());
    }

    /**
     * Method responsible for discarding the made changes.
     */
    public void discard() {
        UserGroup oldUserGroup = em.find(UserGroup.class, user.getUserGroup().getId());
        user.setUserGroup(oldUserGroup);

        log.info("Policies discarded for user " + user.getUsername());
    }

    public String getOrganisation() {
        return organisation;
    }
}
