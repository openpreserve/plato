package eu.scape_project.planning.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.digester3.CallMethodRule;
import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.AlternativesDefinition;
import eu.scape_project.planning.model.CollectionProfile;
import eu.scape_project.planning.model.Decision;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.Evaluation;
import eu.scape_project.planning.model.ExecutablePlanDefinition;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.ImportanceWeighting;
import eu.scape_project.planning.model.Parameter;
import eu.scape_project.planning.model.Plan;
import eu.scape_project.planning.model.PlanDefinition;
import eu.scape_project.planning.model.PlanProperties;
import eu.scape_project.planning.model.PlanState;
import eu.scape_project.planning.model.PlatoException;
import eu.scape_project.planning.model.Policy;
import eu.scape_project.planning.model.PolicyNode;
import eu.scape_project.planning.model.PreservationActionDefinition;
import eu.scape_project.planning.model.ProjectBasis;
import eu.scape_project.planning.model.RequirementsDefinition;
import eu.scape_project.planning.model.ResourceDescription;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.SampleRecordsDefinition;
import eu.scape_project.planning.model.Transformation;
import eu.scape_project.planning.model.TriggerDefinition;
import eu.scape_project.planning.model.Values;
import eu.scape_project.planning.model.XcdlDescription;
import eu.scape_project.planning.model.measurement.Attribute;
import eu.scape_project.planning.model.measurement.CriterionCategory;
import eu.scape_project.planning.model.measurement.Measure;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.FloatRangeScale;
import eu.scape_project.planning.model.scales.FloatScale;
import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.IntRangeScale;
import eu.scape_project.planning.model.scales.IntegerScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.PositiveIntegerScale;
import eu.scape_project.planning.model.scales.YanScale;
import eu.scape_project.planning.model.transform.NumericTransformer;
import eu.scape_project.planning.model.transform.OrdinalTransformer;
import eu.scape_project.planning.model.tree.Leaf;
import eu.scape_project.planning.model.tree.Node;
import eu.scape_project.planning.model.tree.ObjectiveTree;
import eu.scape_project.planning.model.tree.PolicyTree;
import eu.scape_project.planning.model.tree.TemplateTree;
import eu.scape_project.planning.model.values.BooleanValue;
import eu.scape_project.planning.model.values.FloatRangeValue;
import eu.scape_project.planning.model.values.FloatValue;
import eu.scape_project.planning.model.values.FreeStringValue;
import eu.scape_project.planning.model.values.IntRangeValue;
import eu.scape_project.planning.model.values.IntegerValue;
import eu.scape_project.planning.model.values.OrdinalValue;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.PositiveIntegerValue;
import eu.scape_project.planning.model.values.YanValue;
import eu.scape_project.planning.xml.plan.BinaryDataWrapper;
import eu.scape_project.planning.xml.plan.ChangeLogFactory;
import eu.scape_project.planning.xml.plan.EnumConverter;
import eu.scape_project.planning.xml.plan.ExperimentWrapper;
import eu.scape_project.planning.xml.plan.GoDecisionFactory;
import eu.scape_project.planning.xml.plan.OrdinalTransformerMappingFactory;
import eu.scape_project.planning.xml.plan.PlanStateFactory;
import eu.scape_project.planning.xml.plan.RecommendationWrapper;
import eu.scape_project.planning.xml.plan.SampleAggregationModeFactory;
import eu.scape_project.planning.xml.plan.TransformationModeFactory;
import eu.scape_project.planning.xml.plan.TriggerFactory;

/**
 * Creates preservation plans and templates from their XML representations.
 * 
 * @author Michael Kraxner
 * 
 */
public class PlanParser {

    private final static Logger log = LoggerFactory.getLogger(PlanParser.class);

    /**
     * Used by digester
     */
    private List<Plan> plans;

    /**
     * Used by digester
     */
    private List<TemplateTree> templates;

    private final ValidatingParserFactory validatingParserFactory = new ValidatingParserFactory();

    /**
     * Deserializes the plans stored in the file. - the representation must be
     * of the {@link PlanXMLConstants#PLATO_SCHEMA_VERSION current version}
     * 
     * @param file
     * @return
     * @throws PlatoException
     */
    public List<Plan> importProjects(final String file) throws PlatoException {
        try {
            FileInputStream in = new FileInputStream(file);
            return importProjects(in);
        } catch (FileNotFoundException e) {
            throw new PlatoException("IMPORT FAILED: could not find file " + file, e);
        }
    }

    /**
     * can be used to set data for a specific ByteStream, for example for
     * two-pass XML import. but: NOT USED AT THE MOMENT, and probably will not
     * be needed soon!
     * 
     * @param byteStreamID
     * @param data
     *            private void insertData(int byteStreamID, byte[] data) {
     *            ByteStream b = em.find(ByteStream.class, byteStreamID); if (b
     *            == null) {
     *            log.error("INCONSISTENCY: bytestream with ID "+byteStreamID
     *            +" not found!"); return; } b.setData(data); em.persist(b);
     *            em.flush(); b = null; em.clear(); }
     */

    /**
     * Used by the digester every time a project has been parsed.
     * 
     * @param p
     */
    public void setProject(final Plan p) {
        plans.add(p);
    }

