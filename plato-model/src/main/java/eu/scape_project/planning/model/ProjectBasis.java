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
 * 
 * This work originates from the Planets project, co-funded by the European Union under the Sixth Framework Programme.
 ******************************************************************************/
package eu.scape_project.planning.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;

import eu.scape_project.planning.model.policy.ControlPolicy;
import eu.scape_project.planning.model.policy.PreservationCase;
import eu.scape_project.planning.model.tree.PolicyTree;

@Entity
public class ProjectBasis implements Serializable, ITouchable {

    private static final long serialVersionUID = -3106069473781552004L;

    @Id
    @GeneratedValue
    private int id;

    private String identificationCode;

    /**
     * Description of the document types in the collection.
     *
     * Hibernate note: Standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String documentTypes;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String mandate;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String planningPurpose;

    /**
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String designatedCommunity;

    /**definintion
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String applyingPolicies;

    

    @OneToOne(cascade = CascadeType.ALL)
    private TriggerDefinition triggers = new TriggerDefinition();
    
    /**
     * Relevant organisational procedures and workflows.
     *
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String organisationalProcedures;

    /**
     * Contracts and agreements specifying preservation rights.
     *
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String preservationRights;

    /**
     * Reference to agreements of maintenance and access.
     *
     * Hibernaet note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String referenceToAgreements;

    /**
     * Reference to agreements of maintenance and access.
     *
     * Hibernate note: standard length for a string column is 255
     * validation is broken because we use facelet templates (issue resolved in  Seam 2.0)
     * therefore allow "long" entries
     */
    @Lob
    private String planRelations;

    @OneToOne(cascade = CascadeType.ALL)
    private ChangeLog changeLog = new ChangeLog();

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    private PolicyTree policyTree = new PolicyTree();
    
    /**
     * Reference to selected preservation case, that is the URI of the preservation case from the policy definition
     */
    private String selectedPreservationCaseURI;
    
    public void applyPreservationCase(PreservationCase preservationcase) {
        setSelectedPreservationCaseURI(preservationcase.getUri());
        if (StringUtils.isEmpty(documentTypes)) {
            documentTypes = preservationcase.getContentSet();
        }
        if (StringUtils.isEmpty(applyingPolicies)) {
            StringBuilder allPolicies = new StringBuilder();
            for (ControlPolicy policy : preservationcase.getControlPolicies()) {
                allPolicies.append(String.format(". %s: Measure '%s' %s have a value %s %s \n", 
                    policy.getName(), policy.getMeasure().getName(), String.valueOf(policy.getModality()), String.valueOf(policy.getQualifier()), policy.getValue()));
            }
            applyingPolicies = allPolicies.toString();
        }
        if (StringUtils.isEmpty(designatedCommunity)) {
            designatedCommunity =preservationcase.getUserCommunities();
        }
    }
    

    public String getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(String documentTypes) {
        this.documentTypes = documentTypes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog value) {
        changeLog = value;
    }

    public boolean isChanged() {
        return changeLog.isAltered();
    }

    public void touch() {
        changeLog.touch();
    }

    /**
     * @see ITouchable#handleChanges(IChangesHandler)
     */
    public void handleChanges(IChangesHandler h) {
        h.visit(this);
    }


    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getApplyingPolicies() {
        return applyingPolicies;
    }

    public void setApplyingPolicies(String applyingPolicies) {
        this.applyingPolicies = applyingPolicies;
    }

    public String getDesignatedCommunity() {
        return designatedCommunity;
    }

    public void setDesignatedCommunity(String designatedCommunity) {
        this.designatedCommunity = designatedCommunity;
    }

    public String getMandate() {
        return mandate;
    }

    public void setMandate(String mandate) {
        this.mandate = mandate;
    }

    public String getPlanningPurpose() {
        return planningPurpose;
    }

    public void setPlanningPurpose(String planningPurpose) {
        this.planningPurpose = planningPurpose;
    }

    public String getOrganisationalProcedures() {
        return organisationalProcedures;
    }

    public void setOrganisationalProcedures(String organisationalProcedures) {
        this.organisationalProcedures = organisationalProcedures;
    }

    public String getPreservationRights() {
        return preservationRights;
    }

    public void setPreservationRights(String preservationRights) {
        this.preservationRights = preservationRights;
    }

    public String getReferenceToAgreements() {
        return referenceToAgreements;
    }

    public void setReferenceToAgreements(String referenceToAgreements) {
        this.referenceToAgreements = referenceToAgreements;
    }


    public String getPlanRelations() {
        return planRelations;
    }

    public void setPlanRelations(String planRelations) {
        this.planRelations = planRelations;
    }

    public PolicyTree getPolicyTree() {
        return policyTree;
    }

    public void setPolicyTree(PolicyTree policyTree) {
        this.policyTree = policyTree;
    }

    public TriggerDefinition getTriggers() {
        return triggers;
    }

    public void setTriggers(TriggerDefinition triggers) {
        this.triggers = triggers;
    }

    public String getSelectedPreservationCaseURI() {
        return selectedPreservationCaseURI;
    }

    public void setSelectedPreservationCaseURI(String selectedPreservationCase) {
        this.selectedPreservationCaseURI = selectedPreservationCase;
    }
}
