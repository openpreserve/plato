package eu.scape_project.planning.efficiency;

import java.lang.reflect.Field;
import java.util.Date;

public class PlanStatistics {
    public static final int MAX_STATE = 15;

    private long id;
    private long propertyId;
    private String creatorUsername;
    private String creatorName;
    private String creatorEmail;
    private String name;
    private long state;
    private Date createdOn;
    private Date decisionOn;
    private long highestStateAchieved;
    private long numOfSamples;
    private long numOfLeaves;
    private long numOfMappedLeaves;
    private long numOfMeasurementNeeded;
    private long numOfAlternatives;

    private long lengthDocumentTypes;
    private long lengthPropertiesDescription;
    private long lengthBasisMandate;
    private long lengthBasisPlanningPurpose;
    private long lengthBasisDesignatedCommunity;
    private long lengthBasisApplyingPolicies;
    private long lengthBasisOrganisationalProcedures;
    private long lengthBasisPreservationRights;
    private long lengthBasisReferenceToAgreements;
    private long lengthBasisPlanRelations;
    private long lengthTriggersNewCollection;
    private long lengthTriggersPeriodicReview;
    private long lengthTriggersChangedEnvironment;
    private long lengthTriggersChangedObjective;
    private long lengthTriggersChangedCollectionProfile;
    private long lengthCollectionProfileDescription;
    private long lengthCollectionProfileTypeOfObjects;
    private long lengthCollectionProfileExpectedGrowthRate;
    private long lengthCollectionProfileRetentionPeriod;
    private long lengthSamplesDescription;
    private long lengthRequirementsDefinitionDescription;
    private long lengthAlternativesDefinitionDescription;
    private long lengthDecisionReason;
    private long lengthDecisionActionNeeded;
    private long lengthEvaluationComment;
    private long lengthImportanceWeightingComment;
    private long lengthRecommendationReasoning;
    private long lengthRecommendationEffects;
    private long lengthPlanDefinitionCostsRemarks;
    private double percentagePopulatedValues;
    private double percentageDefinedTransformers;
    private int numDistinctUsers;
    private int numPlansCreated;

