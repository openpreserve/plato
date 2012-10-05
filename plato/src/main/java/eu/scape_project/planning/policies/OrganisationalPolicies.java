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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
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
import eu.scape_project.planning.model.policy.Scenario;

@Stateful
@SessionScoped
public class OrganisationalPolicies implements Serializable {
	private static final long serialVersionUID = 1811189638942547758L;
	
	private static final String POLICY_ONTOLOGY_DIR = "data/policy-ontology";
	private static final String CONTROL_POLICY_FILE = "control-policy.rdf";

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;
	
    @Inject
    private CriteriaManager criteriaManager;

	@Inject
	private User user;
	
	private List<Scenario> scenarios = new ArrayList<Scenario>();
	
	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	
	public void init() {
		RDFPolicy policy = user.getUserGroup().getLatestPolicy();
		
		if (policy == null) {
			return;
		}
		
		resolveScenarios(policy.getPolicy());
	}

	/**
	 * Imports a new policy to the users group.
	 * 
	 * @param input
	 *            the policy
	 * @throws IOException
	 *             if the polify could not be read
	 */
	public void importPolicy(InputStream input) throws IOException {
		
		scenarios = new ArrayList<Scenario>(); 
				
		String content = IOUtils.toString(input, "UTF-8");
		input.close();

		RDFPolicy policy = new RDFPolicy();
		policy.setPolicy(content);

		user.getUserGroup().getPolicies().add(policy);
		
		resolveScenarios(content);

		log.info("Imported new policies for user " + user.getUsername());
	}
	
	private void resolveScenarios (String rdfPolicies) {
		
		scenarios.clear();
		
		Model model = ModelFactory.createMemModelMaker().createDefaultModel();
		
		InputStream in = new ByteArrayInputStream(rdfPolicies.getBytes());
		model = model.read(in, null);
		
		try {
			in.close();
		} catch (IOException e) {
		}
		
		String cpModelFile = POLICY_ONTOLOGY_DIR + File.separator + CONTROL_POLICY_FILE;
		Model cpModel = FileManager.get().loadModel(cpModelFile);
		
		model = model.union(cpModel);
		
		String statement = "SELECT ?scenario ?scenario_name ?objective ?objectiveType ?measure ?modality ?value ?qualifier WHERE { " + 
			"?scenario rdf:type pw:Scenario . " +
			"?scenario rdfs:label ?scenario_name . " +
			"?scenario pw:hasObjective ?objective . " +
			"?objective rdf:type ?objectiveType . " +
			"?objectiveType rdfs:subClassOf cp:Objective . " +
			"?objective cp:property ?measure . " +
			"?objective cp:value ?value . " +
			"OPTIONAL {?objective cp:qualifier ?qualifier} . " + 
			"OPTIONAL {?objective cp:modality ?modality} . " +
			"}";		
		        
		String commonNS = 
		 "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		 "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> " +
		 "PREFIX pw: <http://scape-project.eu/pw/vocab/>  " + 
		 "PREFIX cp: <http://scape-project.eu/pw/vocab/control-policy#> ";

		Query query = QueryFactory.create(commonNS + statement, Syntax.syntaxARQ);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		
		try {

			while ((results != null) && (results.hasNext())) {
				QuerySolution qs = results.next();

				String controlPolicyUri = qs.getResource("objective").getURI();
				String scenarioUri = qs.getResource("scenario").getURI();
				String scenarioName = qs.getLiteral("scenario_name").toString();
				String measureUri = qs.getResource("measure").toString();
				String modality = qs.getResource("modality").getLocalName();
				String value = qs.getLiteral("value").getString();
				Resource qualifier = qs.getResource("qualifier");

				Scenario s = getScenario(scenarioUri);

				if (s == null) {
					s = new Scenario();

					s.setName(scenarioName);
					s.setUri(scenarioUri);

					scenarios.add(s);
				}

				ControlPolicy cp = new ControlPolicy();

				Measure m = criteriaManager.getMeasure(measureUri);

				cp.setUri(controlPolicyUri);
				cp.setValue(value);
				cp.setMeasure(m);

				if (qualifier != null) {

					if (qualifier.getLocalName().equalsIgnoreCase("GT")) {
						cp.setQualifier(ControlPolicy.Qualifier.GT);
					} else if (qualifier.getLocalName().equalsIgnoreCase("LT")) {
						cp.setQualifier(ControlPolicy.Qualifier.LT);
					} if (qualifier.getLocalName().equalsIgnoreCase("LE")) {
						cp.setQualifier(ControlPolicy.Qualifier.LE);
					} if (qualifier.getLocalName().equalsIgnoreCase("GE")) {
						cp.setQualifier(ControlPolicy.Qualifier.GE);
					} if (qualifier.getLocalName().equalsIgnoreCase("EQ")) {
						cp.setQualifier(ControlPolicy.Qualifier.EQ);
					}
				} else {
					cp.setQualifier(ControlPolicy.Qualifier.EQ);
				}

				if (modality.equalsIgnoreCase("MUST")) {
					cp.setModality(ControlPolicy.Modality.MUST);
				} else if (modality.equalsIgnoreCase("SHOULD")) {
					cp.setModality(ControlPolicy.Modality.SHOULD);
				}

				s.getControlPolicies().add(cp);

			}
		} catch (Exception e) {

		} finally {
			qe.close();
		}
	}
	
	public Scenario getScenario(String scenarioUri) {
		
		for (Scenario s : scenarios) {
			if (scenarioUri.equalsIgnoreCase(s.getUri())) {
				return s;
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
		UserGroup oldUserGroup = em.find(UserGroup.class, user.getUserGroup()
				.getId());
		user.setUserGroup(oldUserGroup);

		log.info("Policies discarded for user " + user.getUsername());
	}
}