    /**
     * Used by the digester every time a template has been parsed.
     * 
     * @param t
     */
    public void setTemplate(final TemplateTree t) {
        templates.add(t);
    }

    /**
     * Imports the XML representation of templates.
     * 
     * @return list of read templates.
     */
    public List<TemplateTree> importTemplates(final InputStream in) throws PlatoException {

        try {
            Digester digester = new Digester();
            // digester.setValidating(true);
            StrictErrorHandler errorHandler = new StrictErrorHandler();
            digester.setErrorHandler(errorHandler);

            // At the moment XML files for template tree's are only used
            // internally,
            // later we will define a schema and use it also for validation

            digester.push(this);

            digester.addObjectCreate("*/template", TemplateTree.class);
            digester.addSetProperties("*/template");
            digester.addSetRoot("*/template", "setTemplate");
            // digester.addSetNext("*/template/name", "setName");
            // digester.addSetNext("*/template/owner", "setOwner");

            PlanParser.addTreeParsingRulesToDigester(digester);

            digester.addObjectCreate("*/template/node", Node.class);
            digester.addSetProperties("*/template/node");
            digester.addSetNext("*/template/node", "addChild");

            digester.setUseContextClassLoader(true);

            templates = new ArrayList<TemplateTree>();
            digester.parse(in);
            // FIXME:
            /*
             * for (TemplateTree t : templates) { log.info(t.getName() +
             * t.getOwner()); }
             */

            return templates;
        } catch (Exception e) {
            throw new PlatoException("Failed to parse template tree.", e);
        }
    }

    /**
     * Imports the XML representation of plans from the given inputstream.
     * 
     * @return list of read plans
     */
    public List<Plan> importProjects(final InputStream in) throws PlatoException {
        try {

            SAXParser parser = validatingParserFactory.getValidatingParser();
            parser.setProperty(ValidatingParserFactory.JAXP_SCHEMA_SOURCE, PlanXMLConstants.PLAN_SCHEMAS);

            Digester digester = new Digester(parser);

            SchemaResolver schemaResolver = new SchemaResolver();

            schemaResolver.addSchemaLocation(PlanXMLConstants.PLATO_SCHEMA_URI, PlanXMLConstants.PLATO_SCHEMA_LOCATION)
                .addSchemaLocation(PlanXMLConstants.PAP_SCHEMA_URI, PlanXMLConstants.PAP_SCHEMA_LOCATION)
                .addSchemaLocation(PlanXMLConstants.TAVERNA_SCHEMA_URI, PlanXMLConstants.TAVERNA_SCHEMA_LOCATION);

            digester.setEntityResolver(schemaResolver);
            digester.setErrorHandler(new StrictErrorHandler());
            digester.setNamespaceAware(true);
            digester.push(this);

            PlanParser.addRules(digester);

            digester.setUseContextClassLoader(true);
            plans = new ArrayList<Plan>();

            // finally parse the XML representation with all created rules
            digester.parse(in);

            for (Plan plan : plans) {
                String projectName = plan.getPlanProperties().getName();
                if ((projectName != null) && (!"".equals(projectName))) {
                    /*
                     * establish links from values to scales
                     */
                    plan.getTree().initValues(plan.getAlternativesDefinition().getConsideredAlternatives(),
                        plan.getSampleRecordsDefinition().getRecords().size(), true);
                    /*
                     * establish references of Experiment.uploads
                     */
                    HashMap<String, SampleObject> records = new HashMap<String, SampleObject>();
                    for (SampleObject record : plan.getSampleRecordsDefinition().getRecords()) {
                        records.put(record.getShortName(), record);
                    }
                    for (Alternative alt : plan.getAlternativesDefinition().getAlternatives()) {
                        if ((alt.getExperiment() != null) && (alt.getExperiment() instanceof ExperimentWrapper)) {
                            alt.setExperiment(((ExperimentWrapper) alt.getExperiment()).getExperiment(records));
                        }
                    }

                    // CHECK NUMERIC TRANSFORMER THRESHOLDS
                    for (Leaf l : plan.getTree().getRoot().getAllLeaves()) {
                        eu.scape_project.planning.model.transform.Transformer t = l.getTransformer();
                        if (t != null && t instanceof NumericTransformer) {
                            NumericTransformer nt = (NumericTransformer) t;
                            if (!nt.checkOrder()) {
                                StringBuffer sb = new StringBuffer("NUMERICTRANSFORMER THRESHOLD ERROR ");
                                sb.append(l.getName()).append("::NUMERICTRANSFORMER:: ");
                                sb.append(nt.getThreshold1()).append(" ").append(nt.getThreshold2()).append(" ")
                                    .append(nt.getThreshold3()).append(" ").append(nt.getThreshold4()).append(" ")
                                    .append(nt.getThreshold5());
                                log.error(sb.toString());
                            }
                        }
                    }

                    /*
                     * establish references to selected alternative
                     */
                    HashMap<String, Alternative> alternatives = new HashMap<String, Alternative>();
                    for (Alternative alt : plan.getAlternativesDefinition().getAlternatives()) {
                        alternatives.put(alt.getName(), alt);
                    }
                    if ((plan.getRecommendation() != null)
                        && (plan.getRecommendation() instanceof RecommendationWrapper)) {
                        plan.setRecommendation(((RecommendationWrapper) plan.getRecommendation())
                            .getRecommendation(alternatives));
                    }
                    if ((plan.getPlanProperties().getState() == PlanState.ANALYSED)
                        && ((plan.getRecommendation() == null) || (plan.getRecommendation().getAlternative() == null))) {
                        /*
                         * This project is NOT completely analysed
                         */
                        plan.getPlanProperties().setState(PlanState.valueOf(PlanState.ANALYSED.getValue() - 1));
                    }

                } else {
                    throw new PlatoException("Could not find any project data.");
                }
            }
        } catch (Exception e) {
            throw new PlatoException("Failed to import plans.", e);
        }

        return plans;
    }