    // time periods: first changelog of phase, to first changelog of next phase
    private long[] phaseDurations = new long[MAX_STATE + 1];
    // plus: first to decision, first to validated
    private long toDecision;
    private long toCompletion;
  
    
    public PlanStatistics(){
    	
    }

   
    public PlanStatistics(long id, long propId, String username, String creatorName, String creatorEmail, String name,
        long state, Date createdOn, Date decisionOn, long lastAccessedState, long highestStateAchieved,
        long numOfSamples, long numOfLeaves, long numOfMappedLeaves, long numOfMeasurementNeeded,
        long numOfAlternatives, long lengthDocumentTypes, long lengthPropertiesDescription, long lengthBasisMandate,
        long lengthBasisPlanningPurpose, long lengthBasisDesignatedCommunity, long lengthBasisApplyingPolicies,
        long lengthBasisOrganisationalProcedures, long lengthBasisPreservationRights,
        long lengthBasisReferenceToAgreements, long lengthBasisPlanRelations, long lengthTriggersNewCollection,
        long lengthTriggersPeriodicReview, long lengthTriggersChangedEnvironment, long lengthTriggersChangedObjective,
        long lengthTriggersChangedCollectionProfile, long lengthCollectionProfileDescription,
        long lengthCollectionProfileTypeOfObjects, long lengthCollectionProfileExpectedGrowthRate,
        long lengthCollectionProfileRetentionPeriod, long lengthSamplesDescription,
        long lengthRequirementsDefinitionDescription, long lengthAlternativesDefinitionDescription,
        long lengthDecisionReason, long lengthDecisionActionNeeded, long lengthEvaluationComment,
        long lengthImportanceWeightingComment, long lengthRecommendationReasoning, long lengthRecommendationEffects,
        long lengthPlanDefinitionCostsRemarks, double percentagePopulatedValues, double percentageDefinedTransformers,
        int numDistinctUsers, int numPlansCreated) {
        super();
        this.id = id;
        this.propertyId = propId;
        this.creatorUsername = username;
        this.creatorName = creatorName;
        this.creatorEmail = creatorEmail;
        this.name = name;
        this.state = state;
        this.createdOn = createdOn;
        this.decisionOn = decisionOn;
        this.highestStateAchieved = highestStateAchieved;
        this.numOfSamples = numOfSamples;
        this.numOfLeaves = numOfLeaves;
        this.numOfMappedLeaves = numOfMappedLeaves;
        this.numOfMeasurementNeeded = numOfMeasurementNeeded;
        this.numOfAlternatives = numOfAlternatives;
        this.lengthDocumentTypes = lengthDocumentTypes;
        this.lengthPropertiesDescription = lengthPropertiesDescription;
        this.lengthBasisMandate = lengthBasisMandate;
        this.lengthBasisPlanningPurpose = lengthBasisPlanningPurpose;
        this.lengthBasisDesignatedCommunity = lengthBasisDesignatedCommunity;
        this.lengthBasisApplyingPolicies = lengthBasisApplyingPolicies;
        this.lengthBasisOrganisationalProcedures = lengthBasisOrganisationalProcedures;
        this.lengthBasisPreservationRights = lengthBasisPreservationRights;
        this.lengthBasisReferenceToAgreements = lengthBasisReferenceToAgreements;
        this.lengthBasisPlanRelations = lengthBasisPlanRelations;
        this.lengthTriggersNewCollection = lengthTriggersNewCollection;
        this.lengthTriggersPeriodicReview = lengthTriggersPeriodicReview;
        this.lengthTriggersChangedEnvironment = lengthTriggersChangedEnvironment;
        this.lengthTriggersChangedObjective = lengthTriggersChangedObjective;
        this.lengthTriggersChangedCollectionProfile = lengthTriggersChangedCollectionProfile;
        this.lengthCollectionProfileDescription = lengthCollectionProfileDescription;
        this.lengthCollectionProfileTypeOfObjects = lengthCollectionProfileTypeOfObjects;
        this.lengthCollectionProfileExpectedGrowthRate = lengthCollectionProfileExpectedGrowthRate;
        this.lengthCollectionProfileRetentionPeriod = lengthCollectionProfileRetentionPeriod;
        this.lengthSamplesDescription = lengthSamplesDescription;
        this.lengthRequirementsDefinitionDescription = lengthRequirementsDefinitionDescription;
        this.lengthAlternativesDefinitionDescription = lengthAlternativesDefinitionDescription;
        this.lengthDecisionReason = lengthDecisionReason;
        this.lengthDecisionActionNeeded = lengthDecisionActionNeeded;
        this.lengthEvaluationComment = lengthEvaluationComment;
        this.lengthImportanceWeightingComment = lengthImportanceWeightingComment;
        this.lengthRecommendationReasoning = lengthRecommendationReasoning;
        this.lengthRecommendationEffects = lengthRecommendationEffects;
        this.lengthPlanDefinitionCostsRemarks = lengthPlanDefinitionCostsRemarks;
        this.percentagePopulatedValues = percentagePopulatedValues;
        this.percentageDefinedTransformers = percentageDefinedTransformers;
        this.numDistinctUsers = numDistinctUsers;
        this.numPlansCreated = numPlansCreated;
    }

