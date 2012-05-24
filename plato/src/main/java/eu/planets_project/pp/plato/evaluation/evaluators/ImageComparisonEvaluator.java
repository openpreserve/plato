/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.evaluation.evaluators;

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
import eu.planets_project.pp.plato.evaluation.EvaluatorBase;
import eu.planets_project.pp.plato.evaluation.EvaluatorException;
import eu.planets_project.pp.plato.evaluation.IObjectEvaluator;
import eu.planets_project.pp.plato.evaluation.IStatusListener;
import eu.scape_project.planning.model.Alternative;
import eu.scape_project.planning.model.DigitalObject;
import eu.scape_project.planning.model.SampleObject;
import eu.scape_project.planning.model.scales.Scale;
import eu.scape_project.planning.model.util.CriterionUri;
import eu.scape_project.planning.model.values.BooleanValue;
import eu.scape_project.planning.model.values.FloatValue;
import eu.scape_project.planning.model.values.PositiveFloatValue;
import eu.scape_project.planning.model.values.Value;
import eu.scape_project.pw.planning.utils.OS;

public class ImageComparisonEvaluator extends EvaluatorBase implements IObjectEvaluator {
    private static final String NAME = "imagecompare (imagemagick)";
//    private static final String SOURCE = " - evaluated by " + NAME;
	private static Logger log = LoggerFactory.getLogger(ImageComparisonEvaluator.class);
    
    
    private File tempDir = null;
    private Map<DigitalObject, String> tempFiles = new HashMap<DigitalObject, String>();


    private static final String DESCRIPTOR_FILE = "data/evaluation/measurementsConsolidated.xml";
    
    public ImageComparisonEvaluator(){
        // load information about measurements
        loadMeasurementsDescription(DESCRIPTOR_FILE);
    }

    
    public HashMap<CriterionUri, Value> evaluate(Alternative alternative,
            SampleObject sample, DigitalObject result, List<CriterionUri> criterionUris,
            IStatusListener listener) throws EvaluatorException {

        //listener.updateStatus(NAME + ": Start evaluation"); //" for alternative: %s, sample: %s", NAME, alternative.getName(), sample.getFullname()));
        setUp();
        try {
            HashMap<CriterionUri, Value> results = new HashMap<CriterionUri, Value>();
    
            saveTempFile(sample);
            saveTempFile(result);
            
            // NOTE: imageEvaluator is still called once per leaf !
            // -> could be optimized, but the used minimee evaluator will do separate calls anyway 
            ImageCompareEvaluator imageEvaluator = new ImageCompareEvaluator();
            
            for(CriterionUri criterionUri: criterionUris) {
                String propertyURI = criterionUri.getAsURI();
                Scale scale = descriptor.getMeasurementScale(criterionUri);
                if (scale == null)  {
                    // This means that I am not entitled to evaluate this criterion and therefore supposed to skip it:
                    continue;
                }
                String mode = null;
                if (OBJECT_IMAGE_SIMILARITY_EQUAL.equals(propertyURI)) {
                	mode = "equal";
                } else if (OBJECT_IMAGE_SIMILARITY_AE.equals(propertyURI)) {
                	mode = "ae";
                } else if (OBJECT_IMAGE_SIMILARITY_PAE.equals(propertyURI)) {
                	mode = "pae";
                } else if (OBJECT_IMAGE_SIMILARITY_PSNR.equals(propertyURI)) {
                	mode = "psnr";
                } else if (OBJECT_IMAGE_SIMILARITY_MAE.equals(propertyURI)) {
                	mode = "mae";
                } else if (OBJECT_IMAGE_SIMILARITY_MSE.equals(propertyURI)) {
                	mode = "mse";
                } else if (OBJECT_IMAGE_SIMILARITY_RMSE.equals(propertyURI)) {
                	mode = "rmse";
                } else if (OBJECT_IMAGE_SIMILARITY_MEPP.equals(propertyURI)) {
                	mode = "mepp";
                } else if (OBJECT_IMAGE_SIMILARITY_SSIMSIMPLE.equals(propertyURI)) {
                	mode = "ssimSimple";
                } else if (OBJECT_IMAGE_SIMILARITY_SSIMSIMPLEHUE.equals(propertyURI)) {
                	mode = "ssimSimpleHue";
                } else if (OBJECT_IMAGE_SIMILARITY_SSIMSIMPLESATURATION.equals(propertyURI)) {
                	mode = "ssimSimpleSaturation";
                } else if (OBJECT_IMAGE_SIMILARITY_EQUALJUDGED.equals(propertyURI)) {
                	mode = "equalJudged";
                }
                
                if (mode != null) {
                    Value v = null;
                    if (mode.equals("equal")) {
                        Double d= imageEvaluator.evaluate(tempDir.getAbsolutePath(), 
                                tempFiles.get(sample), 
                                tempFiles.get(result),
                                "AE");
                        
                        if (d.compareTo(Scale.MAX_VALUE) == 0) {
                            // No: only evaluation results are returned, no error messages
                            // v.setComment("ImageMagick compare failed or could not be called");
                        } else {
                            v = scale.createValue();
                            ((BooleanValue)v).bool(d.compareTo(0.0) == 0);
                            v.setComment("ImageMagick compare returned "+Double.toString(d)+" different pixels");
                        }
        //                log.debug("difference" + Double.toString(Scale.MAX_VALUE-d));
                    } else {
                        Double d= imageEvaluator.evaluate(tempDir.getAbsolutePath(), 
                                         tempFiles.get(sample), 
                                         tempFiles.get(result),
                                         mode);
                        if (d == null) {
                            // No: only evaluation results are returned, no error messages
                            // v = leaf.getScale().createValue();
                            // v.setComment("ImageMagick comparison failed");
                        } else {
                            v = scale.createValue();
                            if (v instanceof FloatValue) {
                                ((FloatValue)v).setValue(d);
                                v.setComment("computed by ImageMagick compare");                            
                            } else if (v instanceof PositiveFloatValue) {
                                ((PositiveFloatValue)v).setValue(d);
                                v.setComment("computed by ImageMagick compare");                            
                            } else {
                                v.setComment("ImageMagick comparison failed - wrong Scale defined.");
                            }
                        }
                    }
                    if (v != null) {
                        // add the value to the result set
                        results.put(criterionUri, v);
                    }
                }
            }
            return results;
        }finally {
            tearDown();
        }
    }
    

    protected void doClearEm() {
        OS.deleteDirectory(tempDir);
        tempFiles.clear();
    }

    /**
     * 
     * @param migratedObject the object that shall be used as KEY for storing the result bytestream
     * @param resultObject the object that contains the actual bytestream to be stored
     * @return the size of the bytestream
     */
    private void saveTempFile(DigitalObject object) {
        String tempFileName = tempDir.getAbsolutePath()+"/"+System.nanoTime();
        OutputStream fileStream;
        try {
            fileStream = new  BufferedOutputStream (new FileOutputStream(tempFileName));
            if (object != null) {
            	byte[] data = object.getData().getData();            	
	            if (data != null) {
	            	            	
	            	fileStream.write(data);
	            }
            }
            fileStream.close();
            tempFiles.put(object, tempFileName);
        } catch (FileNotFoundException e) {
            log.error("Failed to store tempfile",e);
        } catch (IOException e) {
            log.error("Failed to store tempfile", e);
        }
    }
    
    private void setUp(){
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