    private static void addRules(Digester digester) throws ParserConfigurationException {

        // ConvertUtils.register(new CriterionCategoryConverter(),
        // CriterionCategory.class);
        ConvertUtils.register(new EnumConverter<CriterionCategory>(CriterionCategory.class), CriterionCategory.class);
        // start with a new file
        digester.addObjectCreate("*/plan", Plan.class);
        digester.addSetProperties("*/plan");
        digester.addSetRoot("*/plan", "setProject");

        digester.addFactoryCreate("*/changelog", ChangeLogFactory.class);
        digester.addSetNext("*/changelog", "setChangeLog");

        digester.addObjectCreate("*/plan/properties", PlanProperties.class);
        digester.addSetProperties("*/plan/properties");
        digester.addSetNext("*/plan/properties", "setPlanProperties");
        digester.addCallMethod("*/plan/properties/description", "setDescription", 0);
        digester.addCallMethod("*/plan/properties/owner", "setOwner", 0);

        digester.addFactoryCreate("*/plan/properties/state", PlanStateFactory.class);
        digester.addSetNext("*/plan/properties/state", "setState");

        PlanParser.addCreateUpload(digester, "*/plan/properties/report", "setReportUpload", DigitalObject.class);

        digester.addObjectCreate("*/plan/basis", ProjectBasis.class);
        digester.addSetProperties("*/plan/basis");
        digester.addSetNext("*/plan/basis", "setProjectBasis");
        digester.addCallMethod("*/plan/basis/applyingPolicies", "setApplyingPolicies", 0);
        digester.addCallMethod("*/plan/basis/designatedCommunity", "setDesignatedCommunity", 0);
        digester.addCallMethod("*/plan/basis/mandate", "setMandate", 0);

        digester.addCallMethod("*/plan/basis/documentTypes", "setDocumentTypes", 0);
        digester.addCallMethod("*/plan/basis/identificationCode", "setIdentificationCode", 0);
        digester.addCallMethod("*/plan/basis/organisationalProcedures", "setOrganisationalProcedures", 0);
        digester.addCallMethod("*/plan/basis/planningPurpose", "setPlanningPurpose", 0);
        digester.addCallMethod("*/plan/basis/planRelations", "setPlanRelations", 0);
        digester.addCallMethod("*/plan/basis/preservationRights", "setPreservationRights", 0);
        digester.addCallMethod("*/plan/basis/referenceToAgreements", "setReferenceToAgreements", 0);

        // define common rule for triggers, for all */triggers/...!
        // also used for PlanDefinition
        digester.addObjectCreate("*/triggers", TriggerDefinition.class);
        digester.addSetNext("*/triggers", "setTriggers");
        // every time a */triggers/trigger is encountered:
        digester.addFactoryCreate("*/triggers/trigger", TriggerFactory.class);
        digester.addSetNext("*/triggers/trigger", "setTrigger");

        //
        // Policy Tree
        //
        digester.addObjectCreate("*/plan/basis/policyTree", PolicyTree.class);
        digester.addSetProperties("*/plan/basis/policyTree");
        digester.addSetNext("*/plan/basis/policyTree", "setPolicyTree");

        digester.addObjectCreate("*/plan/basis/policyTree/policyNode", PolicyNode.class);
        digester.addSetProperties("*/plan/basis/policyTree/policyNode");
        digester.addSetNext("*/plan/basis/policyTree/policyNode", "setRoot");

        digester.addObjectCreate("*/policyNode/policyNode", PolicyNode.class);
        digester.addSetProperties("*/policyNode/policyNode");
        digester.addSetNext("*/policyNode/policyNode", "addChild");

        digester.addObjectCreate("*/policyNode/policy", Policy.class);
        digester.addSetProperties("*/policyNode/policy");
        digester.addSetNext("*/policyNode/policy", "addChild");

        //
        // Sample Records
        //

        digester.addObjectCreate("*/plan/sampleRecords", SampleRecordsDefinition.class);
        digester.addSetProperties("*/plan/sampleRecords");
        digester.addSetNext("*/plan/sampleRecords", "setSampleRecordsDefinition");

        digester.addCallMethod("*/plan/sampleRecords/samplesDescription", "setSamplesDescription", 0);

        // - records
        digester.addObjectCreate("*/record", SampleObject.class);
        digester.addSetProperties("*/record");
        digester.addSetNext("*/record", "addRecord");

        digester.addCallMethod("*/record/description", "setDescription", 0);
        digester.addCallMethod("*/record/originalTechnicalEnvironment", "setOriginalTechnicalEnvironment", 0);

        digester.addObjectCreate("*/record/data", BinaryDataWrapper.class);
        digester.addSetTop("*/record/data", "setData");
        digester.addCallMethod("*/record/data", "setFromBase64Encoded", 0);

        // set up an general rule for all jhove strings!
        digester.addObjectCreate("*/jhoveXML", BinaryDataWrapper.class);
        digester.addSetTop("*/jhoveXML", "setString");
        digester.addCallMethod("*/jhoveXML", "setFromBase64Encoded", 0);
        digester.addCallMethod("*/jhoveXML", "setMethodName", 1, new String[] {"java.lang.String"});
        digester.addObjectParam("*/jhoveXML", 0, "setJhoveXMLString");

        // set up general rule for all fitsXMLs
        digester.addObjectCreate("*/fitsXML", BinaryDataWrapper.class);
        digester.addSetTop("*/fitsXML", "setString");
        digester.addCallMethod("*/fitsXML", "setFromBase64Encoded", 0);
        digester.addCallMethod("*/fitsXML", "setMethodName", 1, new String[] {"java.lang.String"});
        digester.addObjectParam("*/fitsXML", 0, "setFitsXMLString");

        digester.addObjectCreate("*/record/formatInfo", FormatInfo.class);
        digester.addSetProperties("*/record/formatInfo");
        digester.addSetNext("*/record/formatInfo", "setFormatInfo");

        PlanParser.addCreateUpload(digester, "*/record/xcdlDescription", "setXcdlDescription", XcdlDescription.class);

        // - collection profile
        digester.addObjectCreate("*/plan/sampleRecords/collectionProfile", CollectionProfile.class);
        digester.addSetProperties("*/plan/sampleRecords/collectionProfile");
        digester.addSetNext("*/plan/sampleRecords/collectionProfile", "setCollectionProfile");

        digester.addCallMethod("*/plan/sampleRecords/collectionProfile/collectionID", "setCollectionID", 0);
        digester.addCallMethod("*/plan/sampleRecords/collectionProfile/description", "setDescription", 0);
        digester.addCallMethod("*/plan/sampleRecords/collectionProfile/numberOfObjects", "setNumberOfObjects", 0);
        digester.addCallMethod("*/plan/sampleRecords/collectionProfile/typeOfObjects", "setTypeOfObjects", 0);
        digester.addCallMethod("*/plan/sampleRecords/collectionProfile/expectedGrowthRate", "setExpectedGrowthRate", 0);
        digester.addCallMethod("*/plan/sampleRecords/collectionProfile/retentionPeriod", "setRetentionPeriod", 0);

        // requirements definition
        digester.addObjectCreate("*/plan/requirementsDefinition", RequirementsDefinition.class);
        digester.addSetProperties("*/plan/requirementsDefinition");
        digester.addSetNext("*/plan/requirementsDefinition", "setRequirementsDefinition");

        digester.addCallMethod("*/plan/requirementsDefinition/description", "setDescription", 0);

        // - uploads
        digester.addObjectCreate("*/plan/requirementsDefinition/uploads", ArrayList.class);
        digester.addSetNext("*/plan/requirementsDefinition/uploads", "setUploads");
        PlanParser
            .addCreateUpload(digester, "*/plan/requirementsDefinition/uploads/upload", "add", DigitalObject.class);

        // alternatives
        digester.addObjectCreate("*/plan/alternatives", AlternativesDefinition.class);
        digester.addSetProperties("*/plan/alternatives");
        digester.addCallMethod("*/plan/alternatives/description", "setDescription", 0);
        digester.addSetNext("*/plan/alternatives", "setAlternativesDefinition");

        digester.addObjectCreate("*/plan/alternatives/alternative", Alternative.class);
        digester.addSetProperties("*/plan/alternatives/alternative");
        digester.addSetNext("*/plan/alternatives/alternative", "addAlternative");
        // - action
        digester.addObjectCreate("*/plan/alternatives/alternative/action", PreservationActionDefinition.class);
        digester.addSetProperties("*/plan/alternatives/alternative/action");
        digester.addBeanPropertySetter("*/plan/alternatives/alternative/action/descriptor");
        digester.addBeanPropertySetter("*/plan/alternatives/alternative/action/parameterInfo");

        digester.addSetNext("*/plan/alternatives/alternative/action", "setAction");

        digester.addCallMethod("*/plan/alternatives/alternative/description", "setDescription", 0);

        // - - params
        digester.addObjectCreate("*/plan/alternatives/alternative/action/params", LinkedList.class);
        digester.addSetNext("*/plan/alternatives/alternative/action/params", "setParams");

        digester.addObjectCreate("*/plan/alternatives/alternative/action/params/param", Parameter.class);
        digester.addSetProperties("*/plan/alternatives/alternative/action/params/param");
        digester.addSetNext("*/plan/alternatives/alternative/action/params/param", "add");
        // - resource description
        digester.addObjectCreate("*/resourceDescription", ResourceDescription.class);
        digester.addSetProperties("*/resourceDescription");
        digester.addSetNext("*/resourceDescription", "setResourceDescription");

        digester.addCallMethod("*/resourceDescription/configSettings", "setConfigSettings", 0);
        digester.addCallMethod("*/resourceDescription/necessaryResources", "setNecessaryResources", 0);
        digester.addCallMethod("*/resourceDescription/reasonForConsidering", "setReasonForConsidering", 0);

        // - experiment
        digester.addObjectCreate("*/experiment", ExperimentWrapper.class);
        digester.addSetProperties("*/experiment");
        digester.addSetNext("*/experiment", "setExperiment");
        digester.addCallMethod("*/experiment/description", "setDescription", 0);
        digester.addCallMethod("*/experiment/settings", "setSettings", 0);

        PlanParser.addCreateUpload(digester, "*/experiment/results/result", null, DigitalObject.class);
        PlanParser.addCreateUpload(digester, "*/result/xcdlDescription", "setXcdlDescription", XcdlDescription.class);

        // call function addUpload of ExperimentWrapper
        CallMethodRule r = new CallMethodRule(1, "addResult", 2); // method
                                                                  // with
                                                                  // two
                                                                  // params
        // every time */experiment/uploads/upload is encountered
        digester.addRule("*/experiment/results/result", r);
        // use attribute "key" as first param
        digester.addCallParam("*/experiment/results/result", 0, "key");
        // and the object on stack (DigitalObject) as the second
        digester.addCallParam("*/experiment/results/result", 1, true);

        // addCreateUpload(digester,
        // "*/experiment/xcdlDescriptions/xcdlDescription", null,
        // XcdlDescription.class);
        // // call function addXcdlDescription of ExperimentWrapper
        // r = new CallMethodRule(1, "addXcdlDescription", 2); //method with
        // two
        // params
        // // every time */experiment/xcdlDescriptions/xcdlDescription is
        // encountered
        // digester.addRule("*/experiment/xcdlDescriptions/xcdlDescription",
        // r);
        // // use attribute "key" as first param
        // digester.addCallParam("*/experiment/xcdlDescriptions/xcdlDescription",
        // 0 , "key");
        // // and the object on stack (DigitalObject) as the second
        // digester.addCallParam("*/experiment/xcdlDescriptions/xcdlDescription",1,true);

        digester.addObjectCreate("*/experiment/detailedInfos/detailedInfo", DetailedExperimentInfo.class);
        digester.addSetProperties("*/experiment/detailedInfos/detailedInfo");
        digester.addBeanPropertySetter("*/experiment/detailedInfos/detailedInfo/programOutput");
        digester.addBeanPropertySetter("*/experiment/detailedInfos/detailedInfo/cpr");

        // call function "addDetailedInfo" of ExperimentWrapper
        r = new CallMethodRule(1, "addDetailedInfo", 2); // method with two
                                                         // params
        // every time */experiment/detailedInfos/detailedInfo is encountered
        digester.addRule("*/experiment/detailedInfos/detailedInfo", r);
        // use attribute "key" as first param
        digester.addCallParam("*/experiment/detailedInfos/detailedInfo", 0, "key");
        // and the object on stack as second parameter
        digester.addCallParam("*/experiment/detailedInfos/detailedInfo", 1, true);

        // read contained measurements:
        digester.addObjectCreate("*/detailedInfo/measurements/measurement", Measurement.class);
        digester.addSetNext("*/detailedInfo/measurements/measurement", "put");
        digester.addSetProperties("*/measurement");
        // values are defined with wild-cards, and therefore set
        // automatically

        /*
         * for each value type a set of rules because of FreeStringValue we need
         * to store the value as XML-element instead of an attribute naming them
         * "ResultValues" wasn't nice too
         */
        PlanParser.addCreateValue(digester, BooleanValue.class, "setValue");
        PlanParser.addCreateValue(digester, FloatRangeValue.class, "setValue");
        PlanParser.addCreateValue(digester, IntegerValue.class, "setValue");
        PlanParser.addCreateValue(digester, IntRangeValue.class, "setValue");
        PlanParser.addCreateValue(digester, OrdinalValue.class, "setValue");
        PlanParser.addCreateValue(digester, PositiveFloatValue.class, "setValue");
        PlanParser.addCreateValue(digester, PositiveIntegerValue.class, "setValue");
        PlanParser.addCreateValue(digester, YanValue.class, "setValue");
        PlanParser.addCreateValue(digester, FreeStringValue.class, "setValue");

        // go no go decision
        digester.addObjectCreate("*/plan/decision", Decision.class);
        digester.addSetProperties("*/plan/decision");
        digester.addSetNext("*/plan/decision", "setDecision");

        digester.addCallMethod("*/plan/decision/actionNeeded", "setActionNeeded", 0);
        digester.addCallMethod("*/plan/decision/reason", "setReason", 0);

        digester.addFactoryCreate("*/plan/decision/goDecision", GoDecisionFactory.class);
        digester.addSetNext("*/plan/decision/goDecision", "setDecision");

        // evaluation
        digester.addObjectCreate("*/plan/evaluation", Evaluation.class);
        digester.addSetProperties("*/plan/evaluation");
        digester.addSetNext("*/plan/evaluation", "setEvaluation");

        digester.addCallMethod("*/plan/evaluation/comment", "setComment", 0);

        // importance weighting
        digester.addObjectCreate("*/plan/importanceWeighting", ImportanceWeighting.class);
        digester.addSetProperties("*/plan/importanceWeighting");
        digester.addSetNext("*/plan/importanceWeighting", "setImportanceWeighting");

        digester.addCallMethod("*/plan/importanceWeighting/comment", "setComment", 0);

        // recommendation
        digester.addObjectCreate("*/plan/recommendation", RecommendationWrapper.class);
        digester.addSetProperties("*/plan/recommendation");
        digester.addSetNext("*/plan/recommendation", "setRecommendation");

        digester.addCallMethod("*/plan/recommendation/reasoning", "setReasoning", 0);
        digester.addCallMethod("*/plan/recommendation/effects", "setEffects", 0);

        // transformation
        digester.addObjectCreate("*/plan/transformation", Transformation.class);
        digester.addSetProperties("*/plan/transformation");
        digester.addSetNext("*/plan/transformation", "setTransformation");

        digester.addCallMethod("*/plan/transformation/comment", "setComment", 0);

        // Tree
        /*
         * Some rules for tree parsing are necessary for importing templates
         * too, that's why they are added by this static method.
         */
        PlanParser.addTreeParsingRulesToDigester(digester);

        digester.addObjectCreate("*/leaf/evaluation", HashMap.class);
        digester.addSetNext("*/leaf/evaluation", "setValueMap");
        /*
         * The valueMap has an entry for each (considered) alternative ... and
         * for each alternative there is a list of values, one per SampleObject.
         * Note: The digester uses a stack, therefore the rule to put the list
         * of values to the valueMap must be added after the rule for adding the
         * values to the list.
         */

        /*
         * 2. and for each alternative there is a list of values, one per
         * SampleObject
         */
        digester.addObjectCreate("*/leaf/evaluation/alternative", Values.class);
        digester.addCallMethod("*/leaf/evaluation/alternative/comment", "setComment", 0);

        /*
         * for each result-type a set of rules they are added to the valueMap by
         * the rules above
         */
        PlanParser.addCreateResultValue(digester, BooleanValue.class);
        PlanParser.addCreateResultValue(digester, FloatValue.class);
        PlanParser.addCreateResultValue(digester, FloatRangeValue.class);
        PlanParser.addCreateResultValue(digester, IntegerValue.class);
        PlanParser.addCreateResultValue(digester, IntRangeValue.class);
        PlanParser.addCreateResultValue(digester, OrdinalValue.class);
        PlanParser.addCreateResultValue(digester, PositiveFloatValue.class);
        PlanParser.addCreateResultValue(digester, PositiveIntegerValue.class);
        PlanParser.addCreateResultValue(digester, YanValue.class);
        PlanParser.addCreateResultValue(digester, FreeStringValue.class);

        /*
         * 1. The valueMap has an entry for each (considered) alternative ...
         */
        // call put of the ValueMap (HashMap)
        r = new CallMethodRule(1, "put", 2);
        digester.addRule("*/leaf/evaluation/alternative", r);
        digester.addCallParam("*/leaf/evaluation/alternative", 0, "key");
        digester.addCallParam("*/leaf/evaluation/alternative", 1, true);

        // digester.addObjectCreate("*/plan/executablePlan/planWorkflow",
        // ExecutablePlanContentWrapper.class);
        // digester.addSetProperties("*/plan/executablePlan/planWorkflow");
        // digester.addSetNext("*/plan/executablePlan/planWorkflow",
        // "setRecommendation");

        // Executable plan definition
        digester.addObjectCreate("*/plan/executablePlan", ExecutablePlanDefinition.class);
        digester.addSetProperties("*/plan/executablePlan");
        digester.addCallMethod("*/plan/executablePlan/objectPath", "setObjectPath", 0);
        digester.addCallMethod("*/plan/executablePlan/toolParameters", "setToolParameters", 0);
        digester.addCallMethod("*/plan/executablePlan/triggersConditions", "setTriggersConditions", 0);
        digester.addCallMethod("*/plan/executablePlan/validateQA", "setValidateQA", 0);
        PlanParser.addCreateUpload(digester, "*/plan/executablePlan/workflow", "setT2flowExecutablePlan",
            DigitalObject.class);
        
        digester.addSetNext("*/plan/executablePlan", "setExecutablePlanDefinition");

        //
        // Import Planets executable plan if present
        //
        // object-create rules are called at the beginning element-tags, in
        // the
        // same order as defined
        // first create the wrapper
//        digester.addObjectCreate("*/plan/executablePlan/planWorkflow", NodeContentWrapper.class);
//        // then an element for workflowConf
//        digester.addRule("*/plan/executablePlan/planWorkflow/workflowConf", new NodeCreateRule());
//
//        // CallMethod and SetNext rules are called at closing element-tags,
//        // (last
//        // in - first out!)
//
//        CallMethodRule rr = new CallMethodRule(1, "setNodeContent", 2);
//        digester.addRule("*/plan/executablePlan/planWorkflow/workflowConf", rr);
//        // right below the wrapper is an instance of
//        // ExecutablePlanDefinition
//        digester.addCallParam("*/plan/executablePlan/planWorkflow/workflowConf", 0, 1);
//        // provide the name of the setter method
//        digester.addObjectParam("*/plan/executablePlan/planWorkflow/workflowConf", 1, "setExecutablePlan");
//
//        // the generated node is not accessible as CallParam (why?!?), but
//        // available for addSetNext
//        digester.addSetNext("*/plan/executablePlan/planWorkflow/workflowConf", "setNode");
//
//        //
//        // Import EPrints executable plan if present
//        //
//        digester.addObjectCreate("*/plan/executablePlan/eprintsPlan", NodeContentWrapper.class);
//        // then an element for workflowConf
//        digester.addRule("*/plan/executablePlan/eprintsPlan", new NodeCreateRule());
//
//        CallMethodRule rr2 = new CallMethodRule(1, "setNodeContentEPrintsPlan", 2);
//        digester.addRule("*/plan/executablePlan/eprintsPlan", rr2);
//        // right below the wrapper is an instance of
//        // ExecutablePlanDefinition
//        digester.addCallParam("*/plan/executablePlan/eprintsPlan", 0, 1);
//        // provide the name of the setter method
//        digester.addObjectParam("*/plan/executablePlan/eprintsPlan", 1, "setEprintsExecutablePlan");

        // digester.addSetNext("*/plan/executablePlan/eprintsPlan", "setNode");


        // Plan definition
        digester.addObjectCreate("*/plan/planDefinition", PlanDefinition.class);
        digester.addSetProperties("*/plan/planDefinition");
        digester.addSetNext("*/plan/planDefinition", "setPlanDefinition");

        digester.addCallMethod("*/plan/planDefinition/costsIG", "setCostsIG", 0);
        digester.addCallMethod("*/plan/planDefinition/costsPA", "setCostsPA", 0);
        digester.addCallMethod("*/plan/planDefinition/costsPE", "setCostsPE", 0);
        digester.addCallMethod("*/plan/planDefinition/costsQA", "setCostsQA", 0);
        digester.addCallMethod("*/plan/planDefinition/costsREI", "setCostsREI", 0);
        digester.addCallMethod("*/plan/planDefinition/costsRemarks", "setCostsRemarks", 0);
        digester.addCallMethod("*/plan/planDefinition/costsRM", "setCostsRM", 0);
        digester.addCallMethod("*/plan/planDefinition/costsTCO", "setCostsTCO", 0);
        digester.addCallMethod("*/plan/planDefinition/responsibleExecution", "setResponsibleExecution", 0);
        digester.addCallMethod("*/plan/planDefinition/responsibleMonitoring", "setResponsibleMonitoring", 0);

        digester.addObjectCreate("*/plan/planDefinition/triggers", TriggerDefinition.class);
        digester.addSetNext("*/plan/planDefinition/triggers", "setTriggers");
        // every time a */plan/basis/triggers/trigger is encountered:
        digester.addFactoryCreate("*/plan/planDefinition/triggers/trigger", TriggerFactory.class);
        digester.addSetNext("*/plan/planDefinition/triggers/trigger", "setTrigger");
    }