    public String[] getHeader() {
        Field[] fields = getClass().getDeclaredFields();

        String header[] = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            header[i] = fields[i].getName();
        }
        return header;
    }

    public long getLengthDocumentTypes() {
        return lengthDocumentTypes;
    }

    public void setLengthDocumentTypes(long lengthDocumentTypes) {
        this.lengthDocumentTypes = lengthDocumentTypes;
    }

    public long getLengthPropertiesDescription() {
        return lengthPropertiesDescription;
    }

    public void setLengthPropertiesDescription(long lengthPropertiesDescription) {
        this.lengthPropertiesDescription = lengthPropertiesDescription;
    }

    public long getLengthBasisMandate() {
        return lengthBasisMandate;
    }

    public void setLengthBasisMandate(long lengthBasisMandate) {
        this.lengthBasisMandate = lengthBasisMandate;
    }

    public long getLengthBasisPlanningPurpose() {
        return lengthBasisPlanningPurpose;
    }

    public void setLengthBasisPlanningPurpose(long lengthBasisPlanningPurpose) {
        this.lengthBasisPlanningPurpose = lengthBasisPlanningPurpose;
    }

    public long getLengthBasisDesignatedCommunity() {
        return lengthBasisDesignatedCommunity;
    }

    public void setLengthBasisDesignatedCommunity(long lengthBasisDesignatedCommunity) {
        this.lengthBasisDesignatedCommunity = lengthBasisDesignatedCommunity;
    }

    public long getLengthBasisApplyingPolicies() {
        return lengthBasisApplyingPolicies;
    }

    public void setLengthBasisApplyingPolicies(long lengthBasisApplyingPolicies) {
        this.lengthBasisApplyingPolicies = lengthBasisApplyingPolicies;
    }

    public long getLengthBasisOrganisationalProcedures() {
        return lengthBasisOrganisationalProcedures;
    }

    public void setLengthBasisOrganisationalProcedures(long lengthBasisOrganisationalProcedures) {
        this.lengthBasisOrganisationalProcedures = lengthBasisOrganisationalProcedures;
    }

    public long getLengthBasisPreservationRights() {
        return lengthBasisPreservationRights;
    }

    public void setLengthBasisPreservationRights(long lengthBasisPreservationRights) {
        this.lengthBasisPreservationRights = lengthBasisPreservationRights;
    }

    public long getLengthBasisReferenceToAgreements() {
        return lengthBasisReferenceToAgreements;
    }

    public void setLengthBasisReferenceToAgreements(long lengthBasisReferenceToAgreements) {
        this.lengthBasisReferenceToAgreements = lengthBasisReferenceToAgreements;
    }

    public long getLengthBasisPlanRelations() {
        return lengthBasisPlanRelations;
    }

    public void setLengthBasisPlanRelations(long lengthBasisPlanRelations) {
        this.lengthBasisPlanRelations = lengthBasisPlanRelations;
    }

    public long getLengthTriggersNewCollection() {
        return lengthTriggersNewCollection;
    }

    public void setLengthTriggersNewCollection(long lengthTriggersNewCollection) {
        this.lengthTriggersNewCollection = lengthTriggersNewCollection;
    }

    public long getLengthTriggersPeriodicReview() {
        return lengthTriggersPeriodicReview;
    }

    public void setLengthTriggersPeriodicReview(long lengthTriggersPeriodicReview) {
        this.lengthTriggersPeriodicReview = lengthTriggersPeriodicReview;
    }

    public long getLengthTriggersChangedEnvironment() {
        return lengthTriggersChangedEnvironment;
    }

    public void setLengthTriggersChangedEnvironment(long lengthTriggersChangedEnvironment) {
        this.lengthTriggersChangedEnvironment = lengthTriggersChangedEnvironment;
    }

    public long getLengthTriggersChangedObjective() {
        return lengthTriggersChangedObjective;
    }

    public void setLengthTriggersChangedObjective(long lengthTriggersChangedObjective) {
        this.lengthTriggersChangedObjective = lengthTriggersChangedObjective;
    }

    public long getLengthTriggersChangedCollectionProfile() {
        return lengthTriggersChangedCollectionProfile;
    }

    public void setLengthTriggersChangedCollectionProfile(long lengthTriggersChangedCollectionProfile) {
        this.lengthTriggersChangedCollectionProfile = lengthTriggersChangedCollectionProfile;
    }

    public long getLengthCollectionProfileDescription() {
        return lengthCollectionProfileDescription;
    }

    public void setLengthCollectionProfileDescription(long lengthCollectionProfileDescription) {
        this.lengthCollectionProfileDescription = lengthCollectionProfileDescription;
    }

    public long getLengthCollectionProfileTypeOfObjects() {
        return lengthCollectionProfileTypeOfObjects;
    }

    public void setLengthCollectionProfileTypeOfObjects(long lengthCollectionProfileTypeOfObjects) {
        this.lengthCollectionProfileTypeOfObjects = lengthCollectionProfileTypeOfObjects;
    }

    public long getLengthCollectionProfileExpectedGrowthRate() {
        return lengthCollectionProfileExpectedGrowthRate;
    }

    public void setLengthCollectionProfileExpectedGrowthRate(long lengthCollectionProfileExpectedGrowthRate) {
        this.lengthCollectionProfileExpectedGrowthRate = lengthCollectionProfileExpectedGrowthRate;
    }

    public long getLengthCollectionProfileRetentionPeriod() {
        return lengthCollectionProfileRetentionPeriod;
    }

    public void setLengthCollectionProfileRetentionPeriod(long lengthCollectionProfileRetentionPeriod) {
        this.lengthCollectionProfileRetentionPeriod = lengthCollectionProfileRetentionPeriod;
    }

    public long getLengthSamplesDescription() {
        return lengthSamplesDescription;
    }

    public void setLengthSamplesDescription(long lengthSamplesDescription) {
        this.lengthSamplesDescription = lengthSamplesDescription;
    }

    public long getLengthRequirementsDefinitionDescription() {
        return lengthRequirementsDefinitionDescription;
    }

    public void setLengthRequirementsDefinitionDescription(long lengthRequirementsDefinitionDescription) {
        this.lengthRequirementsDefinitionDescription = lengthRequirementsDefinitionDescription;
    }

    public long getLengthAlternativesDefinitionDescription() {
        return lengthAlternativesDefinitionDescription;
    }

    public void setLengthAlternativesDefinitionDescription(long lengthAlternativesDefinitionDescription) {
        this.lengthAlternativesDefinitionDescription = lengthAlternativesDefinitionDescription;
    }

    public long getLengthDecisionReason() {
        return lengthDecisionReason;
    }

    public void setLengthDecisionReason(long lengthDecisionReason) {
        this.lengthDecisionReason = lengthDecisionReason;
    }

    public long getLengthDecisionActionNeeded() {
        return lengthDecisionActionNeeded;
    }

    public void setLengthDecisionActionNeeded(long lengthDecisionActionNeeded) {
        this.lengthDecisionActionNeeded = lengthDecisionActionNeeded;
    }

    public long getLengthEvaluationComment() {
        return lengthEvaluationComment;
    }

    public void setLengthEvaluationComment(long lengthEvaluationComment) {
        this.lengthEvaluationComment = lengthEvaluationComment;
    }

    public long getLengthImportanceWeightingComment() {
        return lengthImportanceWeightingComment;
    }

    public void setLengthImportanceWeightingComment(long lengthImportanceWeightingComment) {
        this.lengthImportanceWeightingComment = lengthImportanceWeightingComment;
    }

    public long getLengthRecommendationReasoning() {
        return lengthRecommendationReasoning;
    }

    public void setLengthRecommendationReasoning(long lengthRecommendationReasoning) {
        this.lengthRecommendationReasoning = lengthRecommendationReasoning;
    }

    public long getLengthRecommendationEffects() {
        return lengthRecommendationEffects;
    }

    public void setLengthRecommendationEffects(long lengthRecommendationEffects) {
        this.lengthRecommendationEffects = lengthRecommendationEffects;
    }

    public long getLengthPlanDefinitionCostsRemarks() {
        return lengthPlanDefinitionCostsRemarks;
    }

    public void setLengthPlanDefinitionCostsRemarks(long lengthPlanDefinitionCostsRemarks) {
        this.lengthPlanDefinitionCostsRemarks = lengthPlanDefinitionCostsRemarks;
    }

    public double getPercentagePopulatedValues() {
        return percentagePopulatedValues;
    }

    public void setPercentagePopulatedValues(double percentagePopulatedValues) {
        this.percentagePopulatedValues = percentagePopulatedValues;
    }

    public double getPercentageDefinedTransformers() {
        return percentageDefinedTransformers;
    }

    public void setPercentageDefinedTransformers(double percentageDefinedTransformers) {
        this.percentageDefinedTransformers = percentageDefinedTransformers;
    }

    public int getNumDistinctUsers() {
        return numDistinctUsers;
    }

    public void setNumDistinctUsers(int numDistinctUsers) {
        this.numDistinctUsers = numDistinctUsers;
    }

    public int getNumPlansCreated() {
        return numPlansCreated;
    }

    public void setNumPlansCreated(int numPlansCreated) {
        this.numPlansCreated = numPlansCreated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getDecisionOn() {
        return decisionOn;
    }

    public void setDecisionOn(Date decisionOn) {
        this.decisionOn = decisionOn;
    }


    public long getHighestStateAchieved() {
        return highestStateAchieved;
    }

    public void setHighestStateAchieved(long highestStateAchieved) {
        this.highestStateAchieved = highestStateAchieved;
    }

    public long getNumOfSamples() {
        return numOfSamples;
    }

    public void setNumOfSamples(long numOfSamples) {
        this.numOfSamples = numOfSamples;
    }

    public long getNumOfLeaves() {
        return numOfLeaves;
    }

    public void setNumOfLeaves(long numOfLeaves) {
        this.numOfLeaves = numOfLeaves;
    }

    public long getNumOfMappedLeaves() {
        return numOfMappedLeaves;
    }
    
    public void setNumOfMappedLeaves(long numOfMappedLeaves) {
        this.numOfMappedLeaves = numOfMappedLeaves;
    }

    public long getNumOfMeasurementNeeded() {
        return numOfMeasurementNeeded;
    }

    public void setNumOfMeasurementNeeded(long numOfMeasurementNeeded) {
        this.numOfMeasurementNeeded = numOfMeasurementNeeded;
    }

    public long getNumOfAlternatives() {
        return numOfAlternatives;
    }

    public void setNumOfAlternatives(long numOfAlternatives) {
        this.numOfAlternatives = numOfAlternatives;
    }


    public String getCreatorUsername() {
        return creatorUsername;
    }


    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }


    public long getPhase1() {
        return phaseDurations[1];
    }


    public void setPhase1(long phase1) {
        this.phaseDurations[1] = phase1;
    }


    public long getPhase2() {
        return phaseDurations[2];
    }


    public void setPhase2(long phase2) {
        this.phaseDurations[2] = phase2;
    }


    public long getPhase3() {
        return phaseDurations[3];
    }


    public void setPhase3(long phase3) {
        this.phaseDurations[3] = phase3;
    }


    public long getPhase4() {
        return phaseDurations[4];
    }


    public void setPhase4(long phase4) {
        this.phaseDurations[4] = phase4;
    }


    public long getPhase5() {
        return phaseDurations[5];
    }


    public void setPhase5(long phase5) {
        this.phaseDurations[5] = phase5;
    }


    public long getPhase6() {
        return phaseDurations[6];
    }


    public void setPhase6(long phase6) {
        this.phaseDurations[6] = phase6;
    }


    public long getPhase7() {
        return phaseDurations[7];
    }


    public void setPhase7(long phase7) {
        this.phaseDurations[7] = phase7;
    }


    public long getPhase8() {
        return phaseDurations[8];
    }


    public void setPhase8(long phase8) {
        this.phaseDurations[8] = phase8;
    }


    public long getPhase9() {
        return phaseDurations[9];
    }


    public void setPhase9(long phase9) {
        this.phaseDurations[9] = phase9;
    }


    public long getPhase10() {
        return phaseDurations[10];
    }


    public void setPhase10(long phase10) {
        this.phaseDurations[10] = phase10;
    }


    public long getPhase11() {
        return phaseDurations[11];
    }


    public void setPhase11(long phase11) {
        this.phaseDurations[11] = phase11;
    }


    public long getPhase12() {
        return phaseDurations[12];
    }


    public void setPhase12(long phase12) {
        this.phaseDurations[12] = phase12;
    }


    public long getPhase13() {
        return phaseDurations[13];
    }

    public void setPhase13(long phase13) {
        this.phaseDurations[13] = phase13;
    }
    
    public long getPhase14() {
        return phaseDurations[14];
    }

    public void setPhase14(long phase13) {
        this.phaseDurations[14] = phase13;
    }

    public long getPhase15() {
        return phaseDurations[15];
    }

    public void setPhase15(long phase13) {
        this.phaseDurations[15] = phase13;
    }


    public long[] getPhaseDurations() {
        return phaseDurations;
    }


    public long getToDecision() {
        return toDecision;
    }

    public void setToDecision(long toDecision) {
        this.toDecision = toDecision;
    }


    public long getToCompletion() {
        return toCompletion;
    }


    public void setToCompletion(long toCompletion) {
        this.toCompletion = toCompletion;
    }


    public long getLengthDefineBasis() {
        return lengthDocumentTypes +
            lengthPropertiesDescription +
            lengthBasisMandate + 
            lengthBasisPlanningPurpose + 
            lengthBasisDesignatedCommunity +
            lengthBasisApplyingPolicies + 
            lengthBasisOrganisationalProcedures + 
            lengthBasisPreservationRights + 
            lengthBasisReferenceToAgreements + 
            lengthBasisPlanRelations + 
            lengthTriggersNewCollection + 
            lengthTriggersPeriodicReview + 
            lengthTriggersChangedEnvironment + 
            lengthTriggersChangedObjective + 
            lengthTriggersChangedCollectionProfile;
    }

    public long getLengthDefineSamples() {
        return lengthCollectionProfileDescription + 
        lengthCollectionProfileTypeOfObjects + 
        lengthCollectionProfileExpectedGrowthRate + 
        lengthCollectionProfileRetentionPeriod + 
        lengthSamplesDescription; 
    }
    
    public long getLengthDecision() {
        return lengthDecisionActionNeeded + lengthDecisionReason;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }
}
