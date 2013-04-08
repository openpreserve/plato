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
package eu.scape_project.planning.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remove;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.EvaluationScope;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.FloatScale;
import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.scales.RestrictedScale;
import eu.scape_project.planning.model.scales.Scale;

/**
 * For administration of metrics, measurable properties and criteria This should
 * be the interface to a Measurement Property Registry (MPR) - the registry
 * should be queried for all measurement entities - this would prevent entities
 * being overwritten by accident, and ease notification on changed entities -
 * changes to already known entities should trigger events for preservation
 * watch
 * 
 * @author kraxner
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Startup
@Named("criteriaManager")
public class CriteriaManager implements Serializable {
    private static final long serialVersionUID = -2305838596050068452L;

    public static final String MEASURES_FILE = "data/vocabulary/quality_measures.rdf";
    public static final String ATTRIBUTES_FILE = "data/vocabulary/quality_attributes.rdf";
    public static final String CATEGORIES_FILE = "data/vocabulary/quality_categories.rdf";

    private Logger log = LoggerFactory.getLogger(CriteriaManager.class);

    private Model model;

    public CriteriaManager() {
        model = ModelFactory.createMemModelMaker().createDefaultModel();
    }

    /**
     * cache for looking up CriterionCategories by their uri
     */
    private Map<String, CriterionCategory> knownCategories = new HashMap<String, CriterionCategory>();

    /**
     * cache for lookup of all currently known measures by their uri
     * 
     */
    private Map<String, Measure> knownMeasures = new HashMap<String, Measure>();

    /**
     * cache for lookup of all currently known attributes by their uri
     */
    private Map<String, Attribute> knownAttributes = new HashMap<String, Attribute>();

    /**
     * Returns a list of all known categories IMPORTANT: this list can not (and
     * must not) be altered!
     * 
     * @return
     */
    public Collection<CriterionCategory> getAllCriterionCategories() {
        return Collections.unmodifiableCollection(knownCategories.values());
    }

    /**
     * Returns a list of all known criteria IMPORTANT: this list can not (and
     * must not) be altered!
     * 
     * @return
     */
    @Lock(LockType.READ)
    public Collection<Measure> getAllMeasures() {
        return Collections.unmodifiableCollection(knownMeasures.values());
    }

    /**
     * returns a list of all known properties IMPORTANT: this list can not (and
     * must not) be altered!
     * 
     * @return
     */
    @Lock(LockType.READ)
    public Collection<Attribute> getAllAttributes() {
        return Collections.unmodifiableCollection(knownAttributes.values());
    }

    /**
     * Returns the criterion for the given criterionUri
     * 
     * @param uri
     * @return
     */
    @Lock(LockType.READ)
    public Measure getMeasure(String measureUri) {
        return knownMeasures.get(measureUri);
    }

    @Lock(LockType.READ)
    public Attribute getAttribute(String attributeUri) {
        return knownAttributes.get(attributeUri);
    }

    @Lock(LockType.READ)
    public List<String> getCategoryHierachy(String measureUri) {
        List<String> hierarchy = new ArrayList<String>();

        Measure m = knownMeasures.get(measureUri);

        if (m == null) {
            return hierarchy;
        }

        Attribute a = knownAttributes.get(m.getAttribute().getUri());

        if (a == null) {
            return hierarchy;
        }

        hierarchy.add(0, a.getName());

        CriterionCategory criterionCategory = a.getCategory();

        if (criterionCategory == null) {
            return hierarchy;
        }

        hierarchy.add(0, criterionCategory.getName());

        return hierarchy;
    }

    private void resolveCriterionCategories() {
        knownCategories.clear();
        
        String statement = "SELECT ?c ?cn ?scope WHERE { " + "?c rdf:type quality:CriterionCategory . "
            + "?c skos:prefLabel ?cn . " + "?c quality:scope ?scope }";
        String commonNS = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
            + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#> "
            + "PREFIX quality: <http://purl.org/DP/quality#>";

        Query query = QueryFactory.create(commonNS + statement, Syntax.syntaxARQ);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while ((results != null) && (results.hasNext())) {
            QuerySolution qs = results.next();
            String categoryId = qs.getResource("c").toString();
            String name = qs.getLiteral("cn").getString();
            String scopeStr = qs.getResource("scope").getLocalName();

            EvaluationScope scope = null;

            if ("OBJECT".equals(scopeStr)) {
                scope = EvaluationScope.OBJECT;
            } else {
                scope = EvaluationScope.ALTERNATIVE_ACTION;
            }
            if (scope != null) {
                CriterionCategory category = new CriterionCategory(categoryId, name, scope);
                knownCategories.put(categoryId, category);
            } else {
                log.warn("CriterionCategory without defined scope: " + categoryId + ", " + name);
            }
        }
    }

    private void resolveAttributes() {
        knownAttributes.clear();
        
        String statement = "SELECT ?a ?an ?ad ?ac WHERE { " + "?a rdf:type quality:Attribute . " + "?a skos:prefLabel ?an . "
            + "?a dct:description ?ad . " + "?a quality:criterionCategory ?ac }";

        String commonNS = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
            + "PREFIX dct:<http://purl.org/dc/terms/> "
            + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#> "
            + "PREFIX quality: <http://purl.org/DP/quality#>";

        Query query = QueryFactory.create(commonNS + statement, Syntax.syntaxARQ);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while ((results != null) && (results.hasNext())) {
            QuerySolution qs = results.next();

            Attribute a = new Attribute();

            a.setDescription(qs.getLiteral("ad").getString());
            a.setName(qs.getLiteral("an").getString());
            a.setUri(qs.getResource("a").toString());
            String categoryUri = qs.getResource("ac").toString();
            a.setCategory(knownCategories.get(categoryUri));

            knownAttributes.put(a.getUri(), a);
        }

    }

    private void resolveMeasures() {
        knownMeasures.clear();
        
        String statement = "SELECT ?m ?mn ?md ?a ?s ?r WHERE { " + "?m rdf:type quality:Measure . "
            + "?m quality:attribute ?a . " + "?m skos:prefLabel ?mn . " + "?m dct:description ?md . " + "?m quality:scale ?s . "
            + "optional{?m quality:restriction ?r} }";

        String commonNS = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
            + "PREFIX dct:<http://purl.org/dc/terms/> "
            + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#> "
            + "PREFIX quality: <http://purl.org/DP/quality#>";

        Query query = QueryFactory.create(commonNS + statement, Syntax.syntaxARQ);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        while ((results != null) && (results.hasNext())) {
            QuerySolution qs = results.next();

            Resource attribute = qs.getResource("a");

            String attributeUri = attribute.toString();

            Measure m = new Measure();

            m.setUri(qs.getResource("m").toString());
            m.setName(qs.getLiteral("mn").getString());
            m.setDescription(qs.getLiteral("md").getString());

            Scale s = createScale(qs.getResource("s").getLocalName());
            m.setScale(s);

            if ((s instanceof RestrictedScale) && (qs.contains("r"))) {
                String restriction = qs.getLiteral("r").getString();
                ((RestrictedScale) s).setRestriction(restriction);
            }

            Attribute a = knownAttributes.get(attributeUri);

            m.setAttribute(a);
            
            if ((a != null) && (s != null)) {
                // only add completely defined measures
                knownMeasures.put(m.getUri(), m);
            }
        }
    }

    private Scale createScale(String scaleName) {

        if ("Boolean".equalsIgnoreCase(scaleName)) {
            return new BooleanScale();
        } else if ("FreeText".equalsIgnoreCase(scaleName)) {
            return new FreeStringScale();
        } else if ("Number".equalsIgnoreCase(scaleName)) {
            return new FloatScale();
        } else if ("PositiveNumber".equalsIgnoreCase(scaleName)) {
            return new PositiveFloatScale();
        } else if ("PositiveInteger".equalsIgnoreCase(scaleName)) {
            return new PositiveIntegerScale();
        } else if ("Ordinal".equalsIgnoreCase(scaleName)) {
            return new OrdinalScale();
        }

        return null;
    }

    /**
     * FIXME: reload from RDF
     * 
     * Reads the XML file from {@link #DESCRIPTOR_FILE} and adds the contained
     * criteria to the database. For criteria that already exist in the database
     * (as designated by URI), the information is updated.
     * 
     * @see eu.scape_project.planning.application.ICriteriaManager#reload()
     *      ATTENTION: From all available CRUD operation only CReate and Update
     *      are covered. Delete operations are not executed. Thus, if you have
     *      deleted Properties in your XML they are not deleted in database as
     *      well.
     */
    @Lock(LockType.WRITE)
    public void reload() {
        model = FileManager.get().loadModel(CATEGORIES_FILE);
        model.add(FileManager.get().loadModel(ATTRIBUTES_FILE));
        model.add(FileManager.get().loadModel(MEASURES_FILE));
        

        resolveCriterionCategories();
        resolveAttributes();
        resolveMeasures();
    }

    @PostConstruct
    public void init() {
        if (knownMeasures.isEmpty()) {
            reload();
        }

    }

    @Remove
    public void destroy() {
    }

}
