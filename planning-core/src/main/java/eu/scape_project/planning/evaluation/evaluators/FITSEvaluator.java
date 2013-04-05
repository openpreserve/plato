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

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.scape_project.planning.evaluation.EvaluatorException;
import eu.scape_project.planning.evaluation.IObjectEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.FormatInfo;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.measurement.MeasureConstants;
import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.FreeStringScale;
import eu.scape_project.planning.model.scales.OrdinalScale;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.util.FloatFormatter;
import eu.scape_project.planning.model.values.BooleanValue;
import eu.scape_project.planning.model.values.OrdinalValue;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.services.characterisation.fits.FitsNamespaceContext;

public class FITSEvaluator implements IObjectEvaluator {
    private static final String NAME = "FITS/Jhove/Exiftool";
    private static final String SOURCE = "\n- extracted by " + NAME;

    private static Logger log = LoggerFactory.getLogger(FITSEvaluator.class);

    public FITSEvaluator() {
    }

    public HashMap<String, Value> evaluate(Alternative alternative, SampleObject sample, DigitalObject result,
        List<String> measureUris, IStatusListener listener) throws EvaluatorException {
        HashMap<String, Value> results = new HashMap<String, Value>();

        if (result == null) {
            return results;
        }

        FloatFormatter formatter = new FloatFormatter();

        String fitsXMLResult = result.getFitsXMLString();
        String fitsXMLSample = sample.getFitsXMLString();

        XmlExtractor extractor = new XmlExtractor();
        extractor.setNamespaceContext(new FitsNamespaceContext());
        if ((fitsXMLResult != null) && (fitsXMLSample != null)) {
            // so we have a fits xml, lets analyse it:
            try {
                StringReader reader = new StringReader(fitsXMLResult);
                Document fitsDocResult = extractor.getDocument(new InputSource(reader));
                reader = new StringReader(fitsXMLSample);
                Document fitsDocSample = extractor.getDocument(new InputSource(reader));

                String sampleImageCompressionScheme = extractor.extractText(fitsDocSample,
                    "//fits:compressionScheme/text()");
                String resultImageCompressionScheme = extractor.extractText(fitsDocResult,
                    "//fits:compressionScheme/text()");

                for (String measureUri : measureUris) {
                    Value v = null;
                    if (MeasureConstants.FORMAT_CONFORMITY_WELL_FORMEDNESS.equals(measureUri)) {

                        v = extractor.extractValue(fitsDocResult, new BooleanScale(),
                            "//fits:well-formed[@status='SINGLE_RESULT']/text()",
                            "//fits:filestatus/fits:message/text()");
                    } else if (MeasureConstants.FORMAT_CONFORMITY_VALIDITY.equals(measureUri)) {
                        v = extractor.extractValue(fitsDocResult, new BooleanScale(),
                            "//fits:filestatus/fits:valid[@status='SINGLE_RESULT']/text()",
                            "//fits:filestatus/fits:message/text()");
                    }
                    if (MeasureConstants.COMPRESSION_ALGORITHM.equals(measureUri)) {
                        v = extractor.extractValue(fitsDocResult, new FreeStringScale(),
                            "//fits:compressionScheme/text()", null);
                    }

                    if ((v != null) && (v.getComment() == null || "".equals(v.getComment()))) {
                        v.setComment(SOURCE);
                        results.put(measureUri, v);
                        listener
                            .updateStatus(String.format("%s: measurement: %s = %s", NAME, measureUri, v.toString()));
                        // this leaf has been processed
                        continue;
                    }

                    if (MeasureConstants.FORMAT_CONFORMITY_CONFORMS.equals(measureUri)) {
                        if (alternative.getAction() != null) {
                            String puid = "UNDEFINED";
                            FormatInfo info = alternative.getAction().getTargetFormatInfo();
                            if (info != null) {
                                puid = info.getPuid();
                            }
                            String fitsText = extractor.extractText(fitsDocResult,
                                "//fits:externalIdentifier[@type='puid']/text()");
                            v = identicalValues(puid, fitsText, new BooleanScale());
                        }
                    } else if ((MeasureConstants.IMAGE_SIZE_IMAGE_WIDTH_EQUAL).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample, "//fits:imageWidth/text()");
                        String resultValue = extractor.extractText(fitsDocResult, "//fits:imageWidth/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                    } else if ((MeasureConstants.IMAGE_SIZE_IMAGE_HEIGHT_EQUAL).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample, "//fits:imageHeight/text()");
                        String resultValue = extractor.extractText(fitsDocResult, "//fits:imageHeight/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                    } else if ((MeasureConstants.IMAGE_ASPECT_RATIO_RETAINED).equals(measureUri)) {
                        try {
                            int sampleHeight = Integer.parseInt(extractor.extractText(fitsDocSample,
                                "//fits:imageHeight/text()"));
                            int resultHeight = Integer.parseInt(extractor.extractText(fitsDocResult,
                                "//fits:imageHeight/text()"));
                            int sampleWidth = Integer.parseInt(extractor.extractText(fitsDocSample,
                                "//fits:imageWidth/text()"));
                            int resultWidth = Integer.parseInt(extractor.extractText(fitsDocResult,
                                "//fits:imageWidth/text()"));

                            double sampleRatio = ((double) sampleWidth) / sampleHeight;
                            double resultRatio = ((double) resultWidth) / resultHeight;
                            v = new BooleanValue();
                            ((BooleanValue) v).bool(0 == Double.compare(sampleRatio, resultRatio));
                            v.setComment(String.format("Reference value: %s\nActual value: %s",
                                formatter.formatFloat(sampleRatio), formatter.formatFloat(resultRatio)));
                        } catch (NumberFormatException e) {
                            // not all values are available - aspectRatio cannot
                            // be calculated
                            v = new BooleanValue();
                            v.setComment("Image width and/or height are not available - aspectRatio cannot be calculated");
                        }
                        // } else if
                        // ((OBJECT_COMPRESSION_SCHEME_RETAINED).equals(measureUri))
                        // {
                        // v = identicalValues(sampleImageCompressionScheme,
                        // resultImageCompressionScheme,
                        // new BooleanScale());
                    } else if (MeasureConstants.COMPRESSION_COMPRESSION_TYPE.equals(measureUri)) {
                        v = new OrdinalValue();
                        if ((resultImageCompressionScheme == null) || 
                            ("".equals(resultImageCompressionScheme)) ||
                            ("None".equals(resultImageCompressionScheme)) ||
                            ("Uncompressed".equals(resultImageCompressionScheme))) {
                            v.parse("none");
                        } else if ("LZW".equals(resultImageCompressionScheme) || "Deflate".equals(resultImageCompressionScheme)) {
                            v.parse("lossless");
                        } else {
                            v.parse("lossy");
                            v.setComment("compression scheme: " + resultImageCompressionScheme);
                        }
                    } else if ((MeasureConstants.COLOR_DEPTH_BITS_PER_SAMPLE_EQUAL).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample, "//fits:bitsPerSample/text()");
                        String resultValue = extractor.extractText(fitsDocResult, "//fits:bitsPerSample/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                        // } else if
                        // ((EXIF_SAMPLES_PER_PIXEL_RETAINED).equals(measureUri))
                        // {
                        // String sampleValue =
                        // extractor.extractText(fitsDocSample,
                        // "//fits:samplesPerPixel/text()");
                        // String resultValue =
                        // extractor.extractText(fitsDocResult,
                        // "//fits:samplesPerPixel/text()");
                        // v = identicalValues(sampleValue, resultValue, new
                        // BooleanScale());
                        // } else if
                        // ((OBJECT_IMAGE_PHOTOMETRICINTERPRETATION_COLORSPACE +
                        // "#equal").equals(measureUri)) {
                        // String sampleValue =
                        // extractor.extractText(fitsDocSample,
                        // "//fits:colorSpace/text()");
                        // String resultValue =
                        // extractor.extractText(fitsDocResult,
                        // "//fits:colorSpace/text()");
                        // v = identicalValues(sampleValue, resultValue, new
                        // BooleanScale());
                    } else if ((MeasureConstants.COLOUR_MODEL_RETAINED).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample, "//fits:iccProfileName/text()");
                        String resultValue = extractor.extractText(fitsDocResult, "//fits:iccProfileName/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                    } else if ((MeasureConstants.SAMPLING_FREQUENCY_UNIT).equals(measureUri)) {
                        String resultValue = extractor
                            .extractText(fitsDocResult, "//fits:samplingFrequencyUnit/text()");
                        v = new OrdinalScale().createValue();
                        ((OrdinalValue) v).setValue(resultValue);
                    } else if ((MeasureConstants.X_SAMPLING_FREQUENCY_PRESERVED).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample, "//fits:xSamplingFrequency/text()");
                        String resultValue = extractor.extractText(fitsDocResult, "//fits:xSamplingFrequency/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                    } else if ((MeasureConstants.Y_SAMPLING_FREQUENCY_PRESERVED).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample, "//fits:ySamplingFrequency/text()");
                        String resultValue = extractor.extractText(fitsDocResult, "//fits:ySamplingFrequency/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());

                    } else if ((MeasureConstants.EXIF_ALL_METADATA_RETAINED + "#equal").equals(measureUri)) {
                        // we use the equal metric. reserve PRESERVED metric for
                        // later and get it right.
                        HashMap<String, String> sampleMetadata = extractor.extractValues(fitsDocSample,
                            "//fits:exiftool/*[local-name() != 'rawdata']");
                        HashMap<String, String> resultMetadata = extractor.extractValues(fitsDocResult,
                            "//fits:exiftool/*[local-name() != 'rawdata']");
                        v = preservedValues(sampleMetadata, resultMetadata, new BooleanScale());
                    } else if ((MeasureConstants.PRODUCER_METADATA_ELEMENT_RETAINED).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample,
                            "//fits:ImageCreation/ImageProducer/text()");
                        String resultValue = extractor.extractText(fitsDocResult,
                            "//fits:ImageCreation/ImageProducer/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                    } else if (MeasureConstants.SOFTWARE_METADATA_ELEMENT_RETAINED.equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample,
                            "//fits:creatingApplicationName/text()");
                        String resultValue = extractor.extractText(fitsDocResult,
                            "//fits:creatingApplicationName/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                    } else if ((MeasureConstants.DATE_AND_TIME_OF_CREATION_METADATA_ELEMENT_RETAINED)
                        .equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample,
                            "//fits:ImageCreation/DateTimeCreated/text()");
                        String resultValue = extractor.extractText(fitsDocResult,
                            "//fits:ImageCreation/DateTimeCreated/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                        // FIXME
                        // } else if
                        // ((MeasureConstants.OBJECT_IMAGE_METADATA_LASTMODIFIED
                        // + "#equal").equals(measureUri)) {
                        // String sampleValue = extractor
                        // .extractText(fitsDocSample,
                        // "//fits:fileinfo/lastmodified/text()");
                        // String resultValue = extractor
                        // .extractText(fitsDocResult,
                        // "//fits:fileinfo/lastmodified/text()");
                        // v = identicalValues(sampleValue, resultValue, new
                        // BooleanScale());
                        // // FIXME only a criterion for EXIF IDF0 image
                        // // description is defined
                    } else if ((MeasureConstants.EXIF_IMAGE_DESCRIPTION_RETAINED + "#equal").equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample,
                            "//fits:exiftool/ImageDescription/text()");
                        String resultValue = extractor.extractText(fitsDocResult,
                            "//fits:exiftool/ImageDescription/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());

                    } else if ((MeasureConstants.IMAGE_ORIENTATION_METADATA_ELEMENT_RETAINED).equals(measureUri)) {
                        String sampleValue = extractor.extractText(fitsDocSample, "//fits:exiftool/Orientation/text()");
                        String resultValue = extractor.extractText(fitsDocResult, "//fits:exiftool/Orientation/text()");
                        v = identicalValues(sampleValue, resultValue, new BooleanScale());
                    }

                    if (v != null) {
                        v.setComment(v.getComment() + SOURCE);
                        results.put(measureUri, v);
                        listener.updateStatus(String.format("%s: evaluated measurement: %s = %s", NAME, measureUri,
                            v.toString()));
                    } else {
                        listener.updateStatus(String.format("%s: no evaluator found for measurement: %s", NAME,
                            measureUri));
                    }
                }
            } catch (IOException e) {
                listener.updateStatus(" - could not read FITS xml");
            } catch (SAXException e) {
                listener.updateStatus(" - invalid FITS xml found");
            } catch (ParserConfigurationException e) {
                listener.updateStatus(" - invalid FITS xml found");
            }
        } else {
            listener.updateStatus(" - no FITS xml found");
        }
        return results;
    }

    private Value preservedValues(HashMap<String, String> sampleMetadata, HashMap<String, String> resultMetadata,
        Scale scale) {
        int numMissing = 0;
        int numChanged = 0;
        BooleanValue v = (BooleanValue) scale.createValue();
        StringBuilder comment = new StringBuilder();
        for (String key : sampleMetadata.keySet()) {
            String sampleValue = sampleMetadata.get(key);
            String resultValue = resultMetadata.get(key);
            if (resultValue == null) {
                numMissing++;
                comment.append(" - " + key + "\n");
            } else if (!resultValue.equals(sampleValue)) {
                numChanged++;
                comment.append(" ~ " + key + ": sample=" + sampleValue + ", result=" + resultValue + "\n");
            }
        }
        if ((numChanged == 0) && (numMissing == 0)) {
            v.bool(true);
            v.setComment("result contains complete metadata of sample");
        } else {
            v.bool(false);
            comment.insert(0, "following differences found: (- .. missing, ~ .. altered):\n");
            v.setComment(comment.toString());
        }
        return v;
    }

    private Value identicalValues(String v1, String v2, Scale s) {
        BooleanValue bv = (BooleanValue) s.createValue();
        String s1 = (v1 == null || "".equals(v1)) ? "UNDEFINED" : v1;
        String s2 = (v2 == null || "".equals(v2)) ? "UNDEFINED" : v2;

        if (!"UNDEFINED".equals(s1) && !"UNDEFINED".equals(s2)) {
            // both values are defined:
            if (s1.equals(s2)) {
                bv.bool(true);
                bv.setComment("Both have value " + s1);
            } else {
                bv.bool(false);
                bv.setComment("Reference value: " + s1 + "\nActual value: " + s2);
            }
        } else if (s1.equals(s2)) {
            // both are undefined :
            bv.setComment("Both values are UNDEFINED");
            // bv.setValue("");
        } else {
            // one value is undefined:
            bv.setComment("Reference value: " + s1 + "\nActual value: " + s2);
        }
        return bv;
    }
}
