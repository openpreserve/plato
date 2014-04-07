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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.tuwien.minimee.migration.evaluators.ImageCompareEvaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.planning.evaluation.EvaluatorException;
import eu.scape_project.planning.evaluation.IObjectEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.measurement.MeasureConstants;
import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.values.BooleanValue;
import eu.scape_project.planning.model.values.FloatValue;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.utils.OS;

/**
 * Object evaluator for image comparison.
 */
public class ImageComparisonEvaluator implements IObjectEvaluator {
    private static Logger log = LoggerFactory.getLogger(ImageComparisonEvaluator.class);

    private File tempDir = null;
    private Map<DigitalObject, String> tempFiles = new HashMap<DigitalObject, String>();

    @Override
    public HashMap<String, Value> evaluate(Alternative alternative, SampleObject sample, DigitalObject result,
        List<String> measureUris, IStatusListener listener) throws EvaluatorException {

        setUp();
        try {
            HashMap<String, Value> results = new HashMap<String, Value>();

            saveTempFile(sample);
            saveTempFile(result);

            // NOTE: imageEvaluator is called once per criterion as the used
            // minimee evaluator will do separate calls anyway
            ImageCompareEvaluator imageEvaluator = new ImageCompareEvaluator();

            for (String measureUri : measureUris) {
                Scale scale = null;
                String mode = null;
                if (MeasureConstants.IMAGE_CONTENT_IS_EQUAL.equals(measureUri)) {
                    mode = "equal";
                    scale = new BooleanScale();
                } else if (MeasureConstants.ABSOLUTE_ERROR_AE.equals(measureUri)) {
                    mode = "ae";
                    scale = new PositiveFloatScale();
                } else if (MeasureConstants.PEAK_ABSOLUTE_ERROR_PAE.equals(measureUri)) {
                    mode = "pae";
                    scale = new PositiveFloatScale();
                } else if (MeasureConstants.PEAK_SIGNAL_TO_NOISE_RATIO.equals(measureUri)) {
                    mode = "psnr";
                    scale = new PositiveFloatScale();
                } else if (MeasureConstants.MEAN_ABSOLUTE_ERROR.equals(measureUri)) {
                    mode = "mae";
                    scale = new PositiveFloatScale();
                } else if (MeasureConstants.MEAN_SQUARED_ERROR.equals(measureUri)) {
                    mode = "mse";
                    scale = new PositiveFloatScale();
                } else if (MeasureConstants.IMAGE_DISTANCE_RMSE.equals(measureUri)) {
                    mode = "rmse";
                    scale = new PositiveFloatScale();
                }

                if (mode != null) {
                    if (mode.equals("equal")) {
                        Double d = imageEvaluator.evaluate(tempDir.getAbsolutePath(), tempFiles.get(sample),
                            tempFiles.get(result), "AE");
                        if (d != null && d.compareTo(Scale.MAX_VALUE) != 0) {
                            Value v = scale.createValue();
                            ((BooleanValue) v).bool(d.compareTo(0.0) == 0);
                            v.setComment("ImageMagick compare returned " + Double.toString(d) + " different pixels");
                            results.put(measureUri, v);
                        }
                    } else {
                        Double d = imageEvaluator.evaluate(tempDir.getAbsolutePath(), tempFiles.get(sample),
                            tempFiles.get(result), mode);
                        if (d != null && d.compareTo(Scale.MAX_VALUE) != 0) {
                            Value v = scale.createValue();
                            if (v instanceof FloatValue) {
                                ((FloatValue) v).setValue(d);
                                v.setComment("computed by ImageMagick compare");
                            } else if (v instanceof PositiveFloatValue) {
                                ((PositiveFloatValue) v).setValue(d);
                                v.setComment("computed by ImageMagick compare");
                            } else {
                                v.setComment("ImageMagick comparison failed - wrong Scale defined.");
                            }
                            results.put(measureUri, v);
                        }
                    }
                }
            }
            return results;
        } finally {
            tearDown();
        }
    }

    /**
     * Writes the provided digital object to the temporary directoy.
     * 
     * @param object
     *            the object to write
     */
    private void saveTempFile(DigitalObject object) {
        String tempFileName = tempDir.getAbsolutePath() + "/" + System.nanoTime();
        OutputStream fileStream;
        try {
            fileStream = new BufferedOutputStream(new FileOutputStream(tempFileName));
            if (object != null) {
                byte[] data = object.getData().getData();
                if (data != null) {

                    fileStream.write(data);
                }
            }
            fileStream.close();
            tempFiles.put(object, tempFileName);
        } catch (FileNotFoundException e) {
            log.error("Failed to store tempfile", e);
        } catch (IOException e) {
            log.error("Failed to store tempfile", e);
        }
    }

    /**
     * Set up environment for image comparison.
     */
    private void setUp() {
        if (tempDir != null) {
            tearDown();
        }
        tempDir = new File(OS.getTmpPath() + "imagecompare" + System.nanoTime());
        tempDir.mkdir();
        tempDir.deleteOnExit();
        tempFiles.clear();
        log.debug("using temp directory " + tempDir.getAbsolutePath());

    }

    /**
     * Tear down environment for image comparison.
     */
    private void tearDown() {
        if (tempDir != null) {
            OS.deleteDirectory(tempDir);
            tempFiles.clear();
            tempDir = null;
        }
    }

}