    /**
     * Create a rule for reading an upload entry for the given location
     * <code>pattern</code>, and use the <code>method</code> to set the upload
     * object.
     * 
     * @param digester
     * @param pattern
     * @param method
     */
    private static void addCreateUpload(final Digester digester, final String pattern, final String method,
        final Class objectType) {
        digester.addObjectCreate(pattern, objectType);
        digester.addSetProperties(pattern);
        if ((method != null) && (!"".equals(method))) {
            digester.addSetNext(pattern, method);
        }
        /*
         * Note: It is not possible to read element data, process it and pass it
         * to a function with a simple digester Rule, neither you can define a
         * factory to read the data of an element.
         * 
         * So we have to do it the other way round: (remember: the function
         * added last is executed first!)
         */
        // 1. Create a BinaryDataWrapper if a <data> element is encountered
        digester.addObjectCreate(pattern + "/data", BinaryDataWrapper.class);
        // 3. Finally call setData on the BinaryDataWrapper(!) on top with the
        // object next to top as argument
        // The BinaryDataWrapper will call setData on to object next to top with
        // the previously read and decoded data
        digester.addSetTop(pattern + "/data", "setData");
        // 2. Call setFromBase64Encoded on the BinaryDataWrapper to read the
        // elements content
        digester.addCallMethod(pattern + "/data", "setFromBase64Encoded", 0);

    }

