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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.tuwien.minimee.migration.evaluators.ImageCompareEvaluator;
import eu.scape_project.planning.evaluation.EvaluatorException;
import eu.scape_project.planning.evaluation.IObjectEvaluator;
import eu.scape_project.planning.evaluation.IStatusListener;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.measurement.MeasureConstants;
import eu.scape_project.planning.model.scales.BooleanScale;
import eu.scape_project.planning.model.scales.FloatScale;
import eu.scape_project.planning.model.scales.PositiveFloatScale;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.values.BooleanValue;
import eu.scape_project.planning.model.values.FloatValue;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.planning.utils.OS;

public class ImageComparisonEvaluator implements IObjectEvaluator {
    // private static final String NAME = "imagecompare (imagemagick)";
    // private static final String SOURCE = " - evaluated by " + NAME;
    private static Logger log = LoggerFactory.getLogger(ImageComparisonEvaluator.class);

    private File tempDir = null;
    private Map<DigitalObject, String> tempFiles = new HashMap<DigitalObject, String>();

    public ImageComparisonEvaluator() {
    }

    public HashMap<String, Value> evaluate(Alternative alternative, SampleObject sample, DigitalObject result,
        List<String> measureUris, IStatusListener listener) throws EvaluatorException {

        // listener.updateStatus(NAME + ": Start evaluation");
        // //" for alternative: %s, sample: %s", NAME, alternative.getName(),
        // sample.getFullname()));
        setUp();
        try {
            HashMap<String, Value> results = new HashMap<String, Value>();

            saveTempFile(sample);
            saveTempFile(result);

            // NOTE: imageEvaluator is still called once per criterion !
            // -> could be optimized, but the used minimee evaluator will do
            // separate calls anyway
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
                } else if (MeasureConstants.IMAGE_DISTANCE_SSIM.equals(measureUri)) {
                    mode = "ssimSimple";
                    scale = new FloatScale();
                }

                if (mode != null) {
                    Value v = null;
                    if (mode.equals("equal")) {
                        Double d = imageEvaluator.evaluate(tempDir.getAbsolutePath(), tempFiles.get(sample),
                            tempFiles.get(result), "AE");

                        if (d.compareTo(Scale.MAX_VALUE) == 0) {
                            // No: only evaluation results are returned, no
                            // error messages
                            // v.setComment("ImageMagick compare failed or could not be called");
                        } else {
                            v = scale.createValue();
                            ((BooleanValue) v).bool(d.compareTo(0.0) == 0);
                            v.setComment("ImageMagick compare returned " + Double.toString(d) + " different pixels");
                        }
                    } else {
                        Double d = imageEvaluator.evaluate(tempDir.getAbsolutePath(), tempFiles.get(sample),
                            tempFiles.get(result), mode);
                        if (d == null) {
                            // No: only evaluation results are returned, no
                            // error messages
                            // v.setComment("ImageMagick comparison failed");
                        } else {
                            v = scale.createValue();
                            if (v instanceof FloatValue) {
                                ((FloatValue) v).setValue(d);
                                v.setComment("computed by ImageMagick compare");
                            } else if (v instanceof PositiveFloatValue) {
                                ((PositiveFloatValue) v).setValue(d);
                                v.setComment("computed by ImageMagick compare");
                            } else {
                                v.setComment("ImageMagick comparison failed - wrong Scale defined.");
                            }
                        }
                    }
                    if (v != null) {
                        // add the value to the result set
                        results.put(measureUri, v);
                    }
                }
            }
            return results;
        } finally {
            tearDown();
        }
    }

    protected void doClearEm() {
        OS.deleteDirectory(tempDir);
        tempFiles.clear();
    }

    /**
     * 
     * @param migratedObject
     *            the object that shall be used as KEY for storing the result
     *            bytestream
     * @param resultObject
     *            the object that contains the actual bytestream to be stored
     * @return the size of the bytestream
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

    private void tearDown() {
        if (tempDir != null) {
            OS.deleteDirectory(tempDir);
            tempFiles.clear();
            tempDir = null;
        }
    }

}
