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
package eu.scape_project.planning.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.Hibernate;
import org.slf4j.Logger;

import eu.scape_project.planning.exception.PlanningException;
import eu.scape_project.planning.model.AlternativesDefinition;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.User;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.transform.Transformer;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.ObjectiveTree;
import eu.scape_project.planning.model.tree.TreeNode;
import eu.scape_project.planning.utils.FacesMessages;

/**
 * Stateful session bean for managing plans.
 */
@Stateful
@SessionScoped
@Named("planManager")
public class PlanManager implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * Selection of projects to query.
     */
    public enum WhichProjects {
        ALLPROJECTS,
        PUBLICPROJECTS,
        MYPROJECTS;
    }

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private ByteStreamManager bytestreamManager;

    private WhichProjects lastLoadMode = WhichProjects.MYPROJECTS;

    @Inject
    private User user;

    @Inject
    private FacesMessages facesMessages;

    /**
     * Plan properties of all loaded plans in this session.
     */
    private HashSet<Integer> sessionPlans;

    /**
     * Query to get PlanProperties.
     */
    public class PlanQuery {

        private CriteriaBuilder builder;

        private CriteriaQuery<PlanProperties> cq;

        private Root<Plan> fromPlan;

        private Path<PlanProperties> fromPP;

        private List<Predicate> visibilityPredicates;
        private List<Predicate> stateFilterPredicates;
        private List<Predicate> nameFilterPredicates;
        private Predicate mappedFilterPredicate;

        private Predicate playgroundFilterPredicate;

        /**
         * Initializes the query.
         */
        private void init() {
            builder = em.getCriteriaBuilder();

            cq = builder.createQuery(PlanProperties.class);

            // From
            fromPlan = cq.from(Plan.class);
            fromPP = fromPlan.<PlanProperties> get("planProperties");

            // Select
            cq.select(fromPlan.<PlanProperties> get("planProperties"));

            visibilityPredicates = new ArrayList<Predicate>(2);
            stateFilterPredicates = new ArrayList<Predicate>();
            nameFilterPredicates = new ArrayList<Predicate>();
        }

        /**
         * Adds a visibility criteria to the query.
         * 
         * @param whichProjects
         *            criteria to add
         * @return this plan query
         */
        public PlanQuery addVisibility(WhichProjects whichProjects) {
            // Select usernames of the users group
            if (whichProjects == WhichProjects.MYPROJECTS) {
                Subquery<User> subquery = cq.subquery(User.class);
                Root<User> fromUser = subquery.from(User.class);
                subquery.select(fromUser.<User> get("username"));
                subquery.where(builder.equal(fromUser.get("userGroup"), user.getUserGroup()));

                visibilityPredicates.add(fromPP.get("owner").in(subquery));
            } else if (whichProjects == WhichProjects.PUBLICPROJECTS) {
                visibilityPredicates.add(builder.or(builder.isFalse(fromPP.<Boolean> get("privateProject")),
                    builder.isTrue(fromPP.<Boolean> get("reportPublic"))));
            } else if (whichProjects == WhichProjects.ALLPROJECTS) {
                if (user.isAdmin()) {
                    // Always true
                    visibilityPredicates.add(builder.conjunction());
                }
            }

            return this;
        }

        /**
         * Adds plan states as criteria to the query.
         * 
         * @param planStates
         *            the plan states to add
         * @return this query
         */
        public PlanQuery filterState(PlanState... planStates) {
            if (planStates.length == 0) {
                return this;
            }

            stateFilterPredicates.add(fromPP.<PlanState> get("state").in((Object[]) planStates));

            return this;
        }

        /**
         * Adds a minimum plan state to the query. Plans will have at least the
         * state provided.
         * 
         * @param planState
         *            the plan state
         * @return this query
         */
        public PlanQuery filterMinState(PlanState planState) {
            PlanState[] planStates = new PlanState[PlanState.values().length - planState.ordinal()];

            int i = 0;
            for (PlanState p : PlanState.values()) {
                if (p.compareTo(planState) >= 0) {
                    planStates[i] = p;
                    i++;
                }
            }

            return filterState(planStates);
        }

        /**
         * Adds a filter for the plan name. The query matches plans with a name
         * like the filter string.
         * 
         * If no filter was added to the query, plans with any name matches.
         * 
         * @param filter
         *            the filter string
         * @return this query
         */
        public PlanQuery filterNameLike(String filter) {
            nameFilterPredicates.add(builder.like(fromPP.<String> get("name"), filter));
            return this;
        }

        /**
         * Adds a filter for the plan name. The query matches plans with a name
         * unlike the filter string.
         * 
         * If no filter was added to the query, plans with any name matches.
         * 
         * @param filter
         *            the filter string
         * @return this query
         */
        public PlanQuery filterNameUnlike(String filter) {
            nameFilterPredicates.add(builder.notLike(fromPP.<String> get("name"), filter));
            return this;
        }

        /**
         * Adds a filter to query only for plans with mapped measures.
         * 
         * @return this query
         */
        public PlanQuery filterMapped() {
            Subquery<Integer> subquery = cq.subquery(Integer.class);
            Root<Leaf> fromTreeNode = subquery.from(Leaf.class);
            subquery.select(fromTreeNode.<Integer> get("id"));
            subquery.where(builder.and(
                fromTreeNode.<Measure> get("measure").isNotNull(),
                builder.equal(builder.function("rootNode", Integer.class, fromTreeNode.<Integer> get("id")), fromPlan
                    .<ObjectiveTree> get("tree").<TreeNode> get("root").<Integer> get("id"))));

            mappedFilterPredicate = builder.exists(subquery);
            return this;
        }

        /**
         * Adds a filter excluding all plans marked as playground.
         * 
         * @return this query
         */
        public PlanQuery filterPlayground() {
            playgroundFilterPredicate = builder.isFalse(fromPP.<Boolean> get("playground"));
            return this;
        }

        /**
         * Finishes the query.
         */
        private void finishQuery() {
            List<Predicate> predicates = new ArrayList<Predicate>(5);

            // Where
            predicates.add(builder.or(visibilityPredicates.toArray(new Predicate[visibilityPredicates.size()])));
            if (stateFilterPredicates.size() > 0) {
                predicates.add(builder.or(stateFilterPredicates.toArray(new Predicate[stateFilterPredicates.size()])));
            }
            if (nameFilterPredicates.size() > 0) {
                predicates.add(builder.or(nameFilterPredicates.toArray(new Predicate[nameFilterPredicates.size()])));
            }
            if (mappedFilterPredicate != null) {
                predicates.add(mappedFilterPredicate);
            }
            if (playgroundFilterPredicate != null) {
                predicates.add(playgroundFilterPredicate);
            }
            cq.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));

            // Order by
            cq.orderBy(builder.asc(fromPP.get("id")));
        }
    }

    /**
     * Constructs a new plan manager.
     */
    public PlanManager() {
        sessionPlans = new HashSet<Integer>();
    }

    /**
     * Unlocks all plans opened in this session.
     */
    public void unlockSessionPlans() {
        HashSet<Integer> lockedPlans = new HashSet<Integer>(sessionPlans);
        for (Integer planPropertiesId : lockedPlans) {
            unlockPlan(planPropertiesId);
        }
    }

    /**
     * Creates a new plan query.
     * 
     * @return the plan query
     */
    public PlanQuery createQuery() {
        PlanQuery ps = new PlanQuery();
        ps.init();
        return ps;
    }

    /**
     * Returns all plans that fit the plan query.
     * 
     * @param planQuery
     *            the plan query
     * @return the plans
     */
    public List<PlanProperties> list(PlanManager.PlanQuery planQuery) {
        planQuery.finishQuery();

        TypedQuery<PlanProperties> query = em.createQuery(planQuery.cq);
        List<PlanProperties> planProperties = query.getResultList();

        List<String> usernames = em
            .createQuery("SELECT u.username from User u WHERE u.userGroup = :userGroup", String.class)
            .setParameter("userGroup", user.getUserGroup()).getResultList();

        for (PlanProperties pp : planProperties) {

            // A plan may be edited:
            // user currently logged in is administrator
            // or user currently logged in is the owner
            // or user currently logged in is in the group of the owner
            boolean mayEdit = pp.isClosed()
                && (user.isAdmin() || user.getUsername().equals(pp.getOwner()) || usernames.contains(pp.getOwner()));

            pp.setMayEdit(mayEdit);
            pp.setAllowUnlock(pp.getOpenedByUser().equals(user.getUsername()) || user.isAdmin());
        }

        return planProperties;
    }

    /**
     * Reloads a plan. Checks if the provided plan is opened by the current
     * user.
     * 
     * @param plan
     *            the plan to reload
     * @return the reloaded plan
     * @throws PlanningException
     *             if the plan could not be reloaded
     */
    public Plan reloadPlan(Plan plan) throws PlanningException {
        TypedQuery<Long> q = em
            .createQuery(
                "select count(pp.id) from  PlanProperties pp where (pp.openHandle = 1) and (pp.openedByUser = :user) and (pp.id = :propid)",
                Long.class);
        q.setParameter("user", user.getUsername());
        q.setParameter("propid", plan.getPlanProperties().getId());
        Long planCount = q.getSingleResult();
        if (planCount != 1) {
            throw new PlanningException("This plan has not been loaded before, reload is not possible.");
        }

        Plan reloadedPlan = em.find(Plan.class, plan.getId());
        this.initializePlan(reloadedPlan);
        log.info("Plan " + reloadedPlan.getPlanProperties().getName() + " reloaded!");
        return reloadedPlan;

    }

    /**
     * Loads the plan with the given plan-Id from the database. - without
     * locking the plan!
     * 
     * @param planId
     *            the plan ID
     * @return the loaded plan
     */
    public Plan loadPlan(int planId) {
        Plan plan = em.find(Plan.class, planId);

        this.initializePlan(plan);
        return plan;
    }

    /**
     * Loads the given plan from the database and locks it if requested.
     * 
     * @param propertyId
     *            the plan's PROPERTIES id!
     * @param readOnly
     *            states if the plan should be opened in read only mode
     * @return the loaded plan
     * @throws PlanningException
     *             if the plan could not be loaded
     */
    public Plan load(int propertyId, boolean readOnly) throws PlanningException {

        if (!readOnly) {
            // try to lock the plan
            Query q = em
                .createQuery("update PlanProperties pp set pp.openHandle = 1, pp.openedByUser = :user where (pp.openHandle is null or pp.openHandle = 0) and pp.id = :propid");
            q.setParameter("user", user.getUsername());
            q.setParameter("propid", propertyId);
            int num = q.executeUpdate();
            if (num < 1) {
                throw new PlanningException("The plan has been loaded by another user. Please choose another plan.");
            }
            // and add it to the list of loaded plans, so we can unlock it in
            // any case
            sessionPlans.add(propertyId);
        }
        // then load the plan
        Object result = em.createQuery("select p.id from Plan p where p.planProperties.id = " + propertyId)
            .getSingleResult();
        if (result != null) {
            Plan plan = loadPlan((Integer) result);
            plan.setReadOnly(readOnly);
            log.info("Plan {} : {} loaded.", propertyId, plan.getPlanProperties().getName());
            return plan;
        } else {
            throw new PlanningException("An unexpected error has occured while loading the plan.");
        }
    }

    /**
     * Hibernate initializes project and its parts.
     * 
     * @param p
     *            the plan to initialize
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
     * Unlocks all plans in the database.
     */
    public void unlockAll() {
        this.unlockQuery(-1);
    }

    /**
     * Unlocks a plan with the provided plan properties ID.
     * 
     * @param planPropertiesId
     *            the plan's PROPERTIES id
     */
    public void unlockPlan(int planPropertiesId) {
        // remove from list of locked plans
        sessionPlans.remove(planPropertiesId);

        unlockQuery(planPropertiesId);
    }

    /**
     * Unlocks plans in the database (dependent on parameter). If the pid is -1,
     * all plans are unlocked, otherwise the plan with the provided pid is
     * unlocked.
     * 
     * @param pid
     *            The plan ID to unlock or -1 to unlock all plans
     */
    private void unlockQuery(long pid) {

        String where = "";
        if (pid > -1) {
            where = "where pp.id = " + pid;
        }

        Query q = em.createQuery("update PlanProperties pp set pp.openHandle = 0, pp.openedByUser = '' " + where);
        try {
            if (q.executeUpdate() < 1) {
                log.error("Unlocking plan of plans with with id [{}] failed.", pid);
            } else {
                log.info("Unlocked plans with id [{}].", pid);
            }
        } catch (Throwable e) {
            log.error("Unlocking plans with id [{}] failed:", pid, e);
        }
    }

    /**
     * Updates the state of the provided plan and saves the provided entity.
     * 
     * @param plan
     *            the plan
     * @param currentState
     *            the state of the plan
     * @param entity
     *            the entity to save
     */
    public void save(Plan plan, PlanState currentState, Object entity) {

        log.debug("Persisting plan " + entity.getClass().getName());
        
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

        em.persist(em.merge(entity));
    }

    // --------------- save operations for steps ---------------

    /**
     * Saves changes to the plan settings.
     * 
     * @param planProperties
     *            the plan properties
     * @param alternativesDefinition
     *            alternatives to save
     */
    public void saveForPlanSettings(PlanProperties planProperties, AlternativesDefinition alternativesDefinition) {
        em.persist(em.merge(planProperties));
        em.persist(em.merge(alternativesDefinition));
    }

    // --------------- delete a plan from database ---------------

    /**
     * Method responsible for deleting a plan from database.
     * 
     * @param plan
     *            the plan to delete.
     * @throws PlanningException
     *             if the plan could not be deleted
     */
    public void deletePlan(Plan plan) throws PlanningException {
        if (plan.isReadOnly()) {
            throw new PlanningException("Plans opened in read only mode cannot be deleted!");
        }
        log.info("Deleting plan {} with pid {}", plan.getPlanProperties().getName(), plan.getPlanProperties().getId());
        List<DigitalObject> digitalObjects = plan.getDigitalObjects();
        try {
            em.remove(em.merge(plan));
            em.flush();
            for (DigitalObject obj : digitalObjects) {
                bytestreamManager.delete(obj.getPid());
            }
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new PlanningException("Failed to delete plan: " + plan.getPlanProperties().getName() + " with id: "
                + plan.getPlanProperties().getId(), e);
        }

    }

    // ********** getter/setter **********
    public WhichProjects getLastLoadMode() {
        return lastLoadMode;
    }

    public void setLastLoadMode(WhichProjects lastLoadMode) {
        this.lastLoadMode = lastLoadMode;
    }
}
