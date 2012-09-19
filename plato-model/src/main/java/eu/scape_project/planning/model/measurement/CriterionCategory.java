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
package eu.scape_project.planning.model.measurement;

import java.io.Serializable;

/**
 * This is based on the quality model of preservation planning attributes and measures 
 * 
 * @author Michael Kraxner
 * 
 */
public enum CriterionCategory implements Serializable {
    TIME_BEHAVIOUR("Action", "Action quality", "Performance efficiency", "Time behaviour",
        EvaluationScope.ALTERNATIVE_ACTION),
    RESOURCE_UTILIZATION("Action", "Action quality", "Performance efficiency", "Resource utilization",
        EvaluationScope.ALTERNATIVE_ACTION),
    CAPACITY("Action", "Action quality", "Performance efficiency", "Capacity", EvaluationScope.ALTERNATIVE_ACTION),
    CO_EXISTENCE("Action", "Action quality", "Compatability", "Co-existence", EvaluationScope.ALTERNATIVE_ACTION),
    INTEROPERABILITY("Action", "Action quality", "Compatability", "Interoperability",
        EvaluationScope.ALTERNATIVE_ACTION),
    APPROPRIATENESS_RECOGNIZABILITY("Action", "Action quality", "Usability", "Appropriateness recognizability",
        EvaluationScope.ALTERNATIVE_ACTION),
    LEARNABILITY("Action", "Action quality", "Usability", "Learnability", EvaluationScope.ALTERNATIVE_ACTION),
    OPERABILITY("Action", "Action quality", "Usability", "Operability", EvaluationScope.ALTERNATIVE_ACTION),
    USER_ERROR_PROTECTION("Action", "Action quality", "Usability", "User error protection",
        EvaluationScope.ALTERNATIVE_ACTION),
    USER_INTERFACE_AESTHETICS("Action", "Action quality", "Usability", "User interface aesthetics",
        EvaluationScope.ALTERNATIVE_ACTION),
    ACCESSIBILITY("Action", "Action quality", "Usability", "Accessibility", EvaluationScope.ALTERNATIVE_ACTION),
    ADAPTABILITY("Action", "Action quality", "Portability", "Adaptability", EvaluationScope.ALTERNATIVE_ACTION),
    INSTALLABILITY("Action", "Action quality", "Portability", "Installability", EvaluationScope.ALTERNATIVE_ACTION),
    REPLACEABILITY("Action", "Action quality", "Portability", "Replaceability", EvaluationScope.ALTERNATIVE_ACTION),
    MATURITY("Action", "Action quality", "Reliability", "Maturity", EvaluationScope.ALTERNATIVE_ACTION),
    AVAILABILITY("Action", "Action quality", "Reliability", "Availability", EvaluationScope.ALTERNATIVE_ACTION),
    FAULT_TOLERANCE("Action", "Action quality", "Reliability", "Fault tolerance", EvaluationScope.ALTERNATIVE_ACTION),
    RECOVERABILITY("Action", "Action quality", "Reliability", "Recoverability", EvaluationScope.ALTERNATIVE_ACTION),
    FUNCTIONAL_COMPLETENESS("Action", "Action quality", "Functional suitability", "Functional completeness",
        EvaluationScope.OBJECT),
    FUNCTIONAL_CORRECTNESS_INFORMATION_PROPERTY("Action", "Action quality", "Functional correctness",
        "Functional correctness: Information Property", EvaluationScope.OBJECT),
    FUNCTIONAL_CORRECTNESS_TRANSFORMATIONAL_INFORMATION_PROPERTY("Action", "Action quality", "Functional correctness",
        "Functional correctness: Transformational Information Property", EvaluationScope.OBJECT),
    FUNCTIONAL_CORRECTNESS_REPRESENTATION_INSTANCE_PROPERTY("Action", "Action quality", "Functional correctness",
        "Functional correctness: Representation Instance Property", EvaluationScope.ALTERNATIVE_ACTION),
    FUNCTIONAL_APPROPRIATENESS("Action", "Action quality", "Functional correctness", "Functional appropriateness",
        EvaluationScope.ALTERNATIVE_ACTION),
    MODULARITY("Action", "Action quality", "Maintainability", "Modularity", EvaluationScope.ALTERNATIVE_ACTION),
    REUSABILITY("Action", "Action quality", "Maintainability", "Reusability", EvaluationScope.ALTERNATIVE_ACTION),
    ANALYSABILITY("Action", "Action quality", "Maintainability", "Analysability", EvaluationScope.ALTERNATIVE_ACTION),
    MODIFIABILITY("Action", "Action quality", "Maintainability", "Modifiability", EvaluationScope.ALTERNATIVE_ACTION),
    TESTABILITY("Action", "Action quality", "Maintainability", "Testability", EvaluationScope.ALTERNATIVE_ACTION),
    CONFIDENTIALITY("Action", "Action quality", "Security", "Confidentiality", EvaluationScope.ALTERNATIVE_ACTION),
    INTEGRITY("Action", "Action quality", "Security", "Integrity", EvaluationScope.ALTERNATIVE_ACTION),
    NON_REPUDIATION("Action", "Action quality", "Security", "Non-repudiation", EvaluationScope.ALTERNATIVE_ACTION),
    ACCOUNTABILITY("Action", "Action quality", "Security", "Accountability", EvaluationScope.ALTERNATIVE_ACTION),
    AUTHENTICITY_OF_THE_ACTION("Action", "Action quality", "Security", "Authenticity of the action",
        EvaluationScope.ALTERNATIVE_ACTION),
    ACTION_COSTS("Action", "Action quality", "", "Action costs", EvaluationScope.ALTERNATIVE_ACTION),
    ACTION_LICENSING("Action", "Action quality", "", "Action licensing", EvaluationScope.ALTERNATIVE_ACTION),
    ACTION_MAINTENANCE("Action", "Action quality", "", "Action maintenance", EvaluationScope.ALTERNATIVE_ACTION),
    OUTCOME_EFFECT("Outcome", "", "", "Outcome effect", EvaluationScope.ALTERNATIVE_ACTION),
    FORMAT_SUSTAINABILITY("Outcome", "Format of the content", "", "Format sustainability",
        EvaluationScope.ALTERNATIVE_ACTION),
    FORMAT_QUALITY_AND_FUNCTIONALITY("Outcome", "Format of the content", "", "Format quality and functionality",
        EvaluationScope.ALTERNATIVE_ACTION),
    FORMAT_BUSINESS_FACTORS("Outcome", "Format of the content", "", "Format business factors",
        EvaluationScope.ALTERNATIVE_ACTION);

    private String topCategory;
    private String subCategory;
    private String subsubCategory;
    private String criterionCategory;
    private EvaluationScope scope;

    private CriterionCategory(String topCategory, String subCategory, String subsubCategory, String criterionCategory,
        EvaluationScope scope) {
        this.topCategory = topCategory;
        this.subCategory = subCategory;
        this.subsubCategory = subsubCategory;
        this.criterionCategory = criterionCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public String getSubsubCategory() {
        return subsubCategory;
    }

    public String getCriterionCategory() {
        return criterionCategory;
    }

    public EvaluationScope getScope() {
        return scope;
    }

}
