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
package eu.scape_project.planning.evaluation.evaluators;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.scape_project.planning.evaluation.EvaluatorException;
import eu.scape_project.planning.evaluation.IObjectEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DetailedExperimentInfo;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.beans.MigrationResult;
import eu.scape_project.planning.model.measurement.MeasureConstants;
import eu.scape_project.planning.model.measurement.Measurement;
import eu.scape_project.planning.model.values.OrdinalValue;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.PositiveIntegerValue;
import eu.scape_project.planning.model.values.Value;

/**
 * This class analyses the metadata collected during experiment execution and
 * extracts measurements. Currently focussed on the metadata schema found in
 * minimee experiments.
 * 
 * @author cb TODO add comment to value: which profiling tool was used, etc.
 */
public class ExperimentEvaluator implements IObjectEvaluator {

    private static Logger log = LoggerFactory.getLogger(ExperimentEvaluator.class);

    private static HashMap<String, String> propertyToMeasuredValues = new HashMap<String, String>();

    public ExperimentEvaluator() {
    }

    /**
     * all properties which can be evaluated have to be registered here
     * 
     * Don't forget to configure measurableProperties of the migration engines
     * in miniMEE-tool-configs.xml properly !
     * 
     */
    static {
        propertyToMeasuredValues.put(MigrationResult.MIGRES_MEMORY_GROSS,
            MigrationResult.MIGRES_MEMORY_GROSS);
    }

    /**
     * 
     * @see IObjectEvaluator#evaluate(Alternative, SampleObject, DigitalObject,
     *      List, IStatusListener)
     */
    public HashMap<String, Value> evaluate(Alternative alternative, SampleObject sample, DigitalObject result,
        List<String> measureUris, IStatusListener listener) throws EvaluatorException {

        HashMap<String, Value> results = new HashMap<String, Value>();
        for (String m : measureUris) {
            Value v = evaluate(alternative, sample, result, m);
            if (v != null) {
                results.put(m, v);
            }
        }
        return results;
    }

    public Value evaluate(Alternative alternative, SampleObject sample, DigitalObject result, String measureUri) {
        double sampleSize = sample.getData().getSize() * (1024 * 1024);

        if (MeasureConstants.AMOUNT_OF_LOGGING.equals(measureUri)) {
            Map<SampleObject, DetailedExperimentInfo> detailedInfo = alternative.getExperiment().getDetailedInfo();
            DetailedExperimentInfo detailedExperimentInfo = detailedInfo.get(sample);
            if ((detailedExperimentInfo != null) && (detailedExperimentInfo.getProgramOutput() != null)) {
                PositiveIntegerValue v = new PositiveIntegerValue();
                v.setValue(detailedExperimentInfo.getProgramOutput().length());
                v.setComment("extracted from experiment details");
                return v;
            }
            return null;
        } else if (MeasureConstants.FORMAT_OF_LOGGIN.equals(measureUri)) {
            Map<SampleObject, DetailedExperimentInfo> detailedInfo = alternative.getExperiment().getDetailedInfo();
            DetailedExperimentInfo detailedExperimentInfo = detailedInfo.get(sample);
            if ((detailedExperimentInfo != null) && (detailedExperimentInfo.getProgramOutput() != null)) {
                OrdinalValue v = new OrdinalValue();
                v.setValue(evaluateLogging(detailedExperimentInfo.getProgramOutput()));
                v.setComment("extracted from experiments details");
                return v;
            }
            return null;
//        } else if (MeasureConstants.OBJECT_ACTION_RUNTIME_PERFORMANCE_THROUGHPUT.equals(measureUri)) {
//            Value extracted = extractMeasuredValue(alternative, sample,
//                MeasureConstants.ELAPSED_TIME_PER_OBJECT);
//            if (extracted instanceof PositiveFloatValue) {
//                PositiveFloatValue value = new PositiveFloatValue();
//                double floatVal = ((PositiveFloatValue) extracted).getValue();
//                if (Double.compare(floatVal, 0.0) != 0) {
//                    // calculate msec/MB
//                    floatVal = floatVal / sampleSize;
//                    // throughput is defined in MB per second, time/perMB is
//                    // msec/MB
//                    value.setValue((1.0 / (floatVal / 1000.0)));
//                }
//                value.setComment("extracted from experiment details");
//                return value;
//            }
        } else if (MeasureConstants.ELAPSED_TIME_PER_MB.equals(measureUri)) {
            Value extracted = extractMeasuredValue(alternative, sample,
                MeasureConstants.ELAPSED_TIME_PER_OBJECT);
            if (extracted instanceof PositiveFloatValue) {
                PositiveFloatValue value = new PositiveFloatValue();
                double floatVal = ((PositiveFloatValue) extracted).getValue();
                if (Double.compare(floatVal, 0.0) != 0) {
                    // calculate msec/MB
                    floatVal = floatVal / sampleSize;
                    value.setValue(floatVal);
                }
                value.setComment("extracted from experiment details");
                return value;
            }
        } else if (MeasureConstants.AVERAGE_MEMORY_USED_PER_MB.equals(measureUri)) {
            Value extracted = extractMeasuredValue(alternative, sample,
                MigrationResult.MIGRES_MEMORY_GROSS);
            if (extracted instanceof PositiveFloatValue) {
                PositiveFloatValue value = new PositiveFloatValue();
                double floatVal = ((PositiveFloatValue) extracted).getValue();

                value.setValue(floatVal / sampleSize);
                value.setComment("extracted from experiment details");
                return value;
            }
        }
        Value extracted = extractMeasuredValue(alternative, sample, measureUri);
        if (extracted != null) {
            extracted.setComment("extracted from experiment details");
        }
        return extracted;
    }

    /**
     * extracts a measured value from detailed experimentInfo (which is
     * populated atm by minimee services)
     * 
     * @param alternative
     * @param sample
     * @param key
     * @return
     */
    private Value extractMeasuredValue(Alternative alternative, SampleObject sample, String propertyURI) {

        Map<SampleObject, DetailedExperimentInfo> detailedInfo = alternative.getExperiment().getDetailedInfo();
        DetailedExperimentInfo detailedExperimentInfo = detailedInfo.get(sample);
        if (detailedExperimentInfo != null) {
            // retrieve the key of minimee's measuredProperty
            String measuredProperty = propertyToMeasuredValues.get(propertyURI);
            Measurement m = detailedExperimentInfo.getMeasurements().get(measuredProperty);
            if (m != null) {
                return m.getValue();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private String evaluateLogging(String logOutput) {
        if ((logOutput == null) || "".equals(logOutput)) {
            return "none";
        } else {
            String result = "text";

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                Schema schema = factory.newSchema();
                Validator validator = schema.newValidator();

                validator.validate(new StreamSource(new StringReader(logOutput)));

                // ok, the log is well-formed XML
                result = "XML";
            } catch (SAXException e) {
                // no xml - this is ok
            } catch (IOException e) {
                log.error("logoutput-evaluator is not properly configured: ", e);
            }

            return result;
        }
    }

}