    /**
     * This method adds rules for name, properties, scales, modes and mappings
     * only! Rules for importing measured values of alternatives are defined
     * seperately in importProjects()! (Refactored to its own method by Kevin)
     */
    private static void addTreeParsingRulesToDigester(final Digester digester) {
        digester.addObjectCreate("*/plan/tree", ObjectiveTree.class);
        digester.addSetProperties("*/plan/tree");
        digester.addSetNext("*/plan/tree", "setTree");

        digester.addObjectCreate("*/node/node", Node.class);
        digester.addSetProperties("*/node/node");
        digester.addSetNext("*/node/node", "addChild");

        digester.addCallMethod("*/node/description", "setDescription", 0);

        digester.addObjectCreate("*/plan/tree/node", Node.class);
        digester.addSetProperties("*/plan/tree/node");
        digester.addSetNext("*/plan/tree/node", "setRoot");

        digester.addObjectCreate("*/leaf", Leaf.class);
        digester.addSetProperties("*/leaf");
        digester.addSetNext("*/leaf", "addChild");
        digester.addFactoryCreate("*/leaf/aggregationMode", SampleAggregationModeFactory.class);
        digester.addSetNext("*/leaf/aggregationMode", "setAggregationMode");

        digester.addCallMethod("*/leaf/description", "setDescription", 0);

        PlanParser.addMeasureRules(digester, "*/measure");

        /*
         * for each scale-type a set of rules
         */
        PlanParser.addCreateScale(digester, BooleanScale.class);
        PlanParser.addCreateScale(digester, FloatRangeScale.class);
        PlanParser.addCreateScale(digester, FloatScale.class);
        PlanParser.addCreateScale(digester, IntegerScale.class);
        PlanParser.addCreateScale(digester, IntRangeScale.class);
        PlanParser.addCreateScale(digester, OrdinalScale.class);
        PlanParser.addCreateScale(digester, PositiveFloatScale.class);
        PlanParser.addCreateScale(digester, PositiveIntegerScale.class);
        PlanParser.addCreateScale(digester, YanScale.class);
        PlanParser.addCreateScale(digester, FreeStringScale.class);

        /*
         * for each transformer type a set of rules
         */
        digester.addObjectCreate("*/leaf/numericTransformer", NumericTransformer.class);
        digester.addSetProperties("*/leaf/numericTransformer");
        digester.addFactoryCreate("*/leaf/numericTransformer/mode", TransformationModeFactory.class);
        digester.addSetNext("*/leaf/numericTransformer/mode", "setMode");
        digester.addBeanPropertySetter("*/leaf/numericTransformer/thresholds/threshold1", "threshold1");
        digester.addBeanPropertySetter("*/leaf/numericTransformer/thresholds/threshold2", "threshold2");
        digester.addBeanPropertySetter("*/leaf/numericTransformer/thresholds/threshold3", "threshold3");
        digester.addBeanPropertySetter("*/leaf/numericTransformer/thresholds/threshold4", "threshold4");
        digester.addBeanPropertySetter("*/leaf/numericTransformer/thresholds/threshold5", "threshold5");
        digester.addSetNext("*/leaf/numericTransformer", "setTransformer");

        // digester.addObjectCreate("*/numericTransformer/thresholds",
        // LinkedHashMap.class);
        // digester.addSetNext("*/numericTransformer/thresholds",
        // "setThresholds");
        // digester.addFactoryCreate("*/thresholds/threshold",
        // NumericTransformerThresholdFactory.class);

        digester.addObjectCreate("*/leaf/ordinalTransformer", OrdinalTransformer.class);
        digester.addSetProperties("*/leaf/ordinalTransformer");
        digester.addSetNext("*/leaf/ordinalTransformer", "setTransformer");

        digester.addObjectCreate("*/ordinalTransformer/mappings", LinkedHashMap.class);
        digester.addSetNext("*/ordinalTransformer/mappings", "setMapping");
        digester.addFactoryCreate("*/mappings/mapping", OrdinalTransformerMappingFactory.class);

        digester.addRule("*/mappings/mapping", new CallMethodRule(1, "put", 2)); // method
                                                                                 // with
                                                                                 // two
                                                                                 // params
        digester.addCallParam("*/mappings/mapping", 0, "ordinal"); // use
                                                                   // attribute
                                                                   // "ordinal"
                                                                   // as first
                                                                   // argument
        digester.addCallParam("*/mappings/mapping", 1, true); // and the object
                                                              // on the stack as
                                                              // second
    }

