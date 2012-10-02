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
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.AlternativesDefinition;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PlanType;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.transform.Transformer;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.utils.FacesMessages;

/**
 * stateful session bean for managing plans
 */
@Stateful
@SessionScoped
@Named("planManager")
public class PlanManager implements Serializable {
    private static final long serialVersionUID = -1L;

    public enum WhichProjects {
        ALLPROJECTS, ALLFTEPROJECTS, PUBLICPROJECTS, MYPROJECTS, FTEPROJECTS, PUBLICFTEPROJECTS;
    }

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    private WhichProjects lastLoadMode = WhichProjects.MYPROJECTS;

    @Inject
    private User user;

    @Inject
    private FacesMessages facesMessages;

    public PlanManager() {
    }

    @Remove
    public void destroy() {
    }

    public WhichProjects getLastLoadMode() {
        return lastLoadMode;
    }

    public void setLastLoadMode(WhichProjects lastLoadMode) {
        this.lastLoadMode = lastLoadMode;
    }

    // FIXME @Observer("projectListChanged")
    public List<PlanProperties> relist() {
        List<PlanProperties> planList = list(lastLoadMode);
        log.debug("reloading  in " + lastLoadMode + ": number of projects loaded: " + planList.size());
        return planList;
    }

    /**
     * Furthermore, checks if project is locked by the current user, who may
     * thus be allowed to unlock the project. In this case the
     * {@link PlanProperties#isAllowReload()} is set. In the user interface this
     * flag means that an 'Unlock' button is displayed.
     */
    public List<PlanProperties> list(WhichProjects whichProjects) {

        Query query = null;

        if (whichProjects == WhichProjects.MYPROJECTS) {
            // load user's projects
            query = em.createQuery("select p.planProperties from Plan p where"
                + " (p.planProperties.owner = :owner)"
                + " and ((p.projectBasis.identificationCode) = null or (p.planProperties.planType = :planType) )"
                + " order by p.planProperties.id");
            
            query.setParameter("owner", user.getUsername());
            query.setParameter("planType", PlanType.FULL);
        } else if ((whichProjects == WhichProjects.ALLPROJECTS || whichProjects == WhichProjects.ALLFTEPROJECTS) && (user.isAdmin())) {
            // load all projects, public and private,
            // but ONLY if the user is an admin
            query = em.createQuery("select p from PlanProperties p where (p.planType = :planType) order by p.id");
            if (whichProjects == WhichProjects.ALLFTEPROJECTS) {
                query.setParameter("planType", PlanType.FTE);
            } else {
                query.setParameter("planType", PlanType.FULL);
            }
        } else if (whichProjects == WhichProjects.FTEPROJECTS) {
            query = em.createQuery("select p.planProperties from Plan p where" + " (p.planProperties.owner = :owner) "
                + " and (p.planProperties.planType = :planType)"
                + " order by p.planProperties.id");
            query.setParameter("owner", user.getUsername());
            query.setParameter("planType", PlanType.FTE);
        } else if (whichProjects == WhichProjects.PUBLICFTEPROJECTS) {

            query = em.createQuery("select p.planProperties from Plan p where"
                + " (p.planProperties.privateProject = false )"
                + " and (p.planProperties.planType = :planType)" 
                + " order by p.planProperties.id");
            query.setParameter("planType", PlanType.FTE);
        } else {
            // load all public projects, which includes those with published
            // reports
            query = em.createQuery("select p.planProperties from Plan p where ((p.planProperties.privateProject = false)"
                + " or (p.planProperties.reportPublic = true)) " 
                + " and (p.planProperties.planType = :planType) "
                + " order by p.planProperties.id");
            query.setParameter("planType", PlanType.FULL);
        }

        @SuppressWarnings("unchecked")
        List<PlanProperties> planList = (List<PlanProperties>)query.getResultList();

        //
        // readOnly in PlanProperties is *transient*, it is used
        // to determine if a user is allowed to load a project
        //
        for (PlanProperties pp : planList) {

            //
            // a project may NOT be loaded when
            // ... it is set to private
            // ... AND the user currently logged in is not the administrator
            // ... AND the user currently logged in is not the owner of that
            // project
            boolean readOnly = pp.isPrivateProject() && !user.isAdmin() && !user.getUsername().equals(pp.getOwner());

            boolean allowReload = pp.getOpenedByUser().equals(user.getUsername()) || user.isAdmin();

            pp.setReadOnly(readOnly);
            pp.setAllowReload(allowReload);
        }
        setLastLoadMode(whichProjects);
        return planList;
    }

    public Plan reloadPlan(Plan plan) throws PlanningException {
        Query q = em
            .createQuery("select count(pp.id) from  PlanProperties pp where (pp.openHandle = 1) and (pp.openedByUser = :user) and (pp.id = :propid)");
        q.setParameter("user", user.getUsername());
        q.setParameter("propid", plan.getPlanProperties().getId());
        Object result = q.getSingleResult();
        long num = 0;
        if (result != null) {
            num = ((Long) result).longValue();
        }
        if (num < 1) {
            throw new PlanningException("This plan has not been loaded before, reload is not possible.");
        }

        Plan reloadedPlan = em.find(Plan.class, plan.getId());

        this.initializePlan(reloadedPlan);
        log.info("Plan " + reloadedPlan.getPlanProperties().getName() + " reloaded!");
        return reloadedPlan;

    }
    
    /**
     * Loads the plan with the given plan-Id from the database.
     * - without locking the plan!
     * 
     * @param planId
     * @return
     */
    public Plan loadPlan(int planId) {
        Plan plan = em.find(Plan.class, planId);

        this.initializePlan(plan);
        log.info("Plan " + plan.getPlanProperties().getName() + " loaded!");
        return plan;
    }