    private static void addMeasureRules(final Digester digester, final String pattern) {
        digester.addObjectCreate(pattern, Measure.class);
        digester.addSetNext(pattern, "setMeasure");
        digester.addSetProperties(pattern, "ID", "uri");
        digester.addBeanPropertySetter(pattern + "/name");
        digester.addBeanPropertySetter(pattern + "/description");

        PlanParser.addAttributeRules(digester, pattern + "/attribute");
        // scale will be set by global rule
    }

    private static void addCreateResultValue(final Digester digester, final Class c) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        String pattern = "*/" + name.replace("Value", "Result");
        digester.addObjectCreate(pattern, c);
        digester.addSetProperties(pattern);
        digester.addBeanPropertySetter(pattern + "/value");
        digester.addBeanPropertySetter(pattern + "/comment");
        digester.addSetNext(pattern, "add");
    }

    private static void addCreateValue(final Digester digester, final Class c, final String setNextMethod) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        String pattern = "*/" + name;
        digester.addObjectCreate(pattern, c);
        // digester.addSetProperties(pattern);
        digester.addBeanPropertySetter(pattern + "/value");
        digester.addSetNext(pattern, setNextMethod);
    }

    private static void addCreateScale(final Digester digester, final Class c) {
        String name = c.getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        String pattern = "*/" + name;
        digester.addObjectCreate(pattern, c);
        digester.addSetProperties(pattern);
        digester.addSetNext(pattern, "setScale");
    }

    private static void addAttributeRules(final Digester digester, final String pattern) {
        digester.addObjectCreate(pattern, Attribute.class);
        digester.addSetNext(pattern, "setAttribute");
        digester.addSetProperties(pattern, "ID", "uri");

        digester.addBeanPropertySetter(pattern + "/name");
        digester.addBeanPropertySetter(pattern + "/description");
        digester.addBeanPropertySetter(pattern + "/category");
    }
}