    /**
     * Loads the given plan from the database and locks it.
     * 
     * @param propertyId
     *            the plan's PROPERTIES id!
     */
    public Plan load(int propertyId) throws PlanningException {
        // try to lock the project

        Query q = em
            .createQuery("update PlanProperties pp set pp.openHandle = 1, pp.openedByUser = :user where (pp.openHandle is null or pp.openHandle = 0) and pp.id = :propid");
        q.setParameter("user", user.getUsername());
        q.setParameter("propid", propertyId);
        int num = q.executeUpdate();
        if (num < 1) {
            throw new PlanningException(
                "In the meantime the plan has been loaded by an other user. Please choose another plan.");
        }
        Object result = em.createQuery("select p.id from Plan p where p.planProperties.id = " + propertyId)
            .getSingleResult();
        if (result != null) {
            return loadPlan((Integer)result);
        } else {
            throw new PlanningException("An unexpected error has occured while loading the plan.");
        }
    }

    public void store(Plan plan) throws PlanningException {
        em.persist(em.merge(plan));
    }

    /**
     * Hibernate initializes project and its parts.
     */
    private void initializePlan(Plan p) {
        Hibernate.initialize(p);
        Hibernate.initialize(p.getAlternativesDefinition());
        Hibernate.initialize(p.getSampleRecordsDefinition());
        Hibernate.initialize(p.getTree());
        initializeNodeRec(p.getTree().getRoot());
        log.debug("plan initialised");
    }

    /**
     * Traverses down the nodes in the tree and calls
     * <code>Hibernate.initialize</code> for each leaf. This is necessary to
     * provide the application with a convenient way of working with lazily
     * initialized collections or proxies.
     * 
     * @param node
     *            node from where initialization shall start
     */
    private void initializeNodeRec(TreeNode node) {

        Hibernate.initialize(node);
        if (node.isLeaf()) {
            Leaf leaf = (Leaf) node;
            Transformer t = leaf.getTransformer();
            Hibernate.initialize(t);
            if (t instanceof OrdinalTransformer) {
                OrdinalTransformer nt = (OrdinalTransformer) t;
                Hibernate.initialize(nt.getMapping());
            }
            // log.debug("hibernate initialising Transformer: " +
            // leaf.getTransformer());
            for (Values value : leaf.getValueMap().values()) {
                Hibernate.initialize(value);
            }
        } else if (node instanceof Node) {
            Node recnode = (Node) node;
            Hibernate.initialize(node.getChildren());
            for (TreeNode newNode : recnode.getChildren()) {
                initializeNodeRec(newNode);
            }
        }
    }

    /**
     * Unlocks all projects in database.
     */
    public void unlockAll() {
        this.unlockQuery(-1);
    }

    /**
     * Unlocks certain projects in database (dependent on parameter)
     * 
     * @param useId
     *            If this is true, only project with id
     *            {@link #planPropertiesId} will be unlocked; otherwise, all
     *            projects in database will be unlocked
     */
    private void unlockQuery(long pid) {

        String where = "";
        if (pid > -1) {
            where = "where pp.id = " + pid;
        }

        Query q = em.createQuery("update PlanProperties pp set pp.openHandle = 0, pp.openedByUser = '' " + where);
        try {
            if (q.executeUpdate() < 1) {
                log.debug("Unlocking plan failed.");
            } else {
                log.debug("Unlocked plan");
            }
        } catch (Throwable e) {
            log.error("Unlocking plan failed:", e);
        }

        pid = 0;
    }

    public void unlockPlan(int planPropertiesId) {
        unlockQuery(planPropertiesId);
    }

    /**
     * Saves a certain entity of the preservation planning project and updates
     * the project state.
     * 
     * @param entity
     *            Entity that shall be saved.
     */
    public void save(Plan plan, PlanState currentState, Object entity) {

        if (log.isDebugEnabled()) {
            log.debug("Persisting entity " + entity.getClass().getName());
        }

        /** dont forget to prepare changed entities e.g. set current user */
        // PrepareChangesForPersist prep = new
        // PrepareChangesForPersist(user.getUsername());

        /** firstly, we set the project state to requiredPlanState */
        // prep.prepare(selectedPlan.getState());
        plan.getPlanProperties().setState(currentState);

        if (plan.getPlanProperties().getReportUpload().isDataExistent()) {
            plan.getPlanProperties().setReportUpload(new DigitalObject());

            String msg = "Please consider that because data underlying the preservation plan has been changed, the uploaded report was automatically removed. ";
            msg += "If you would like to make the updated report available, please generate it again and upload it in 'Plan Settings'.";
            facesMessages.addInfo(msg);
        }

        PlanProperties planProperties = em.merge(plan.getPlanProperties());
        em.persist(planProperties);
        plan.setPlanProperties(planProperties);

        /** secondly, we save the intended entity */
        // prep.prepare(entity);
        em.persist(em.merge(entity));
        // //em.flush();
    }

    // --------------- save operations for steps ---------------

    public void saveForPlanSettings(PlanProperties planProperties, AlternativesDefinition alternativesDefinition) {
        em.persist(em.merge(planProperties));
        em.persist(em.merge(alternativesDefinition));
    }

    // --------------- delete a plan from database ---------------

    /**
     * Method responsible for deleting a plan from database.
     * 
     * @param plan
     *            Plan to delete.
     */
    public void deletePlan(Plan plan) {
        log.info("Deleting plan with id " + plan.getId());
        em.remove(em.merge(plan));
    }
}
